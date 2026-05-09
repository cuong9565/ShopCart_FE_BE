import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faShoppingCart, faUser } from '@fortawesome/free-solid-svg-icons';

const Navbar = () => {
  return (
    <nav className="flex items-center justify-between px-6 py-4 bg-white shadow-md sticky top-0 z-50">
      {/* Left: Logo */}
      <div className="flex items-center">
        <img src="/logo.png" alt="Logo" className="h-10 w-auto" />
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
