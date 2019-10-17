/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.matteomauro.notification_server.repository;

import com.matteomauro.notification_server.model.Topic;
import org.junit.Before;
import org.junit.Test;
import javax.websocket.Session;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author matteo
 */
@RunWith(MockitoJUnitRunner.class)
public class RedisRepositoryIT {

    private RedisRepository repository;

    @Before
    public void setup() {
        repository = new RedisRepository();
        repository.notifications_map.clear();
    }

    @Test
    public void testWhenOneTopicAndMultipleSessionsArePersisted() {
        Topic topic = new Topic("test1");
        Session session1 = mock(Session.class);
        Session session2 = mock(Session.class);
        when(session1.getId()).thenReturn("1");
        when(session2.getId()).thenReturn("2");

        repository.insertNotification(topic, session1.getId());
        repository.insertNotification(topic, session2.getId());
        assertThat(repository.getAllSessionsId(topic)).containsExactly(session1.getId(), session2.getId());
    }

    @Test
    public void testWhenOneSessionIdIsRemoved() {
        Topic topic = new Topic("test1");
        Session session1 = mock(Session.class);
        when(session1.getId()).thenReturn("1");

        repository.insertNotification(topic, session1.getId());

        repository.removeNotification(topic, session1.getId());
        assertThat(repository.getAllSessionsId(topic)).isEmpty();
    }

    @Test
    public void testWhenAllSessionIdsForUserAreRemoved() {
        Topic topic = new Topic("test1");
        Session session1 = mock(Session.class);
        Session session2 = mock(Session.class);
        when(session1.getId()).thenReturn("1");
        when(session2.getId()).thenReturn("2");

        repository.insertNotification(topic, session1.getId());
        repository.insertNotification(topic, session2.getId());

        repository.removeAllNotificationsForUser(session1.getId());
        assertThat(repository.getAllSessionsId(topic)).containsExactly(session2.getId());
    }

}
