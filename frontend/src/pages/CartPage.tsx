import { Link } from 'react-router-dom';
import { useCart } from '../hooks/useCart';

const CartPage = () => {
  const { cart, updateQuantity, removeItem, total, loading } = useCart(true);

  // 1. EMPTY CART UI
  if (cart.length === 0) {
    if (loading) {
      return (
        <div className="max-w-6xl mx-auto px-4 py-16 text-center">
          <p className="text-gray-500">Đang tải giỏ hàng...</p>
        </div>
      );
    }
    return (
      <div className="max-w-6xl mx-auto px-4 py-16 text-center">
        <h1 className="text-3xl font-bold mb-4">Giỏ hàng</h1>

        <p className="text-gray-500 mb-6">
          Giỏ hàng của bạn đang trống
        </p>

        <Link
          to="/"
          className="bg-black text-white px-6 py-3 rounded-lg"
        >
          Tiếp tục mua sắm
        </Link>
      </div>
    );
  }

  // 6. MAIN UI
  return (
    <div className="max-w-6xl mx-auto px-4 py-10">
      <h1 className="text-3xl font-bold mb-8">
        Giỏ hàng
      </h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">

        {/* LEFT: CART ITEMS */}
        <div className="lg:col-span-2 space-y-4">
          {cart.map((item) => (
            <div
              key={item.productId}
              data-testid="cart-item"
              className="bg-white rounded-xl shadow-sm p-4 flex gap-4"
            >
              {/* IMAGE */}
              <img
                src={item.thumbnailImage}
                className="w-28 h-28 object-cover rounded-lg"
              />

              {/* INFO */}
              <div className="flex-1">
                <h2 className="font-semibold text-lg">
                  {item.productName}
                </h2>

                <p className="text-red-500 font-bold mt-2">
                  {item.productPrice.toLocaleString('vi-VN')}₫
                </p>

                {/* QUANTITY */}
                <div className="flex items-center gap-3 mt-4">
                  <button
                    onClick={() =>
                      updateQuantity(
                        item.productId,
                        item.quantity - 1
                      )
                    }
                    data-testid="decrease-qty-btn"
                    className="w-8 h-8 border rounded"
                  >
                    -
                  </button>

                  <span data-testid="cart-item-qty">{item.quantity}</span>

                  <button
                    onClick={() =>
                      updateQuantity(
                        item.productId,
                        item.quantity + 1
                      )
                    }
                    data-testid="increase-qty-btn"
                    className="w-8 h-8 border rounded"
                  >
                    +
                  </button>
                </div>
              </div>

              {/* DELETE */}
              <button
                onClick={() => removeItem(item.productId)}
                data-testid="remove-item-btn"
                className="text-red-500 font-medium"
              >
                Xóa
              </button>
            </div>
          ))}
        </div>

        {/* RIGHT: SUMMARY */}
        <div className="bg-white rounded-xl shadow-sm p-6 h-fit">
          <h2 className="text-2xl font-bold mb-6">
            Tóm tắt đơn hàng
          </h2>

          <div className="flex justify-between mb-4">
            <span>Tổng tiền</span>

            <span
              data-testid="cart-total-price"
              className="font-bold text-red-500 text-xl"
            >
              {total.toLocaleString('vi-VN')}₫
            </span>
          </div>

          <button
            data-testid="checkout-btn"
            className="w-full bg-black text-white py-3 rounded-lg mt-4 hover:bg-gray-800 transition"
          >
            Thanh toán
          </button>
        </div>

      </div>
    </div>
  );
};

export default CartPage;