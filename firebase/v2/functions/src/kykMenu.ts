import { FieldValue, type Firestore } from "firebase-admin/firestore";
import { HttpsError } from "firebase-functions/v2/https";
import { requireGood4Admin } from "./adminPortal.js";
import type { KykMenuDay } from "./kykMenuParser.js";

export type { KykMenuDay } from "./kykMenuParser.js";
export { parseKykMenuText } from "./kykMenuParser.js";

export const KYK_MENU_COLLECTION = "kyk_menu_days";
const MAX_DAYS = 31;
const MAX_ITEMS = 20;
const MAX_ITEM_LENGTH = 120;
const ISO_DATE = /^\d{4}-\d{2}-\d{2}$/;

function requireMenuItems(value: unknown, field: string): string[] {
  if (value === undefined) return [];
  if (!Array.isArray(value) || value.length > MAX_ITEMS) {
    throw new HttpsError("invalid-argument", "KYK_MENU_ITEMS_INVALID", { field });
  }
  return value.map((item) => {
    if (typeof item !== "string" || !item.trim() || item.trim().length > MAX_ITEM_LENGTH) {
      throw new HttpsError("invalid-argument", "KYK_MENU_ITEMS_INVALID", { field });
    }
    return item.trim();
  });
}

function requireMenuDay(value: unknown): KykMenuDay {
  const input = (value ?? {}) as { date?: unknown; breakfast?: unknown; dinner?: unknown };
  const date = typeof input.date === "string" ? input.date : "";
  const parsed = new Date(`${date}T00:00:00Z`);
  if (!ISO_DATE.test(date) || Number.isNaN(parsed.getTime()) || parsed.toISOString().slice(0, 10) !== date) {
    throw new HttpsError("invalid-argument", "KYK_MENU_DATE_INVALID");
  }
  const breakfast = requireMenuItems(input.breakfast, "breakfast");
  const dinner = requireMenuItems(input.dinner, "dinner");
  if (breakfast.length === 0 && dinner.length === 0) {
    throw new HttpsError("invalid-argument", "KYK_MENU_DAY_EMPTY");
  }
  return { date, breakfast, dinner };
}

/** Good4 admins publish KYK days; each day is its own document so the app reads one small doc. */
export async function saveKykMenuService(
  database: Firestore,
  actorUid: string,
  input: { days?: unknown },
): Promise<{ savedDates: string[] }> {
  await requireGood4Admin(database, actorUid);
  if (!Array.isArray(input.days) || input.days.length === 0 || input.days.length > MAX_DAYS) {
    throw new HttpsError("invalid-argument", "KYK_MENU_DAYS_INVALID");
  }
  const days = input.days.map(requireMenuDay);
  if (new Set(days.map((day) => day.date)).size !== days.length) {
    throw new HttpsError("invalid-argument", "KYK_MENU_DAY_DUPLICATE");
  }
  const batch = database.batch();
  for (const day of days) {
    batch.set(database.collection(KYK_MENU_COLLECTION).doc(day.date), {
      ...day,
      updatedAt: FieldValue.serverTimestamp(),
      updatedBy: actorUid,
    });
  }
  const savedDates = days.map((day) => day.date).sort();
  batch.create(database.collection("auditLogs").doc(), {
    action: "kykMenu.updated",
    actorUid,
    targetType: "kykMenu",
    targetId: `${savedDates[0]}..${savedDates[savedDates.length - 1]}`,
    metadata: { dayCount: days.length },
    createdAt: FieldValue.serverTimestamp(),
  });
  await batch.commit();
  return { savedDates };
}
