import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faTimes,
  faEnvelope,
  faLock,
  faSpinner,
  faCircleExclamation,
} from '@fortawesome/free-solid-svg-icons';

// ─── Helpers ─────────────────────────────────────────────────────────────────
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const validateEmail = (val: string): string | null => {
  if (!val.trim())         return 'Vui lòng nhập email';
  if (!EMAIL_REGEX.test(val.trim())) return 'Email không đúng định dạng (VD: ten@gmail.com)';
  return null;
};

const validatePassword = (val: string): string | null => {
  if (!val) return 'Vui lòng nhập mật khẩu';
  if (val.length < 6) return 'Mật khẩu phải có ít nhất 6 ký tự';
  return null;
};

// ─── Sub-component: Field error message ──────────────────────────────────────
const FieldError = ({ msg }: { msg: string | null }) =>
  msg ? (
    <p className="flex items-center gap-1 text-xs font-medium text-red-500 pl-1 mt-1">
      <FontAwesomeIcon icon={faCircleExclamation} className="text-[10px]" />
      {msg}
    </p>
  ) : null;

// ─── Main component ───────────────────────────────────────────────────────────
const LoginModal: React.FC = () => {
  const { showLoginModal, setShowLoginModal, login } = useAuth();

  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading]   = useState(false);

  // Field-level errors (client-side validation + server 401 error)
  const [emailError, setEmailError]       = useState<string | null>(null);
  const [passwordError, setPasswordError] = useState<string | null>(null);

  if (!showLoginModal) return null;

  const handleClose = () => {
    setEmail('');
    setPassword('');
    setEmailError(null);
    setPasswordError(null);
    setShowLoginModal(false);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // 1. Client-side validation
    const eErr = validateEmail(email);
    const pErr = validatePassword(password);
    setEmailError(eErr);
    setPasswordError(pErr);
    if (eErr || pErr) return;

    // 2. API call
    try {
      setLoading(true);
      await login(email, password);
      handleClose();
    } catch (err: any) {
      const status = err?.response?.status;

      if (status === 401) {
        // Show inline red text under both fields so user knows which to fix
        setEmailError('Email hoặc mật khẩu không chính xác');
        setPasswordError('Vui lòng kiểm tra lại mật khẩu');
      } else {
        // Unexpected server error → show under email field as generic notice
        const msg =
          err?.response?.data?.message ||
          'Có lỗi xảy ra, vui lòng thử lại sau';
        setEmailError(msg);
      }
    } finally {
      setLoading(false);
    }
  };

  // Determine input border class
  const inputCls = (hasError: boolean) =>
    `w-full pl-10 pr-4 py-3 bg-gray-50 border rounded-xl focus:outline-none focus:ring-2 transition-all ${
      hasError
        ? 'border-red-400 bg-red-50/30 focus:ring-red-200 focus:border-red-400'
        : 'border-gray-200 focus:ring-primary/20 focus:border-primary'
    }`;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
      {/* Backdrop */}
      <div className="absolute inset-0" onClick={handleClose} />

      {/* Modal card */}
      <div className="relative w-full max-w-md bg-white rounded-2xl shadow-2xl overflow-hidden border border-gray-100 flex flex-col">

        {/* Close button */}
        <button
          onClick={handleClose}
          className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors p-2 rounded-full hover:bg-gray-100 cursor-pointer"
          aria-label="Đóng"
        >
          <FontAwesomeIcon icon={faTimes} />
        </button>

        {/* Header */}
        <div className="px-8 pt-8 pb-4 text-center">
          <h2 className="text-3xl font-black text-gray-900 mb-1">Đăng Nhập</h2>
          <p className="text-gray-500 text-sm">Chào mừng bạn quay trở lại!</p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} noValidate className="px-8 pb-8 space-y-5">

          {/* ── Email ── */}
          <div className="space-y-1">
            <label className="text-sm font-semibold text-gray-700">
              Email <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-400">
                <FontAwesomeIcon icon={faEnvelope} />
              </span>
              <input
                id="login-email"
                type="email"
                data-testid="username-input"
                placeholder="ten@example.com"
                value={email}
                autoComplete="email"
                onChange={(e) => {
                  setEmail(e.target.value);
                  // Live-validate only if there was already an error
                  if (emailError) setEmailError(validateEmail(e.target.value));
                }}
                className={inputCls(!!emailError)}
              />
            </div>
            <FieldError msg={emailError} />
          </div>

          {/* ── Password ── */}
          <div className="space-y-1">
            <label className="text-sm font-semibold text-gray-700">
              Mật khẩu <span className="text-red-500">*</span>
            </label>
            <div className="relative">
              <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-400">
                <FontAwesomeIcon icon={faLock} />
              </span>
              <input
                id="login-password"
                type="password"
                data-testid="password-input"
                placeholder="••••••••"
                value={password}
                autoComplete="current-password"
                onChange={(e) => {
                  setPassword(e.target.value);
                  if (passwordError) setPasswordError(validatePassword(e.target.value));
                }}
                className={inputCls(!!passwordError)}
              />
            </div>
            <FieldError msg={passwordError} />
          </div>

          {/* ── Submit ── */}
          <button
            type="submit"
            id="login-btn"
            data-testid="login-btn"
            disabled={loading}
            className="w-full bg-primary hover:bg-primary-dark disabled:bg-gray-300 text-white font-bold py-3.5 rounded-xl transition-all shadow-lg shadow-primary/10 flex items-center justify-center gap-2 cursor-pointer mt-2"
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
