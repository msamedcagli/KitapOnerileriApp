import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faAndroid, faGooglePlay } from '@fortawesome/free-brands-svg-icons';

function PromoModal({ onClose }) {
  return (
    <div id="promoModal" className="modal" style={{ display: 'flex' }}>
      <div className="modal-content" style={{ maxWidth: '600px', minWidth: '320px', textAlign: 'center', padding: '48px 32px 40px 32px' }}>
        <button className="close" onClick={onClose} style={{ top: '28px', right: '28px', fontSize: '36px' }}>&times;</button>
        <div style={{ marginTop: '10px', marginBottom: '40px' }}>
          <FontAwesomeIcon icon={faAndroid} size="5x" style={{ color: '#388e3c' }} />
        </div>
        <h2 style={{ color: '#8B4513', marginBottom: '32px', fontSize: '2.3rem', fontWeight: '800', letterSpacing: '1px' }}>Mobil uygulamamız yayında!</h2>
        <a id="googlePlayBtn" href="https://play.google.com/store/apps/details?id=ornek.link" target="_blank" rel="noopener noreferrer" style={{ display: 'inline-block', background: 'linear-gradient(90deg,#388e3c 0%,#4caf50 100%)', color: 'white', fontSize: '1.5rem', fontWeight: '700', padding: '22px 80px', borderRadius: '50px', boxShadow: '0 8px 32px rgba(56,142,60,0.18)', textDecoration: 'none', marginBottom: '10px', transition: 'background 0.2s,transform 0.2s', letterSpacing: '0.5px' }}>
          <FontAwesomeIcon icon={faGooglePlay} style={{ marginRight: '16px', fontSize: '2rem', vertical-align: 'middle' }} /> Google Play
        </a>
      </div>
    </div>
  );
}

export default PromoModal;