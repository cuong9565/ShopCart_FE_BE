import { useParams, Link } from 'react-router-dom';
import { useProductDetail } from '../hooks/useProductDetail';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronRight, faCartPlus, faMinus, faPlus } from '@fortawesome/free-solid-svg-icons';
import { useState } from 'react';
import { showToast } from '../utils/toast';
import { useCart } from '../hooks/useCart';

const ProductDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const { product, loading, error } = useProductDetail(id);
  const [quantity, setQuantity] = useState(1);
  const [mainImage, setMainImage] = useState<string | null>(null);
  const { addToCart } = useCart();

  const handleAddToCart = async () => {
    if (!product) return;
    if (quantity > product.stockQuantity) {
      showToast('Số lượng vượt quá tồn kho', 'error');
      return;
    }
    await addToCart(product.id, quantity);
  };

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-12 animate-pulse">
        <div className="h-6 bg-gray-200 rounded w-1/3 mb-8"></div>
        <div className="flex flex-col md:flex-row gap-10">
          <div className="w-full md:w-1/2 h-96 bg-gray-200 rounded-xl"></div>
          <div className="w-full md:w-1/2 space-y-4">
            <div className="h-10 bg-gray-200 rounded w-3/4"></div>
            <div className="h-6 bg-gray-200 rounded w-1/4"></div>
            <div className="h-8 bg-gray-200 rounded w-1/3 mt-6"></div>
          </div>
        </div>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-12 text-center">
        <h2 className="text-2xl font-bold text-red-600 mb-4">Lỗi</h2>
        <p className="text-gray-600">{error || 'Không tìm thấy sản phẩm'}</p>
        <Link to="/" className="text-blue-600 hover:underline mt-4 inline-block">Về trang chủ</Link>
      </div>
    );
  }

  const images = product.images && product.images.length > 0 ? product.images : [product.thumbnailImage];
  const currentMainImage = mainImage || images[0];

  return (
    <div className="bg-gray-50 py-8 min-h-screen">
      <div className="max-w-7xl mx-auto px-4">
        {/* Breadcrumb */}
        <nav className="flex items-center text-sm text-gray-500 mb-8">
          <Link to="/" className="hover:text-blue-600 transition-colors">Trang chủ</Link>
          <FontAwesomeIcon icon={faChevronRight} className="mx-3 text-xs" />
          <span className="text-gray-900">{product.category.name}</span>
          <FontAwesomeIcon icon={faChevronRight} className="mx-3 text-xs" />
          <span className="text-blue-600 font-medium truncate">{product.name}</span>
        </nav>

        {/* Product Details */}
        <div className="bg-white rounded-2xl p-6 md:p-10 flex flex-col md:flex-row gap-10">

          {/* Left: Gallery */}
          <div className="w-full md:w-1/2 flex flex-col gap-4">
            <div className="aspect-square rounded-xl overflow-hidden border border-gray-100 bg-gray-50 flex items-center justify-center p-4">
              <img src={currentMainImage} alt={product.name} className="max-w-full max-h-full object-contain mix-blend-multiply" />
            </div>
            {images.length > 1 && (
              <div className="flex gap-4 overflow-x-auto pb-2">
                {images.map((img, idx) => (
                  <button
                    key={idx}
                    onClick={() => setMainImage(img)}
                    className={`flex-shrink-0 w-20 h-20 rounded-lg overflow-hidden border-2 transition-colors ${currentMainImage === img ? 'border-blue-500' : 'border-gray-200 hover:border-blue-300'}`}
                  >
                    <img src={img} alt={`Thumbnail ${idx}`} className="w-full h-full object-cover" />
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Right: Info */}
          <div className="w-full md:w-1/2 flex flex-col">
            <h1 className="text-3xl font-black text-gray-900 mb-4">{product.name}</h1>

            <div className="flex items-center gap-4 mb-6">
              <span className="text-3xl font-bold">
                {product.price.toLocaleString('vi-VN')}đ
              </span>
              <span className={`px-3 py-1 rounded-full text-sm font-medium ${product.stockQuantity > 0 ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                {product.stockQuantity > 0 ? `Còn hàng` : 'Hết hàng'}
              </span>
            </div>

            <div className="prose prose-sm text-gray-600 mb-8 max-w-none">
              <p className="whitespace-pre-wrap leading-relaxed">{product.description || 'Chưa có mô tả cho sản phẩm này.'}</p>
            </div>

            <div className="mt-auto border-t border-gray-100 pt-8">
              <div className="flex items-center gap-6 mb-6">
                <span className="text-gray-700 font-medium">Số lượng:</span>
                <div className="flex items-center border border-gray-200 rounded-lg overflow-hidden">
                  <button
                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                    className="px-4 py-2 bg-gray-50 hover:bg-gray-100 text-gray-600 transition-colors"
                  >
                    <FontAwesomeIcon icon={faMinus} className="text-xs" />
                  </button>
                  <input
                    type="number"
                    value={quantity}
                    onChange={(e) => {
                      const val = parseInt(e.target.value);
                      if (!isNaN(val)) {
                        setQuantity(val);
                      } else {
                        setQuantity(1);
                      }
                    }}
                    className="w-12 text-center font-medium text-gray-900 border-none focus:outline-none focus:ring-0"
                    data-testid="quantity-input"
                  />
                  <button
                    onClick={() => setQuantity(Math.min(product.stockQuantity, quantity + 1))}
                    className="px-4 py-2 bg-gray-50 hover:bg-gray-100 text-gray-600 transition-colors disabled:opacity-50"
                    disabled={quantity >= product.stockQuantity}
                  >
                    <FontAwesomeIcon icon={faPlus} className="text-xs" />
                  </button>
                </div>
              </div>

              <button
                onClick={handleAddToCart}
                data-testid="add-to-cart-btn"
                className="w-full bg-primary hover:bg-primary-dark text-white font-bold py-4 rounded-xl transition-all flex items-center justify-center gap-3 disabled:bg-gray-400 disabled:cursor-not-allowed shadow-lg shadow-primary/10"
                disabled={product.stockQuantity === 0}
              >
                <FontAwesomeIcon icon={faCartPlus} size="lg" />
                {product.stockQuantity > 0 ? 'Thêm vào giỏ hàng' : 'Tạm hết hàng'}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetailPage;
