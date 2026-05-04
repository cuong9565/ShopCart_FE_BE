function App() {
  return (
    <>
      <h1 className="text-3xl font-bold text-blue-500">
        Hello {import.meta.env.VITE_APP_NAME}
      </h1>
      <p className="mt-4 text-gray-600">
        API URL: {import.meta.env.VITE_API_URL}
      </p>
    </>
  );
}

export default App;