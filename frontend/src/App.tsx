import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import HomePage from './pages/HomePage';
import ProductDetailPage from './pages/ProductDetailPage';
import Footer from './components/Footer';
import LoginModal from './components/LoginModal';

function App() {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />
      <main className="w-full flex-grow">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/product/:slug/:id" element={<ProductDetailPage />} />
        </Routes>
      </main>
      <Footer />
      <LoginModal />
    </div>
  );
}

export default App;
