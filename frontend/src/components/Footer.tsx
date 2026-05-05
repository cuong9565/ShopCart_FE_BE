import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faFacebook, faInstagram, faYoutube, faTwitter } from '@fortawesome/free-brands-svg-icons';
import { faEnvelope, faPhone, faMapMarkerAlt } from '@fortawesome/free-solid-svg-icons';

const Footer = () => {
  return (
    <footer className="bg-gray-900 text-gray-300 pt-16 pb-8">
      <div className="max-w-7xl mx-auto px-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-12">
        {/* Brand Section */}
        <div className="space-y-6">
          <p className="text-white font-bold text-[30px] mb-6">DECATHLON</p>
          <p className="text-gray-400 leading-relaxed">
            Chuyên cung cấp dụng cụ thể thao cao cấp, đặc biệt là Pickleball. Cam kết chất lượng và trải nghiệm tốt nhất cho vận động viên.
          </p>
          <div className="flex space-x-5">
            <a href="#" className="hover:text-blue-500 transition-colors">
              <FontAwesomeIcon icon={faFacebook} size="lg" />
            </a>
            <a href="#" className="hover:text-pink-500 transition-colors">
              <FontAwesomeIcon icon={faInstagram} size="lg" />
            </a>
            <a href="#" className="hover:text-red-500 transition-colors">
              <FontAwesomeIcon icon={faYoutube} size="lg" />
            </a>
            <a href="#" className="hover:text-blue-400 transition-colors">
              <FontAwesomeIcon icon={faTwitter} size="lg" />
            </a>
          </div>
        </div>

        {/* Quick Links */}
        <div>
          <h4 className="text-white font-bold text-lg mb-6">Liên kết nhanh</h4>
          <ul className="space-y-4">
            <li><a href="#" className="hover:text-white transition-colors">Trang chủ</a></li>
            <li><a href="#" className="hover:text-white transition-colors">Sản phẩm</a></li>
            <li><a href="#" className="hover:text-white transition-colors">Về chúng tôi</a></li>
            <li><a href="#" className="hover:text-white transition-colors">Tin tức</a></li>
            <li><a href="#" className="hover:text-white transition-colors">Liên hệ</a></li>
          </ul>
        </div>

        {/* Support */}
        <div>
          <h4 className="text-white font-bold text-lg mb-6">Hỗ trợ khách hàng</h4>
          <ul className="space-y-4">
            <li><a href="#" className="hover:text-white transition-colors">Chính sách bảo hành</a></li>
            <li><a href="#" className="hover:text-white transition-colors">Chính sách đổi trả</a></li>
            <li><a href="#" className="hover:text-white transition-colors">Phương thức thanh toán</a></li>
            <li><a href="#" className="hover:text-white transition-colors">Vận chuyển & Giao hàng</a></li>
          </ul>
        </div>

        {/* Contact Info */}
        <div>
          <h4 className="text-white font-bold text-lg mb-6">Thông tin liên hệ</h4>
          <ul className="space-y-4">
            <li className="flex items-start space-x-3">
              <FontAwesomeIcon icon={faMapMarkerAlt} className="mt-1" />
              <span>123 Đường Thể Thao, Quận 1, TP. Hồ Chí Minh</span>
            </li>
            <li className="flex items-center space-x-3">
              <FontAwesomeIcon icon={faPhone} />
              <span>+84 123 456 789</span>
            </li>
            <li className="flex items-center space-x-3">
              <FontAwesomeIcon icon={faEnvelope} />
              <span>support@shopcart.vn</span>
            </li>
          </ul>
        </div>
      </div>

      {/* Copyright */}
      <div className="max-w-7xl mx-auto px-4 mt-16 pt-8 border-t border-gray-800 text-center text-sm text-gray-500">
        <p>© 2024 ShopCart Sports. All rights reserved. Designed for athletes.</p>
      </div>
    </footer>
  );
};

export default Footer;
