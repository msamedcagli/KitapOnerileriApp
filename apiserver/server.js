const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');

const app = express();
const port = 80;

app.use(cors());

// API rotaları - En spesifik rotalar en üste
function kitaplariOku() {
  const dosyaYolu = path.join(__dirname, 'data', 'books.json');
  const veri = fs.readFileSync(dosyaYolu, 'utf-8');
  return JSON.parse(veri);
}

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

// Statik dosyaları sun - API rotalarından sonra
app.use(express.static(path.join(__dirname, 'client', 'build')));
app.use('/bookimages', express.static(path.join(__dirname, 'bookimages')));

app.listen(port, '0.0.0.0', () => {
  console.log(`Sunucu http://localhost:${port} adresinde çalışıyor.`);
});

// React uygulaması için tüm yolları index.html'e yönlendir
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'client', 'build', 'index.html'));
});
