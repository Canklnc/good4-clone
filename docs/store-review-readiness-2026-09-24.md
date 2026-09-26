# Good4 V2 mağaza incelemesi kontrol listesi

Tarih: 24 Eylül 2026. Kapsam: `/Users/cankilinc/Desktop/Good4/Good4 dev` kaynak incelemesi, iki yasal bağlantının canlı yanıtı ve Android `prodRelease` derlemesi. Mevcut Good4 uygulamasının **güncellemesi** olarak yayımlanması hedefleniyor. App Store Connect ve Play Console ayarları ile iOS arşivi bu incelemede doğrulanmadı.

## Yayından önce kapatılacaklar

- [ ] **Üyelik sözleşmesi bağlantısı (iki mağaza):** Sözleşme `https://good4tr-v2.web.app/uyelik-sozlesmesi` adresine eklendi ve kayıt ekranının kaynak bağlantısı güncellendi. Canlı sitede ve mağazadaki son sürümde açıldığını dağıtım sonrasında doğrula; önceki `https://good4tr.com/uyelik-sozlesmesi-kullanici.pdf` adresi 404 veriyordu.
- [ ] **Kullanıcı içeriği güvenliği (iki mağaza):** Topluluk yöneticileri etkinlikleri doğrudan yayımlayabiliyor; içerik/kullanıcı için bağlamsal şikâyet ve kullanıcı engelleme akışı bulunamadı. Genel “Geri Bildirim” formu içerik ve kullanıcı kimliğini bağlamıyor. Yayımlama öncesi uygunsuz içerik filtresi, içerik/kullanıcı şikâyeti, engelleme ve şikâyetleri işleme süreci ekle. Kupon onay akışı var; etkinlikler aynı şekilde onaylanmıyor.
- [ ] **Hesap ve ilişkili veri silme (iki mağaza):** V2 callable `deleteMyAccount` kaynak koda eklendi. Kullanıcı profilini, etkinlik kayıt/check-in'lerini, takip ve üyelik belgelerini, kupon/ürün kodlarını, geri bildirimleri ve e-posta anahtarlı topluluk erişimini temizliyor; paylaşılan sipariş, işletme, kampanya, etkinlik ve denetim geçmişinde kullanıcı kimliklerini silinmiş hesap etiketiyle değiştiriyor; Firebase Authentication hesabını en son siliyor. Firebase Emulator'daki 32 Functions testi geçti. Cloud Functions henüz dağıtılmadı ve mağaza sürümünde gerçek hesapla uçtan uca silme denenmedi. Kategori bazında saklama süresi ve istisnaları belirle; kullanıcıya özel Storage nesnesi veya ek V1/harici koleksiyon olmadığını canlı veri envanterinde teyit et.
- [ ] **Google Play web üzerinden silme:** Gizlilik sayfasında e-posta ile silme talebi bilgisi var, fakat bağlantı doğrudan ve belirgin bir silme yoluna açılmıyor. Play Console'a verilecek, Good4 adını gösteren ve uygulama olmadan silme talebi başlatan belirgin bir sayfa/ankraj hazırla.
- [ ] **iOS Google ile giriş eşdeğeri:** iOS girişinde Google var; aktif kaynakta Apple ile giriş veya Apple'ın 4.8 kuralındaki gizli e-posta seçimini sağlayan eşdeğer giriş yok. Kural istisnası gerçekten geçerli değilse Apple ile giriş ekle. Üniversite öğrencilerine yönelik olmak tek başına, mevcut okul/kurum hesabıyla giriş zorunluluğu istisnasını kanıtlamıyor.
- [ ] **Mevcut uygulama kimliği ve imzası:** Test V2 kimlikleri (`com.good4.iosApp.v2`, `com.good4.v2`) ayrı uygulama olur. Güncelleme için iOS `iosApp Prod` / `com.good4.iosApp` ve Android `prod` / `com.good4` paketlerinin mağazadaki kayıtlarla, geliştirici takımı ve imza anahtarlarıyla aynı olduğunu doğrula. Sürüm/build numaralarını mağazadaki son sürümün üzerine çıkar.
- [ ] **Android yayın imzası:** `prodRelease` derlemesi başarılı, fakat yerel `keystore.properties` yok ve çıktı `composeApp-prod-release-unsigned.apk`. Play App Signing/upload anahtarını doğrula; imzalı AAB üret ve iç test kanalına yükle.

## Mağaza formu ve son testler

- [ ] **Gizlilik:** `https://good4tr-v2.web.app/gizlilik` canlıda erişilebilir; bu kaynakta KVKK Aydınlatma Metni ve Gizlilik Politikası oluşturuldu. Kayıt ekranında sözleşme kabulü ile aydınlatma bağlantısı ayrıldı; aydınlatma için zorunlu kabul kutusu yok. Firebase Analytics ve Crashlytics Android/iOS hedef bağımlılıklarından çıkarıldı ve toplama devre dışı bırakma ayarları eklendi; son mağaza paketlerinde ve mağaza beyanlarında bunu ayrıca doğrula. Firebase Console'da Firestore `eur3`, Storage `US-EAST1` ve dağıtılmış Functions `europe-west1` doğrulandı; Google Firebase açıklamasına göre Authentication ABD'de işlenir. Sözleşme tarafı, Good4 hesabındaki Cloud Veri İşleme Eki kabulü, KVKK m. 9 mekanizması ve varsa standart sözleşme bildirimi henüz belgeyle doğrulanmadı; `docs/legal/google-kvkk-aktarim-dosyasi-taslak.md` içindeki açık adımları tamamla. Hesap silme callable'ını dağıtıp gerçek cihazda test et, saklama sürelerini belirle; ardından çalışma sürümü uyarısını kaldır, sayfayı hukuk uzmanına incelet ve kullanıcı Ayarları/Hakkında bölümüne bağlantıları ekle.
- [ ] **İnceleme erişimi:** Öğrenci, topluluk, işletme ve gerekli yönetici özelliklerini gösterebilen test hesapları, onaylanmış örnek içerik ve QR/kupon deneme adımları hazırla. E-posta doğrulama ve rol onayı inceleyeni kilitlememeli. App Store inceleme notlarını ve Play App access alanını doldur.
- [ ] **Metaveri:** Mağaza açıklamaları, ekran görüntüleri, destek bağlantısı, yaş derecelendirmesi ve veri beyanlarını gerçek V2 davranışıyla eşleştir. Bunlar depoda bulunmadığı için tamamlanma durumu bilinmiyor.
- [ ] **Gerçek cihaz kalite testi:** iOS `Prod/Release` arşivini ve Android imzalı `prodRelease` AAB'yi kurup giriş, kayıt, hesap silme, kamera/QR, görsel yükleme, etkinlik/kupon, bağlantılar ve çevrimdışı hata akışlarını dene. Dört `composeResources/font/inter_*.ttf` dosyası gerçek font yerine HTML metni içeriyor; kullanılıyorsa düzelt, kullanılmıyorsa kaldır. Android Gradle eklentisi 8.6.0, API 36 için uyumluluk uyarısı veriyor.

## Şu anda doğru görünenler

- Android hedef API düzeyi 36; 31 Ağustos 2026 sonrası yeni gönderim şartını karşılıyor.
- Gizlilik sayfası HTTP 200 veriyor; iOS gizlilik manifesti var.
- Uygulamada hesap silme düğmesi ve V2 sunucu tarafı ilişkili veri temizleme callable'ı var; callable henüz dağıtılmadı ve yayın sürümünde uçtan uca doğrulanmadı.
- Android prod-release Kotlin derlemesi ve iOS `DebugV2` simülatör derlemesi başarılı; imzalı iOS `Prod/Release` arşivi ve cihazdan silme testi doğrulanmadı.
- Android `:composeApp:assembleProdRelease --offline` 24 Eylül'de başarılı oldu. Çıktı imzasızdır.

## GitHub durumu

`origin` herkese açık `https://github.com/Canklnc/good4-clone` deposuna bağlı. Depodaki `main` yalnızca ilk commit'te; güncel V2 çalışması yerel `feature/akdeniz-yemekhane-menu` dalında ve hiçbir `origin` dalı bu commit'i içermiyor. Çalışma ağacında ayrıca çok sayıda izlenmeyen ` 2` adlı kopya dosya var. Yayına hazırlık çalışmasından önce hangi dosyaların gerçek kaynak olduğu ayrıştırılmalı, gizli bilgiler gözden geçirilmeli ve temiz bir geliştirme dalı GitHub'a gönderilmeli.

## Resmî kurallar

- Apple App Review Guidelines: https://developer.apple.com/app-store/review/guidelines/ (1.2, 2.1, 4.8, 5.1.1)
- Apple hesap silme: https://developer.apple.com/support/offering-account-deletion-in-your-app
- Google Play kullanıcı içeriği: https://support.google.com/googleplay/android-developer/answer/9876937?hl=en
- Google Play hesap silme: https://support.google.com/googleplay/android-developer/answer/13327111?hl=en
- Google Play hedef API: https://support.google.com/googleplay/android-developer/answer/11926878?hl=en-gb
