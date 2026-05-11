import axiosClient from '../api/axiosClient';
import type { OrderRequest, OrderResponse } from '../types';

export const orderService = {
  placeOrder: async (data: OrderRequest): Promise<OrderResponse> => {
    return axiosClient.post('/orders', data);
  },
};
