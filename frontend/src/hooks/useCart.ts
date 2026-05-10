import { useState, useEffect } from 'react';
import axios from 'axios';
import { showToast } from '../utils/toast';
import type { CartItem } from '../types';

export const useCart = (autoFetch = false) => {
  const [cart, setCart] = useState<CartItem[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchCart = async () => {
    try {
      setLoading(true);
      const res = await axios.get('http://localhost:8080/api/cart', {
        withCredentials: true,
      });
      setCart(res.data);
    } catch (error) {
      console.log('Lỗi load cart:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (autoFetch) {
      fetchCart();
    }
  }, [autoFetch]);

  const addToCart = async (productId: string, quantity: number): Promise<boolean> => {
    try {
      setLoading(true);
      await axios.post(
        'http://localhost:8080/api/cart',
        { productId, quantity },
        { withCredentials: true }
      );
      window.dispatchEvent(new Event('cartUpdated'));
      showToast('Thêm vào giỏ hàng thành công', 'success');
      return true;
    } catch (err: any) {
      console.log('Add to cart error:', err);
      if (err.response?.status === 401) {
        showToast('Vui lòng đăng nhập để thêm vào giỏ hàng', 'error');
        return false;
      }
      const msg = err.response?.data?.message || '';
      if (msg.toLowerCase().includes('inventory') || msg.toLowerCase().includes('tồn kho')) {
        showToast('Số lượng vượt quá tồn kho', 'error');
      } else {
        showToast('Thêm vào giỏ hàng thất bại', 'error');
      }
      return false;
    } finally {
      setLoading(false);
    }
  };

  const updateQuantity = async (productId: string, quantity: number): Promise<boolean> => {
    if (quantity < 1) return false;
    try {
      setLoading(true);
      await axios.put(
        'http://localhost:8080/api/cart',
        { productId, quantity },
        { withCredentials: true }
      );
      await fetchCart();
      return true;
    } catch (err: any) {
      console.log('Update quantity error:', err);
      const msg = err.response?.data?.message || '';
      if (msg.toLowerCase().includes('inventory') || msg.toLowerCase().includes('tồn kho')) {
        showToast('Số lượng vượt quá tồn kho', 'error');
      } else {
        showToast('Cập nhật số lượng thất bại', 'error');
      }
      return false;
    } finally {
      setLoading(false);
    }
  };

  const removeItem = async (productId: string): Promise<boolean> => {
    try {
      setLoading(true);
      await axios.delete('http://localhost:8080/api/cart', {
        data: { productId },
        withCredentials: true,
      });
      await fetchCart();
      showToast('Đã xóa sản phẩm khỏi giỏ hàng', 'success');
      return true;
    } catch (err: any) {
      console.log('Remove item error:', err);
      showToast('Xóa sản phẩm thất bại', 'error');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const total = cart.reduce((sum, item) => sum + item.subtotal, 0);

  return { cart, fetchCart, addToCart, updateQuantity, removeItem, total, loading };
};
