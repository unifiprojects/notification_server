var socket = new WebSocket("ws://localhost:8080/notification_server/topic");
socket.onmessage = onMessage;

function onMessage(event) {
    var notification = JSON.parse(event.data);
    alert("topic: " + notification.topic + "\nmessage: " + notification.message);
}

function formSubscribe() {
    var form = document.getElementById("subscribeForm");
    var nameTopic = form.elements["topic_name"].value;
    subscribeTopic(nameTopic);
}

function formPublish() {
    var form = document.getElementById("publishForm");
    var nameTopic = form.elements["topic_name"].value;
    var message = form.elements["topic_message"].value;
    publishTopic(nameTopic, message);
}

function subscribeTopic(nameTopic) {
    var action = {
        action: "subscribe",
        topic_name: nameTopic
    };
    socket.send(JSON.stringify(action));
}

function publishTopic(nameTopic, message) {
    var action = {
        action: "publish",
        topic_name: nameTopic,
        topic_message: message
    };
    socket.send(JSON.stringify(action));
}