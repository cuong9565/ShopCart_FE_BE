import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCartPlus } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router-dom';
import { useCart } from '../hooks/useCart';
import type { Product } from '../types';

const ProductCard = ({ product }: { product: Product }) => {
  const { addToCart } = useCart();

  return (
    <Link
      to={`/product/${product.slug}/${product.id}`}
      className="bg-white rounded-xl hover:shadow-xl transition-all duration-300 group overflow-hidden border border-gray-100 flex flex-col h-full"
    >
      {/* IMAGE */}
      <div className="relative overflow-hidden aspect-square">
        <img
          src={product.thumbnailImage}
          alt={product.name}
          className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
        />
      </div>

      {/* INFO */}
      <div className="p-5 flex flex-col flex-grow">
        <h3 className="text-lg font-bold text-gray-800 mb-2 line-clamp-2">
          {product.name}
        </h3>

        <div className="mt-auto flex items-center justify-between">
          
          {/* PRICE */}
          <span className="text-xl font-black text-gray-900">
            {product.price.toLocaleString('vi-VN')}đ
          </span>

          {/* ADD TO CART BUTTON */}
          <button
            onClick={(e) => {
              e.preventDefault(); // không redirect link
              addToCart(product.id, 1);
            }}
            className="p-3 cursor-pointer text-gray-400 hover:text-blue-600 transition-colors"
          >
            <FontAwesomeIcon icon={faCartPlus} />
          </button>

        </div>
      </div>
    </Link>
  );
};

export default ProductCard;