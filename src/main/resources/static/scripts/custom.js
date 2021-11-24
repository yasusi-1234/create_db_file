const addBtn = document.querySelector('.add-btn');
const backForm = document.querySelector('.form-background');

addBtn.addEventListener('click', () => backForm.classList.add('view'));

backForm.addEventListener('click', e => {
    console.log(e.target.className)
    if (e.target.className == "form-background view" || e.target.className == "close-icon-inner" || e.target.className == "close-icon") {
        backForm.classList.remove('view')
    }
});