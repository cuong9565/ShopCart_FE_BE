const CategorySkeleton = () => {
  return (
    <ul className="flex flex-wrap gap-3 animate-pulse">
      {[1, 2, 3, 4, 5, 6].map((i) => (
        <li key={i}>
          <div className="h-10 bg-gray-200 rounded-lg w-32"></div>
        </li>
      ))}
    </ul>
  );
};

export default CategorySkeleton;
