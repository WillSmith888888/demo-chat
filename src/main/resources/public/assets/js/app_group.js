function initEvent6() {

    $('#createGroup').find('[data-orientation="next"]').click(function() {
        var step = $('#actual-step').val();
        if (step == 2) {
            var groupName = $('#groupName').val();
            if (!groupName) {
                $('#create_group_result').html('创建失败：请输入群名称');
                return;
            }
            var logo = $('#profilePictureInput').attr('upload-name');
            if (!logo) {
                $('#create_group_result').html('创建失败：请选择群图片');
                return;
            }
            var friendAccounts = [];
            friendAccounts.push(window.localStorage.getItem('token'));
            $('input[id^=chx-user-]').each(function(index, ele) {
                if (ele.checked) {
                    var account = ele.id.replaceAll('chx-user-', '');
                    friendAccounts.push(account);
                }
            });
            if (friendAccounts.length < 3) {
                $('#create_group_result').html('创建失败：群人数必须大于3，故你必须选择2个及2个以上的朋友');
                return;
            }
            $.ajax({
                type: 'POST',
                url: '/chat/createGroupChat.do',
                data: { groupName: groupName, accounts: friendAccounts.join(','), logo: logo },
                success: function(resp) {
                    if (resp.code == '000000') {
                        $('#create_group_result').html('群创建成功');
                    } else {
                        $('#create_group_result').html("创建失败：" + resp.msg);
                    }
                }
            });
        } else if (step == 3) {
            var txt = $('#create_group_result').html();
            if (txt == '群创建成功') {
                window.location.reload();
            }
        }
    });

    $('.chat-header').on('click', '[data-sessionid]', function() {
        var sessionId = $(this).attr('data-sessionid');
        console.info('删除会话' + sessionId);
        $.ajax({
            url: '/chat/delGroupChat.do',
            type: 'post',
            data: { sessionId: sessionId },
            success: function(resp) {
                if (resp.code == '000000') {
                    alert(resp.msg);
                    window.location.reload();
                } else {
                    alert(resp.msg);
                }
            }
        });
    });


}

function renderGroupFriends(msg) {
    if (msg.data.friends) {
        var content = [];
        for (var i = 0; i < msg.data.friends.length; i++) {
            var _temp = window.groupFriend;
            var friend = msg.data.friends[i];
            for (var key in friend) {
                _temp = _temp.replaceAll('${' + key + '}', friend[key]);
            }
            content.push(_temp);
        }
        $('#createGroup .list-group-flush').html(content.join(''));
    }
}


initEvent6();