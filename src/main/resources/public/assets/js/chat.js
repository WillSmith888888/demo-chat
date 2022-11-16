window.chatMsg = {};
window.messageTemp;
window.messageTempSelf;
window.contactItem;
window.contactGroup;
window.user;
window.friendPage;
window.profile;
window.chatHeader;
var websocket;

var heartCheck = {
    timeout: 50000,
    timeoutObj: null,
    reset: function() {
        clearTimeout(this.timeoutObj);
        return this;
    },
    start: function() {
        this.timeoutObj = setTimeout(function() {
            websocket.send("ping");
        }, this.timeout)
    }
}


function initEvent() {
    $('.friends .container-xl').on('click', '[data-close]', function(e) {
        e.preventDefault()
        $(".main").removeClass("main-visible")
    });
    $('.contacts-list').on('click', '.contacts-link', function() {
        $(".main").addClass("main-visible");
        var account = $(this).attr('data-account');
        if (account) {
            showFriendPage(account);
        }
        var sessionid = $(this).attr('data-sessionid');
        if (sessionid) {
            showSession(sessionid);
        }
    });
    $('.friends .container-xl').on('click', '.start-chat', function() {
        var account = $(this).attr('data-account');
        var accounts = window.localStorage.getItem('token') + ',' + account;
        $.ajax({
            url: '/chat/getSessionId.do',
            type: 'post',
            data: { accounts: accounts },
            success: function(resp) {
                if (resp.code = '000000') {
                    var sessionId = resp.data;
                    if ($('#chatContactTab [data-sessionid="' + sessionId + '"]').length == 0) {
                        createSession(sessionId);
                    }
                    $('#chats-tab').click();
                    $('#chatContactTab [data-sessionid="' + sessionId + '"]').click();
                } else {
                    alert('开启会话失败！');
                }
            }
        });
    });
    $('#sendMsg').click(function() {
        send();
    });
    $("body").keydown(function(e) {
        if (e.keyCode == 13) {
            send();
        }
    });
    $('#profile-content').on('click', '.logout', function() {
        $.ajax({
            url: '/chat/logout.do',
            data: { token: window.localStorage.getItem('token') },
            type: 'post',
            success: function(resp) {
                if (resp.code = '000000') {
                    alert('退出登录成功');
                    window.location.href = 'login.html';
                } else {
                    alert(resp.msg);
                }
            }
        });
    });
}

function send() {
    var sessionId = $('#sendMsg').attr('data-sessionid');
    var content = $('#msg').val();
    if (content) {
        var time = new Date().format('yyyy-MM-dd hh:mm:ss')
        var account = window.user.account;
        if (websocket.readyState == websocket.CLOSED) {
            var msg = "连接已经断开，确认重新连接吗？请确认！";
            if (confirm(msg) == true) {
                initWebsocket();
                alert('连接成功');
            }
            return;
        }
        websocket.send(JSON.stringify({
            sessionId: sessionId,
            account: account,
            content: content,
            time: time
        }));
        $('#msg').val('');

    }
}

function initWebsocket() {

    if (!window.localStorage.getItem('token')) {
        alert('登录信息缺失，请从登录面重新进入');
        window.location.href = 'login.html';
        return;
    }

    if (websocket) {
        if (websocket.readyState != websocket.CLOSED) {
            alert('目前已经处于连接状态！');
            return;
        }
    }

    websocket = new WebSocket('ws://' + document.location.host + '/chat/engine/' + window.localStorage.getItem('token'));
    websocket.onerror = function() {
        console.info("error");
    };

    // 连接成功建立的回调方法
    websocket.onopen = function(event) {
        console.info("open");
        // 清空聊天窗口
        $('#messageBody .message-day').html('');
        heartCheck.reset().start();
    }

    // 接收到消息的回调方法
    websocket.onmessage = function(event) {
        if ("pong" == event.data) {
            console.info("pong");
        } else {
            var msg = JSON.parse(event.data);
            if (msg.type == 1) {
                alert(msg.data);
            } else if (msg.type == 2) {
                renderFriends(msg);
                renderProfile(msg);
            } else {
                renderSession(msg);
            }
        }
        heartCheck.reset().start();
    }

    // 连接关闭的回调方法
    websocket.onclose = function() {
        console.info("close");
    }

    // 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function() {
        websocket.close();
    }
}

function loadTemp() {
    $.get('temp/message.html', function(temp) {
        window.messageTemp = temp;
        $.get('temp/message-self.html', function(temp) {
            window.messageTempSelf = temp;
            $.get('temp/contacts-item.html', function(temp) {
                window.contactItem = temp;
                $.get('temp/contacts-group.html', function(temp) {
                    window.contactGroup = temp;
                    $.get('temp/friend-page.html', function(temp) {
                        window.friendPage = temp;
                        $.get('temp/profile.html', function(temp) {
                            window.profile = temp;
                            $.get('temp/chat-header.html', function(temp) {
                                window.chatHeader = temp;
                                initWebsocket();
                                initEvent();
                            });
                        });
                    });
                });
            });
        });
    });
}

// 渲染朋友列表
function renderFriends(msg) {
    if (msg.data.friends) {
        $.get('temp/friend.html', function(temp) {
            var content = [];
            for (var i = 0; i < msg.data.friends.length; i++) {
                var _temp = temp;
                var friend = msg.data.friends[i];
                for (var key in friend) {
                    _temp = _temp.replaceAll('${' + key + '}', friend[key]);
                }
                content.push(_temp);
            }
            $('#friendsTab').html(content.join(''));
        });
    }
}

// 渲染个人信息页面
function renderProfile(msg) {
    window.user = msg.data;
    var profile = window.profile;
    for (var key in window.user) {
        profile = profile.replaceAll('${' + key + '}', window.user[key]);
    }
    $('#profile-content').html(profile);
}

function renderSession(msg) {
    var sessionId = msg.sessionId;
    if (!window.chatMsg[sessionId]) {
        window.chatMsg[sessionId] = [];
    }
    window.chatMsg[sessionId].push(msg);

    var message;
    if (window.user.account == msg.account) {
        message = window.messageTempSelf;
    } else {
        message = window.messageTemp;
    }

    for (var key in msg) {
        message = message.replaceAll('${' + key + '}', msg[key]);
    }

    if ($('#chatContactTab [data-sessionid="' + sessionId + '"]').length == 0) {
        createSession(sessionId);
    }
    $('#messageBody [data-sessionid="' + sessionId + '"]').append(message);
    var chatWindow = $('#chatContactTab [data-sessionid="' + sessionId + '"]');
    var scrollHeight = chatWindow.prop("scrollHeight");
    chatWindow.scrollTop(scrollHeight, 100);
}

function showFriendPage(account) {
    var friendPage = window.friendPage;
    var friends = window.user.friends;
    for (var i = 0; i < friends.length; i++) {
        var friend = friends[i];
        if (friend.account == account) {
            for (var key in friend) {
                friendPage = friendPage.replaceAll('${' + key + '}', friend[key]);
            }
            $('.friends .container-xl').html(friendPage);
        }
    }
}


function showSession(sessionId) {
    $('#messageBody .message-day').each(function(index, ele) {
        if ($(ele).hasClass(sessionId)) {
            $(ele).show();
        } else {
            $(ele).hide();
        }
        var accounts = sessionId.replaceAll('SESSION.', '').split('-');
        if (accounts.length == 2) {
            var chatHeader = window.chatHeader;
            for (var i = 0; i < accounts.length; i++) {
                if (accounts[i] != window.user.account) {
                    for (var j = 0; j < window.user.friends.length; j++) {
                        if (accounts[i] == window.user.friends[j].account) {
                            for (var key in window.user.friends[j]) {
                                chatHeader = chatHeader.replaceAll('${' + key + '}', window.user.friends[j][key]);
                            }
                        }
                    }
                }
            }
            $('.chat-header').html(chatHeader);
        }
        $('#sendMsg').attr('data-sessionid', sessionId);

    });
}


function createSession(sessionId) {
    var accounts = sessionId.replaceAll('SESSION.', '').split('-');
    var contact;
    var data = {};
    if (accounts.length > 2) {
        contact = window.contactGroup;
        // TODO 待定
    } else {
        contact = window.contactItem;
        if (accounts[0] != window.user.account) {
            data.friendAccount = accounts[0];
        } else {
            data.friendAccount = accounts[1];
        }
        for (var i = 0; i < window.user.friends.length; i++) {
            if (window.user.friends[i].account == data.friendAccount) {
                data.friendName = window.user.friends[i].name;
            }
        }
    }

    var time = new Date();
    data.sessionId = sessionId;
    data.time = time.format('yyyy-MM-dd hh:mm:ss');
    for (var key in data) {
        contact = contact.replaceAll('${' + key + '}', data[key]);
    }
    $('#chatContactTab').prepend(contact);
    $('#messageBody .container').append('<div data-sessionid="' + sessionId + '" class="message-day ' + sessionId + '"></div>');
    $('#messageBody .container .message-day').each(function(index, ele) {
        if ($(ele).hasClass(sessionId)) {
            $(ele).show();
        } else {
            $(ele).hide();
        }
    });
}

loadTemp();