package chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint
public class ChatClient {

    private Session session;

    public ChatClient(String serverUri) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(serverUri));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to the chat server.");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
    }

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java chat.ChatClient <username>");
            return;
        }

        String username = args[0];
        String serverUri = "ws://localhost:8080/websockets/chat/" + username;

        try {
            ChatClient client = new ChatClient(serverUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter messages to send (type 'quit' to exit):");
            String line;
            while ((line = reader.readLine()) != null) {
                if ("quit".equalsIgnoreCase(line)) {
                    break;
                }
                client.sendMessage(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}