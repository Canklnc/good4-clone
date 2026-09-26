# Good4 V2 topluluk etkinlik filtreleri — 26.09.2026

Kaynak: `/Users/cankilinc/Desktop/Good4/Good4 dev`, dal: `feature/v2-current-source`.

Kullanıcı önce plan istedi, ardından etkinlik bazlı kategori önerisini ve tek callable ile takip filtresini onayladı. V2 takip et/bırak işlemini açmayı ayrıca açıkça onayladı. Commit, stage, deploy ve canlı veri değişikliği yapılmadı.

## Uygulanan davranış

- Öğrenci Topluluklar ana ekranında kategori seçimi ve “Takip ettiğim topluluklar” filtresi. Birlikte uygulanır; toplulukların Keşfet araması değişmez.
- Bütün uygun etkinlik adayları üzerinde filtreleme, ardından ilk 10 kart. Filtre değişiminde yeni etkinlik/takip sorgusu yapılmaz; takip listesi kullanıcıya göre önbelleğe alınır. Ekrana dönüş/yenileme ve takip değişimi listeleri günceller. Hesap değişiminde önceki kullanıcıya ait ekran verisi temizlenir.
- Topluluk detayının öğrenci etkinlik sekmesinde kategori ve mevcut “Etkinlik kayıtlarım” filtresi birlikte çalışır. Yönetici liste filtreleri korunur.
- Boş filtre sonucunda örnek debug etkinlikleri gösterilmez. Takip okuma hatası, “hiç takip yok” olarak sunulmaz; tekrar deneme vardır.
- Web ve mobil yönetici etkinlik formlarında kategori zorunlu seçilir. Mobil etkinlik detayında ve web özeti/detayında kategori gösterilir.

## Veri modeli ve yetki

`events.categoryId`: `academic-science`, `career-entrepreneurship`, `technology`, `culture-arts`, `sports-nature`, `social-entertainment`, `volunteering`, `other`.

Türkçe etiketler ve sözleşme `firebase/v2/CANONICAL_DATA_MODEL.md` içinde. Backend/web sabitleri `functions/src/eventCategories.ts`, mobil eşlemesi `community/EventCategories.kt` içinde.

Eski etkinlikler için backfill yok. Eksik/bilinmeyen kategori “Kategori belirtilmemiş” olarak görünür; “Tümü” ve özel kategorisiz filtreye dahildir. `uncategorized` yalnızca arayüz filtresidir, etkinliğe yazılmaz. Eski istemciden kategori gelmezse mevcut değer korunur; açıkça boş veya bilinmeyen kategori reddedilir. Etkinlik doğrudan yazma kuralları gevşetilmedi.

`getFollowingCommunityIds` UID'yi callable oturumundan alır, aktif profili doğrular ve tek `followers` collection group sorgusu yapar. Yalnızca `organizations/{id}/followers/{uid}` biçimindeki, belge kimliği oturum UID'siyle eşleşen kayıtları döndürür. İstemci bunları zaten yüklenmiş aktif/engellenmemiş topluluklarla eşleştirir. Başka UID veya belge içeriği istemciden kabul edilmez. İstemciye collection group okuma izni eklenmedi; Admin SDK kuralları atladığı için bu kontroller sunucudadır.

`setCommunityFollowing` aktif kullanıcıyı doğrular; yeni takipte topluluğun aktif ve community türünde olmasını zorunlu kılar. Belgeyi sunucu zaman damgasıyla oluşturur/siler, takip sayacını aynı transaction içinde yalnızca gerçek değişiklikte günceller. Yinelenen/eşzamanlı çağrılar sayacı iki kez değiştirmez. Sayaç bulunmayan eski topluluklarda ilk gerçek değişiklikte mevcut takip belgelerinden başlatılır. Sonradan kapatılmış/silinmiş bir topluluk takipten çıkarılabilir. Doğrudan istemci takip yazmaları kurallarda kapatıldı.

`followers.userId` collection group indeksi `firestore.indexes.json` içine eklendi. Yeni Firestore DTO yok; mevcut DTO serializer kayıtları korunuyor. iOS özel `V2EventDto` decoder'ına kategori, mevcut `CommunityFollowDto` için Timestamp/epoch saniyesi decoder'ı eklendi.

## Testler

- `npm --prefix firebase/v2 run test:rules`: 17/17 geçti. Kendi takip belgesini okuma, başkasını/anonim okumayı reddetme, doğrudan takip/sayaç yazmasını ve istemci collection group sorgusunu reddetme, yöneticinin kategoriye doğrudan yazmasını reddetme kontrol edildi.
- `npm --prefix firebase/v2 run test:functions`: 65/65 geçti. Kategori doğrulaması/korunması, organizasyon sahipliği, kanonik takip yolları, aktif hesap ve topluluk kontrolleri, eşzamanlı/tekrarlı takip, eski sayacın başlatılması kontrol edildi.
- `./gradlew :composeApp:testV2DebugUnitTest`: 3/3 filtre testi geçti. İlk 10 dışındaki eşleşme, birleşik filtre, kategorisiz kayıt ve boş sonuç kontrol edildi.
- `npm --prefix firebase/v2/web run build`: geçti.
- Simülatör: `iosApp V2` / `DebugV2`; DerivedData: `~/Library/Developer/Xcode/DerivedData/Good4CommunityFiltersQA`. Son uygulama derlemesi gerçek giriş/kaynak yapılandırmasıyla alınır.
- UI testi için yalnızca yerel Auth/Firestore/Functions emulator'leri, iki yapay hesap, dört topluluk ve 15 etkinlik kullanıldı. Geçici giriş ekranı/yönlendirme dosyaları testten sonra özgün hâline döndürüldü; emulator adresleri uygulama kaynağında kalmadı. İlk imzasız simülatör denemesi keychain erişimini engelledi; yerel simülatör imzasıyla düzeldi. UI test betiğinin mevcut düğme etiketine/erişilebilirlik etiketlerine uyumu düzeltildi.

UI sonucu: yönetici kategori formu geçti; öğrenci kategori/birleşik filtre/boş sonuç/kategorisiz kayıt/takipten çıkma/hiç takip olmaması/yeniden takip/listeye dönüş akışı geçti (`TEST SUCCEEDED`). Takip et/bırak/yeniden takip sonrasında emulator'de `followerCount=1`, takip belgesi sayısı=1, doğru UID ve Firestore Timestamp doğrulandı.

- Öğrenci ekranı: `docs/feedback-assets/community-filters-student-2026-09-26.png`.
- Yönetici formu: `docs/feedback-assets/community-category-editor-2026-09-26.png`.
- Yerel UI test betiği: `/tmp/good4-community-qa/CommunityFiltersUITests.swift`.
- Öğrenci test sonucu: `/tmp/good4-community-qa/ui-results-final.xcresult`.
- Yönetici test sonucu: `/tmp/good4-community-qa/ui-results-signed.xcresult` (yönetici testi başarılı; bu ilk turdaki öğrenci testi, betikteki düğme etiketi nedeniyle başarısız oldu ve son turda düzeltildi).
- Son normal V2 derlemesi: `/tmp/good4-community-final-build.log` (`BUILD SUCCEEDED`).

UI testi sonunda emulator oturumu kapatıldı. Testlerden sonra normal V2 derlemesi simülatöre geri kuruldu ve başlatılmadı; fiziksel iPhone'a kurulum yapılmadı. Yerel emulator ve görsel sunucusu kapatıldı.

## Yayın durumu

Canlıda hiçbir değişiklik yapılmadı. Sonraki yayın ayrı kullanıcı yetkilendirmesiyle yapılmalıdır: takip indeksi hazır olmalı; yeni takip callable'ları ve kategori destekli portal fonksiyonları, takip yazma kuralları, ardından mobil/web sürümü yayınlanmalıdır. Eski kategori göndermeyen istemciler desteklenir. Commit istenirse önce `.claude/skills/pre-commit-review/SKILL.md` uygulanmalı ve sonucu kullanıcıya gösterilmelidir.

Bu oturum schedule kaynaklarına, schedule-audit araçlarına/devir notuna ve kullanıcıya ait `tools/install-v2-iphone.sh` dosyasına dokunmadı. Backlog dosyasında yalnızca bu etkinlik filtresi maddesi güncellendi; önceden mevcut değişiklikler korundu.
