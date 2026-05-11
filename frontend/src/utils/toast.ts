export type ToastType = 'success' | 'error' | 'info' | 'warning';

export const showToast = (message: string, type: ToastType = 'success') => {
  window.dispatchEvent(
    new CustomEvent('toast', {
      detail: { message, type },
    })
  );
};
