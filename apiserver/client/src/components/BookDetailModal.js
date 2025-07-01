import React from 'react';

function BookDetailModal({ book, onClose }) {
  if (!book) return null;

  return (
    <div id="bookDetailModal" className="modal book-detail-modal" style={{ display: 'flex' }}>
      <div className="modal-content">
        <button className="close" onClick={onClose}>&times;</button>
        <div className="book-detail-header">
          <img className="book-detail-image" src={book.image || ''} alt="Kitap Görseli" />
          <div className="book-detail-info">
            <h2>{book.title}</h2>
            <p>Yazar: {book.author || 'Bilinmiyor'}</p>
            <p>Kategori: {book.category || 'Belirtilmemiş'}</p>
          </div>
        </div>
        <div className="book-detail-summary">
          <h3>Kitap Özeti</h3>
          <p>{book.summary || 'Bu kitap için özet bulunmuyor.'}</p>
        </div>
      </div>
    </div>
  );
}

export default BookDetailModal;