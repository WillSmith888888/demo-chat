var key = CryptoJS.enc.Utf8.parse("zhgerXHBVaaKm89a");

window.encrypt = function(text) {
    console.info('加密前：' + text);
    var encryptedData = CryptoJS.AES.encrypt(text, key, {
        iv: CryptoJS.enc.Utf8.parse(key),
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
    });
    console.info('加密后：' + encryptedData);
    return encryptedData.toString();
}


window.decrypt = function(encryptedData) {
    console.info('解密前：' + encryptedData);
    var decryptedData = CryptoJS.AES.decrypt(encryptedData, key, {
        iv: CryptoJS.enc.Utf8.parse(key),
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
    });
    var text = decryptedData.toString(CryptoJS.enc.Utf8);
    console.info('解密后：' + text);
    return text;
}