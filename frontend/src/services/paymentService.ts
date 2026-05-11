import axiosClient from '../api/axiosClient';
import type { PaymentMethod } from '../types';

export const paymentService = {
  getPaymentMethods: async (): Promise<PaymentMethod[]> => {
    return axiosClient.get('/payment-methods');
  },

  getPaymentMethodById: async (id: string): Promise<PaymentMethod> => {
    return axiosClient.get(`/payment-methods/${id}`);
  },
};
