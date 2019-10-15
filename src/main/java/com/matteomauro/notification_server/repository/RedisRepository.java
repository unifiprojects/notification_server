package com.matteomauro.notification_server.repository;

import com.matteomauro.notification_server.model.Topic;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import javax.websocket.Session;
import org.redisson.api.RMultimap;

@ApplicationScoped
public class RedisRepository {

    private final String PORT = "6379";
    private final String URL = "redis://127.0.0.1:";
    private final String NOTIFICATION_MAP = "notifications";
    private final RMultimap<Topic, Session> notifications_map;

    public RedisRepository() {
        Config config = new Config();
        config.useSingleServer().setAddress(URL + PORT);
        RedissonClient redisson = Redisson.create(config);
        notifications_map = redisson.getSetMultimap(NOTIFICATION_MAP);
    }

    public void insertNotification(Topic topic, Session session) {
        notifications_map.put(topic, session);
    }

    public void removeNotification(Topic topic, Session session) {
        notifications_map.remove(topic, session);
    }

    public Collection<Session> getAllSessions(Topic topic) {
        return notifications_map.getAll(topic);
    }

    public void removeAllNotificationsForUser(Session session) {
        notifications_map.values().clear();
    }
}
