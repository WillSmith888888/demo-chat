function initEvent() {
    $('#save').on('click', function() {
        save();
    });

    $('#iconFile').change(function() {
        var imgUrl = window.URL.createObjectURL(this.files[0]);
        var img = document.getElementById('profile_icon');
        img.setAttribute('src', imgUrl);
    });
}

function save() {
    var formData = new FormData();
    var file = $('#iconFile')[0].files[0];
    formData.append('file', file);
    var account = $('#account').val();
    formData.append('account', account);
    var name = $('#name').val();
    formData.append('name', name);
    $.ajax({
        type: 'POST',
        url: "/chat/createUser.do",
        data: formData,
        async: false,
        processData: false,
        contentType: false,
        success: function(res) {
            if ('000000' == res) {
                alert('用户创建成功');
            }

        }
    });
}


initEvent();