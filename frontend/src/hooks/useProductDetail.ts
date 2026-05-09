import { useState, useEffect } from 'react';
import axiosClient from '../api/axiosClient';
import type { Product } from '../types';

export const useProductDetail = (id: string | undefined) => {
  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) {
      setLoading(false);
      return;
    }

    const fetchProduct = async () => {
      try {
        setLoading(true);
        const data = await axiosClient.get<any, Product>(`/products/detail/${id}`);
        setProduct(data);
        setError(null);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Có lỗi xảy ra khi tải chi tiết sản phẩm');
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id]);

  return { product, loading, error };
};
