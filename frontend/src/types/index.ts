export interface Category {
  id: string;
  name: string;
  description?: string;
  createdAt?: string;
}

export interface Product {
  id: string;
  name: string;
  price: number;
  description?: string;
  status: string;
  slug: string;
  createdAt: string;
  category: Category;
  stockQuantity: number;
  thumbnailImage: string;
  images?: string[];
}

export interface User {
  email: string;
  userId: string;
}
