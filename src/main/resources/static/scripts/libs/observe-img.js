window.addEventListener('DOMContentLoaded', () => {

    const targets = document.querySelectorAll('.item-content-img');
    
    const cb = function(entries, observer){
        entries.forEach(entry => {
            if(entry.isIntersecting){
                entry.target.classList.add('in-view');
                observer.unobserve(entry.target);
            }
        })
    }
    
    const io = new IntersectionObserver(cb, {rootMargin: "-100px 0px"});
    
    targets.forEach(target => io.observe(target));
});
