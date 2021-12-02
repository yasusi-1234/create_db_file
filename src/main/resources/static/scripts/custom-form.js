window.addEventListener('DOMContentLoaded', () => {
    const checkboxes = document.querySelectorAll('.checkbox-el');

    const formElements = document.querySelectorAll('.form-block__item');

    // 初期化
    checkboxes.forEach((checkbox, index) => {
        if(!checkbox.checked){
            formElements[index].classList.add('form-off');
        }
    });

    checkboxes.forEach((checkbox, index) => {
        checkbox.addEventListener('click', e => {
            console.log(e.target.checked);
            if(e.target.checked){
                formElements[index].classList.remove('form-off');
            }else{
                formElements[index].classList.add('form-off');
            }
        })
    })


});