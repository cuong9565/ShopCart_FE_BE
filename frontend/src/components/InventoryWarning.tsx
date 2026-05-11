import React from 'react';
import { checkInventoryAvailability, type InventoryCheckItem } from '../utils/priceCalculation';

interface InventoryWarningProps {
  items: Array<{
    productId: string;
    productName: string;
    requested: number;
    stock: number;
  }>;
}

export const InventoryWarning: React.FC<InventoryWarningProps> = ({ items }) => {
  // Convert structure to checkInventoryAvailability items
  const checkItems: InventoryCheckItem[] = items.map((i) => ({
    productId: i.productId,
    requested: i.requested,
    stock: i.stock,
  }));

  const checkResult = checkInventoryAvailability(checkItems);

  if (checkResult.available) {
    return (
      <div className="bg-green-50 border border-green-200 rounded-xl p-4 text-green-700 text-sm font-semibold" data-testid="stock-all-available">
        ✓ Tất cả sản phẩm đều đủ hàng sẵn sàng giao!
      </div>
    );
  }

  return (
    <div className="bg-red-50 border border-red-200 rounded-2xl p-5" data-testid="stock-warnings-container">
      <h4 className="text-red-700 font-bold text-sm mb-3">
        ⚠ Cảnh báo: Có {checkResult.insufficientItems.length} sản phẩm không đủ tồn kho!
      </h4>
      <div className="space-y-2">
        {checkResult.insufficientItems.map((item) => {
          const original = items.find((i) => i.productId === item.productId);
          return (
            <div
              key={item.productId}
              className="flex justify-between items-center text-xs text-red-600 bg-white border border-red-100 rounded-lg p-3"
              data-testid="stock-warning-item"
            >
              <span className="font-semibold truncate max-w-[200px]" data-testid="warn-product-name">
                {original ? original.productName : `Sản phẩm #${item.productId}`}
              </span>
              <span className="font-bold">
                Yêu cầu {item.requested} / Còn lại {item.stock}
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default InventoryWarning;
