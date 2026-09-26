# Good4 V2 mağaza incelemesi: güncel durum

25 Eylül 2026. Mevcut Good4 uygulamasına güncelleme hedefleniyor. Kod kaynağı `Good4 dev`, dal `feature/v2-current-source`.

## Tamamlanan teknik işler

- Üyelik sözleşmesi, KVKK sayfası, gizlilik politikası ve web üzerinden hesap silme talebi sayfası V2 sitesinde yayımlandı. Kayıt ekranında yasal belgeler var; hesap ayarlarına da belge bağlantıları eklendi.
- Apple ile giriş kodu ve hesap silme öncesi Apple token iptali hazır. Arkadaşın Apple Developer takımındaki yetki ve Firebase Apple sağlayıcı ayarı bekleniyor.
- V2 hesap silme, etkinlik kaydı/katılımı, geri bildirim ve eğitim e-postası doğrulama işlevleri dağıtıldı. Gerçek mağaza paketinde uçtan uca doğrulama gerekiyor.
- İçerik bildirme ve topluluk engelleme mobil akışı mevcut. Yeni içerik bildirimleri yönetici panelinde öncelikli görünüyor; yönetici bildirimi kapatabiliyor veya bildirilen etkinliği/kuponu yayından kaldırabiliyor. Bu son yönetim akışı V2 sunucusu ve sitesine dağıtıldı. Gerçek hesaplarla operasyonel deneme gerekiyor.

## İncelemeye göndermeden önce açık işler

1. **Apple hesabı:** Review takımının Account Holder'ı güncel Apple sözleşmesini kabul etmeli. Aynı takımda Sign in with Apple yetkisi/anahtarı açılmalı; Firebase Authentication Apple sağlayıcısı tamamlanmalı ve giriş gerçek cihazda doğrulanmalı. Bunlar tamamlanmadan iOS sürüm taslağı da açılamıyor.
2. **Gizlilik ve veri beyanı:** App Store Connect'teki eski gizlilik URL'si 404. Yeni bağlantı hazır: `https://good4tr-v2.web.app/gizlilik-politikasi`. Ancak Firebase yurt dışı aktarımı, veri saklama süreleri ve nihai metin hukuki incelemesi açık. ASC App Privacy ve Play Data Safety beyanları son imzalı paketle eşleştirilmeli.
3. **Mağaza metaverisi:** Mevcut App Store açıklaması, inceleme notları ve ekran görüntüleri V1 Askıda Yemek/Destekçi akışını anlatıyor. V2 taslak metin `docs/app-store-v2-metadata-draft.md` içinde. Yeni V2 ekran görüntüleri ve çalışan inceleme hesabı hazırlanmalı. Eski inceleme hesabının V2'de çalıştığı varsayılmamalı.
4. **Kullanıcı içeriği operasyonu:** Bildirilen içeriği kaldırma aracı var; gelen bildirimleri kimin ve ne sıklıkla inceleyeceği belirlenmeli. Topluluk yöneticisi etkinliği hâlâ doğrudan yayımlayabiliyor. İnceleme öncesi uygunsuz içerik kontrolü ve gerçek bildirim senaryosu denenmeli.
5. **Hesap silme:** Uygulama içi ve web talep akışı gerçek kullanıcı/veriyle doğrulanmalı; saklama istisnaları ve ilişkili kayıt envanteri kesinleştirilmeli. Play Console hesap silme bağlantısı eklenmeli.
6. **Yayın paketleri:** Android upload imzası/Play App Signing anahtarı bu bilgisayarda doğrulanmadı. Arkadaşın iOS dağıtım takımında imzalı Prod arşivi ve mağaza yüklemesi yapılmadı. App Store/Play Store paket kimliği, sürüm ve build numarası son paketlerde doğrulanmalı.
7. **Mağaza erişimi:** App Store Connect'te incelemeye gönderme yetkisi App Manager rolüyle var; Apple sözleşmesi işlemi blokluyor. Play Console bu oturumda doğru hesaba erişim vermediği için Data Safety, App access ve Account deletion alanları kontrol edilemedi.

Hiçbir mağazaya inceleme gönderimi yapılmadı.
