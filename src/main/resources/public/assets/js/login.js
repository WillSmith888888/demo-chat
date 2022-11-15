$(document).ready(function() {
    $('#login').on('click', function() {
        var account = $('#account').val();
        var password = $('#password').val();
        if (!account || !password) {
            alert('请输入用户名或者密码');
            return;
        }
        $.ajax({
            url: '/chat/login.do',
            data: { account: account, password: password },
            type: 'post',
            success: function(resp) {
                console.info('请求相应结果：' + resp);
                if (resp.code == '000000') {
                    window.localStorage.setItem('token', resp.data);
                    window.location.href = 'chat.html';
                } else {
                    alert(resp.msg);
                }
            }
        });
    });
})