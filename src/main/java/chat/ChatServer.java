package chat;

import org.glassfish.tyrus.server.Server;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat/{username}")
public class ChatServer {

    private static Map<String, Guild> guilds = new ConcurrentHashMap<>();
    private static Map<Session, String> sessionUsernameMap = new ConcurrentHashMap<>();
    private static Map<String, Session> usernameSessionMap = new ConcurrentHashMap<>();
    private static Map<Session, String> userGuildMap = new ConcurrentHashMap<>();

    static {
        guilds.put("default", new Guild("default"));
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
        joinGuild("default", session);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        String username = sessionUsernameMap.get(session);
        System.out.println("Message from " + username + ": " + message);

        if (message.startsWith("/join ")) {
            String[] parts = message.substring(6).split(" ");
            if (parts.length == 2) {
                joinChannel(parts[0], parts[1], session);
            }
        } else if (message.startsWith("/guild ")) {
            String guildName = message.substring(7).trim();
            joinGuild(guildName, session);
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
            String guildName = userGuildMap.get(session);
            Guild guild = guilds.get(guildName);
            if (guild != null) {
                String channelName = guild.getUserChannel(session);
                if (channelName != null) {
                    broadcast(guildName, channelName, username + ": " + message);
                }
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

    private void joinGuild(String guildName, Session session) {
        guilds.computeIfAbsent(guildName, Guild::new);
        userGuildMap.put(session, guildName);
        String username = sessionUsernameMap.get(session);
        sendMessage(session, "[System]: You have joined guild '" + guildName + "'");
        joinChannel(guildName, "general", session);
        broadcastGuildChannelList(guildName, session);
    }

    private void joinChannel(String guildName, String channelName, Session session) {
        Guild guild = guilds.get(guildName);
        if (guild != null) {
            guild.joinChannel(channelName, session);
            String username = sessionUsernameMap.get(session);
            broadcast(guildName, channelName, "[System]: User '" + username + "' has joined #" + channelName);
            broadcastUserList(guildName, channelName);
        }
    }

    private void leaveChannel(Session session) {
        String guildName = userGuildMap.get(session);
        Guild guild = guilds.get(guildName);
        if (guild != null) {
            String channelName = guild.getUserChannel(session);
            if (channelName != null) {
                guild.leaveChannel(session);
                String username = sessionUsernameMap.get(session);
                broadcast(guildName, channelName, "[System]: User '" + username + "' has left #" + channelName);
                broadcastUserList(guildName, channelName);
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

    private void broadcast(String guildName, String channelName, String message) {
        Guild guild = guilds.get(guildName);
        if (guild != null) {
            Set<Session> users = guild.getChannelUsers(channelName);
            if (users != null) {
                String timestamp = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
                for (Session s : users) {
                    sendMessage(s, timestamp + " " + message);
                }
            }
        }
    }

    private void broadcastToGuild(String guildName, String message) {
        Guild guild = guilds.get(guildName);
        if (guild != null) {
            guild.getChannels().forEach(channelName -> {
                guild.getChannelUsers(channelName).forEach(session -> sendMessage(session, message));
            });
        }
    }

    private void broadcastUserList(String guildName, String channelName) {
        Guild guild = guilds.get(guildName);
        if (guild != null) {
            Set<Session> users = guild.getChannelUsers(channelName);
            if (users != null) {
                String userListStr = users.stream()
                        .map(s -> sessionUsernameMap.get(s))
                        .filter(s -> s != null)
                        .reduce((s1, s2) -> s1 + "," + s2)
                        .orElse("");
                for (Session s : users) {
                    sendMessage(s, "USERLIST:" + userListStr);
                }
            }
        }
    }

    private void broadcastGuildChannelList(String guildName, Session session) {
        Guild guild = guilds.get(guildName);
        if (guild != null) {
            String channelListMessage = "CHANNELLIST:" + String.join(",", guild.getChannels());
            sendMessage(session, channelListMessage);
        }
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