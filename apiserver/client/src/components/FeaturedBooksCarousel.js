import React, { useState, useEffect } from 'react';
import FeaturedBook from './FeaturedBook';

function FeaturedBooksCarousel({ books, onBookClick }) {
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    if (books && books.length > 0) {
      const interval = setInterval(() => {
        setCurrentIndex((prevIndex) => (prevIndex + 1) % books.length);
      }, 5000); // Her 5 saniyede bir geçiş
      return () => clearInterval(interval);
    }
  }, [books]);

  if (!books || books.length === 0) {
    return null;
  }

  return (
    <div className="featured-book-carousel">
      {books.map((book, index) => (
        <div
          key={book.id || index}
          className={`featured-book-carousel-item ${index === currentIndex ? 'active' : ''}`}
        >
          <FeaturedBook book={book} onClick={onBookClick} />
        </div>
      ))}
    </div>
  );
}

export default FeaturedBooksCarousel;
