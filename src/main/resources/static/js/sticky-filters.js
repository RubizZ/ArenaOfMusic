function stickyFilters(id) {
    document.addEventListener('DOMContentLoaded', function () {
        const stickyFilters = document.getElementById(id);

        // Busca el contenedor con overflow-y: auto más cercano para pegarse a el
        let container = stickyFilters.closest('[style*="overflow-y: auto"], [class*="overflow-y-auto"]');

        // Si no usa el viewport por default
        if (!container) {
            container = document.documentElement;
        }

        const sentinel = document.createElement('div');
        sentinel.style.height = '0px';
        sentinel.style.width = '100%';
        sentinel.style.position = 'absolute';
        sentinel.style.visibility = 'hidden';

        stickyFilters.parentNode.insertBefore(sentinel, stickyFilters);

        const observer = new IntersectionObserver(
            ([entry]) => {
                if (!entry.isIntersecting) {
                    stickyFilters.classList.add('sticky-active');
                } else {
                    stickyFilters.classList.remove('sticky-active');
                }
            },
            {
                root: container,
                threshold: 0
            }
        );

        observer.observe(sentinel);
    });
}
