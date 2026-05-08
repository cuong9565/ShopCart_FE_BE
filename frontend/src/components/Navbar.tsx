import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faShoppingCart, faUser, faFlask } from '@fortawesome/free-solid-svg-icons';

interface NavbarProps {
  onNavigate: (view: 'home' | 'test') => void;
  currentView: 'home' | 'test';
}

const Navbar = ({ onNavigate, currentView }: NavbarProps) => {
  return (
    <nav className="flex items-center justify-between px-6 py-4 bg-white shadow-md sticky top-0 z-50">
      {/* Left: Logo */}
      <div className="flex items-center space-x-4">
        <img src="/logo.png" alt="Logo" className="h-10 w-auto" />
        <button
          onClick={() => onNavigate('home')}
          className={`px-3 py-1 rounded transition-colors ${
            currentView === 'home' 
              ? 'bg-blue-500 text-white' 
              : 'text-gray-700 hover:text-blue-600'
          }`}
        >
          Home
        </button>
        <button
          onClick={() => onNavigate('test')}
          className={`px-3 py-1 rounded transition-colors ${
            currentView === 'test' 
              ? 'bg-blue-500 text-white' 
              : 'text-gray-700 hover:text-blue-600'
          }`}
        >
          <FontAwesomeIcon icon={faFlask} className="mr-1" />
          Test API
        </button>
      </div>

      {/* Right: Icons */}
      <div className="flex items-center space-x-6">
        <button className="text-gray-700 hover:text-blue-600 transition-colors cursor-pointer relative">
          <FontAwesomeIcon icon={faShoppingCart} size="lg" />
          <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
            0
          </span>
        </button>
        <button className="text-gray-700 hover:text-blue-600 transition-colors cursor-pointer">
          <FontAwesomeIcon icon={faUser} size="lg" />
        </button>
      </div>
    </nav>
  );
};

export default Navbar;
