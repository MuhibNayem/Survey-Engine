// intersectionReveal.ts
export function reveal(node: HTMLElement, { duration = 800, delay = 0, y = 20 } = {}) {
  let cancelled = false;

  const animateIn = () => {
    if (cancelled) return;
    node.style.transition = `all ${duration}ms cubic-bezier(0.16, 1, 0.3, 1) ${delay}ms`;
    node.style.opacity = '1';
    node.style.transform = 'translateY(0) scale(1)';
    node.classList.add('revealed');
  };

  node.style.opacity = '0';
  node.style.transform = `translateY(${y}px) scale(0.99)`;
  node.style.willChange = 'transform, opacity';

  // Ensure transition is visible even when the element is already in view on reload.
  requestAnimationFrame(() => {
    requestAnimationFrame(() => {
      if (cancelled) return;
      node.style.transition = `all ${duration}ms cubic-bezier(0.16, 1, 0.3, 1) ${delay}ms`;
    });
  });

  if (typeof IntersectionObserver === 'undefined') {
    requestAnimationFrame(animateIn);
    return {
      destroy() {
        cancelled = true;
        node.style.willChange = '';
      }
    };
  }

  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        animateIn();
        observer.unobserve(node);
      });
    },
    { rootMargin: '0px 0px -50px 0px', threshold: 0.01 }
  );

  observer.observe(node);

  return {
    destroy() {
      cancelled = true;
      observer.unobserve(node);
      node.style.willChange = '';
    }
  };
}
