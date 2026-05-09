import { useCart } from "../components/CartContext";

const CartPage = () => {
  const { cart, increase, decrease, removeItem } = useCart();

  // Tính tổng tiền
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
          {/* DANH SÁCH */}
          {cart.map((item) => {
            const isMax = item.quantity >= item.stock;
            const isMin = item.quantity <= 1;

            return (
              <div
                key={item.id}
                className="flex justify-between items-center border-b py-4 gap-4"
              >
                {/* THÔNG TIN */}
                <div className="flex gap-4 items-center w-[40%]">
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

                    {/* tồn kho */}
                    <p className="text-sm text-gray-400">
                      Còn lại: {item.stock}
                    </p>
                  </div>
                </div>

                {/* SỐ LƯỢNG */}
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => decrease(item.id)}
                    disabled={isMin}
                    className={`px-3 py-1 rounded
                      ${
                        isMin
                          ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                          : "bg-gray-200 hover:bg-gray-300"
                      }`}
                  >
                    -
                  </button>

                  <span className="min-w-[30px] text-center font-semibold">
                    {item.quantity}
                  </span>

                  <button
                    onClick={() => increase(item.id)}
                    disabled={isMax}
                    className={`px-3 py-1 rounded
                      ${
                        isMax
                          ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                          : "bg-gray-200 hover:bg-gray-300"
                      }`}
                  >
                    +
                  </button>
                </div>

                {/* THÀNH TIỀN */}
                <div className="font-bold w-[120px] text-right">
                  {(item.price * item.quantity).toLocaleString("vi-VN")}đ
                </div>

                {/* XOÁ */}
                <button
                  onClick={() => {
                    if (confirm("Bạn có chắc muốn xoá?")) {
                      removeItem(item.id);
                    }
                  }}
                  className="text-red-500 hover:underline"
                >
                  Xoá
                </button>
              </div>
            );
          })}

          {/* TỔNG TIỀN */}
          <div className="text-right mt-6 text-2xl font-bold">
            Tổng: {total.toLocaleString("vi-VN")}đ
          </div>

          {/* BUTTON THANH TOÁN (bonus) */}
          <div className="text-right mt-4">
            <button className="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600">
              Thanh toán
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default CartPage;
