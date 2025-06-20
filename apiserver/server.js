const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');

const app = express();
const port = 3000;

app.use(cors());

// Kök dizindeki statik dosyaları (index.html gibi) servis et
app.use(express.static(path.join(__dirname)));

// bookimages klasörünü /bookimages olarak servis et
app.use('/bookimages', express.static(path.join(__dirname, 'bookimages')));

// Kitap verisini data/books.json dosyasından oku
function kitaplariOku() {
  const dosyaYolu = path.join(__dirname, 'data', 'books.json');
  const veri = fs.readFileSync(dosyaYolu, 'utf-8');
  return JSON.parse(veri);
}

// Tüm kitapları dönen API
app.get('/api/kitaplar', (req, res) => {
  try {
    const kitaplar = kitaplariOku();
    res.json(kitaplar);
  } catch (error) {
    res.status(500).json({ message: 'Kitaplar yüklenemedi.' });
  }
});

// Belirli kitap (id ile) API
app.get('/api/kitaplar/:id', (req, res) => {
  try {
    const kitaplar = kitaplariOku();
    const id = parseInt(req.params.id);
    const kitap = kitaplar.find(k => k.id === id);
    if (!kitap) return res.status(404).json({ message: 'Kitap bulunamadı.' });
    res.json(kitap);
  } catch (error) {
    res.status(500).json({ message: 'Kitaplar yüklenemedi.' });
  }
});

app.listen(port, () => {
  console.log(`Sunucu http://localhost:${port} adresinde çalışıyor.`);
});
