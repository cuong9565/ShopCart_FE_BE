import { createContext, useContext, useState } from "react";
import type { Product } from "../data/products";

type CartItem = Product & {
  quantity: number;
};

type CartContextType = {
  cart: CartItem[];
  addToCart: (product: Product) => void;
  increase: (id: number) => void;
  decrease: (id: number) => void;
  removeItem: (id: number) => void;
};

const CartContext = createContext<CartContextType | null>(null);

export const CartProvider = ({ children }: { children: React.ReactNode }) => {
  const [cart, setCart] = useState<CartItem[]>([]);
    // const addToCart = (product: Product) => {
    //   setCart((prev) => {
    //     const exist = prev.find((item) => item.id === product.id);
    //     if (exist) {
    //       return prev.map((item) =>
    //         item.id === product.id
    //           ? { ...item, quantity: item.quantity + 1 }
    //           : item
    //       );
    //     }
    //     return [...prev, { ...product, quantity: 1 }];
    //   });
    // };
    const addToCart = (product: any) => {
      // 1. Check product tồn tại
      if (!product) {
        alert("Sản phẩm không tồn tại");
        return;
      }

      // 2. Check status
      if (product.status !== "ACTIVE") {
        alert("Sản phẩm đã ngừng bán");
        return;
      }

      // 3. Check stock
      if (product.stock <= 0) {
        alert("Sản phẩm đã hết hàng");
        return;
      }

      const existing = cart.find((item) => item.id === product.id);

      // 4. Nếu đã có trong giỏ
      if (existing) {
        if (existing.quantity >= product.stock) {
          alert("Vượt quá tồn kho");
          return;
        }

        setCart(
          cart.map((item) =>
            item.id === product.id
              ? { ...item, quantity: item.quantity + 1 }
              : item
          )
        );
      } else {
        // 5. Thêm mới
        setCart([...cart, { ...product, quantity: 1 }]);
      }
    };

  const increase = (id: number) => {
    setCart(
      cart.map((item) => {
        if (item.id === id) {
          if (item.quantity >= item.stock) {
            alert("Đã đạt tối đa tồn kho");
            return item;
          }
          return { ...item, quantity: item.quantity + 1 };
        }
        return item;
      })
    );
  };
  const decrease = (id: number) => {
    setCart(
      cart.map((item) => {
        if (item.id === id) {
          if (item.quantity <= 1) return item;
          return { ...item, quantity: item.quantity - 1 };
        }
        return item;
      })
    );
  };
  const removeItem = (id: number) => {
    setCart(cart.filter((item) => item.id !== id));
  };


  return (
    <CartContext.Provider value={{ cart, addToCart, increase, decrease, removeItem }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) throw new Error("useCart must be used inside CartProvider");
  return context;
};