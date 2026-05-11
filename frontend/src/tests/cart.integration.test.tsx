/**
 * cart.integration.test.tsx
 * Integration tests for Cart components using
 * Vitest + React Testing Library + jsdom
 */

import { describe, test, expect, afterEach } from 'vitest';
import {
  render,
  screen,
  fireEvent,
  waitFor,
  cleanup
} from '@testing-library/react';

import React from 'react';

// Cleanup DOM sau mỗi test
afterEach(() => {
  cleanup();
});

// Import component Cart
import Cart from '../components/Cart';

// ═══════════════════════════════════════════════════════════════════
// a) Render cart và hiển thị dữ liệu sản phẩm
// ═══════════════════════════════════════════════════════════════════

describe('Kiểm thử tích hợp Component Cart', () => {

  test('TC1: Hiển thị đúng danh sách sản phẩm trong giỏ hàng', () => {

    const mockCart = {
      items: [
        {
          productId: 'p1',
          productName: 'Laptop Dell',
          price: 15000000,
          quantity: 1,
        },
        {
          productId: 'p2',
          productName: 'Chuột Logitech',
          price: 500000,
          quantity: 2,
        },
      ],
    };

    render(<Cart cart={mockCart} />);

    // Kiểm tra tên sản phẩm
    expect(screen.getByText('Laptop Dell')).toBeTruthy();
    expect(screen.getByText('Chuột Logitech')).toBeTruthy();

    // Kiểm tra số lượng hiển thị
    expect(screen.getByDisplayValue('1')).toBeTruthy();
    expect(screen.getByDisplayValue('2')).toBeTruthy();
  });

  // ═══════════════════════════════════════════════════════════════
  // b) Test tăng giảm số lượng sản phẩm
  // ═══════════════════════════════════════════════════════════════

  test('TC2: Tăng số lượng sản phẩm cập nhật tổng tiền đúng', async () => {

    const mockCart = {
      items: [
        {
          productId: 'p1',
          productName: 'Bàn phím cơ',
          price: 1000000,
          quantity: 1,
        },
      ],
    };

    render(<Cart cart={mockCart} />);

    // Click nút tăng số lượng
    const increaseBtn = screen.getByTestId('increase-btn-p1');

    fireEvent.click(increaseBtn);

    await waitFor(() => {

      // Quantity = 2
      expect(screen.getByDisplayValue('2')).toBeTruthy();

      // Tổng tiền = 2 triệu
      expect(
        screen.getByTestId('cart-total').textContent
      ).toContain('2.000.000');

    });
  });

  test('TC3: Giảm số lượng sản phẩm cập nhật đúng', async () => {

    const mockCart = {
      items: [
        {
          productId: 'p1',
          productName: 'Tai nghe Sony',
          price: 3000000,
          quantity: 2,
        },
      ],
    };

    render(<Cart cart={mockCart} />);

    const decreaseBtn = screen.getByTestId('decrease-btn-p1');

    fireEvent.click(decreaseBtn);

    await waitFor(() => {

      // Quantity còn 1
      expect(screen.getByDisplayValue('1')).toBeTruthy();

      // Tổng tiền = 3 triệu
      expect(
        screen.getByTestId('cart-total').textContent
      ).toContain('3.000.000');

    });
  });

  // ═══════════════════════════════════════════════════════════════
  // c) Test xóa sản phẩm khỏi cart
  // ═══════════════════════════════════════════════════════════════

  test('TC4: Xóa sản phẩm khỏi giỏ hàng thành công', async () => {

    const mockCart = {
      items: [
        {
          productId: 'p1',
          productName: 'iPhone 15',
          price: 25000000,
          quantity: 1,
        },
        {
          productId: 'p2',
          productName: 'Apple Watch',
          price: 10000000,
          quantity: 1,
        },
      ],
    };

    render(<Cart cart={mockCart} />);

    // Click nút xóa iPhone
    const removeBtn = screen.getByTestId('remove-btn-p1');

    fireEvent.click(removeBtn);

    await waitFor(() => {

      // iPhone bị xóa
      expect(
        screen.queryByText('iPhone 15')
      ).toBeNull();

      // Chỉ còn Apple Watch
      expect(
        screen.getByText('Apple Watch')
      ).toBeTruthy();

    });
  });

  // ═══════════════════════════════════════════════════════════════
  // d) Test cảnh báo vượt tồn kho
  // ═══════════════════════════════════════════════════════════════

  test('TC5: Hiển thị cảnh báo khi số lượng vượt tồn kho', async () => {

    const mockCart = {
      items: [
        {
          productId: 'p1',
          productName: 'RTX 4090',
          price: 50000000,
          quantity: 1,
          stock: 2,
        },
      ],
    };

    render(<Cart cart={mockCart} />);

    const increaseBtn = screen.getByTestId('increase-btn-p1');

    // Quantity = 2
    fireEvent.click(increaseBtn);

    // Quantity = 3 (vượt kho)
    fireEvent.click(increaseBtn);

    await waitFor(() => {

      expect(
        screen.getByTestId('stock-warning').textContent
      ).toContain('Vượt quá số lượng tồn kho');

    });
  });

});