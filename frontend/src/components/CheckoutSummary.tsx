import React from 'react';

export interface CartItem {
  id?: string;
  productId: string;
  productName: string;
  productPrice: number;
  quantity: number;
  subtotal: number;
  thumbnailImage?: string;
}

interface CheckoutSummaryProps {
  cart: {
    items: CartItem[];
  };
}

export const CheckoutSummary: React.FC<CheckoutSummaryProps> = ({ cart }) => {
  const subtotal = cart.items.reduce((sum, item) => sum + item.subtotal, 0);

  return (
    <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100">
      <h3 className="text-lg font-bold text-gray-900 mb-4">Tóm tắt giỏ hàng</h3>
      
      <div className="divide-y divide-gray-100 mb-4" data-testid="checkout-items-list">
        {cart.items.map((item) => (
          <div key={item.productId} className="flex justify-between items-center py-3" data-testid="checkout-item">
            <div>
              <p className="font-semibold text-gray-800 text-sm" data-testid="item-name">{item.productName}</p>
              <p className="text-xs text-gray-500">
                x{item.quantity} × {item.productPrice.toLocaleString('vi-VN')}đ
              </p>
            </div>
            <span className="font-bold text-gray-900 text-sm" data-testid="item-subtotal">
              {item.subtotal.toLocaleString('vi-VN')}đ
            </span>
          </div>
        ))}
      </div>

      <div className="border-t border-gray-100 pt-4 flex justify-between items-center">
        <span className="text-gray-600 text-sm font-medium">Tạm tính:</span>
        <span className="text-lg font-black text-primary" data-testid="subtotal-display">
          {subtotal.toLocaleString('vi-VN')}
        </span>
      </div>
    </div>
  );
};

export default CheckoutSummary;
