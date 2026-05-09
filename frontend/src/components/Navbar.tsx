import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faShoppingCart,
  faUser,
  faFlask,
} from "@fortawesome/free-solid-svg-icons";

import { useNavigate, useLocation } from "react-router-dom";
import { useCart } from "./CartContext";

const Navbar = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const { cart } = useCart();

  // Tổng số lượng sản phẩm
  const total = cart.reduce(
    (sum, item) => sum + item.quantity,
    0
  );

  return (
    <nav className="flex items-center justify-between px-6 py-4 bg-white shadow-md sticky top-0 z-50">
      
      {/* Left */}
      <div className="flex items-center space-x-4">
        
        {/* Logo */}
        <img
          src="/logo.png"
          alt="Logo"
          className="h-10 w-auto cursor-pointer"
          onClick={() => navigate("/")}
        />

        {/* Home */}
        <button
          onClick={() => navigate("/")}
          className={`px-3 py-1 rounded transition-colors ${
            location.pathname === "/"
              ? "bg-blue-500 text-white"
              : "text-gray-700 hover:text-blue-600"
          }`}
        >
          Home
        </button>

        {/* Test API */}
        <button
          onClick={() => navigate("/test")}
          className={`px-3 py-1 rounded transition-colors flex items-center ${
            location.pathname === "/test"
              ? "bg-blue-500 text-white"
              : "text-gray-700 hover:text-blue-600"
          }`}
        >
          <FontAwesomeIcon icon={faFlask} className="mr-1" />
          Test API
        </button>
      </div>

      {/* Right */}
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
          onClick={() => navigate("/profile")}
          className="text-gray-700 hover:text-blue-600 transition-colors cursor-pointer"
        >
          <FontAwesomeIcon icon={faUser} size="lg" />
        </button>
      </div>
    </nav>
  );
};

export default Navbar;