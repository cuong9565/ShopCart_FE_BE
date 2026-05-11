export const calculateTotal = (items: any[]) => {
  return items.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );
};

export const validateStock = (
  quantity: number,
  stock: number
) => {
  return quantity <= stock;
};

export const removeItem = (
  cart: any[],
  productId: string
) => {
  return cart.filter(
    item => item.productId !== productId
  );
};