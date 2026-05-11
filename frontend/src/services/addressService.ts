import axiosClient from '../api/axiosClient';
import type { Address } from '../types';

export const addressService = {
  getAddresses: async (): Promise<Address[]> => {
    return axiosClient.get('/address');
  },

  addAddress: async (data: Omit<Address, 'id' | 'isDefault' | 'userId'>): Promise<Address> => {
    return axiosClient.post('/address', data);
  },

  updateAddress: async (
    addressId: string,
    data: Omit<Address, 'id' | 'userId'>
  ): Promise<Address> => {
    return axiosClient.put(`/address/${addressId}`, data);
  },
};
