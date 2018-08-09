var webSocket = new WebSocket('ws://localhost:8080/websocket/console');

webSocket.onerror = function (event) {
    onError(event)
};

webSocket.onopen = function (event) {
    onOpen(event)
};

webSocket.onmessage = function (event) {
    onMessage(event)
};

function onMessage(event)
{
    $('#div-console').append('<p class="cmsg">' + event.data.replace(/(\r\n|\n|\r)/gm, " ") + '</p>');
    // $('#div-console').animate({scrollTop: $('#div-console').prop("scrollHeight")}, 5);
    // $('#div-console').scrollTop =  $('#div-console').prop("scrollHeight");
}

function onOpen(event)
{
    $("#div-console").append('<p class="cmsg"> Console Connection established </p>');
}

function onError(event)
{
    alert(event.data);
}

function keepAlive()
{
    var timeout = 20000;
    if (webSocket.readyState == webSocket.OPEN)
    {
        webSocket.send('ping');
    }
    setTimeout(keepAlive, timeout);
}

setInterval(keepAlive, 30000);
