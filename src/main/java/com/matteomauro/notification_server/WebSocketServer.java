package com.matteomauro.notification_server;

import com.matteomauro.notification_server.model.Topic;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

@ApplicationScoped
@ServerEndpoint("/topic")
public class WebSocketServer {

    private UserSessionHandler userSessionHandler;

    public WebSocketServer() {
        this.userSessionHandler = new UserSessionHandler();
    }
    
    @OnOpen
    public void open(Session session) {
        userSessionHandler.addSession(session);
        Logger.getLogger(UserSessionHandler.class.getName()).info("Session: " + session.getId() + " has opened a connection with the server.");
    }

    @OnClose
    public void close(Session session) {
        userSessionHandler.removeSession(session);
        userSessionHandler.removeNotificationsForSession(session);
        Logger.getLogger(UserSessionHandler.class.getName()).info("Session: " + session.getId() + " has closed the connection with the server.");
    }

    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String requestMessage, Session session) {
        Logger.getLogger(WebSocketServer.class.getName()).info("Handling message for session: " + session);
        try ( JsonReader reader = Json.createReader(new StringReader(requestMessage))) {
            JsonObject jsonMessage = reader.readObject();

            if ("subscribe".equals(jsonMessage.getString("action"))) {
                Topic topic = new Topic(jsonMessage.getString("topic_name"));
                userSessionHandler.subscribeToTopic(topic, session);
            } else if ("publish".equals(jsonMessage.getString("action"))) {
                String topic = jsonMessage.getString("topic_name");
                String message = jsonMessage.getString("topic_message");
                userSessionHandler.sendNotifications(new Topic(topic), message);
            }
        }
    }
}
