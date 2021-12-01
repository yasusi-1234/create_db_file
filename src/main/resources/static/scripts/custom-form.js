window.addEventListener('DOMContentLoaded', () => {
    const checkboxes = document.querySelectorAll('.checkbox-el');

    const formElements = document.querySelectorAll('.form-block__item');

    checkboxes.forEach((checkbox, index) => {
        checkbox.addEventListener('click', e => {
            console.log(e.target.checked);
            if(e.target.checked){
                console.log('on')
                formElements[index].classList.remove('form-off');
            }else{
                console.log('off')
                formElements[index].classList.add('form-off');
            }
        })
    })


});