package chat;

import jakarta.websocket.Session;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Guild {
    private String name;
    private Map<String, Set<Session>> channelUsersMap = new ConcurrentHashMap<>();
    private Map<Session, String> userChannelMap = new ConcurrentHashMap<>();

    public Guild(String name) {
        this.name = name;
        channelUsersMap.put("general", ConcurrentHashMap.newKeySet());
    }

    public String getName() {
        return name;
    }

    public Set<String> getChannels() {
        return channelUsersMap.keySet();
    }

    public Set<Session> getChannelUsers(String channel) {
        return channelUsersMap.get(channel);
    }

    public void joinChannel(String channel, Session session) {
        leaveChannel(session);
        channelUsersMap.computeIfAbsent(channel, k -> ConcurrentHashMap.newKeySet()).add(session);
        userChannelMap.put(session, channel);
    }

    public void leaveChannel(Session session) {
        String currentChannel = userChannelMap.remove(session);
        if (currentChannel != null) {
            channelUsersMap.get(currentChannel).remove(session);
        }
    }

    public String getUserChannel(Session session) {
        return userChannelMap.get(session);
    }
}