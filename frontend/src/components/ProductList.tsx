import { useState } from 'react';
import { useCategories } from '../hooks/useCategories';
import { useProducts } from '../hooks/useProducts';
import ProductCard from './ProductCard';
import ProductCardSkeleton from './skeletons/ProductCardSkeleton';
import CategorySkeleton from './skeletons/CategorySkeleton';

const ProductList = () => {
  const { categories, loading: loadingCategories } = useCategories();
  const { products, featuredProducts, loading: loadingProducts } = useProducts();
  const [selectedCategoryId, setSelectedCategoryId] = useState<string | null>(null);

  const filteredProducts = selectedCategoryId
    ? products.filter((p) => p.category.id === selectedCategoryId)
    : products;

  return (
    <section className="py-12 bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 flex flex-col gap-8">

        {/* Top Section - Categories */}
        <div className="w-full">
          <div className="bg-white">
            {loadingCategories ? (
              <CategorySkeleton />
            ) : (
              <ul className="flex flex-wrap gap-x-8 gap-y-3 border-b border-gray-100">
                <li>
                  <button
                    onClick={() => setSelectedCategoryId(null)}
                    className={`pb-3 -mb-[1px] text-sm transition-all cursor-pointer border-b-2 ${selectedCategoryId === null
                      ? 'text-primary font-bold border-primary'
                      : 'text-gray-500 hover:text-primary font-semibold border-transparent hover:border-gray-200'
                      }`}
                  >
                    Tất cả sản phẩm
                  </button>
                </li>
                {categories.map((category) => (
                  <li key={category.id}>
                    <button
                      onClick={() => setSelectedCategoryId(category.id)}
                      className={`pb-3 -mb-[1px] text-sm transition-all cursor-pointer border-b-2 ${selectedCategoryId === category.id
                        ? 'text-primary font-bold border-primary'
                        : 'text-gray-500 hover:text-primary font-semibold border-transparent hover:border-gray-200'
                        }`}
                    >
                      {category.name}
                    </button>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>

        {/* Bottom Content - Products */}
        <div className="w-full flex flex-col gap-10">
          {loadingProducts ? (
            <>
              {/* Featured Products Skeleton */}
              {selectedCategoryId === null && (
                <div>
                  <div className="flex items-center justify-between mb-6">
                    <div className="h-8 bg-gray-200 rounded w-48 animate-pulse"></div>
                  </div>
                  <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                    {[1, 2, 3, 4].map((i) => <ProductCardSkeleton key={i} />)}
                  </div>
                </div>
              )}
              {/* All Products Skeleton */}
              <div>
                <div className="flex items-center justify-between mb-6">
                  <div className="h-8 bg-gray-200 rounded w-48 animate-pulse"></div>
                </div>
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                  {[1, 2, 3, 4, 5, 6, 7, 8].map((i) => <ProductCardSkeleton key={i} />)}
                </div>
              </div>
            </>
          ) : (
            <>
              {/* Featured Products - Only show when no category is selected */}
              {selectedCategoryId === null && featuredProducts.length > 0 && (
                <div>
                  <div className="flex items-center justify-between mb-6">
                    <h2 className="text-2xl font-black text-gray-900">
                      Sản phẩm nổi bật
                    </h2>
                  </div>
                  <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                    {featuredProducts.map((product) => (
                      <ProductCard key={product.id} product={product} />
                    ))}
                  </div>
                </div>
              )}

              {/* All / Filtered Products */}
              <div>
                <div className="flex items-center justify-between mb-6">
                  <h2 className="text-2xl font-black text-gray-900">
                    {selectedCategoryId === null ? 'Tất cả sản phẩm' : 'Sản phẩm theo danh mục'}
                  </h2>
                </div>
                {filteredProducts.length > 0 ? (
                  <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                    {filteredProducts.map((product) => (
                      <ProductCard key={product.id} product={product} />
                    ))}
                  </div>
                ) : (
                  <div className="text-center py-10 text-gray-500">
                    Không tìm thấy sản phẩm nào trong danh mục này.
                  </div>
                )}
              </div>
            </>
          )}

        </div>
      </div>
    </section>
  );
};

export default ProductList;
