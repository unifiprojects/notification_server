package com.matteomauro.notification_server;

import com.matteomauro.notification_server.model.Topic;
import java.io.IOException;
import java.util.HashMap;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

@ApplicationScoped
public class UserSessionHandler {

    private final Map<Topic, Set<Session>> topicAndSessions = new HashMap();
    private final Set<Session> sessionsNotSubscribed = new HashSet<>();

    public void addSession(Session session) {
        sessionsNotSubscribed.add(session);
        Logger.getLogger(UserSessionHandler.class.getName()).info("Session registered: " + session);
    }

    public void subscribeToTopic(Topic topic, Session session) {
        if (topicAndSessions.containsKey(topic)) {
            topicAndSessions.get(topic).add(session);
        } else {
            HashSet<Session> firstSession = new HashSet<>();
            firstSession.add(session);
            topicAndSessions.put(topic, firstSession);
        }
        sessionsNotSubscribed.remove(session);
        Logger.getLogger(UserSessionHandler.class.getName()).info("Session: " + session + " has subscribed to topic: " + topic);
    }

    public void sendNotifications(Topic topic, String message) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject messageJson = provider.createObjectBuilder()
                .add("topic", topic.getName())
                .add("message", message)
                .build();
        for (Session session : topicAndSessions.get(topic)) {
            sendToSession(session, messageJson.toString());
        }
    }

    private void sendToSession(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            Logger.getLogger(UserSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
