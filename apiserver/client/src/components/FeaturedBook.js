import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBook } from '@fortawesome/free-solid-svg-icons';

function FeaturedBook({ book, onClick }) {
  if (!book) return null;

  const truncateSummary = (summary, maxLength) => {
    if (!summary) return 'Bu kitap için özet bulunmuyor.';
    if (summary.length <= maxLength) return summary;
    return summary.substring(0, maxLength) + '...';
  };

  return (
    <div className="featured-book-card" onClick={() => onClick(book)}>
      <div className="featured-book-image">
        {book.image ? (
          <img src={book.image} alt={book.title} />
        ) : (
          <FontAwesomeIcon icon={faBook} size="5x" />
        )}
      </div>
      <div className="featured-book-info">
        <h2 className="featured-book-title">{book.title}</h2>
        <p className="featured-book-author">{book.author || 'Bilinmiyor'}</p>
        <p className="featured-book-category">{book.category || 'Belirtilmemiş'}</p>
        <p className="featured-book-summary">{truncateSummary(book.summary, 250)}</p>
      </div>
    </div>
  );
}

export default FeaturedBook;
