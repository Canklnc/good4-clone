import { FieldValue, type Firestore } from "firebase-admin/firestore";
import { HttpsError } from "firebase-functions/v2/https";
import { requireActiveActor, requireNonEmptyString } from "./shared.js";

export async function submitFeedbackService(
  database: Firestore,
  actorUid: string,
  input: { subject: unknown; message: unknown },
): Promise<{ feedbackId: string }> {
  const subject = requireNonEmptyString(input.subject, "subject", 120);
  const message = requireNonEmptyString(input.message, "message", 2000);
  if (subject.length < 3) throw new HttpsError("invalid-argument", "SUBJECT_INVALID");
  if (message.length < 10) throw new HttpsError("invalid-argument", "MESSAGE_INVALID");

  await database.runTransaction((transaction) => requireActiveActor(database, transaction, actorUid));
  const user = await database.doc(`users/${actorUid}`).get();
  const feedback = database.collection("feedbackSubmissions").doc();
  await feedback.set({
    userId: actorUid,
    userEmail: String(user.get("email") ?? ""),
    userDisplayName: String(user.get("displayName") ?? ""),
    subject,
    message,
    source: "mobile",
    environment: "v2",
    status: "new",
    createdAt: FieldValue.serverTimestamp(),
  });
  return { feedbackId: feedback.id };
}
