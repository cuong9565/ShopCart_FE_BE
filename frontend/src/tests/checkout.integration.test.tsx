/**
 * checkout.integration.test.tsx
 * Integration tests for frontend checkout components (CheckoutSummary, PriceCalculator, InventoryWarning)
 * using Vitest, React Testing Library, and jsdom.
 */
import { describe, test, expect, afterEach } from 'vitest';
import { render, screen, fireEvent, waitFor, cleanup } from '@testing-library/react';
import React from 'react';

// Tự động dọn dẹp DOM sau mỗi test case tránh rò rỉ dữ liệu component
afterEach(() => {
  cleanup();
});

// Import các components tích hợp chuyên dụng cho bài test
import CheckoutSummary, { type CartItem } from '../components/CheckoutSummary';
import PriceCalculator from '../components/PriceCalculator';
import InventoryWarning from '../components/InventoryWarning';

// ═══════════════════════════════════════════════════════════════════════════
// a) Test CheckoutSummary component với dữ liệu giỏ hàng (0.25 điểm)
// ═══════════════════════════════════════════════════════════════════════════

describe('Kiểm thử tích hợp Component CheckoutSummary', () => {
  test('TC1: Hiển thị danh sách sản phẩm và tính tổng giá tạm tính chính xác', () => {
    // Dữ liệu giả lập cho giỏ hàng
    const mockCart = {
      items: [
        {
          productId: 'p1',
          productName: 'Laptop Dell',
          productPrice: 15000000,
          quantity: 2,
          subtotal: 30000000,
        },
        {
          productId: 'p2',
          productName: 'Mouse Logitech',
          productPrice: 500000,
          quantity: 1,
          subtotal: 500000,
        },
      ] as CartItem[],
    };

    render(<CheckoutSummary cart={mockCart} />);

    // Kiểm tra tên các sản phẩm có hiển thị đúng không
    expect(screen.getByText('Laptop Dell')).toBeTruthy();
    expect(screen.getByText('Mouse Logitech')).toBeTruthy();

    // Kiểm tra số tiền từng dòng sản phẩm
    const itemSubtotals = screen.getAllByTestId('item-subtotal');
    expect(itemSubtotals[0].textContent).toContain('30.000.000');
    expect(itemSubtotals[1].textContent).toContain('500.000');

    // Kiểm tra hiển thị tổng tiền tạm tính khớp với yêu cầu đề bài
    const subtotalDisplay = screen.getByTestId('subtotal-display');
    expect(subtotalDisplay.textContent).toContain('30.500.000');
  });

  test('TC2: Giỏ hàng chỉ có một sản phẩm hiển thị đúng giá tiền', () => {
    const mockCart = {
      items: [
        {
          productId: 'p1',
          productName: 'Bàn phím Razer',
          productPrice: 2000000,
          quantity: 1,
          subtotal: 2000000,
        },
      ],
    };

    render(<CheckoutSummary cart={mockCart} />);
    expect(screen.getByTestId('subtotal-display').textContent).toContain('2.000.000');
  });
});

// ═══════════════════════════════════════════════════════════════════════════
// b) Test PriceCalculator component (tính giá real-time) (0.25 điểm)
// ═══════════════════════════════════════════════════════════════════════════

describe('Kiểm thử tích hợp Component PriceCalculator (Tính giá thời gian thực)', () => {
  test('TC1: Mặc định tính đúng tổng giá khi chưa áp dụng mã giảm giá', () => {
    render(<PriceCalculator initialSubtotal={1000000} />);

    // Phí ship mặc định 30,000đ, không có mã giảm giá
    expect(screen.getByTestId('calc-subtotal').textContent).toContain('1.000.000');
    expect(screen.getByTestId('calc-shipping').textContent).toContain('30.000');
    expect(screen.getByTestId('calc-total').textContent).toContain('1.030.000');
  });

  test('TC2: Thay đổi phí vận chuyển cập nhật lại tổng thanh toán thời gian thực', async () => {
    render(<PriceCalculator initialSubtotal={500000} />);

    const shippingInput = screen.getByTestId('input-shipping');
    
    // Đổi phí vận chuyển thành 50,000đ
    fireEvent.change(shippingInput, { target: { value: '50000' } });

    await waitFor(() => {
      expect(screen.getByTestId('calc-shipping').textContent).toContain('50.000');
      expect(screen.getByTestId('calc-total').textContent).toContain('550.000');
    });
  });

  test('TC3: Áp dụng mã giảm giá FIXED khấu trừ đúng số tiền cố định', async () => {
    render(<PriceCalculator initialSubtotal={500000} />);

    const selectType = screen.getByTestId('select-coupon-type');
    
    // Đổi loại mã giảm giá sang tiền mặt cố định (FIXED)
    fireEvent.change(selectType, { target: { value: 'FIXED' } });

    // Nhập giá trị giảm 100,000đ
    const couponInput = screen.getByTestId('input-coupon-value');
    fireEvent.change(couponInput, { target: { value: '100000' } });

    await waitFor(() => {
      expect(screen.getByTestId('calc-discount').textContent).toContain('-100.000');
      // 500,000đ hàng + 30,000đ ship - 100,000đ giảm giá = 430,000đ
      expect(screen.getByTestId('calc-total').textContent).toContain('430.000');
    });
  });

  test('TC4: Áp dụng mã PERCENT giảm đúng tỉ lệ phần trăm và giới hạn bởi mức tối đa', async () => {
    render(<PriceCalculator initialSubtotal={1000000} />);

    const selectType = screen.getByTestId('select-coupon-type');
    
    // Chọn loại mã giảm giá theo phần trăm (PERCENT)
    fireEvent.change(selectType, { target: { value: 'PERCENT' } });

    // Nhập giảm 10% (10% của 1,000,000đ là 100,000đ)
    const couponInput = screen.getByTestId('input-coupon-value');
    fireEvent.change(couponInput, { target: { value: '10' } });

    // Đặt mức giảm tối đa maxDiscount = 50,000đ
    const maxDiscountInput = screen.getByTestId('input-max-discount');
    fireEvent.change(maxDiscountInput, { target: { value: '50000' } });

    await waitFor(() => {
      // 10% là 100k nhưng bị chặn tối đa 50k
      expect(screen.getByTestId('calc-discount').textContent).toContain('-50.000');
      // 1,000,000đ + 30,000đ - 50,000đ = 980,000đ
      expect(screen.getByTestId('calc-total').textContent).toContain('980.000');
    });
  });
});

// ═══════════════════════════════════════════════════════════════════════════
// c) Test InventoryWarning component (cảnh báo hết hàng)
// ═══════════════════════════════════════════════════════════════════════════

describe('Kiểm thử tích hợp Component InventoryWarning (Cảnh báo hàng tồn kho)', () => {
  test('TC1: Không hiển thị bất kỳ cảnh báo nào nếu tất cả sản phẩm đều đủ hàng', () => {
    const mockItems = [
      { productId: 'p1', productName: 'iPhone 15 Pro', requested: 2, stock: 10 },
      { productId: 'p2', productName: 'iPad Air', requested: 1, stock: 5 },
    ];

    render(<InventoryWarning items={mockItems} />);

    expect(screen.getByTestId('stock-all-available')).toBeTruthy();
    expect(screen.getByText(/Tất cả sản phẩm đều đủ hàng sẵn sàng giao/)).toBeTruthy();
    expect(screen.queryByTestId('stock-warnings-container')).toBeNull();
  });

  test('TC2: Hiển thị đúng thông báo lỗi màu đỏ kèm chi tiết sản phẩm bị thiếu hàng', () => {
    const mockItems = [
      { productId: 'p1', productName: 'MacBook Pro', requested: 5, stock: 2 }, // Thiếu hàng
      { productId: 'p2', productName: 'AirPods Max', requested: 1, stock: 10 }, // Đủ hàng
    ];

    render(<InventoryWarning items={mockItems} />);

    expect(screen.queryByTestId('stock-all-available')).toBeNull();
    expect(screen.getByTestId('stock-warnings-container')).toBeTruthy();
    expect(screen.getByText(/Có 1 sản phẩm không đủ tồn kho/)).toBeTruthy();

    // Kiểm tra thông tin hiển thị sản phẩm thiếu
    const warningItem = screen.getByTestId('stock-warning-item');
    expect(warningItem.textContent).toContain('MacBook Pro');
    expect(warningItem.textContent).toContain('Yêu cầu 5 / Còn lại 2');
  });

  test('TC3: Hiển thị đầy đủ tất cả sản phẩm khi có nhiều sản phẩm bị thiếu hàng cùng lúc', () => {
    const mockItems = [
      { productId: 'p1', productName: 'MacBook Pro', requested: 5, stock: 2 }, // Thiếu
      { productId: 'p2', productName: 'Tai nghe Sony', requested: 4, stock: 3 }, // Thiếu
    ];

    render(<InventoryWarning items={mockItems} />);

    expect(screen.getByText(/Có 2 sản phẩm không đủ tồn kho/)).toBeTruthy();
    const warningItems = screen.getAllByTestId('stock-warning-item');
    expect(warningItems).toHaveLength(2);
    expect(warningItems[0].textContent).toContain('MacBook Pro');
    expect(warningItems[1].textContent).toContain('Tai nghe Sony');
  });
});
