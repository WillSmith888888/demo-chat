(function() {

    function initEvent() {
        $('#chat').on('click', function() {
            var account = $('#account').val();
            var friends = $('#friends').val();
            var password = $('#password').val();
            if (!account) {
                alert('请输入账号！');
                return;
            }
            if (!password) {
                alert('请输入密码！');
                return;
            }
            if (!friends) {
                alert('请输入朋友账号！');
                return;
            }
            window.localStorage.setItem('account', account);
            window.localStorage.setItem('password', password);
            window.localStorage.setItem('friends', friends);
            window.location.href = 'chat.html';
        });
    }

    initEvent();

})();