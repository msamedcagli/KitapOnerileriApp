const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');
const rateLimit = require('express-rate-limit');
const helmet = require('helmet');

const app = express();
const port = 80;

// Güvenlik başlıklarını ayarla
app.use(helmet());

// CORS'u etkinleştir
app.use(cors());

// Rate Limit (IP başına 15 dakikada en fazla 100 istek)
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 dakika
  max: 100, // Her IP için limit
  message: {
    status: 429,
    message: 'Çok fazla istek gönderdiniz. Lütfen biraz sonra tekrar deneyin.'
  },
});
app.use('/api/', limiter); // Sadece /api yollarına uygula

// Kitapları JSON dosyasından oku
function kitaplariOku() {
  const dosyaYolu = path.join(__dirname, 'data', 'books.json');
  const veri = fs.readFileSync(dosyaYolu, 'utf-8');
  return JSON.parse(veri);
}

// Kitap listesini dönen API
app.get('/api/kitaplar', (req, res) => {
  try {
    let kitaplar = kitaplariOku();

    const q = req.query.q;

    if (q) {
      const search = q.toLowerCase();
      kitaplar = kitaplar.filter(k =>
        k.title.toLowerCase().includes(search) ||
        k.author.toLowerCase().includes(search) ||
        k.category.toLowerCase().includes(search)
      );
    }

    res.json(kitaplar);
  } catch (error) {
    res.status(500).json({ message: 'Kitaplar yüklenemedi.' });
  }
});

// Belirli bir kitabı ID ile dönen API
app.get('/api/kitaplar/:id', (req, res) => {
  try {
    const kitaplar = kitaplariOku();
    const id = req.params.id;
    const kitap = kitaplar.find(k => k.id === Number(id));
    if (!kitap) return res.status(404).json({ message: 'Kitap bulunamadı.' });
    res.json(kitap);
  } catch (error) {
    res.status(500).json({ message: 'Kitaplar yüklenemedi.' });
  }
});

// Statik dosyaları sun (ön yüz ve kitap resimleri)
app.use(express.static(path.join(__dirname, 'client', 'build')));
app.use('/bookimages', express.static(path.join(__dirname, 'bookimages')));

// React uygulamasına gelen tüm diğer yolları index.html'e yönlendir
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'client', 'build', 'index.html'));
});

// Sunucuyu başlat
app.listen(port, '0.0.0.0', () => {
  console.log(`Sunucu http://localhost:${port} adresinde çalışıyor.`);
});
