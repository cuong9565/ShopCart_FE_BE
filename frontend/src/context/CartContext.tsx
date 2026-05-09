import {
  createContext,
  useContext,
  useState,
  type ReactNode,
} from "react";

export interface CartItem {
  id: string;
  name: string;
  price: number;
  image: string;
  stock: number;
  quantity: number;
}

interface CartContextType {
  cart: CartItem[];

  addToCart: (product: Omit<CartItem, "quantity">) => void;

  increase: (id: string) => void;

  decrease: (id: string) => void;

  removeItem: (id: string) => void;

  clearCart: () => void;
}

const CartContext = createContext<CartContextType | undefined>(
  undefined
);

export const CartProvider = ({
  children,
}: {
  children: ReactNode;
}) => {
  const [cart, setCart] = useState<CartItem[]>([]);

  // THÊM GIỎ HÀNG
  const addToCart = (product: Omit<CartItem, "quantity">) => {
    setCart((prev) => {
      const exist = prev.find((item) => item.id === product.id);

      // đã tồn tại
      if (exist) {
        return prev.map((item) =>
          item.id === product.id
            ? {
                ...item,
                quantity:
                  item.quantity < item.stock
                    ? item.quantity + 1
                    : item.quantity,
              }
            : item
        );
      }

      // chưa tồn tại
      return [...prev, { ...product, quantity: 1 }];
    });
  };

  // TĂNG SỐ LƯỢNG
  const increase = (id: string) => {
    setCart((prev) =>
      prev.map((item) =>
        item.id === id && item.quantity < item.stock
          ? { ...item, quantity: item.quantity + 1 }
          : item
      )
    );
  };

  // GIẢM SỐ LƯỢNG
  const decrease = (id: string) => {
    setCart((prev) =>
      prev.map((item) =>
        item.id === id && item.quantity > 1
          ? { ...item, quantity: item.quantity - 1 }
          : item
      )
    );
  };

  // XOÁ ITEM
  const removeItem = (id: string) => {
    setCart((prev) =>
      prev.filter((item) => item.id !== id)
    );
  };

  // XOÁ TOÀN BỘ
  const clearCart = () => {
    setCart([]);
  };

  return (
    <CartContext.Provider
      value={{
        cart,
        addToCart,
        increase,
        decrease,
        removeItem,
        clearCart,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

// CUSTOM HOOK
export const useCart = () => {
  const context = useContext(CartContext);

  if (!context) {
    throw new Error(
      "useCart must be used within CartProvider"
    );
  }

  return context;
};