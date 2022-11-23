function initEvent3() {
    $('.chat-footer').on('click', '.upload', function() {
        $('.file-input').click();
    });
    $('.chat-footer').on('change', '.file-input', function() {
        var file = $('.file-input')[0].files[0];
        $('#msg').val('FILE: ' + file.name + '; PASSWORD:');
    });
}

function uploadFile() {
    var formData = new FormData();
    var file = $('.file-input')[0].files[0];
    formData.append('file', file);
    var password = $('#msg').val().replace('FILE: ' + file.name + '; PASSWORD:', '');
    if (!password) {
        alert('请输入加密密码');
        return;
    }
    formData.append('password', password);
    var sessionId = $('.file-input').closest('form').attr('data-sessionid');
    $.ajax({
        type: 'POST',
        url: "/chat/upload.do",
        data: formData,
        async: false,
        processData: false,
        contentType: false,
        success: function(res) {
            if (res.code == '000000') {
                var zipName = res.data;
                var data = {
                    sessionId: sessionId,
                    content: cryptTool.encrypt(sessionId, '<a style="color: white;" href="http://' + document.location.host + '/dir/' + zipName + '" download>文件: ' + zipName + '</a>'),
                    time: new Date().format("yyyy-MM-dd hh:mm:ss"),
                    account: window.user.account
                };
                websocket.send(JSON.stringify(data));
                $('#msg').val('');

            } else {
                alert('文件上传失败');
            }
        }
    });
}


function commonUploadFile(file, fileInput) {
    var formData = new FormData();
    formData.append('file', file);
    $.ajax({
        type: 'POST',
        url: "/chat/common/file.do",
        data: formData,
        async: false,
        processData: false,
        contentType: false,
        success: function(res) {
            if (res.code == '000000') {
                var fileName = res.data;
                fileInput.attr('upload-name', fileName);
            } else {
                alert('文件上传失败');
            }
        }
    });
}


initEvent3();