import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTimes, faEnvelope, faLock, faSpinner } from '@fortawesome/free-solid-svg-icons';

const LoginModal: React.FC = () => {
  const { showLoginModal, setShowLoginModal, login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Validation states
  const [emailError, setEmailError] = useState<string | null>(null);
  const [passwordError, setPasswordError] = useState<string | null>(null);

  if (!showLoginModal) return null;

  const validateEmail = (val: string) => {
    if (!val) {
      return 'Vui lòng nhập email';
    }
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regex.test(val)) {
      return 'Email không đúng định dạng';
    }
    return null;
  };

  const validatePassword = (val: string) => {
    if (!val) {
      return 'Vui lòng nhập mật khẩu';
    }
    if (val.length < 6) {
      return 'Mật khẩu phải có ít nhất 6 ký tự';
    }
    return null;
  };

  const handleClose = () => {
    setEmail('');
    setPassword('');
    setError(null);
    setEmailError(null);
    setPasswordError(null);
    setShowLoginModal(false);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const eErr = validateEmail(email);
    const pErr = validatePassword(password);

    setEmailError(eErr);
    setPasswordError(pErr);

    if (eErr || pErr) return;

    try {
      setLoading(true);
      setError(null);
      await login(email, password);
      handleClose(); // Reset form and close modal upon successful login
    } catch (err: any) {
      if (err.response?.status === 401) {
        setError('Email hoặc mật khẩu không chính xác');
      } else {
        setError(err.response?.data?.message || 'Có lỗi xảy ra trong quá trình đăng nhập');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 transition-opacity duration-300">
      {/* Backdrop overlay */}
      <div className="absolute inset-0" onClick={handleClose}></div>

      {/* Modal Container */}
      <div className="relative w-full max-w-md bg-white rounded-2xl shadow-2xl overflow-hidden border border-gray-100 transform transition-all duration-300 scale-100 flex flex-col">
        {/* Close Button */}
        <button
          onClick={handleClose}
          className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors p-2 rounded-full hover:bg-gray-50 cursor-pointer"
        >
          <FontAwesomeIcon icon={faTimes} className="text-xl" />
        </button>

        {/* Header */}
        <div className="px-8 pt-8 pb-4 text-center">
          <h2 className="text-3xl font-black text-gray-900 mb-2">Đăng Nhập</h2>
          <p className="text-gray-500 text-sm">Chào mừng bạn quay trở lại! Vui lòng điền thông tin đăng nhập.</p>
        </div>

        {/* Content Form */}
        <form onSubmit={handleSubmit} className="px-8 pb-8 space-y-5">
          {/* General Error Alert */}
          {error && (
            <div className="p-3 bg-red-50 border border-red-200 text-red-600 rounded-xl text-sm font-medium animate-shake text-center">
              {error}
            </div>
          )}

          {/* Email field */}
          <div className="space-y-1">
            <label className="text-sm font-semibold text-gray-700">Email</label>
            <div className="relative">
              <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-400">
                <FontAwesomeIcon icon={faEnvelope} />
              </span>
              <input
                data-testid="email-input"
                type="email"
                placeholder="ten@example.com"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value);
                  if (emailError) setEmailError(validateEmail(e.target.value));
                }}
                className={`w-full pl-10 pr-4 py-3 bg-gray-50 border rounded-xl focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all ${
                  emailError ? 'border-red-300 bg-red-50/10' : 'border-gray-200'
                }`}
              />
            </div>
            {emailError && <p className="text-xs font-medium text-red-500 pl-1">{emailError}</p>}
          </div>

          {/* Password field */}
          <div className="space-y-1">
            <label className="text-sm font-semibold text-gray-700">Mật khẩu</label>
            <div className="relative">
              <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-400">
                <FontAwesomeIcon icon={faLock} />
              </span>
              <input
                data-testid="password-input"
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value);
                  if (passwordError) setPasswordError(validatePassword(e.target.value));
                }}
                className={`w-full pl-10 pr-4 py-3 bg-gray-50 border rounded-xl focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all ${
                  passwordError ? 'border-red-300 bg-red-50/10' : 'border-gray-200'
                }`}
              />
            </div>
            {passwordError && <p className="text-xs font-medium text-red-500 pl-1">{passwordError}</p>}
          </div>

          {/* Action button */}
          <button
            type="submit"
            data-testid="login-btn"
            disabled={loading}
            className="w-full bg-primary hover:bg-primary-dark disabled:bg-gray-400 text-white font-bold py-3.5 rounded-xl transition-all shadow-lg shadow-primary/10 flex items-center justify-center gap-2 cursor-pointer"
          >
            {loading ? (
              <>
                <FontAwesomeIcon icon={faSpinner} className="animate-spin" />
                Đang đăng nhập...
              </>
            ) : (
              'Đăng Nhập'
            )}
          </button>
        </form>
      </div>
    </div>
  );
};

export default LoginModal;
