(function() {

    function initEvent() {
        $('#chat').on('click', function() {
            var account = $('#account').val();
            var password = $('#password').val();
            if (!account) {
                alert('请输入账号！');
                return;
            }
            if (!password) {
                alert('请输入密码！');
                return;
            }
            login(account, password);
        });
    }


    function login(account, password) {
        $.ajax({
            url: '/chat/login.do',
            type: 'POST',
            data: { account: account, password: password },
            success: function(resp) {
                console.info('登录返回结果：' + resp);
                resp = JSON.parse(resp);
                if (resp.code == '000000') {
                    window.localStorage.setItem('token', token);
                    window.location.href = 'chat.html';
                } else {
                    alert(resp.msg);
                }
            }
        });
    }

    initEvent();

})();