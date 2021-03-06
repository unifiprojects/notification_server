var socket = new WebSocket("ws://localhost:8080/topic");
socket.onmessage = onMessage;

function onMessage(event) {
    var notification = JSON.parse(event.data);
    if(!window.Push.Permission.has()){
        console.log("permission requested")
        window.Push.Permission.request(onGranted, onDenied);
    }else{
        console.log("permission already requested");
        onGranted(notification);
    }
}

//Notification
function onGranted(notification){
    console.log("permission granted");
    window.Push.create("topic:" + notification.topic, {
        body: "message: " + notification.message,
        timeout: 4000,
    });
}

function onDenied() {
    alert("Can't get permission for notification.");
}

//Page
function formSubscribe() {
    var form = document.getElementById("addToFollowed_form");
    var nameTopic = form.elements["followedToAdd"].value;
    subscribeTopic(nameTopic);
}

function formPublish() {
    var form = document.getElementById("like_form");
    var topic = form.elements["topicName"].value;
    var game = form.elements["gameToAdd"].value;
    console.log(topic + " " + game);
    publishTopic(topic, game);
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
