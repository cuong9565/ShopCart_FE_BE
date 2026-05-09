import { useState, useEffect } from 'react';
import axiosClient from '../api/axiosClient';
import type { Product } from '../types';

export const useProducts = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [featuredProducts, setFeaturedProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [productsData, featuredData] = await Promise.all([
          axiosClient.get<any, Product[]>('/products'),
          axiosClient.get<any, Product[]>('/products/featured'),
        ]);

        setProducts(productsData);
        setFeaturedProducts(featuredData);
        setError(null);
      } catch (err: any) {
        setError(err.message || 'Failed to fetch products');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  return { products, featuredProducts, loading, error };
};
