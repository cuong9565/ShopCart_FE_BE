import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faShoppingCart, faUser } from '@fortawesome/free-solid-svg-icons';
import { useNavigate } from 'react-router-dom';
import { useCart } from './CartContext';

const Navbar = () => {
  const navigate = useNavigate();
  const { cart } = useCart();

  // Tổng số lượng sản phẩm
  const total = cart.reduce((sum, item) => sum + item.quantity, 0);

  return (
    <nav className="flex items-center justify-between px-6 py-4 bg-white shadow-md sticky top-0 z-50">
      {/* Logo */}
      <div className="flex items-center cursor-pointer" onClick={() => navigate("/")}>
        <img src="/logo.png" alt="Logo" className="h-10 w-auto" />
      </div>

      {/* Icons */}
      <div className="flex items-center space-x-6">
        
        {/* Cart */}
        <button
          onClick={() => navigate("/cart")}
          className="text-gray-700 hover:text-blue-600 transition-colors cursor-pointer relative"
        >
          <FontAwesomeIcon icon={faShoppingCart} size="lg" />

          {/* Badge */}
          {total > 0 && (
            <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
              {total}
            </span>
          )}
        </button>

        {/* User */}
        <button
          onClick={() => console.log("Go to profile")}
          className="text-gray-700 hover:text-blue-600 transition-colors cursor-pointer"
        >
          <FontAwesomeIcon icon={faUser} size="lg" />
        </button>
      </div>
    </nav>
  );
};

export default Navbar;