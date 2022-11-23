function initEvent4() {
    $('.addFriend').click(function() {
        var account = $('#inviteEmailAddress').val();
        if (!account) {
            alert('请输入朋友账号');
            return;
        }
        $.ajax({
            type: 'POST',
            url: "/chat/addFriend.do",
            data: { token: window.localStorage.getItem("token"), account: account },
            async: true,
            success: function(resp) {
                alert(resp.msg);
                if (resp.code == '000000') {
                    var _temp = window.friend;
                    for (var key in resp.data) {
                        _temp = _temp.replaceAll('${' + key + '}', resp.data[key]);
                    }
                    $('#friendsTab').append(_temp);
                    $('.addFriend').closest('div').find('.text-muted').click();
                }
            }
        });
    });

    $('.friends .container-xl').on('click', '.removeFriend', function() {
        var account = $(this).attr('data-account');
        $.ajax({
            type: 'POST',
            url: "/chat/removeFriend.do",
            data: { token: window.localStorage.getItem("token"), account: account },
            async: true,
            success: function(resp) {
                alert(resp.msg);
                if (resp.code == '000000') {
                    $('#friendsTab').find('li').each(function(index, ele) {
                        var _account = $(ele).find('a').attr('data-account');
                        if (account == _account) {
                            $(ele).remove();
                        }
                    });
                }
            }
        });
    });
}

initEvent4();