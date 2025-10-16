package chat;

import java.net.URI;
import javax.swing.JTextField;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint
public class ChatClient {

    private Session session;
    private ChatGUI chatGUI;

    public ChatClient(String serverUri) {
        try {
            this.chatGUI = new ChatGUI();
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(serverUri));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        chatGUI.appendMessage("Connected to the chat server.");
    }

    @OnMessage
    public void onMessage(String message) {
        chatGUI.appendMessage(message);
    }

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JTextField getMessageField() {
        return chatGUI.getMessageField();
    }

    public ChatGUI getChatGUI() {
        return chatGUI;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java chat.ChatClient <username>");
            return;
        }

        String username = args[0];
        String serverUri = "ws://localhost:8080/websockets/chat/" + username;

        ChatClient client = new ChatClient(serverUri);
        client.getChatGUI().addSendButtonListener(() -> {
            String message = client.getMessageField().getText();
            if (!message.isEmpty()) {
                client.sendMessage(message);
                client.getMessageField().setText("");
            }
        });
    }
}