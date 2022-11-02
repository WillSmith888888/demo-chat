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
        };
        reader.readAsText(file);
    }
}