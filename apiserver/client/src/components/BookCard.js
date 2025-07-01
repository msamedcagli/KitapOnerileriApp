import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBook } from '@fortawesome/free-solid-svg-icons';

function BookCard({ book, onClick }) {
  return (
    <div className="book-card" onClick={() => onClick(book)}>
      <div className="book-image">
        {book.image ? (
          <img src={book.image} alt={book.title} />
        ) : (
          <FontAwesomeIcon icon={faBook} size="3x" />
        )}
      </div>
      <div className="book-info">
        <div className="book-title">{book.title}</div>
        <div className="book-author">{book.author || 'Bilinmiyor'}</div>
        <div className="book-category">{book.category || 'Belirtilmemiş'}</div>
      </div>
    </div>
  );
}

export default BookCard;