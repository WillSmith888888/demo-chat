var handler = {

    handle: function(resp) {
        if (resp.mapped == 'ping') {
            this.pong(resp.data);
        } else if (resp.mapped == 'addFriend') {
            this.addFriend(resp.data);
        } else if (resp.mapped == 'onOpen') {
            this.addFriend(resp.data);
        } else {
            this.accept(resp.data);
        }
    },

    getSessionId: function(sessionId) {

    },

    addFriend: function(user) {

    },

    onOpen: function() {

    },

    pong: function(pong) {
        console.info(pong);
    },

    accept: function(msg) {

    }



};