(function() {

    function initEvent() {
        $('#chat').on('click', function() {
            var account = $('#account').val();
            var friends = $('#friends').val();
            if (!account) {
                alert('please input account!');
                return;
            }
            if (!friends) {
                alert('please input friends!');
                return;
            }
            window.localStorage.setItem('account', account);
            window.localStorage.setItem('friends', friends);
            window.location.href = 'chat.html';
        });
    }

    initEvent();

})();