package chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.Endpoint;
import java.util.Set;
import java.util.HashSet;
import org.glassfish.tyrus.server.Server;

@ServerEndpoint(value = "/chat/{username}")
public class ChatServer {

    private static Map<Session, String> sessionUsernameMap = new ConcurrentHashMap<>();
    private static Map<String, Session> usernameSessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        String username = session.getPathParameters().get("username");
        if (usernameSessionMap.containsKey(username)) {
            try {
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);
        String message = "User '" + username + "' has joined the chat.";
        broadcast(message);
        System.out.println(message);
        broadcastUserList();
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        String username = sessionUsernameMap.get(session);
        System.out.println("Message from " + username + ": " + message);

        if (message.startsWith("/msg ")) {
            String[] parts = message.split(" ", 3);
            if (parts.length == 3) {
                String destUsername = parts[1];
                String privateMessage = parts[2];
                Session destSession = usernameSessionMap.get(destUsername);
                if (destSession != null) {
                    sendMessage(destSession, "[PM from " + username + "]: " + privateMessage);
                    sendMessage(session, "[PM to " + destUsername + "]: " + privateMessage);
                } else {
                    sendMessage(session, "[System]: User '" + destUsername + "' not found.");
                }
            } else {
                sendMessage(session, "[System]: Invalid private message format. Use /msg <user> <message>");
            }
        } else {
            broadcast(username + ": " + message);
        }
    }

    @OnClose
    public void onClose(Session session) {
        String username = sessionUsernameMap.remove(session);
        if (username != null) {
            usernameSessionMap.remove(username);
            String message = "User '" + username + "' has left the chat.";
            broadcast(message);
            System.out.println(message);
            broadcastUserList();
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error for session " + session.getId() + ": " + throwable.getMessage());
    }

    private void sendMessage(Session session, String message) {
        try {
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        for (Session session : sessionUsernameMap.keySet()) {
            sendMessage(session, message);
        }
    }

    private void broadcastUserList() {
        String userListMessage = "USERLIST:" + String.join(",", usernameSessionMap.keySet());
        broadcast(userListMessage);
    }

    public static void main(String[] args) {
        Server server = new Server("localhost", 8080, "/websockets", null, new HashSet<Class<?>>() {{
            add(ChatServer.class);
        }});

        try {
            server.start();
            System.out.println("Press any key to stop the server...");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}