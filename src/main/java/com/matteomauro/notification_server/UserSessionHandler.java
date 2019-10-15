package com.matteomauro.notification_server;

import com.matteomauro.notification_server.model.Topic;
import com.matteomauro.notification_server.repository.RedisRepository;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

@ApplicationScoped
public class UserSessionHandler {

    @Inject
    private RedisRepository repositoryNotifications;

    public void subscribeToTopic(Topic topic, Session session) {
        repositoryNotifications.insertNotification(topic, session);
        Logger.getLogger(UserSessionHandler.class.getName()).info("Session: " + session + " has subscribed to topic: " + topic);
    }

    public void sendNotifications(Topic topic, String message) {
        JsonObject messageJson = buildJsonMessage(topic, message);
        for (Session session : repositoryNotifications.getAllSessions(topic)) {
            sendToSession(session, messageJson.toString());
        }
    }

    private JsonObject buildJsonMessage(Topic topic, String message) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject messageJson = provider.createObjectBuilder()
                .add("topic", topic.getName())
                .add("message", message)
                .build();
        return messageJson;
    }

    private void sendToSession(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            Logger.getLogger(UserSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void removeNotificationsForSession(Session session) {
        repositoryNotifications.removeAllNotificationsForUser(session);
    }

}
