import Navbar from './components/Navbar';
import Carousel from './components/Carousel';
import ProductList from './components/ProductList';
import Footer from './components/Footer';
import CartPage from './pages/CartPage';

import { Routes, Route } from 'react-router-dom';

function Home() {
  return (
    <>
      <Carousel />
      <ProductList />
    </>
  );
}

function App() {
  console.log("APP OK");
  return (
        <div className="min-h-screen bg-gray-50 flex flex-col">
          <Navbar />

          <main className="w-full flex-grow">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/cart" element={<CartPage />} />
            </Routes>
          </main>

          <Footer />
        </div>
  );
}

export default App;