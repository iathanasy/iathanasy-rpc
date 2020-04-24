(function (win) {
    var ws;
    var auth = false;
    var Client = function (options) {
        var MAX_CONNECT_TIME = 10;
        var DELAY = 15000;
        this.options = options || {};
        this.createConnect(MAX_CONNECT_TIME, DELAY);
    }

    Client.prototype.send = function () {
        var content = document.getElementById("content").value;

        ws.send(JSON.stringify({
            'v': 1,
            'cm': 2,
            'content': content
        }));
    }

    Client.prototype.createConnect = function (max, delay) {
        var self = this;
        if (max === 0) {
            return;
        }
        connect();

        var heartbeatInterval;

        function connect() {
            ws = new WebSocket('ws://localhost:5892/websocket');

            ws.onopen = function () {
            }

            ws.onmessage = function (evt) {
                var data = JSON.parse(evt.data);
                if (data.cm == 3) {
                    var notify = self.options.notify;
                    if (notify) {
                        notify(data.content);
                    }
                }
            }

            ws.onclose = function () {
                if (heartbeatInterval) {
                    clearInterval(heartbeatInterval);
                }
                setTimeout(reConnect, delay);
            }

            function heartbeat() {
                ws.send(JSON.stringify({
                    'v': 1,
                    'cm': 0
                }));
            }

        }

        function reConnect() {
            self.createConnect(--max, delay * 2);
        }
    }

    win['MyClient'] = Client;
})(window);