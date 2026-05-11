import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faTrashAlt,
  faCartShopping,
  faArrowLeft,
  faMinus,
  faPlus,
} from '@fortawesome/free-solid-svg-icons';
import { useCart } from '../hooks/useCart';

interface QuantityInputProps {
  productId: string;
  quantity: number;
  updateQuantity: (productId: string, quantity: number) => Promise<boolean>;
  loading: boolean;
}

const QuantityInput = ({ productId, quantity, updateQuantity, loading }: QuantityInputProps) => {
  const [inputValue, setInputValue] = useState<string>(quantity.toString());

  useEffect(() => {
    setInputValue(quantity.toString());
  }, [quantity]);

  const handleCommit = (val: string) => {
    const parsed = parseInt(val, 10);
    if (isNaN(parsed) || parsed < 1) {
      setInputValue(quantity.toString());
    } else if (parsed !== quantity) {
      updateQuantity(productId, parsed);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value;
    if (/^\d*$/.test(val)) {
      setInputValue(val);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleCommit(inputValue);
      e.currentTarget.blur();
    }
  };

  const handleBlur = () => {
    handleCommit(inputValue);
  };

  return (
    <input
      type="text"
      pattern="[0-9]*"
      inputMode="numeric"
      value={inputValue}
      onChange={handleChange}
      onBlur={handleBlur}
      onKeyDown={handleKeyDown}
      disabled={loading}
      className="w-12 text-center font-bold text-gray-800 text-sm focus:outline-none bg-transparent border-none p-0 selection:bg-primary/20"
    />
  );
};

const CartPage = () => {
  const { cart, updateQuantity, removeItem, total, loading } = useCart(true);

  // ─── Loading State ───────────────────────────────────────────────────────
  if (loading && cart.length === 0) {
    return (
      <div className="bg-gray-50 min-h-screen py-10">
        <div className="max-w-6xl mx-auto px-4">
          <div className="animate-pulse space-y-4">
            {[1, 2, 3].map((i) => (
              <div key={i} className="bg-white rounded-2xl h-36 shadow-sm" />
            ))}
          </div>
        </div>
      </div>
    );
  }

  // ─── Empty Cart ──────────────────────────────────────────────────────────
  if (cart.length === 0) {
    return (
      <div className="bg-gray-50 min-h-screen py-10">
        <div className="max-w-6xl mx-auto px-4">

          {/* Breadcrumb */}
          <nav className="text-sm text-gray-500 mb-6">
            <Link to="/" className="hover:text-primary transition">Trang chủ</Link>
            <span className="mx-2">/</span>
            <span className="text-gray-800 font-medium">Giỏ hàng</span>
          </nav>

          <div className="bg-white rounded-2xl shadow-sm p-16 text-center">
            <FontAwesomeIcon icon={faCartShopping} className="text-gray-200 text-7xl mb-6" />
            <h1 className="text-2xl font-black text-gray-800 mb-3">Giỏ hàng trống</h1>
            <p className="text-gray-500 mb-8">Bạn chưa có sản phẩm nào trong giỏ hàng.</p>
            <Link
              to="/"
              className="inline-flex items-center gap-2 bg-primary hover:bg-primary-dark text-white font-bold px-8 py-3.5 rounded-xl transition shadow-lg shadow-primary/20"
            >
              <FontAwesomeIcon icon={faArrowLeft} />
              Tiếp tục mua sắm
            </Link>
          </div>
        </div>
      </div>
    );
  }

  // ─── Main Cart UI ────────────────────────────────────────────────────────
  return (
    <div className="bg-gray-50 min-h-screen py-10">
      <div className="max-w-6xl mx-auto px-4">

        {/* Breadcrumb */}
        <nav className="text-sm text-gray-500 mb-6">
          <Link to="/" className="hover:text-primary transition">Trang chủ</Link>
          <span className="mx-2">/</span>
          <span className="text-gray-800 font-medium">Giỏ hàng</span>
        </nav>

        <h1 className="text-3xl font-black text-gray-900 mb-6">
          Giỏ hàng
          <span className="ml-3 text-base font-medium text-gray-400">({cart.length} sản phẩm)</span>
        </h1>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

          {/* ═══ LEFT: CART ITEMS ═══════════════════════════════════════════ */}
          <div className="lg:col-span-2 space-y-4">
            {cart.map((item) => (
              <div
                key={item.productId}
                data-testid="cart-item"
                className="bg-white rounded-2xl shadow-sm p-5 flex gap-5 hover:shadow-md transition-shadow"
              >
                {/* Product Image */}
                <Link to={`/product/${item.productId}`} className="flex-shrink-0">
                  <img
                    src={item.thumbnailImage}
                    alt={item.productName}
                    className="w-28 h-28 object-cover rounded-xl"
                  />
                </Link>

                {/* Product Info */}
                <div className="flex-1 min-w-0 flex flex-col justify-between">
                  <div>
                    <h2 className="font-bold text-gray-800 text-base leading-snug line-clamp-2">
                      {item.productName}
                    </h2>
                    <p className="text-primary font-black text-lg mt-1">
                      {item.productPrice.toLocaleString('vi-VN')}đ
                    </p>
                  </div>

                  <div className="flex items-center justify-between mt-3">
                    {/* Quantity Controls */}
                    <div className="flex items-center border border-gray-200 rounded-xl overflow-hidden">
                      <button
                        onClick={() => updateQuantity(item.productId, item.quantity - 1)}
                        data-testid="decrease-qty-btn"
                        disabled={loading || item.quantity <= 1}
                        className="px-3 py-2 bg-gray-50 hover:bg-gray-100 text-gray-600 transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
                      >
                        <FontAwesomeIcon icon={faMinus} className="text-xs" />
                      </button>

                      <QuantityInput
                        productId={item.productId}
                        quantity={item.quantity}
                        updateQuantity={updateQuantity}
                        loading={loading}
                      />
                      <span data-testid="cart-item-qty" className="sr-only">
                        {item.quantity}
                      </span>

                      <button
                        onClick={() => updateQuantity(item.productId, item.quantity + 1)}
                        data-testid="increase-qty-btn"
                        disabled={loading}
                        className="px-3 py-2 bg-gray-50 hover:bg-gray-100 text-gray-600 transition-colors disabled:opacity-40"
                      >
                        <FontAwesomeIcon icon={faPlus} className="text-xs" />
                      </button>
                    </div>

                    {/* Subtotal + Delete */}
                    <div className="flex items-center gap-4">
                      <span className="font-black text-gray-900 text-base">
                        {item.subtotal.toLocaleString('vi-VN')}đ
                      </span>
                      <button
                        onClick={() => removeItem(item.productId)}
                        data-testid="remove-item-btn"
                        className="text-gray-300 hover:text-red-500 transition-colors p-2 rounded-lg hover:bg-red-50"
                        title="Xóa sản phẩm"
                      >
                        <FontAwesomeIcon icon={faTrashAlt} />
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))}

            {/* Continue shopping link */}
            <Link
              to="/"
              className="inline-flex items-center gap-2 text-primary hover:text-primary-dark font-semibold text-sm transition mt-2"
            >
              <FontAwesomeIcon icon={faArrowLeft} />
              Tiếp tục mua sắm
            </Link>
          </div>

          {/* ═══ RIGHT: ORDER SUMMARY ════════════════════════════════════════ */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-2xl shadow-sm p-6 sticky top-24">
              <h2 className="text-xl font-black text-gray-900 mb-5">Tóm tắt đơn hàng</h2>

              {/* Subtotal */}
              <div className="space-y-3 mb-5">
                <div className="flex justify-between text-sm text-gray-600">
                  <span>Tạm tính ({cart.length} sản phẩm)</span>
                  <span>{total.toLocaleString('vi-VN')}đ</span>
                </div>
                <div className="flex justify-between text-sm text-gray-400">
                  <span>Phí vận chuyển</span>
                  <span className="italic">Tính ở bước tiếp theo</span>
                </div>
                <div className="flex justify-between text-sm text-gray-400">
                  <span>Mã giảm giá</span>
                  <span className="italic">Áp dụng ở bước tiếp theo</span>
                </div>
              </div>

              {/* Divider */}
              <div className="border-t border-gray-100 pt-5 mb-5">
                <div className="flex justify-between items-center">
                  <span className="font-bold text-gray-800">Tổng cộng</span>
                  <span
                    data-testid="cart-total-price"
                    className="font-black text-2xl text-primary"
                  >
                    {total.toLocaleString('vi-VN')}đ
                  </span>
                </div>
                <p className="text-xs text-gray-400 mt-1">(Chưa bao gồm phí vận chuyển)</p>
              </div>

              {/* Checkout Button */}
              <Link
                to="/checkout"
                data-testid="checkout-btn"
                className="block w-full bg-primary hover:bg-primary-dark text-white text-center font-bold py-4 rounded-xl transition-all shadow-lg shadow-primary/20"
              >
                Tiến hành thanh toán
              </Link>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
};

export default CartPage;