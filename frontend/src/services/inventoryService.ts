import axiosClient from '../api/axiosClient';

export interface StockCheckItem {
  productId: string;
  quantity: number;
}

export interface StockCheckResponse {
  available: boolean;
  message?: string;
}

export const checkStock = async (items: StockCheckItem[]): Promise<StockCheckResponse> => {
  return axiosClient.post('/inventory/check-stock', items);
};
