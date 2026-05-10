export const showToast = (message: string, type: 'success' | 'error' = 'success') => {
  window.dispatchEvent(
    new CustomEvent('toast', {
      detail: { message, type },
    })
  );
};
