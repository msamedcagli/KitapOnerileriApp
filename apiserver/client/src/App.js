import React, { useState, useEffect } from 'react';
import { isMobile } from 'react-device-detect';
import { faBookOpen, faMagic } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import NavBar from './components/NavBar';
import BooksGridSection from './components/BooksGridSection';
import BookDetailModal from './components/BookDetailModal';
import RecommendationModal from './components/RecommendationModal';
import FeaturedBooksCarousel from './components/FeaturedBooksCarousel';

// Mobil ve Masaüstü görünümleri için ayrı bileşenler
const MobileView = ({ children }) => <div className="mobile-view">{children}</div>;
const DesktopView = ({ children }) => <div className="desktop-view">{children}</div>;

function App() {
  const [allBooks, setAllBooks] = useState([]);
  const [filteredBooks, setFilteredBooks] = useState([]);
  const [bookDetailModalOpen, setBookDetailModalOpen] = useState(false);
  const [recommendationModalOpen, setRecommendationModalOpen] = useState(false);
  const [selectedBook, setSelectedBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [popularBooks, setPopularBooks] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    const fetchBooks = async () => {
      try {
        const response = await fetch('/api/kitaplar');
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setAllBooks(data);
        setFilteredBooks(data);
        setPopularBooks(data.slice(0, 5));

        const uniqueCategories = [...new Set(data.map(book => book.category))].filter(Boolean);
        setCategories(['Tümü', ...uniqueCategories]);

      } catch (err) {
        setError('Kitaplar yüklenirken hata oluştu: ' + err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchBooks();
  }, []);

  useEffect(() => {
    if (selectedCategory === 'Tümü' || selectedCategory === '') {
      setFilteredBooks(allBooks);
    } else {
      setFilteredBooks(allBooks.filter(book => book.category === selectedCategory));
    }
  }, [selectedCategory, allBooks]);

  const openBookDetailModal = (book) => {
    setSelectedBook(book);
    setBookDetailModalOpen(true);
  };

  const closeBookDetailModal = () => {
    setBookDetailModalOpen(false);
    setSelectedBook(null);
  };

  const openRecommendationModal = () => {
    setRecommendationModalOpen(true);
  };

  const closeRecommendationModal = () => {
    setRecommendationModalOpen(false);
  };

  const handleCategoryChange = (event) => {
    setSelectedCategory(event.target.value);
  };

  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const scrollToBooks = () => {
    const booksSection = document.getElementById('all-books-section');
    if (booksSection) {
      booksSection.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <>
      <NavBar onRecommendClick={openRecommendationModal} onHomeClick={scrollToTop} onBooksClick={scrollToBooks} />

      {isMobile ? (
        <MobileView>
          <div className="container">
            {popularBooks.length > 0 && (
              <div className="featured-book-section">
                <h2 className="section-title">
                  <FontAwesomeIcon icon={faBookOpen} /> Öne Çıkan Kitaplar
                </h2>
                <FeaturedBooksCarousel books={popularBooks} onBookClick={openBookDetailModal} />
              </div>
            )}

            <div id="all-books-section" className="books-grid-section">
              <div className="section-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
                <h2 className="section-title" style={{ margin: 0, borderBottom: 'none', paddingBottom: '0' }}>
                  <FontAwesomeIcon icon={faBookOpen} /> Tüm Kitaplar
                </h2>
                <div className="category-filter-container">
                  <label htmlFor="categoryFilter">Kategori:</label>
                  <select id="categoryFilter" className="category-filter-select" value={selectedCategory} onChange={handleCategoryChange}>
                    {categories.map(cat => (
                      <option key={cat} value={cat}>{cat}</option>
                    ))}
                  </select>
                </div>
              </div>
              <BooksGridSection
                title=""
                icon={null}
                books={filteredBooks}
                loading={loading}
                error={error}
                onBookClick={openBookDetailModal}
              />
            </div>

            {bookDetailModalOpen && selectedBook && (
              <BookDetailModal book={selectedBook} onClose={closeBookDetailModal} />
            )}

            {recommendationModalOpen && (
              <RecommendationModal allBooks={allBooks} onClose={closeRecommendationModal} />
            )}
          </div>
        </MobileView>
      ) : (
        <DesktopView>
          <div className="container">
            {popularBooks.length > 0 && (
              <div className="featured-book-section">
                <h2 className="section-title">
                  <FontAwesomeIcon icon={faBookOpen} /> Öne Çıkan Kitaplar
                </h2>
                <FeaturedBooksCarousel books={popularBooks} onBookClick={openBookDetailModal} />
              </div>
            )}

            <div id="all-books-section" className="books-grid-section">
              <div className="section-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
                <h2 className="section-title" style={{ margin: 0, borderBottom: 'none', paddingBottom: '0' }}>
                  <FontAwesomeIcon icon={faBookOpen} /> Tüm Kitaplar
                </h2>
                <div className="category-filter-container">
                  <label htmlFor="categoryFilter">Kategori:</label>
                  <select id="categoryFilter" className="category-filter-select" value={selectedCategory} onChange={handleCategoryChange}>
                    {categories.map(cat => (
                      <option key={cat} value={cat}>{cat}</option>
                    ))}
                  </select>
                </div>
              </div>
              <BooksGridSection
                title=""
                icon={null}
                books={filteredBooks}
                loading={loading}
                error={error}
                onBookClick={openBookDetailModal}
              />
            </div>

            {bookDetailModalOpen && selectedBook && (
              <BookDetailModal book={selectedBook} onClose={closeBookDetailModal} />
            )}

            {recommendationModalOpen && (
              <RecommendationModal allBooks={allBooks} onClose={closeRecommendationModal} />
            )}
          </div>
        </DesktopView>
      )}
    </>
  );
}

export default App;