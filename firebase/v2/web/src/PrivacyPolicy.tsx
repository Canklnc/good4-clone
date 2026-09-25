const sections = [
  {
    title: "1. Veri sorumlusu",
    paragraphs: [
      "Bu gizlilik politikası, Good4 mobil uygulaması ve Good4 web hizmetleri kapsamında işlenen kişisel verilere ilişkindir. Bu hizmetler bakımından veri sorumlusu Ahmetcan Kılınç Şahıs Şirketi'dir.",
      "Gizlilik ve kişisel veri başvurularınız için cannklnc7@gmail.com adresinden bize ulaşabilirsiniz.",
    ],
  },
  {
    title: "2. Hangi verileri işliyoruz?",
    paragraphs: [
      "Hizmeti kullanma biçiminize göre ad ve soyadı, e-posta adresi, profil fotoğrafı, Google hesap kimliği, üniversite ve kampüs bilgisi, hesap rolü ve kullanıcı tercihleri işlenebilir.",
      "Etkinlik kaydı ve katılımı, topluluk takibi, kupon kullanımı, rezervasyonlar, yemekhane görüntüleme tercihleri, geri bildirimler ve uygulama içi işlem kayıtları hizmetin sunulması amacıyla tutulabilir.",
      "Güvenlik ve hata giderme amacıyla cihaz türü, işletim sistemi, uygulama sürümü, IP adresi, oturum ve hata kayıtları gibi teknik veriler işlenebilir.",
    ],
  },
  {
    title: "3. Google ile giriş",
    paragraphs: [
      "Google ile giriş yaptığınızda yalnızca kimliğinizi doğrulamak ve Good4 hesabınızı oluşturmak veya eşleştirmek için gerekli temel hesap bilgileri (ad, e-posta adresi, profil fotoğrafı ve Google hesap kimliği) alınır.",
      "Good4; Gmail iletilerinize, Google Drive dosyalarınıza, takviminize veya kişiler listenize erişim istemez. Google'dan alınan veriler reklam amacıyla satılmaz ya da bağımsız pazarlama profili oluşturmak için kullanılmaz.",
    ],
  },
  {
    title: "4. İşleme amaçları ve hukuki sebepler",
    paragraphs: [
      "Veriler; hesabınızı oluşturmak ve korumak, kampüs hizmetlerini göstermek, etkinlik, topluluk, kupon ve rezervasyon işlemlerini yürütmek, geri bildirimleri yanıtlamak, dolandırıcılığı önlemek, güvenliği sağlamak ve hizmeti geliştirmek amacıyla işlenir.",
      "İşleme faaliyetleri; hizmet sözleşmesinin kurulması veya ifası, hukuki yükümlülüklerin yerine getirilmesi, bir hakkın tesisi, kullanılması veya korunması ve temel haklarınıza zarar vermemek kaydıyla Good4'ün meşru menfaatleri hukuki sebeplerine dayanır. Açık rıza gereken durumlarda ayrıca onayınız alınır.",
    ],
  },
  {
    title: "5. Verilerin paylaşılması ve yurt dışına aktarım",
    paragraphs: [
      "Veriler; yalnızca hizmetin çalışması için gerekli olduğu ölçüde Google Firebase ve Google Cloud gibi altyapı sağlayıcılarıyla, işlem yaptığınız üniversite, topluluk veya işletmeyle ve kanunen yetkili kamu kurumlarıyla paylaşılabilir.",
      "Firebase Authentication ve bazı bulut hizmetleri kapsamında veriler Türkiye dışında bulunan sunucularda işlenebilir. Bu aktarımlar yürürlükteki mevzuata uygun güvence ve sözleşmeler çerçevesinde gerçekleştirilir.",
    ],
  },
  {
    title: "6. Saklama süresi ve güvenlik",
    paragraphs: [
      "Kişisel veriler, ilgili hizmeti sunmak için gereken süre boyunca; hesabın kapanmasından sonra ise yasal yükümlülükler, uyuşmazlıkların çözümü ve güvenlik ihtiyaçlarının gerektirdiği sınırlı sürelerle saklanır. Süre sonunda veriler silinir, yok edilir veya anonim hâle getirilir.",
      "Yetkisiz erişimi, kaybı ve kötüye kullanımı önlemek için erişim kontrolü, kimlik doğrulama, kayıt izleme ve aktarım güvenliği gibi uygun teknik ve idari tedbirler uygulanır.",
    ],
  },
  {
    title: "7. Haklarınız",
    paragraphs: [
      "6698 sayılı Kişisel Verilerin Korunması Kanunu'nun 11. maddesi kapsamında verilerinizin işlenip işlenmediğini öğrenme; işlenmişse bilgi talep etme; amacına uygun kullanılıp kullanılmadığını öğrenme; aktarıldığı kişileri bilme; düzeltilmesini, silinmesini veya yok edilmesini isteme; bu işlemlerin aktarılan üçüncü kişilere bildirilmesini talep etme; otomatik analiz sonucu aleyhinize çıkan bir sonuca itiraz etme ve zararın giderilmesini isteme haklarına sahipsiniz.",
      "Talebinizi kimliğinizi doğrulamaya elverişli bilgilerle birlikte cannklnc7@gmail.com adresine gönderebilirsiniz. Hesabınızın ve ilişkili verilerin silinmesini de aynı adresten talep edebilirsiniz.",
    ],
  },
  {
    title: "8. Çocukların gizliliği ve değişiklikler",
    paragraphs: [
      "Good4 üniversite öğrencilerine yönelik bir hizmettir ve bilerek 13 yaşın altındaki kişilerden hesap oluşturmasını istemez.",
      "Bu politika, hizmetlerde veya mevzuatta meydana gelen değişikliklere göre güncellenebilir. Önemli değişiklikler uygulama veya web sitesi üzerinden duyurulur.",
    ],
  },
];

export default function PrivacyPolicy() {
  return (
    <div className="privacy-page">
      <header className="privacy-header">
        <a className="privacy-brand" href="https://good4tr.com" aria-label="Good4 ana sayfa">
          <img src="/good4-logo.png" alt="" />
          <span>Good4</span>
        </a>
        <a className="privacy-home-link" href="https://good4tr.com">Ana sayfa</a>
      </header>

      <main className="privacy-content">
        <div className="privacy-intro">
          <p className="privacy-eyebrow">Gizlilik ve kişisel veriler</p>
          <h1>Gizlilik Politikası</h1>
          <p>
            Good4'ü kullanırken hangi verilerin neden işlendiğini ve verileriniz üzerindeki
            haklarınızı açık ve anlaşılır biçimde öğrenebilirsiniz.
          </p>
          <time dateTime="2026-09-19">Son güncelleme: 19 Eylül 2026</time>
        </div>

        <div className="privacy-layout">
          <nav className="privacy-nav" aria-label="Sayfa içeriği">
            <strong>İçindekiler</strong>
            {sections.map((section, index) => (
              <a key={section.title} href={`#bolum-${index + 1}`}>{section.title}</a>
            ))}
          </nav>

          <article className="privacy-document">
            {sections.map((section, index) => (
              <section id={`bolum-${index + 1}`} key={section.title}>
                <h2>{section.title}</h2>
                {section.paragraphs.map((paragraph) => <p key={paragraph}>{paragraph}</p>)}
              </section>
            ))}
          </article>
        </div>
      </main>

      <footer className="privacy-footer">
        <span>© 2026 Good4</span>
        <a href="mailto:cannklnc7@gmail.com">İletişim</a>
      </footer>
    </div>
  );
}
