var websocket;

var unReceiveMsg = [];

var msg_template = {};

var accountNameMap;

function renderMsgNotReceive(datas) {
    for (var i = 0; i < datas.length; i++) {
        renderMsg(datas[i]);
    }
}

function renderMsg(data) {
    data.content = window.decrypt(data.content);
    data.maxWidth = $(window).width() / 1.8;
    data.src = 'img/' + data.account + '.jpg';
    data.name = accountNameMap[data.account];
    var chatWindow = $(".window");
    var text_temp = (data.account == window.localStorage.getItem('account')) ? msg_template.right_text : msg_template.left_text;
    chatWindow.append(wrapTemplate(data, text_temp));
    var scrollHeight = chatWindow.prop("scrollHeight");
    chatWindow.scrollTop(scrollHeight, 100);
}

function initWebsocket() {

    if (!window.localStorage.getItem('account') || !window.localStorage.getItem('friends')) {
        alert('没有输入用户或者朋友名称，请从登录面重新进入');
        window.location.href = 'login.html';
        return;
    }

    if (websocket != null && websocket.readyState != websocket.CLOSED) {
        alert('目前已经处于连接状态！');
        return;
    }

    websocket = new WebSocket('ws://' + document.location.host + '/chat/engine/' +
        window.localStorage.getItem('account') + '/' +
        window.localStorage.getItem('friends'));
    websocket.onerror = function() {
        console.info("error");
    };

    //连接成功建立的回调方法
    websocket.onopen = function(event) {
        console.info("open");
        $(".window").html('');
    }

    //接收到消息的回调方法
    websocket.onmessage = function(event) {
        if (event.data.startsWith('SESSION.')) {
            window.localStorage.setItem('sessionId', event.data);
            var arr = event.data.split('<--->');
            window.localStorage.setItem('sessionId', arr[0]);
            var info = JSON.parse(arr[1]);
            for (var key in info) {
                window.localStorage.setItem(key, info[key]);
            }
            accountNameMap = JSON.parse(arr[2]);
            $(".window").html('');
            return;
        }
        var data = JSON.parse(event.data);
        if (typeof window.decrypt === 'undefined') {
            console.info('接收信息，请先设置密钥');
            unReceiveMsg.push(data);
            return;
        }
        renderMsg(data);
    }

    //连接关闭的回调方法
    websocket.onclose = function() {
        console.info("close");
        alert('你已经断开连接');
    }

    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function() {
        websocket.close();
    }
}


function initEvent() {
    $('#send').on('click', function() {
        var content = $("#input-text").val();
        if ($.trim(content) === '') {
            return;
        }
        if (typeof window.encrypt === 'undefined') {
            alert('发送信息，请先设置密钥');
            return;
        }
        if (websocket == null || websocket.readyState == websocket.CLOSED) {
            alert('连接已经断开，请重新连接！');
            return;
        }
        var data = {
            sessionId: window.localStorage.getItem('sessionId'),
            content: window.encrypt(content),
            time: new Date().format("yyyy-MM-dd hh:mm:ss"),
            account: window.localStorage.getItem('account')
        };
        websocket.send(JSON.stringify(data));
        $("#input-text").val('');
    });


    $(document).keypress(function(event) {
        var key = event.keyCode;
        if (key == '13') {
            $('#send').click();
        }
    });

    $('#connect').on('click', function() {
        initWebsocket();
    });
}

function initMsgTemplate() {
    $.get({
        url: '/component/right_text.tmp',
        success: function(temp) {
            msg_template.right_text = temp;
        }
    });
    $.get({
        url: '/component/left_text.tmp',
        success: function(temp) {
            msg_template.left_text = temp;
        }
    });
}

Date.prototype.format = function(fmt) {
    var o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S": this.getMilliseconds()
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

function wrapTemplate(data, template) {
    for (var key in data) {
        template = template.replace('${' + key + '}', data[key]);
    }
    return template;
}

function clickFile() {
    $('#secret').click();
}

function inputKey(input) {
    var files = input.files;
    if (files.length) {
        var file = files[0];
        var reader = new FileReader();
        reader.onload = function() {
            var text = this.result;
            console.info(text);
            eval(text);
            alert('设置成功！');
            renderMsgNotReceive(unReceiveMsg);
            unReceiveMsg = [];
        };
        reader.readAsText(file);
    }
}



window.onbeforeunload = function() {
    console.info('刷新或者关闭页面，关闭websocket连接');
    websocket.close();
}


initWebsocket();
initEvent();
initMsgTemplate();