window.addEventListener('DOMContentLoaded', () => {
    
    const useInfo = document.querySelector('.use-info-heading').getBoundingClientRect();
    const dist = window.scrollY + useInfo.y;
    window.scroll({top: dist, behavior: 'smooth'});
})
