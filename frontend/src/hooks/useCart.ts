import { useState, useEffect } from 'react';
import { cartService } from '../services/cartService';
import { showToast } from '../utils/toast';
import { useAuth } from '../context/AuthContext';
import axiosClient from '../api/axiosClient';
import type { CartItem } from '../types';

export const useCart = (autoFetch = false) => {
  const { user, setShowLoginModal } = useAuth();
  const [cart, setCart] = useState<CartItem[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchCart = async () => {
    try {
      setLoading(true);
      const data = await cartService.getCart();
      setCart(data);
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
    if (!user) {
      showToast('Vui lòng đăng nhập để thêm vào giỏ hàng', 'error');
      setShowLoginModal(true);
      return false;
    }

    try {
      setLoading(true);
      await cartService.addToCart(productId, quantity);
      window.dispatchEvent(new Event('cartUpdated'));
      showToast('Thêm vào giỏ hàng thành công', 'success');
      return true;
    } catch (err: any) {
      console.log('Add to cart error:', err);
      if (err.response?.status === 401) {
        showToast('Vui lòng đăng nhập để thêm vào giỏ hàng', 'error');
        setShowLoginModal(true);
        return false;
      }
      const msg = err.response?.data?.message || '';
      if (msg.toLowerCase().includes('inventory') || msg.toLowerCase().includes('tồn kho')) {
        try {
          const product = await axiosClient.get<any, any>(`/products/detail/${productId}`);
          if (product && typeof product.stockQuantity === 'number') {
            showToast(`Chỉ còn ${product.stockQuantity}`, 'error');
          } else {
            showToast('Số lượng vượt quá tồn kho', 'error');
          }
        } catch {
          showToast('Số lượng vượt quá tồn kho', 'error');
        }
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
    const previousCart = [...cart];
    
    // Optimistically update state
    setCart(prevCart =>
      prevCart.map(item =>
        item.productId === productId
          ? { ...item, quantity, subtotal: item.productPrice * quantity }
          : item
      )
    );

    try {
      await cartService.updateQuantity(productId, quantity);
      const data = await cartService.getCart();
      setCart(data);
      return true;
    } catch (err: any) {
      console.log('Update quantity error:', err);
      setCart(previousCart); // Rollback state
      const msg = err.response?.data?.message || '';
      if (msg.toLowerCase().includes('inventory') || msg.toLowerCase().includes('tồn kho')) {
        try {
          const product = await axiosClient.get<any, any>(`/products/detail/${productId}`);
          if (product && typeof product.stockQuantity === 'number') {
            showToast(`Chỉ còn ${product.stockQuantity}`, 'error');
          } else {
            showToast('Số lượng vượt quá tồn kho', 'error');
          }
        } catch {
          showToast('Số lượng vượt quá tồn kho', 'error');
        }
      } else {
        showToast('Cập nhật số lượng thất bại', 'error');
      }
      return false;
    }
  };


  const removeItem = async (productId: string): Promise<boolean> => {
    try {
      setLoading(true);
      await cartService.removeItem(productId);
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
