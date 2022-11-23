cryptTool._sessionId = {
    key: CryptoJS.enc.Utf8.parse("zhgerXHBVaaKm89a"),
    encrypt: function(text, time) {
        console.info('加密前：' + text);
        var md5_encryptedData = CryptoJS.AES.encrypt(hex_md5(time.replaceAll('-', '*')), this.key, {
            iv: CryptoJS.enc.Utf8.parse(this.key),
            mode: CryptoJS.mode.CBC,
            padding: CryptoJS.pad.Pkcs7
        }).toString();
        var encryptedData = CryptoJS.AES.encrypt(text, this.key, {
            iv: CryptoJS.enc.Utf8.parse(this.key),
            mode: CryptoJS.mode.CBC,
            padding: CryptoJS.pad.Pkcs7
        }).toString();
        var index = Math.floor(Math.random() * encryptedData.toString().length);
        var str1 = encryptedData.substr(0, index);
        var str2 = encryptedData.substr(index);
        console.info('加密后：' + str1 + md5_encryptedData + str2);
        return str1 + md5_encryptedData + str2;
    },
    decrypt: function(encryptedData, time) {
        console.info('解密前：' + encryptedData);
        var md5_encryptedData = CryptoJS.AES.encrypt(hex_md5(time.replaceAll('-', '*')), this.key, {
            iv: CryptoJS.enc.Utf8.parse(this.key),
            mode: CryptoJS.mode.CBC,
            padding: CryptoJS.pad.Pkcs7
        }).toString();
        encryptedData = encryptedData.replace(md5_encryptedData, '');
        var decryptedData = CryptoJS.AES.decrypt(encryptedData, this.key, {
            iv: CryptoJS.enc.Utf8.parse(this.key),
            mode: CryptoJS.mode.CBC,
            padding: CryptoJS.pad.Pkcs7
        });
        var text = decryptedData.toString(CryptoJS.enc.Utf8);
        console.info('解密后：' + text);
        return text;
    }
}