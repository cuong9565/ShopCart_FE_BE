import { useState, useEffect } from "react";

interface TestData {
  id?: number;
  name?: string;
  description?: string;
}

function TestPage() {
  const [testData, setTestData] = useState<TestData[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");

  const fetchTestData = async () => {
    setLoading(true);
    setError("");

    try {
      const response = await fetch("http://localhost:8080/api/public/test");

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      setTestData(data);
      console.log("Test data received:", data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to fetch data");
      console.error("Error fetching test data:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTestData();
  }, []);

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="bg-white rounded-lg shadow-md p-6">
        <h1 className="text-3xl font-bold text-gray-800 mb-6">API Test Page</h1>

        <div className="mb-6">
          <button
            onClick={fetchTestData}
            disabled={loading}
            className="bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
          >
            {loading ? "Loading..." : "Refresh Data"}
          </button>
        </div>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            <strong>Error:</strong> {error}
          </div>
        )}

        <div className="bg-gray-50 rounded p-4">
          <h2 className="text-xl font-semibold text-gray-700 mb-4">
            API Response
          </h2>

          {loading ? (
            <div className="text-gray-600">Loading data from backend...</div>
          ) : testData.length > 0 ? (
            <div className="space-y-3">
              {testData.map((item, index) => (
                <div
                  key={index}
                  className="bg-white p-3 rounded border border-gray-200"
                >
                  <div className="font-medium text-gray-800">
                    ID: {item.id || "N/A"}
                  </div>
                  <div className="text-gray-600">
                    Name: {item.name || "N/A"}
                  </div>
                  <div className="text-gray-600">
                    Description: {item.description || "N/A"}
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-gray-600">No data received from backend</div>
          )}
        </div>

        <div className="mt-6 bg-blue-50 border border-blue-200 rounded p-4">
          <h3 className="font-semibold text-blue-800 mb-2">
            API Endpoint Info:
          </h3>
          <div className="text-sm text-blue-700">
            <p>
              <strong>URL:</strong> http://localhost:8080/api/test
            </p>
            <p>
              <strong>Method:</strong> GET
            </p>
            <p>
              <strong>Status:</strong>{" "}
              {loading
                ? "Requesting..."
                : testData.length > 0
                  ? "Success"
                  : "No data"}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default TestPage;
