package com.matteomauro.notification_server.client;

import com.matteomauro.notification_server.model.Topic;
import com.matteomauro.notification_server.server.WebSocketServer;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint
public class WebSocketClient {

    private Session userSession = null;

    public WebSocketClient(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider
                    .getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (IOException | DeploymentException e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        Logger.getLogger(WebSocketClient.class.getName()).info("\nOnOpen() di WebSocketClient");
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        Logger.getLogger(WebSocketClient.class.getName()).info("\nOnClose() di WebSocketClient");
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(String message) {
        Logger.getLogger(WebSocketClient.class.getName()).info("Message received at client: " + message);
    }

    public void sendMessage(String action, String topic, String message) {
        JsonObject messageJson = buildJsonMessage(action, new Topic(topic), message);
        if (userSession != null && userSession.isOpen()) {
            try {
                this.userSession.getBasicRemote().sendText(messageJson.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Logger.getLogger(WebSocketClient.class.getName()).info("Session " + userSession.getId() + " can't send any message because is closed.");
        }
    }

    private JsonObject buildJsonMessage(String action, Topic topic, String message) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject messageJson = provider.createObjectBuilder()
                .add("action", action)
                .add("topic", topic.getName())
                .add("message", message)
                .build();
        return messageJson;
    }

}
