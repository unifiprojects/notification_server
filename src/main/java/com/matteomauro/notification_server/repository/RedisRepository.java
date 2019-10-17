package com.matteomauro.notification_server.repository;

import com.matteomauro.notification_server.model.Topic;
import org.redisson.Redisson;
import org.redisson.api.RMultimap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;

@ApplicationScoped
public class RedisRepository {

    private final String PORT = "6379";
    private final String URL = "redis://127.0.0.1:";
    private final String NOTIFICATION_MAP = "notifications";
    final RMultimap<Topic, String> notifications_map; // todo package private is not correct

    public RedisRepository() {
        Config config = new Config();
        config.useSingleServer().setAddress(URL + PORT);
        RedissonClient redisson = Redisson.create(config);
        notifications_map = redisson.getSetMultimap(NOTIFICATION_MAP);
    }

    public void insertNotification(Topic topic, String sessionId) {
        notifications_map.put(topic, sessionId);
    }

    public void removeNotification(Topic topic,  String sessionId) {
        notifications_map.remove(topic, sessionId);
    }

    public Collection<String> getAllSessionsId(Topic topic) {
        return notifications_map.getAll(topic);
    }

    public void removeAllNotificationsForUser(String sessionId) {
        notifications_map.keySet().stream().forEach(topic -> removeNotification(topic, sessionId));
    }

    public String getPORT() {
        return PORT;
    }

    public String getURL() {
        return URL;
    }

    public String getNOTIFICATION_MAP() {
        return NOTIFICATION_MAP;
    }
}
