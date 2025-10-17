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
    private static Map<String, Set<Session>> channelUsersMap = new ConcurrentHashMap<>();
    private static Map<Session, String> userChannelMap = new ConcurrentHashMap<>();

    static {
        channelUsersMap.put("general", new HashSet<>());
    }

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
        joinChannel("general", session);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        String username = sessionUsernameMap.get(session);
        System.out.println("Message from " + username + ": " + message);

        if (message.startsWith("/join ")) {
            String channel = message.substring(6).trim();
            joinChannel(channel, session);
        } else if (message.startsWith("/leave")) {
            leaveChannel(session);
        } else if (message.startsWith("/msg ")) {
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
            String currentChannel = userChannelMap.get(session);
            if (currentChannel != null) {
                broadcast(currentChannel, username + ": " + message);
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        String username = sessionUsernameMap.remove(session);
        if (username != null) {
            usernameSessionMap.remove(username);
            leaveChannel(session);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error for session " + session.getId() + ": " + throwable.getMessage());
    }

    private void joinChannel(String channel, Session session) {
        leaveChannel(session); // Leave current channel before joining a new one

        channelUsersMap.computeIfAbsent(channel, k -> new HashSet<>()).add(session);
        userChannelMap.put(session, channel);
        String username = sessionUsernameMap.get(session);
        broadcast(channel, "[System]: User '" + username + "' has joined #" + channel);
        broadcastUserList(channel);
        broadcastChannelList();
    }

    private void leaveChannel(Session session) {
        String currentChannel = userChannelMap.remove(session);
        if (currentChannel != null) {
            channelUsersMap.get(currentChannel).remove(session);
            String username = sessionUsernameMap.get(session);
            if(username != null) {
                broadcast(currentChannel, "[System]: User '" + username + "' has left #" + currentChannel);
                broadcastUserList(currentChannel);
            }
        }
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

    private void broadcast(String channel, String message) {
        Set<Session> users = channelUsersMap.get(channel);
        if (users != null) {
            for (Session session : users) {
                sendMessage(session, message);
            }
        }
    }

    private void broadcastToAll(String message) {
        for (Session session : sessionUsernameMap.keySet()) {
            sendMessage(session, message);
        }
    }

    private void broadcastUserList(String channel) {
        Set<Session> users = channelUsersMap.get(channel);
        if (users != null) {
            String userListStr = users.stream()
                                      .map(s -> sessionUsernameMap.get(s))
                                      .filter(s -> s != null)
                                      .reduce((s1, s2) -> s1 + "," + s2)
                                      .orElse("");
            broadcast(channel, "USERLIST:" + userListStr);
        }
    }

    private void broadcastChannelList() {
        String channelListMessage = "CHANNELLIST:" + String.join(",", channelUsersMap.keySet());
        broadcastToAll(channelListMessage);
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