import { FieldValue, type Firestore } from "firebase-admin/firestore";
import { HttpsError } from "firebase-functions/v2/https";

export type StudentIdentity = {
  uid: string;
  email?: string;
  emailVerified: boolean;
  displayName?: string;
  providers: string[];
};

export type StudentProfileInput = {
  displayName?: unknown;
  university?: unknown;
};

export async function ensureStudentProfileService(
  database: Firestore,
  identity: StudentIdentity,
  input: StudentProfileInput = {},
): Promise<{ created: boolean; role: string; status: string }> {
  const email = identity.email?.trim().toLowerCase() ?? "";
  if (!email) {
    throw new HttpsError("failed-precondition", "EMAIL_REQUIRED");
  }
  const isGoogle = identity.providers.includes("google.com");
  const isPassword = identity.providers.includes("password");
  if (!isGoogle && !isPassword) {
    throw new HttpsError("permission-denied", "SUPPORTED_SIGN_IN_REQUIRED");
  }

  const requestedName = typeof input.displayName === "string" ? input.displayName.trim() : "";
  const requestedUniversity = typeof input.university === "string" ? input.university.trim() : "";
  const isEduEmail = /@(?:[a-z0-9-]+\.)*edu\.tr$/i.test(email);

  const userRef = database.doc(`users/${identity.uid}`);
  return database.runTransaction(async (transaction) => {
    const existing = await transaction.get(userRef);
    if (existing.exists) {
      const existingRole = String(existing.get("role") ?? "student");
      const existingStatus = String(existing.get("status") ?? "active");
      if (
        existingRole === "student" &&
        existingStatus === "pendingEmailVerification" &&
        identity.emailVerified
      ) {
        transaction.update(userRef, {
          status: "active",
          updatedAt: FieldValue.serverTimestamp(),
        });
        return { created: false, role: existingRole, status: "active" };
      }
      return {
        created: false,
        role: existingRole,
        status: existingStatus,
      };
    }

    if (isGoogle && !identity.emailVerified) {
      throw new HttpsError("failed-precondition", "VERIFIED_EMAIL_REQUIRED");
    }
    if (isPassword && !isEduEmail) {
      throw new HttpsError("permission-denied", "EDU_EMAIL_REQUIRED");
    }

    const fallbackName = email.split("@")[0] ?? "Öğrenci";
    const displayName = (requestedName || identity.displayName?.trim() || fallbackName).slice(0, 120);
    const status = identity.emailVerified ? "active" : "pendingEmailVerification";
    transaction.create(userRef, {
      email,
      displayName,
      role: "student",
      status,
      university: requestedUniversity.slice(0, 160),
      createdAt: FieldValue.serverTimestamp(),
      updatedAt: FieldValue.serverTimestamp(),
    });
    return { created: true, role: "student", status };
  });
}
