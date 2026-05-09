import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCartPlus } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router-dom';
import type { Product } from '../types';
//import { useCart } from '../context/CartContext';

const ProductCard = ({ product }: { product: Product }) => {
  return (
    <Link 
      to={`/product/${product.slug}/${product.id}`} 
      className="bg-white rounded-xl hover:shadow-xl transition-all duration-300 group overflow-hidden border border-gray-100 flex flex-col h-full"
    >
      {/* Product Image */}
      <div className="relative overflow-hidden aspect-square">
        <img
          src={product.thumbnailImage}
          alt={product.name}
          className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
        />
      </div>

      {/* Product Info */}
      <div className="p-5 flex flex-col flex-grow">
        <h3 className="text-lg font-bold text-gray-800 mb-2 transition-colors line-clamp-2">
          {product.name}
        </h3>

        <div className="mt-auto flex items-center justify-between">
          <span className="text-xl font-black text-gray-900">
            {product.price.toLocaleString('vi-VN')}đ
          </span>
          <button 
            onClick={(e) => {
              e.preventDefault();
              // TODO: Add to cart logic
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

// import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
// import { faCartPlus } from '@fortawesome/free-solid-svg-icons';
// import { Link } from 'react-router-dom';
// import { useCart } from '../context/CartContext';
// import type { Product } from '../types';

// const ProductCard = ({ product }: { product: Product }) => {
//   const { addToCart } = useCart();

//   return (
//     <Link
//       to={`/product/${product.slug}/${product.id}`}
//       className="bg-white rounded-xl hover:shadow-xl transition-all duration-300 group overflow-hidden border border-gray-100 flex flex-col h-full"
//     >
//       {/* Product Image */}
//       <div className="relative overflow-hidden aspect-square">
//         <img
//           src={product.thumbnailImage}
//           alt={product.name}
//           className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
//         />
//       </div>

//       {/* Product Info */}
//       <div className="p-5 flex flex-col flex-grow">
//         <h3 className="text-lg font-bold text-gray-800 mb-2 transition-colors line-clamp-2">
//           {product.name}
//         </h3>

//         <div className="mt-auto flex items-center justify-between">
//           <span className="text-xl font-black text-gray-900">
//             {product.price.toLocaleString('vi-VN')}đ
//           </span>

//           <button
//             onClick={(e) => {
//               e.preventDefault();

//               addToCart({
//                 id: product.id,
//                 name: product.name,
//                 price: product.price,
//                 image: product.thumbnailImage,
//                 stock: product.stock ?? 10,
//               });
//             }}
//             className="p-3 cursor-pointer text-gray-400 hover:text-blue-600 transition-colors"
//           >
//             <FontAwesomeIcon icon={faCartPlus} />
//           </button>
//         </div>
//       </div>
//     </Link>
//   );
// };

// export default ProductCard;