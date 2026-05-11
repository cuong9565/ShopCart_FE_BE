import axiosClient from '../api/axiosClient';
import type { ShippingMethod } from '../types';

export const shippingService = {
  getShippingMethods: async (): Promise<ShippingMethod[]> => {
    return axiosClient.get('/shipping-methods');
  },

  getShippingMethodById: async (id: string): Promise<ShippingMethod> => {
    return axiosClient.get(`/shipping-methods/${id}`);
  },
};
