import axiosClient from '../api/axiosClient';
import type { CartItem } from '../types';

export const cartService = {
  getCart: async (): Promise<CartItem[]> => {
    return axiosClient.get('/cart');
  },

  addToCart: async (productId: string, quantity: number): Promise<void> => {
    await axiosClient.post('/cart', { productId, quantity });
  },

  updateQuantity: async (productId: string, quantity: number): Promise<void> => {
    await axiosClient.put('/cart', { productId, quantity });
  },

  removeItem: async (productId: string): Promise<void> => {
    await axiosClient.delete('/cart', {
      data: { productId },
    });
  },
};
