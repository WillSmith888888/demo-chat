var cryptTool = {
    default: {
        key: CryptoJS.enc.Utf8.parse("zhgerXHBVaaKm89a"),
        encrypt: function(text) {
            console.info('加密前：' + text);
            var encryptedData = CryptoJS.AES.encrypt(text, this.key, {
                iv: CryptoJS.enc.Utf8.parse(this.key),
                mode: CryptoJS.mode.CBC,
                padding: CryptoJS.pad.Pkcs7
            });
            console.info('加密后：' + encryptedData);
            return encryptedData.toString();
        },
        decrypt: function(encryptedData) {
            console.info('解密前：' + encryptedData);
            var decryptedData = CryptoJS.AES.decrypt(encryptedData, this.key, {
                iv: CryptoJS.enc.Utf8.parse(this.key),
                mode: CryptoJS.mode.CBC,
                padding: CryptoJS.pad.Pkcs7
            });
            var text = decryptedData.toString(CryptoJS.enc.Utf8);
            console.info('解密后：' + text);
            return text;
        }
    },
    encrypt: function(sessionId, str, time) {
        var key = sessionId.replace('.', '').replaceAll('-', '');
        if (cryptTool[key]) {
            return this[key].encrypt(str, time);
        } else {
            return this.default.encrypt(str);
        }

    },
    decrypt: function(sessionId, str, time) {
        var key = sessionId.replace('.', '').replaceAll('-', '');
        if (cryptTool[key]) {
            return this[key].decrypt(str, time);
        } else {
            return this.default.decrypt(str);
        }
    }
};

function initEvent2() {
    $('.chat-footer').on('click', '.secret', function() {
        $('.secret-input').click();
    });
    $('.chat-footer').on('change', '.secret-input', function() {
        var sessionId = $('.secret-input').closest('form').attr('data-sessionid');
        var file = $('.secret-input')[0].files[0];
        var reader = new FileReader();
        reader.onload = function() {
            var text = this.result;
            console.info(text);
            text = text.replace('_sessionId', sessionId.replace('.', '').replaceAll('-', ''));
            eval(text);
            alert('密钥文件设置成功！');
            $('.secret-input').val('');
        };
        reader.readAsText(file);
    });
}

initEvent2();