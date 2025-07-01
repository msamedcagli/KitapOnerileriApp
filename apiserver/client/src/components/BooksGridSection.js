import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner } from '@fortawesome/free-solid-svg-icons';
import BookCard from './BookCard';

function BooksGridSection({ title, icon, books, loading, error, onBookClick }) {
  return (
    <div className="books-grid-section">
      
      <div className="books-grid">
        {loading ? (
          <div className="loading">
            <FontAwesomeIcon icon={faSpinner} spin />
            Kitaplar yükleniyor...
          </div>
        ) : error ? (
          <div className="error">{error}</div>
        ) : books && books.length > 0 ? (
          books.map((book, idx) => (
            <BookCard key={idx} book={book} onClick={onBookClick} />
          ))
        ) : (
          <div style={{ textAlign: 'center', color: '#A0522D', padding: '20px' }}>Kitap bulunamadı.</div>
        )}
      </div>
    </div>
  );
}

export default BooksGridSection;