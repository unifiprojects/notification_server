package com.matteomauro.notification_server;

import com.matteomauro.notification_server.model.Topic;
import com.matteomauro.notification_server.repository.RedisRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class UserSessionHandler {

    private RedisRepository repositoryNotifications;

    public UserSessionHandler() {
        this.repositoryNotifications = new RedisRepository();
    }

    private List<Session> sessions = new LinkedList<>();

    public void subscribeToTopic(Topic topic, Session session) {
        repositoryNotifications.insertNotification(topic, session.getId());
        Logger.getLogger(UserSessionHandler.class.getName()).info("Session: " + session.getId() + " has subscribed to topic: " + topic.getName());
    }

    public void sendNotifications(Topic topic, String message) {
        Collection<String> ids = repositoryNotifications.getAllSessionsId(topic);
        JsonObject messageJson = buildJsonMessage(topic, message);
        sessions.stream().
                filter(session -> ids.contains(session.getId())).
                forEach(session -> sendToSession(session, messageJson.toString()));
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
        repositoryNotifications.removeAllNotificationsForUser(session.getId());
    }

    public void addSession(Session session) {
        sessions.add(session);
    }

    public void removeSession(Session session) {
        removeNotificationsForSession(session);
        sessions.remove(session);
    }
}
