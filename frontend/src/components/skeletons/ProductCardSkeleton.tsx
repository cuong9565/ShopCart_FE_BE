const ProductCardSkeleton = () => {
  return (
    <div className="bg-white rounded-xl border border-gray-100 flex flex-col h-full animate-pulse">
      <div className="relative aspect-square bg-gray-200"></div>
      <div className="p-5 flex flex-col flex-grow">
        <div className="h-6 bg-gray-200 rounded w-3/4 mb-2"></div>
        <div className="h-6 bg-gray-200 rounded w-1/2 mb-4"></div>
        <div className="mt-auto flex items-center justify-between">
          <div className="h-8 bg-gray-200 rounded w-1/3"></div>
          <div className="w-10 h-10 bg-gray-200 rounded-full"></div>
        </div>
      </div>
    </div>
  );
};

export default ProductCardSkeleton;
