import { dummyProducts, type Product } from '../data/products';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCartPlus, faHeart } from '@fortawesome/free-solid-svg-icons';
import { useCart } from "../components/CartContext";

const ProductCard = ({ product }: { product: Product }) => {
  const { addToCart } = useCart();

  return (
    <div className="bg-white rounded-xl hover:shadow-xl transition-all duration-300 group overflow-hidden border border-gray-100 flex flex-col h-full">
      
      {/* Image */}
      <div className="relative overflow-hidden aspect-square">
        <img
          src={product.image}
          alt={product.name}
          className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
        />
      </div>

      {/* Info */}
      <div className="p-5 flex flex-col flex-grow">
        <h3 className="text-lg font-bold text-gray-800 mb-2 line-clamp-2">
          {product.name}
        </h3>

        <div className="mt-auto flex items-center justify-between">
          <span className="text-xl font-black text-gray-900">
            {product.price.toLocaleString("vi-VN")}đ
          </span>

          {/* Nút mua */}
          <button
            onClick={() => addToCart(product)}
            className="p-3 cursor-pointer hover:text-blue-500"
          >
            <FontAwesomeIcon icon={faCartPlus} />
          </button>
        </div>
      </div>
    </div>
  );
};


const ProductList = () => {
  return (
    <section className="py-12 bg-gray-50">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-center justify-between mb-10">
          <h2 className="text-3xl font-black text-gray-900">
            Sản phẩm v bật
          </h2>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
          {dummyProducts.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      </div>
    </section>
  );
};

export default ProductList;
