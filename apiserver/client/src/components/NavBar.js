import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHome, faBook, faMagic } from '@fortawesome/free-solid-svg-icons';

function NavBar({ onRecommendClick, onHomeClick, onBooksClick }) {
  return (
    <nav className="navbar">
      <a href="#" onClick={onHomeClick} className="navbar-brand">Kitap Önerilerim</a>
      <div className="nav-links">
        <button onClick={onHomeClick} className="nav-button">
          <FontAwesomeIcon icon={faHome} /> Ana Sayfa
        </button>
        <button onClick={onBooksClick} className="nav-button">
          <FontAwesomeIcon icon={faBook} /> Kitaplar
        </button>
        <button onClick={onRecommendClick} className="nav-button">
          <FontAwesomeIcon icon={faMagic} /> Öneri Al
        </button>
      </div>
    </nav>
  );
}

export default NavBar;
