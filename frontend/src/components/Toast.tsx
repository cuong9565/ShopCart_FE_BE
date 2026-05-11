import { useState, useEffect, useCallback } from 'react';
import type { ToastType } from '../utils/toast';

interface ToastData {
  id: number;
  message: string;
  type: ToastType;
}

const ICONS: Record<ToastType, string> = {
  success: '✓',
  error: '✕',
  info: 'ℹ',
  warning: '⚠',
};

const STYLES: Record<ToastType, string> = {
  success: 'bg-emerald-50 border-emerald-400 text-emerald-900',
  error:   'bg-rose-50    border-rose-400    text-rose-900',
  info:    'bg-blue-50    border-blue-400    text-blue-900',
  warning: 'bg-amber-50   border-amber-400   text-amber-900',
};

const DOT_STYLES: Record<ToastType, string> = {
  success: 'bg-emerald-500',
  error:   'bg-rose-500',
  info:    'bg-blue-500',
  warning: 'bg-amber-500',
};

function Toast() {
  const [toasts, setToasts] = useState<ToastData[]>([]);

  const dismiss = useCallback((id: number) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  // Listen for toast events
  useEffect(() => {
    const handleToast = (e: Event) => {
      const { message, type } = (e as CustomEvent<{ message: string; type: ToastType }>).detail;
      if (!message) return;
      const id = Date.now() + Math.random();
      setToasts((prev) => [...prev, { id, message, type: type ?? 'success' }]);
      // Auto-dismiss after 4 s
      setTimeout(() => setToasts((prev) => prev.filter((t) => t.id !== id)), 4000);
    };

    window.addEventListener('toast', handleToast);
    return () => window.removeEventListener('toast', handleToast);
  }, []);

  if (toasts.length === 0) return null;

  return (
    <>
      <style>{`
        @keyframes toastIn {
          from { transform: translateX(110%) scale(0.95); opacity: 0; }
          to   { transform: translateX(0)    scale(1);    opacity: 1; }
        }
        .toast-in { animation: toastIn 0.35s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
      `}</style>

      {/* Stack container – bottom-right on desktop, top-center on mobile */}
      <div className="fixed bottom-6 right-6 z-[9999] flex flex-col gap-3 items-end sm:items-end max-w-sm w-full pointer-events-none">
        {toasts.map((t) => (
          <div
            key={t.id}
            data-testid={t.type === 'success' ? 'success-toast' : 'error-toast'}
            className={`toast-in w-full pointer-events-auto flex items-start gap-3 px-5 py-4 rounded-2xl shadow-xl border-2 ${STYLES[t.type]}`}
          >
            {/* Icon bubble */}
            <span className={`flex-shrink-0 mt-0.5 w-6 h-6 rounded-full flex items-center justify-center text-white text-xs font-black ${DOT_STYLES[t.type]}`}>
              {ICONS[t.type]}
            </span>

            {/* Message */}
            <p className="flex-1 text-sm font-semibold leading-snug">{t.message}</p>

            {/* Dismiss button */}
            <button
              onClick={() => dismiss(t.id)}
              className="flex-shrink-0 mt-0.5 opacity-50 hover:opacity-100 transition-opacity text-lg leading-none cursor-pointer"
              aria-label="Đóng thông báo"
            >
              ×
            </button>
          </div>
        ))}
      </div>
    </>
  );
}

export default Toast;
