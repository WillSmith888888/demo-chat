function initEvent5() {
    $('#chatContactTab').on('click', '.contacts-item', function() {
        $('#chatContactTab .contacts-item').each(function(index, ele) {
            if ($(ele).hasClass('active')) {
                $(ele).removeClass('active');
            }

        });
        $(this).addClass('active');
        $('#messageBody').scrollTop($('#messageBody').prop("scrollHeight"));
    });

    $('#friendsTab').on('click', '.contacts-item', function() {
        $('#friendsTab .contacts-item').each(function(index, ele) {
            if ($(ele).hasClass('active')) {
                $(ele).removeClass('active');
            }
        });
        $(this).addClass('active');
    });
}

initEvent5();