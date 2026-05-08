import { useState } from 'react';
import Navbar from './components/Navbar';
import Carousel from './components/Carousel';
import ProductList from './components/ProductList';
import TestPage from './components/TestPage';
import Footer from './components/Footer';

function App() {
  const [currentView, setCurrentView] = useState<'home' | 'test'>('home');

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navbar onNavigate={setCurrentView} currentView={currentView} />
      <main className="w-full flex-grow">
        {currentView === 'home' ? (
          <>
            <Carousel />
            <ProductList />
          </>
        ) : (
          <TestPage />
        )}
      </main>
      <Footer />
    </div>
  );
}

export default App;
