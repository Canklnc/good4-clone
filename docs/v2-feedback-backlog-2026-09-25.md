# Good4 V2 geri bildirim listesi (25.09.2026)

Telefonda yapılan testten çıkan notlar. Durum: hepsi açık.

## Hatalar

- [x] **Günün menüsü çalışmıyor (otomatik aktarım 25.09'da deploy edildi; ilk çalışma 28.09 07:30).** Kafeterya / günün menüsü ekranı veri göstermiyor. Kaynağı (Firestore, scraper, function) kontrol et.
  - 25.09 inceleme: Uygulama `app_config/akdeniz_dining_menu` belgesini okuyor ve sadece bu haftanın menüsünü gösteriyor. Canlıdaki belge en son 18.09'da, 14–18 Eylül haftası için güncellenmiş (14–17 Eylül'de günde 1 yemek var, deneme verisi gibi). Bu hafta menü girilmediği için kutucuk "Bugün için menü yayınlanmadı" gösteriyor.
  - Menü sadece yönetim panelinden (`saveDiningMenu`) elle giriliyor; otomatik aktarım yok. Koddaki 7–11 Eylül yedek menüsü artık hiçbir haftaya denk gelmiyor.
  - Ana sayfadaki "Günün Menüsü" kutucuğu dokunulabilir değil. Haftalık menü bileşeni `AkdenizDiningMenuCard` yazılmış ama hiçbir yerde kullanılmıyor.
  - Seçenekler: (1) bu haftanın menüsünü panelden gir, (2) SKS kaynağından haftalık otomatik aktarım yapan zamanlanmış bir Cloud Function yaz (25.09'da SKS sitesine erişilemedi), (3) kutucuğa dokununca haftalık menüyü göster.
  - Kaynak sayfa: https://sks.akdeniz.edu.tr/tr/haftalik_yemek_listesi-6391. 25.09 akşamı sks, www ve webis.akdeniz.edu.tr sunucularının hepsi zaman aşımına düştü; sayfanın güncel biçimi görülemedi.
  - Arşivdeki 08.03.2026 kopyasında menü metin değil, görsel olarak yayımlanmış (`webis.akdeniz.edu.tr/uploads/1019/yemekhane/Menü güncellemesi.jpg`, 1280×720). Yanında "Yemek içerikleri kitabı" PDF'i var. Kaynak görselse otomatik aktarım için OCR gerekir (Cloud Vision ya da Claude API); HTML tabloysa doğrudan ayrıştırılabilir.
  - 25.09 22:44 güncel biçim: Sayfa (son güncelleme 25.09.2026 17:30) tek bir tasarım görseli yayımlıyor (`webis.akdeniz.edu.tr/uploads/1019/yemekhane/Slayt1 copy <uuid>.JPG`, 2933×1650). Görselde gelecek haftanın (28.09–02.10) öğle menüsü var: her gün ayrı renkli kartta tarih ve gün, yemekler ve kalorileri ("DOMATES ÇORBA(161)") ile "KAL:1085" toplamı. Kartların yerleşimi serbest; otomatik okuma için görsel tanıma gerekiyor (Claude API ya da Cloud Vision). Menü cuma günü bir sonraki hafta için yükleniyor. Servis seçimi kullanıcıda bekliyor.
  - Plan: site açılınca biçimi incele. Pazartesi sabahı çalışan, başarısız olursa gün içinde tekrar deneyen zamanlanmış bir function yaz. Ayrıştırılan menüyü doğrula (5 gün, tarihler bu haftada, her günde yemek var). Yalnızca geçerliyse `app_config/akdeniz_dining_menu`'ya yaz; değilse eski menüyü ezme, hatayı logla. Paneldeki elle giriş yedek olarak kalsın.
  - 25.09 uygulandı (commit/deploy edilmedi): `importSksDiningMenu` zamanlanmış function (hafta içi her gün 07:30 ve 19:30, Europe/Istanbul, 1 GiB). SKS sayfasındaki menü görselini bulur; görsel değişmediyse OCR çalıştırmadan çıkar. Değiştiyse Tesseract ile okur; Türkçe dil verisi pakette, dış API yok. Her günün yemek kalorileri toplamı görseldeki "KAL" toplamına eşit değilse menüyü yazmaz. Geçerli menüyü yönetim paneliyle aynı yoldan (`writeDiningMenu`) yazar, durum bilgisini `system/dining_menu_import`'a kaydeder. Eski haftanın görseli yeni menüyü ezmez.
  - Dosyalar: `functions/src/diningMenuImport.ts`, `diningMenuOcr.ts`, `diningMenuImport.test.ts`, `diningMenuImport.fixture.ts`, `adminPortal.ts` (`writeDiningMenu` ayrıldı), `index.ts`, `package.json`/`package-lock.json` (tesseract.js 7.0.0, sharp 0.35.4, @tesseract.js-data/tur 1.0.0). 57/57 test geçti. Canlı görselle uçtan uca denendi: 28.09–02.10 menüsü birebir okundu.
- [x] **Ders programı bazı bölümlerde çöküyor.** (Düzeltildi; ayrıntı aşağıdaki maddede.) Bazı bölümler seçildiğinde uygulama crash oluyor. Hangi bölümlerde olduğunu bul, crash logunu al, eksik veya beklenmedik alanlara karşı ayrıştırmayı sağlamlaştır.
- [ ] **Login / register ekranında klavye açılırken bug var.** Klavye açılırken yerleşim bozuluyor (alanlar kayıyor, kapanıyor ya da zıplıyor olabilir). Tekrar üret; iOS'ta ime/klavye inset ve scroll davranışını düzelt.

- [x] **Apple ile giriş hata veriyor (login / register).** (Kullanıcı Firebase'de Apple sağlayıcısını açtı; giriş çalışıyor.) Sign in with Apple akışı login ve register ekranlarında başarısız oluyor.
  - 25.09 inceleme: V2 build'inin imzasında `com.apple.developer.applesignin` var. Bundle ID `com.good4.iosApp.v2`, `good4tr-v2` projesinde kayıtlı ve `GoogleService-Info` doğru. `ensureStudentProfile` Cloud Function loglarında 20.09'dan beri hata ya da çağrı kaydı yok.
  - Gerçek hata nedeni yutuluyor: `FirebaseAuthRepositoryIOS.signInWithAppleToken` exception'ı loglamadan genel mesaj dönüyor, `IOSApp.swift` içindeki `didCompleteWithError` da `NSError` kodunu loglamıyor.
  - Teşhis için geçici loglar eklendi, neden bulunduktan sonra kaldırıldı.
  - 25.09 cihaz testi: Apple doğrulama adımı başarılı. Hata sonrasında, Firebase `signInWithCredential` çağrısında çıkıyor.
  - **Neden bulundu:** `FIRAuthErrorDomain Code=17006 ERROR_OPERATION_NOT_ALLOWED`, "The identity provider configuration is not found." Apple sağlayıcısı `good4tr-v2` Firebase Authentication'da etkin değil.
  - Çözüm (Firebase Console): Authentication > Sign-in method > Apple'ı etkinleştir. Hesap silmede Apple token iptali (`revokeToken`) de çalışsın diye OAuth code flow alanlarına Apple Team ID, Key ID ve Sign in with Apple private key'i gir.
  - 25.09 Codex güncellemesi: Kullanıcı onayıyla Apple sağlayıcısı etkinleştirildi ve Firebase Authentication'da yeni Apple kullanıcısı oluştu. Yasal onay/profil oluşturma ve hesap silme testi bekliyor; OAuth code flow anahtarları henüz girilmedi.
  - Uyarı: Kotlin/Native'de `NSLog("%@", kotlinString)` gibi argümanlı çağrı uygulamayı çökertiyor (EXC_BREAKPOINT). Mevcut `Logger` kullanılmalı.
- [x] **Firestore izin hataları (cihaz logu, 25.09).** Öğrenci oturumu açıkken şu okumalar `Missing or insufficient permissions` (kod 7) ile reddediliyor:
  - Düzeltildi (cihazda doğrulanmadı): `app_config/global` / `universities` ve `codes`, V2'ye taşınmamış eski rezervasyon sisteminin verileri (`LEGACY_DEPENDENCIES.md`). Kurallar bilinçli olarak kapalı. V2'de `AppConfigRepository` ve `CodeRepository` bu sorguları artık yapmıyor; varsayılan ayar ve boş sonuç dönüyor. Kurallar gevşetilmedi.
  - `app_config/global` belgesi (`getDocument`, açılışta).
  - `codes` koleksiyonu: `userId == uid` ve `status == pending` sorgusu, `userId == uid` + `createdAt desc` + `limit 10` sorgusu ve `userId` ile id sorgusu.
  - Yapılacak: `firebase/v2/firestore.rules` ile istemcinin yaptığı sorguları karşılaştır. Kural sorguyu kapsamıyorsa kuralı ya da sorguyu düzelt; `codes` V2'de artık kullanılmıyorsa istemci çağrısını kaldır. Kural testlerini güncelle.
- [x] **Topluluk üyelik belgesi bulunamıyor.** `organizations/kadin-girisimciler-toplulugu/members/{uid}` için `getDocument` "not found" dönüyor. Kullanıcı üye değilse bu beklenen bir durum olabilir; öyleyse hata olarak loglanmamalı. Değilse üyelik verisini kontrol et. Firebase Console > Authentication > Sign-in method > Apple sağlayıcısının açık olduğunu kontrol et. Ekranda görünen hata metnini ve cihaz logunu al.
  - Düzeltildi: `CommunityRepository.access()` her öğrenci için bütün toplulukları listeleyip her birinin üyelik belgesini okuyordu (N+1 sorgu, profil, ayarlar ve topluluklar ekranında). Artık önce `users/{uid}.role` okunuyor; rol `communityManager`, `communityStaff` ya da `good4Admin` değilse tarama yapılmıyor. Rol okunamazsa eski taramaya dönülüyor.

- [x] **Ders programı bazı bölümlerde çöküyor (düzeltildi).** Ziraat Fakültesi verisindeki Türkçe gün adları (`PAZARTESİ`…) `ScheduleDay.valueOf` ile ayrıştırılınca çöküyordu; ayrıca 5. sınıf seçiliyken başka fakülteye geçmek liste sınırı dışına çıkıyordu. 80 bölüm × tüm sınıflar (321 kombinasyon) çökmeden açılıyor.
- [x] **Bilgisayar Mühendisliği ve Yapay Zeka ve Veri Mühendisliği programları (düzeltildi).** Google Sheets aktarımı sadece ilk saati almıştı; bazı dersler yanlış gün ve derslikteydi. Kaynaktan yeniden üretildi.
- [x] **Tüm ders programlarının kaynakla karşılaştırılması (tamamlandı; 2 bölüm kaynak bekliyor).** Görüntü tabanlı PDF'lerden yapılan aktarımda ders başlangıç ve bitişleri kayabiliyor (Turizm ve Gastronomi 1. sınıfta 5 ders düzeltildi). 26.09 itibarıyla tablodaki bütün bölümler denetlendi ve düzeltmeler uygulandı (commit edilmedi). Jeoloji Mühendisliği de PNG kaynağıyla denetlendi. Sosyal Bilgiler Öğretmenliği ve Hemşirelik 3–4 için güncel kaynak yayımlanmadığından veri değiştirilmedi ve kullanıcı onayıyla beklemeye alındı; uygulamadaki kaynak uyarıları duruyor. Ayrıntı: `docs/handoff-schedule-audit-2026-09-25.md`.

## İyileştirmeler

- [x] **Ders programı ilk açılışta bölüm seçimine yönlendirsin.** Fakülte, bölüm veya sınıf seçilmemişse Ders Programı açılınca öğrenci Hesap Ayarları'ndaki akademik seçime gönderilsin; kaydedince programa dönsün.

- [ ] **Apple/Google girişinde bekleme geri bildirimi (düzeltildi, test bekliyor).** Apple sayfası kapandıktan sonra Firebase girişi ve profil kontrolü sürerken buton yalnızca griye dönüyordu. Artık butonda dönen gösterge ve "Giriş yapılıyor…" yazısı çıkıyor (`LoginState.federatedSignIn`).

- [x] **Bildirim ringindeki sarı nokta.** Öğrenci bildirimi görüldükten sonra ringin üstündeki sarı nokta kaybolmalı. Görüldü (okundu) durumu kaydedilmeli.
  - Düzeltildi (26.09, simülatörde doğrulandı): Zildeki nokta koşulsuz çiziliyordu. Bildirimler artık sabit kimlikli bir listede (`notification/NotificationInbox.kt`). Görülen kimlikler cihazda saklanıyor (iOS `NSUserDefaults`, Android `SharedPreferences`). Bildirimler ekranı açılınca hepsi görüldü sayılıyor, nokta kayboluyor ve uygulama yeniden açılınca geri gelmiyor. Yeni bildirim eklerken yeni bir `id` vermek yeterli.
- [ ] **Topluluk etkinliklerine filtre.** (26.09: Kullanıcı `events.categoryId`, sekiz sabit kategori, tek callable ile takip okuması ve V2 takip et/bırak işlemini onayladı. Kaynakta uygulandı; deploy yapılmadı.)
  - 26.09 Codex tamamladı (commit/deploy edilmedi): 8 kategori (`events.categoryId`), "Takip ettiğim topluluklar" filtresi, tek sorguyla takip listesi, V2 takip et/bırak. Testler: kurallar 17/17, backend 65/65, filtreler 3/3; simülatörde doğrulandı. Canlıya geçiş için functions, indeks, kurallar ve mobil/web yayını gerekiyor. Rapor: `docs/handoff-community-filters-2026-09-26.md`.
  - Kategori ve "Takip ettiğim topluluklar" filtreleri öğrenci Topluluklar ekranında birlikte, 10 etkinlik sınırından önce uygulanır. Topluluk detayında kategori ve mevcut kayıt filtresi birlikte çalışır. Kategorisiz eski etkinlikler korunur.
  - Web/mobil etkinlik formlarında kategori seçimi; backend izin listesi ve eski istemcinin kategori göndermediği düzenlemelerde mevcut değeri koruma eklendi.
  - `getFollowingCommunityIds` tek collection group sorgusuyla yalnızca oturum sahibinin kanonik takip kimliklerini döndürür. `setCommunityFollowing`, aktif kullanıcı/topluluk kontrolüyle takip belgesi ve sayacını aynı transaction içinde günceller. İstemcinin doğrudan takip yazmaları kapatıldı; toplu takip okumaları için yeni istemci kuralı açılmadı.
  - Test ve devir: `docs/handoff-community-filters-2026-09-26.md`. Canlıya geçiş için functions, takip indeksi, kurallar ve istemci/panel yayını ayrıca gerekiyor.
- [ ] **Ana sayfa kutucuklarını düzenleme ("Sayfanı Düzenle").** Kullanıcı ana sayfadaki menü kutucuklarını istediği gibi sıralayıp gösterip gizleyebilmeli.
  - Referans tasarım: [home-edit-reference-2026-09-25.webp](feedback-assets/home-edit-reference-2026-09-25.webp)
  - Referanstaki yapı: kırmızı başlık ("Sayfanı Düzenle", geri oku, sağda ayar ikonu). Her satırda renkli ikon kutusu, başlık ve sağda sürükleme tutamağı (≡) var. Bir ayırıcı çizgi listeyi ikiye bölüyor: üstte ana sayfada görünenler (Kafeterya, Kampüs Navigasyonu, Takvim, ODTUClass, Ders Bilgileri, ODTÜ Mail), altta gizli olanlar (METUFix, Ring Saatleri, Mediko, Kampüs Haberleri, Spor Rezervasyonu…).
  - Sıralama ve görünürlük kullanıcı bazında kalıcı olarak saklanmalı.
- [ ] **Topluluk Admin paneli tasarımı eski.** Paneli uygulamanın güncel V2 tasarım diline göre yenile ve iyileştir. Mevcut QA notlarına da bak: `design-qa-admin-panel.md`.

## Son review (bekliyor)

Commit öncesinde bütün değişiklikler `.claude/skills/pre-commit-review/SKILL.md` ile güvenlik öncelikli olarak review edilecek. Kapsam (25.09 itibarıyla commit edilmemiş):

- Giriş bekleme göstergesi: `LoginState.kt`, `LoginViewModel.kt`, `LoginScreen.kt`, `AuthDesign.kt`, `AppleSignInButton.kt` (common, iOS, Android), `GoogleSignInButton.kt` (common, iOS, Android).
- Ders programı verileri ve çökme: `ClassSchedule.kt`, `AgricultureSchedules.kt`, `TourismSchedules.kt`, `ClassScheduleScreen.kt` (boş program uyarısı).
- Ders programı yönlendirmesi: `Route.kt`, `NavGraph.kt`, `ClassScheduleViewModel.kt`, `ClassScheduleScreen.kt`, `AccountSettingsState.kt`, `AccountSettingsViewModel.kt`, `AccountSettingsScreen.kt`.
- Denetim araçları: `tools/schedule-audit/`.
- Yemek menüsü otomatik aktarımı: `firebase/v2/functions/src/diningMenuImport*.ts`, `diningMenuOcr.ts`, `adminPortal.ts`, `index.ts`, `package.json`, `package-lock.json`. Yeni bağımlılıkların (sharp yerel ikili dosyaları, tesseract.js) güvenlik açısından incelenmesi gerekiyor.
- Notlar ve devir: `docs/v2-feedback-backlog-2026-09-25.md`, `docs/handoff-apple-signin-2026-09-25.md`, `docs/feedback-assets/`.
- Skill: `.claude/skills/pre-commit-review/SKILL.md`.
- Kullanıcıya ait, dokunulmayacak ve izinsiz stage edilmeyecek: `tools/install-v2-iphone.sh`.
