import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faShoppingCart, faSignOutAlt, faEnvelope } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useState, useRef, useEffect } from 'react';

const Navbar = () => {
  const { user, setShowLoginModal, logout } = useAuth();
  const [showDropdown, setShowDropdown] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  return (
    <nav className="flex items-center justify-between px-6 py-2 bg-white shadow-md sticky top-0 z-50">
      {/* Left: Logo */}
      <div className="flex items-center space-x-4">
        <Link to="/">
          <img
            src="/logo.png"
            alt="Logo"
            className="h-16 w-auto hover:opacity-80 transition-opacity"
          />
        </Link>
      </div>

      {/* Right: Icons */}
      <div className="flex items-center space-x-6">
        {user ? (
          <>
            <Link
              to="/cart"
              className="text-gray-700 hover:text-blue-600 transition-colors cursor-pointer relative mr-6"
            >
              <FontAwesomeIcon icon={faShoppingCart} size="lg" />

              <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                0
              </span>
            </Link>

            <div className="relative" ref={dropdownRef}>
              <button
                onClick={() => setShowDropdown(!showDropdown)}
                className="h-10 w-10 rounded-full bg-primary/10 flex items-center justify-center text-primary font-bold text-sm cursor-pointer hover:bg-primary/20 transition-all border border-primary/20 focus:outline-none"
              >
                {user.email.substring(0, 1).toUpperCase()}
              </button>

              {showDropdown && (
                <div className="absolute right-0 mt-6 w-64 bg-white rounded-xl shadow-xl border border-gray-100 py-3 z-50 transform origin-top-right transition-all duration-200 animate-fadeIn">
                  <div className="px-4 py-2 border-b border-gray-50 flex items-center gap-2 text-gray-700">
                    <FontAwesomeIcon icon={faEnvelope} className="text-gray-400 text-sm" />
                    <span className="text-sm font-semibold truncate max-w-[190px]">{user.email}</span>
                  </div>
                  <div className="px-2 pt-2">
                    <button
                      onClick={() => {
                        setShowDropdown(false);
                        logout();
                      }}
                      className="w-full text-left px-3 py-2 text-sm font-semibold text-gray-600 hover:text-red-600 hover:bg-red-50/50 rounded-lg transition-colors flex items-center gap-2 cursor-pointer"
                    >
                      <FontAwesomeIcon icon={faSignOutAlt} />
                      <span>Đăng xuất</span>
                    </button>
                  </div>
                </div>
              )}
            </div>
          </>
        ) : (
          <button
            onClick={() => setShowLoginModal(true)}
            className="bg-primary hover:bg-primary-dark text-white transition-all font-semibold text-sm cursor-pointer py-1.5 px-5 rounded-md border border-transparent shadow-lg shadow-primary/10"
          >
            Đăng nhập
          </button>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
