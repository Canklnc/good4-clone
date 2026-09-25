# Devir notu: Apple ile giriş hatası (25.09.2026)

Claude Code oturumundan Codex'e devir. Çalışma dizini: `/Users/cankilinc/Desktop/Good4/Good4 dev`, dal `feature/v2-current-source`. Genel kurallar için `AGENTS.md`'yi oku.

## Durum

Apple ile giriş, login ve register ekranlarında "Apple ile giriş tamamlanamadı" hatası veriyor.

**Neden bulundu (cihaz logu, 20:34):**

```
FIRAuthErrorDomain Code=17006 "The identity provider configuration is not found."
FIRAuthErrorUserInfoNameKey=ERROR_OPERATION_NOT_ALLOWED
```

- Apple doğrulama adımı (ASAuthorization) başarılı. Hata sonraki Firebase `signInWithCredential` çağrısında çıkıyor.
- Apple sağlayıcısı `good4tr-v2` Firebase Authentication'da etkin değil.
- Kod tarafı doğru: imzada `com.apple.developer.applesignin` var, bundle `com.good4.iosApp.v2` `good4tr-v2` projesinde kayıtlı ve `GoogleService-Info` doğru.

## Codex güncellemesi (25.09.2026)

- Kullanıcı onayıyla `good4tr-v2` Firebase Authentication'da Apple sağlayıcısı etkinleştirildi. Console, `Apple enabled` sonucunu gösterdi.
- Sağlayıcı açıldıktan sonra Firebase Authentication'da yeni bir Apple kullanıcısı oluştu; önceki `ERROR_OPERATION_NOT_ALLOWED` engeli aşıldı.
- Telefon ekranında yasal onay ve profil oluşturma sonucu henüz doğrulanmadı.
- OAuth code flow için Team ID, Key ID ve private key alanları boş bırakıldı. Hesap silmedeki Apple token iptali bu yapılandırma tamamlandıktan sonra test edilmeli.

## Kullanıcının yapacağı iş (Firebase Console, kod değil)

1. Tamamlandı: `good4tr-v2` > Authentication > Sign-in method > Apple > Enable.
2. Bekliyor: OAuth code flow configuration için Apple Team ID `5N68564396`, Key ID ve Sign in with Apple private key (.p8). Bu alanlar hesap silmedeki `Auth.auth().revokeToken(withAuthorizationCode:)` için gerekli (`iosApp/iosApp/IOSApp.swift`). Bunlar olmadan hesap silme durur.

Bu bir canlı proje ayarıdır. Kullanıcı yapmalı, CLI ile değiştirme.

## Commit edilmemiş değişiklikler

Teşhis logları (`FirebaseAuthRepositoryIOS.kt`, `IOSApp.swift`) kullanıcının isteğiyle kaldırıldı; kaynak kodda Apple girişiyle ilgili bekleyen değişiklik yok. İkinci test telefonundaki mevcut kurulumda bu loglar hâlâ var; bir sonraki kurulumda gider.

| Dosya | Değişiklik | Sahibi |
|---|---|---|
| `docs/v2-feedback-backlog-2026-09-25.md`, `docs/feedback-assets/` | Kullanıcının geri bildirim listesi ve referans ekran görüntüsü. | Claude |
| `.claude/skills/pre-commit-review/SKILL.md` | Commit öncesi güvenlik review kontrol listesi. | Claude |
| `tools/install-v2-iphone.sh` | Cihaz ID'si parametre olarak alınıyor, provisioning bayrakları eklendi. | **Kullanıcı. Dokunma.** |

## Dikkat

- Kotlin/Native'de `NSLog("…%@…", kotlinString)` gibi argümanlı çağrı uygulamayı çökertiyor (EXC_BREAKPOINT, cihazda doğrulandı). Log için `com.good4.core.util.Logger` kullan.
- `tools/install-v2-iphone.sh`, `rg` kullanıyor ama bu makinede ripgrep kurulu değil. Script'i değiştirmeden çalıştırmak için PATH'e `grep`'e yönlendiren geçici bir `rg` koyuldu. İstenirse script'te `rg -q -F` yerine `grep -q -F` kullanılabilir.
- Varsayılan test telefonu o sırada ulaşılamaz durumdaydı; test, kullanıcının isteğiyle ikinci bir iPhone'da yapıldı. Başka bir cihaza kurmak için: `bash tools/install-v2-iphone.sh <UDID>`
- Log okumak için: `xcrun devicectl device process launch --console --terminate-existing --device <UDID> com.good4.iosApp.v2`

## Sıradaki adımlar

1. Apple sağlayıcısı açıldı ve Firebase Authentication'da Apple kullanıcısı oluştu. Telefon ekranında hata kalmadığını ve kullanıcının içeri girdiğini doğrula. Sadece bu test için yeniden kurmaya gerek yok.
2. Yeni Apple hesabıyla ilk girişte `ensureStudentProfile` akışını, yani yasal onay ekranını ve profil oluşturmayı kontrol et.
3. Hesap silmede Apple token iptalini test et.
4. İsteğe bağlı: `ERROR_OPERATION_NOT_ALLOWED` gibi yapılandırma hatalarında kullanıcıya daha açıklayıcı bir mesaj göster.
5. Commit öncesinde `.claude/skills/pre-commit-review/SKILL.md`'deki güvenlik kontrol listesini uygula. Kullanıcının `tools/install-v2-iphone.sh` değişikliğini izinsiz stage etme.

Diğer açık maddeler (günün menüsü, ders programı crash, Firestore izin hataları, filtreler, ana sayfa düzenleme, admin paneli) `docs/v2-feedback-backlog-2026-09-25.md` içinde.
