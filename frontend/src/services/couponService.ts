import axiosClient from '../api/axiosClient';
import type { Coupon } from '../types';

export const couponService = {
  getValidCoupons: async (): Promise<Coupon[]> => {
    return axiosClient.get('/coupons/valid');
  },
};
