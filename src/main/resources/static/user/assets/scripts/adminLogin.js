const $loginForm = document.getElementById('login-form');
$loginForm.onsubmit=(e)=>{
    e.preventDefault();
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('email',$loginForm['loginEmail'].value)
    formData.append('password',$loginForm['loginPassword'].value)
    xhr.onreadystatechange=()=>{
        if(xhr.readyState !== XMLHttpRequest.DONE){
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300){
            alert('요청중 오류');
            return;
        }
        const response = JSON.parse(xhr.responseText);
        switch(response.result){
            case'failure':
                alert('관리자가 아닙니다.')
                location.href="/user/login"
                break;
            case'success_admin':
                alert('관리자 로그인 완료');
                location.href='/user/admin'
                break;
            default:
                break;
        }
    };
    xhr.open('POST', '/api/user/admin');
    xhr.send(formData);
}