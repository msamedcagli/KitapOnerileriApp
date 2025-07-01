import React, { useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagic, faDice } from '@fortawesome/free-solid-svg-icons';

function RecommendationModal({ allBooks, onClose }) {
  const [selectedCategory, setSelectedCategory] = useState('');
  const [recommendedBook, setRecommendedBook] = useState(null);

  const getRecommendation = () => {
    if (!selectedCategory) {
      alert('Lütfen bir kategori seçin!');
      return;
    }

    const categoryBooks = allBooks.filter(book => book.category === selectedCategory);
    
    if (categoryBooks.length === 0) {
      alert('Bu kategoride kitap bulunamadı!');
      return;
    }

    const randomBook = categoryBooks[Math.floor(Math.random() * categoryBooks.length)];
    setRecommendedBook(randomBook);
  };

  const categories = [
    "Modern Roman", "Klasik Roman", "Çocuk Romanı", "Distopik Roman",
    "Felsefe", "Kişisel Gelişim", "Tarihî Roman", "Bilimkurgu",
    "Şiir", "Tiyatro / Trajedi", "Psikolojik Roman", "Macera Romanı",
    "Biyografi / Anı"
  ];

  return (
    <div id="recommendationModal" className="modal recommendation-modal" style={{ display: 'flex' }}>
      <div className="modal-content">
        <button className="close" onClick={onClose}>&times;</button>
        <h2><FontAwesomeIcon icon={faMagic} /> Kitap Önerisi</h2>
        
        <select className="category-select" value={selectedCategory} onChange={(e) => setSelectedCategory(e.target.value)}>
          <option value="">Kategori seçiniz...</option>
          {categories.map(category => (
            <option key={category} value={category}>{category}</option>
          ))}
        </select>
        
        <button className="get-recommendation-btn" onClick={getRecommendation}>
          <FontAwesomeIcon icon={faDice} /> Öneri Al
        </button>
        
        {recommendedBook && (
          <div className="recommended-book">
            <img src={recommendedBook.image || ''} alt="Kitap Görseli" />
            <h3>{recommendedBook.title}</h3>
            <p>Yazar: {recommendedBook.author || 'Bilinmiyor'}</p>
            <p>Kategori: {recommendedBook.category || 'Belirtilmemiş'}</p>
            <p style={{ marginTop: '15px', fontStyle: 'italic', color: '#4A3F35' }}>{recommendedBook.summary || ''}</p>
          </div>
        )}
      </div>
    </div>
  );
}

export default RecommendationModal;