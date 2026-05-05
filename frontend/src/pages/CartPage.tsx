import { useCart } from "../components/CartContext";

const CartPage = () => {
  const { cart, increase, decrease, removeItem } = useCart();

  const total = cart.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );

  return (
    <div className="max-w-5xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Giỏ hàng</h1>

      {cart.length === 0 ? (
        <p className="text-gray-500">Chưa có sản phẩm</p>
      ) : (
        <>
          {/* Danh sách sản phẩm */}
          {cart.map((item) => (
            <div
              key={item.id}
              className="flex justify-between items-center border-b py-4"
            >
              {/* Thông tin */}
              <div className="flex gap-4 items-center">
                <img
                  src={item.image}
                  alt={item.name}
                  className="w-20 h-20 object-cover rounded"
                />

                <div>
                  <h3 className="font-semibold">{item.name}</h3>
                  <p className="text-gray-600">
                    {item.price.toLocaleString("vi-VN")}đ
                  </p>
                </div>
              </div>

              {/* Điều chỉnh số lượng */}
              <div className="flex items-center gap-2">
                <button
                  onClick={() => decrease(item.id)}
                  className="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300"
                >
                  -
                </button>

                <span className="min-w-[20px] text-center">
                  {item.quantity}
                </span>

                <button
                  onClick={() => increase(item.id)}
                  className="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300"
                >
                  +
                </button>
              </div>

              {/* Thành tiền */}
              <div className="font-bold">
                {(item.price * item.quantity).toLocaleString("vi-VN")}đ
              </div>

              {/* Xoá */}
              <button
                onClick={() => removeItem(item.id)}
                className="text-red-500 hover:underline"
              >
                Xoá
              </button>
            </div>
          ))}

          {/* Tổng tiền */}
          <div className="text-right mt-6 text-2xl font-bold">
            Tổng: {total.toLocaleString("vi-VN")}đ
          </div>
        </>
      )}
    </div>
  );
};

export default CartPage;