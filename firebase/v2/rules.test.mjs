import { readFile } from 'node:fs/promises';
import { after, before, beforeEach, test } from 'node:test';
import assert from 'node:assert/strict';
import {
  assertFails,
  assertSucceeds,
  initializeTestEnvironment,
} from '@firebase/rules-unit-testing';
import {
  collection,
  doc,
  getDoc,
  getDocs,
  query,
  serverTimestamp,
  setDoc,
  updateDoc,
  where,
} from 'firebase/firestore';

const projectId = 'demo-good4-v2';
let testEnv;

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId,
    firestore: {
      host: '127.0.0.1',
      port: 8285,
      rules: await readFile(new URL('./firestore.rules', import.meta.url), 'utf8'),
    },
  });
});

beforeEach(async () => {
  await testEnv.clearFirestore();
});

after(async () => {
  await testEnv?.cleanup();
});

async function seed(path, value) {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await setDoc(doc(context.firestore(), path), value);
  });
}

test('unauthenticated users cannot read active organizations', async () => {
  await seed('organizations/community-1', {
    type: 'community',
    status: 'active',
    name: 'Test Community',
  });

  const db = testEnv.unauthenticatedContext().firestore();
  await assertFails(getDoc(doc(db, 'organizations/community-1')));
});

test('verified students can create only their own student profile', async () => {
  const auth = { email: 'student@example.com', email_verified: true };
  const db = testEnv.authenticatedContext('student-1', auth).firestore();

  await assertSucceeds(setDoc(doc(db, 'users/student-1'), {
    email: 'student@example.com',
    displayName: 'Student',
    role: 'student',
    status: 'active',
    university: 'Akdeniz University',
    createdAt: serverTimestamp(),
    updatedAt: serverTimestamp(),
  }));

  await assertFails(setDoc(doc(db, 'users/admin-1'), {
    email: 'student@example.com',
    displayName: 'Fake Admin',
    role: 'good4Admin',
    status: 'active',
    university: '',
    createdAt: serverTimestamp(),
    updatedAt: serverTimestamp(),
  }));
});

test('active students can update profile selections but not protected fields', async () => {
  await seed('users/student-profile-update', {
    email: 'student@example.com',
    displayName: 'Student',
    role: 'student',
    status: 'active',
    university: 'Akdeniz University',
    createdAt: 1,
    updatedAt: 1,
  });

  const db = testEnv.authenticatedContext('student-profile-update', {
    email: 'student@example.com',
    email_verified: true,
  }).firestore();

  await assertSucceeds(setDoc(doc(db, 'users/student-profile-update'), {
    fullName: 'Student Updated',
    phoneNumber: null,
    faculty: 'İktisadi ve İdari Bilimler Fakültesi',
    major: 'İşletme',
    classYear: '2. Sınıf',
    educationLevel: 'Lisans',
    updatedAt: 1,
  }, { merge: true }));

  await assertFails(setDoc(doc(db, 'users/student-profile-update'), {
    role: 'good4Admin',
    updatedAt: serverTimestamp(),
  }, { merge: true }));
});

test('profile save updates only editable fields on a server-created user', async () => {
  await seed('users/student-profile-existing', {
    email: 'student@example.com',
    displayName: 'Old Name',
    role: 'student',
    status: 'active',
    university: '',
    createdAt: 1,
    updatedAt: 1,
  });

  const db = testEnv.authenticatedContext('student-profile-existing', {
    email: 'student@example.com',
    email_verified: true,
  }).firestore();
  const user = doc(db, 'users/student-profile-existing');

  await assertFails(setDoc(user, {
    email: 'student@example.com',
    displayName: 'Can Kılınç',
    fullName: 'Can Kılınç',
    role: 'student',
    status: 'active',
    university: 'Akdeniz Üniversitesi',
    faculty: 'İktisadi ve İdari Bilimler Fakültesi',
    major: 'İşletme',
    classYear: '3. Sınıf',
    createdAt: 1,
    updatedAt: 1,
    credit: null,
  }));

  await assertSucceeds(updateDoc(user, {
    displayName: 'Can Kılınç',
    fullName: 'Can Kılınç',
    university: 'Akdeniz Üniversitesi',
    faculty: 'İktisadi ve İdari Bilimler Fakültesi',
    major: 'İşletme',
    classYear: '3. Sınıf',
  }));

  const stored = (await getDoc(user)).data();
  assert.equal(stored.email, 'student@example.com');
  assert.equal(stored.role, 'student');
  assert.equal(stored.status, 'active');
  assert.equal(stored.credit, undefined);
  assert.equal(stored.classYear, '3. Sınıf');
});

test('active organization members can read their organization', async () => {
  await seed('users/manager-1', {
    role: 'communityManager',
    status: 'active',
  });
  await seed('organizations/community-1', {
    type: 'community',
    status: 'pending',
    name: 'Test Community',
  });
  await seed('organizations/community-1/members/manager-1', {
    role: 'manager',
    status: 'active',
  });

  const db = testEnv.authenticatedContext('manager-1').firestore();
  await assertSucceeds(getDoc(doc(db, 'organizations/community-1')));
});

test('campaign code state cannot be changed directly by a business user', async () => {
  await seed('users/business-1', {
    role: 'businessOwner',
    status: 'active',
  });
  await seed('campaignCodes/code-1', {
    studentId: 'student-1',
    organizationId: 'business-org-1',
    status: 'issued',
  });

  const db = testEnv.authenticatedContext('business-1').firestore();
  await assertFails(setDoc(doc(db, 'campaignCodes/code-1'), {
    studentId: 'student-1',
    organizationId: 'business-org-1',
    status: 'redeemed',
  }));
});

test('Good4 admins can read audit logs but cannot write them from a client', async () => {
  await seed('users/admin-1', {
    role: 'good4Admin',
    status: 'active',
  });
  await seed('auditLogs/log-1', { action: 'organization.created' });

  const db = testEnv.authenticatedContext('admin-1').firestore();
  await assertSucceeds(getDoc(doc(db, 'auditLogs/log-1')));
  await assertFails(setDoc(doc(db, 'auditLogs/log-2'), {
    action: 'role.changed',
  }));
});

test('signed-in users can read the published dining menu but clients cannot write it', async () => {
  await seed('users/student-1', { role: 'student', status: 'active' });
  await seed('users/admin-1', { role: 'good4Admin', status: 'active' });
  await seed('app_config/akdeniz_dining_menu', {
    weekLabel: '21–25 Eylül 2026',
    weekStart: '2026-09-21',
    weekEnd: '2026-09-25',
    days: [],
  });

  const studentDb = testEnv.authenticatedContext('student-1').firestore();
  const adminDb = testEnv.authenticatedContext('admin-1').firestore();
  const anonymousDb = testEnv.unauthenticatedContext().firestore();
  await assertSucceeds(getDoc(doc(studentDb, 'app_config/akdeniz_dining_menu')));
  await assertFails(getDoc(doc(anonymousDb, 'app_config/akdeniz_dining_menu')));
  await assertFails(setDoc(doc(adminDb, 'app_config/akdeniz_dining_menu'), {
    weekLabel: 'Değiştirilmiş',
  }));
});

test('signed-in users can read the home banner but clients cannot write it', async () => {
  await seed('users/student-1', { role: 'student', status: 'active' });
  await seed('users/admin-1', { role: 'good4Admin', status: 'active' });
  await seed('app_config/home_banner', {
    imageUrl: 'https://example.com/banner.jpg', active: true,
  });
  const studentDb = testEnv.authenticatedContext('student-1').firestore();
  const adminDb = testEnv.authenticatedContext('admin-1').firestore();
  const anonymousDb = testEnv.unauthenticatedContext().firestore();
  await assertSucceeds(getDoc(doc(studentDb, 'app_config/home_banner')));
  await assertFails(getDoc(doc(anonymousDb, 'app_config/home_banner')));
  await assertFails(setDoc(doc(adminDb, 'app_config/home_banner'), { active: false }));
});

test('unknown collections are denied by default', async () => {
  await seed('users/student-1', { role: 'student', status: 'active' });
  await seed('unexpected/data', { value: true });

  const db = testEnv.authenticatedContext('student-1').firestore();
  await assertFails(getDoc(doc(db, 'unexpected/data')));
});

test('students can query only their own server-created event registration and check-in', async () => {
  await seed('users/student-1', { role: 'student', status: 'active' });
  await seed('events/event-1', { organizationId: 'community-1', status: 'published' });
  await seed('events/event-1/registrations/reg-1', { userId: 'student-1', status: 'registered' });
  await seed('events/event-1/registrations/reg-2', { userId: 'student-2', status: 'registered' });
  await seed('events/event-1/checkins/reg-1', { userId: 'student-1' });

  const db = testEnv.authenticatedContext('student-1').firestore();
  await assertSucceeds(getDocs(query(
    collection(db, 'events/event-1/registrations'),
    where('userId', '==', 'student-1'),
  )));
  await assertFails(getDocs(collection(db, 'events/event-1/registrations')));
  await assertSucceeds(getDocs(query(
    collection(db, 'events/event-1/checkins'),
    where('userId', '==', 'student-1'),
  )));
});

test('clients cannot create event registrations or check-ins directly', async () => {
  await seed('users/student-1', { role: 'student', status: 'active' });
  await seed('events/event-1', { organizationId: 'community-1', status: 'published' });
  const db = testEnv.authenticatedContext('student-1').firestore();
  await assertFails(setDoc(doc(db, 'events/event-1/registrations/reg-1'), {
    userId: 'student-1', status: 'registered',
  }));
  await assertFails(setDoc(doc(db, 'events/event-1/checkins/reg-1'), {
    userId: 'student-1', checkedInBy: 'student-1',
  }));
});

test('test harness is active', () => {
  assert.ok(testEnv);
});
