window.addEventListener('DOMContentLoaded', () => {

    const navMenu = document.querySelector('.nav-menu');
    const background = document.querySelector('.background');
    const backgroundMenu = document.querySelector('.background-menu');
    
    navMenu.addEventListener('click', (e) => {
        background.classList.toggle('view');
    });
    
    background.addEventListener('click', (e) => {
        if(backgroundMenu.getBoundingClientRect().x >= e.x){
            background.classList.toggle('view');
        }
    })
    
    let isUpView = false;
    let innerHeight = window.innerHeight;
    const bodyHeight = document.querySelector('body').clientHeight;
    const upView = document.querySelector('.up-view');
    
    upView.addEventListener('click', () => {
        if(isUpView){
            window.scroll({top: 0, behavior: 'smooth'});
        }
    });
    
    window.addEventListener('scroll', s => {
        
        if(!isUpView && window.scrollY > innerHeight - 100){
            upView.classList.add('open-view');
            isUpView = true;
        }else if(isUpView && window.scrollY < innerHeight - 100){
            upView.classList.remove('open-view');
            isUpView = false;
        }
    });

    const useInfoLink = document.querySelectorAll('.use-info-link');
    
    useInfoLink.forEach(target => {
        target.addEventListener('click', () => {
            const useInfo = document.querySelector('.use-info-heading').getBoundingClientRect();
            const dist = window.scrollY + useInfo.y;
            window.scroll({top: dist, behavior: 'smooth'});
        })
    })

    const sendBtn = document.querySelector('.form__btn');
    const waitScreen = document.querySelector('.wait-screen');
    
    if(sendBtn){
        sendBtn.addEventListener('click', () => waitScreen.classList.add('in-view'));
    }
});
