# 📚 Kitap Önerileri Uygulaması

Bu proje, kullanıcıya özel kitap önerileri sunan, çok katmanlı bir sistemdir. **Amazon EC2** üzerinde çalışan özel bir sunucu, **React** ile geliştirilmiş modern bir web arayüzü ve Firebase servisleriyle entegre edilmiş **Android (Kotlin)** tabanlı bir mobil uygulamadan oluşmaktadır.

Amacımız, kitap keşfini kolaylaştırmak, kullanıcı etkileşimini artırmak ve düzenli okuma alışkanlığını teşvik eden bir dijital deneyim sunmaktır.

---

## 🧱 Proje Mimarisi

### 🔧 Backend – Amazon EC2 Üzerinde Özel Sunucu
- **Amazon EC2** üzerinde çalışan özel bir **Node.js** sunucusu kuruldu.
- Kitap verileri burada barındırılır ve istemciler buradan veri çeker.
- **Express.js** altyapısıyla geliştirilmiş **RESTful API** yapısı oluşturuldu.

### 🌐 Web Arayüzü – React.js
- **React** kullanarak modern, bileşen tabanlı bir kullanıcı arayüzü geliştirdik.
- Kategorilere göre filtreleme, detay görüntüleme ve öneri sistemi bu arayüzde mevcuttur.

### 📱 Mobil Uygulama – Android (Kotlin)
- **Kotlin dili** ile modern, kullanıcı dostu arayüz geliştirildi.
- Uygulama, **EC2** sunucusundan kitap verilerini çekecek şekilde tasarlandı.
- Kullanıcılar kitap arayabilir, detaylara ulaşabilir ve öneri alabilmektedir.
- **Firebase Authentication**, Firestore ve FCM sistemleriyle tamamen entegredir.

---

## 🔐 Firebase Entegrasyonu
### 👤 Kullanıcı Girişi ve Kayıt
- **Firebase Authentication** ile e-posta ve şifre kullanılarak kullanıcılar kayıt olabilir ve giriş yapabilmektedir.
- Giriş sonrası her kullanıcıya özel veri alanları Firestore üzerinde açılmaktadır.
- Uygulamada aşağıdaki kimlik doğrulama özellikleri de sunulmaktadır:
  - 📩 **E-posta doğrulama:** Kayıt sonrası kullanıcıdan e-posta adresini onaylaması istenir.
  - 🔑 **Şifre sıfırlama:** Şifresini unutan kullanıcılar için sıfırlama bağlantısı e-posta ile gönderilir.
  - 🔐 Tüm bu işlemler Firebase altyapısı ile güvenli ve kullanıcı dostu bir şekilde gerçekleşir.


### ❤️ Favori Kitaplar
- Kullanıcılar kitapları favorilerine ekleyebilir.
- Kullanıcılar, Favoriler sayfasından kitaplara ulaşabilir ve favorilerden kaldırabilmektedir.
- **Firestore güvenlik kuralları** sayesinde her kullanıcı yalnızca kendi verilerine erişebilir.

### 🔔 Bildirim ve Hatırlatıcı Sistemi
- **Firebase Cloud Messaging (FCM)** ile kullanıcıya bildirim gönderim sistemi hazırlandı.
- Ayrıca kullanıcılar kendilerine okuma hatırlatıcıları kurabilmektedir:
  - Bildirim saatini seçebilir.
  - Kurulan hatırlatıcıları güncelleyebilir veya silebilir.
  - Bildirimler otomatik olarak belirtilen saatte gösterilir.

---

## 🎯 Temel Özellikler

### 📚 Kitap Öneri Sistemi
- Seçilen kategori üzerinden **rastgele bir kitap önerilmektedir**.
- **Önerilen kitabın detayları** gösterilir ve kullanıcı kitabı beğendiyse **favorilere ekleyebilmektedir**.

### 🔎 Gerçek Zamanlı Arama
- Yazılan sorguya göre **kitaplar filtrelenerek anlık gösterilir**.

### 📖 Kitap Detayları
- Tıklanan kitaplar **detaylı bilgi ekranında** alt sayfa (BottomSheet) olarak açılır.

### ⏰ Hatırlatıcılar
- Belirli bir saate **kitap okuma hatırlatıcıları** kurma, düzenleme ve silme özellikleri.

---

## 📸 Ekran Görüntüleri

###Uygulama içi görseller **images** klasöründe bulunmaktadır.

