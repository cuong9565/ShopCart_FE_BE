/**
 * Cart.tsx
 * Component giỏ hàng dùng cho Integration Testing
 */

import React, { useState } from 'react';

// Kiểu dữ liệu sản phẩm
export interface CartItem {
  productId: string;
  productName: string;
  price: number;
  quantity: number;
  stock?: number;
}

interface CartProps {
  cart: {
    items: CartItem[];
  };
}

const Cart: React.FC<CartProps> = ({ cart }) => {

  // State quản lý cart
  const [items, setItems] = useState<CartItem[]>(cart.items);

  // State cảnh báo tồn kho
  const [warning, setWarning] = useState('');

  // Format tiền VNĐ
  const formatCurrency = (value: number) => {
    return value.toLocaleString('vi-VN');
  };

  // Tính tổng tiền
  const calculateTotal = () => {
    return items.reduce(
      (total, item) =>
        total + item.price * item.quantity,
      0
    );
  };

  // Tăng số lượng
  const increaseQuantity = (productId: string) => {

    setItems(prev =>
      prev.map(item => {

        if (item.productId === productId) {

          const newQuantity = item.quantity + 1;

          // Kiểm tra tồn kho
          if (
            item.stock !== undefined &&
            newQuantity > item.stock
          ) {
            setWarning('Vượt quá số lượng tồn kho');
            return item;
          }

          setWarning('');

          return {
            ...item,
            quantity: newQuantity
          };
        }

        return item;
      })
    );
  };

  // Giảm số lượng
  const decreaseQuantity = (productId: string) => {

    setItems(prev =>
      prev.map(item => {

        if (
          item.productId === productId &&
          item.quantity > 1
        ) {

          return {
            ...item,
            quantity: item.quantity - 1
          };
        }

        return item;
      })
    );
  };

  // Xóa sản phẩm
  const removeItem = (productId: string) => {

    setItems(prev =>
      prev.filter(
        item => item.productId !== productId
      )
    );
  };

  // Cart rỗng
  if (items.length === 0) {
    return (
      <div>
        <h2>Giỏ hàng trống</h2>
      </div>
    );
  }

  return (
    <div>

      <h2>Shopping Cart</h2>

      {/* Warning */}
      {
        warning && (
          <div
            data-testid="stock-warning"
            style={{
              color: 'red',
              marginBottom: '10px'
            }}
          >
            {warning}
          </div>
        )
      }

      {/* Danh sách sản phẩm */}
      {
        items.map(item => (

          <div
            key={item.productId}
            data-testid={`cart-item-${item.productId}`}
            style={{
              border: '1px solid #ccc',
              padding: '10px',
              marginBottom: '10px'
            }}
          >

            <h3>{item.productName}</h3>

            <p>
              Giá:
              {' '}
              {formatCurrency(item.price)}
              đ
            </p>

            {/* Quantity */}
            <div>

              <button
                data-testid={`decrease-btn-${item.productId}`}
                onClick={() =>
                  decreaseQuantity(item.productId)
                }
              >
                -
              </button>

              <input
                value={item.quantity}
                readOnly
                style={{
                  width: '50px',
                  textAlign: 'center',
                  margin: '0 10px'
                }}
              />

              <button
                data-testid={`increase-btn-${item.productId}`}
                onClick={() =>
                  increaseQuantity(item.productId)
                }
              >
                +
              </button>

            </div>

            {/* Subtotal */}
            <p data-testid="item-subtotal">
              Thành tiền:
              {' '}
              {formatCurrency(
                item.price * item.quantity
              )}
              đ
            </p>

            {/* Remove button */}
            <button
              data-testid={`remove-btn-${item.productId}`}
              onClick={() =>
                removeItem(item.productId)
              }
            >
              Xóa sản phẩm
            </button>

          </div>
        ))
      }

      {/* Tổng tiền */}
      <h3 data-testid="cart-total">
        Tổng tiền:
        {' '}
        {formatCurrency(calculateTotal())}
        đ
      </h3>

    </div>
  );
};

export default Cart;