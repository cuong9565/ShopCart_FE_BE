import Navbar from './components/Navbar';
import Carousel from './components/Carousel';
import ProductList from './components/ProductList';
import Footer from './components/Footer';

function App() {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar />
      <main className="w-full flex-grow">
        <Carousel />
        <ProductList />
      </main>
      <Footer />
    </div>
  );
}

export default App;
