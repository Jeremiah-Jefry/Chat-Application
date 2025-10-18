package chat;

import com.formdev.flatlaf.FlatDarkLaf;
import java.net.URI;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

@ClientEndpoint
public class ChatClient {

    private Session session;
    private ChatGUI chatGUI;

    public ChatClient(String serverUri) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                this.chatGUI = new ChatGUI();
            });
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(serverUri));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        SwingUtilities.invokeLater(() -> chatGUI.appendMessage("Connected to the chat server."));
    }

    @OnMessage
    public void onMessage(String message) {
        if (message.startsWith("USERLIST:")) {
            updateUsers(message.substring(9));
        } else if (message.startsWith("CHANNELLIST:")) {
            chatGUI.updateChannelList(message.substring(12).split(","));
        } else {
            chatGUI.appendMessage(message);
        }
    }

    private void updateUsers(String userList) {
        chatGUI.updateUserList(userList.isEmpty() ? new String[0] : userList.split(","));
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
        FlatDarkLaf.setup();

        if (args.length != 1) {
            System.out.println("Usage: java chat.ChatClient <username>");
            return;
        }

        String username = args[0];
        String serverUri = "ws://localhost:8080/websockets/chat/" + username;

        ChatClient client = new ChatClient(serverUri);
        client.getChatGUI().getFrame().setVisible(true);
        client.getChatGUI().addSendButtonListener(() -> {
            String message = client.getMessageField().getText();
            if (!message.isEmpty()) {
                client.sendMessage(message);
                client.getMessageField().setText("");
            }
        });

        client.getChatGUI().addChannelSelectionListener(channel -> {
            if (channel != null) {
                client.sendMessage("/join " + channel);
            }
        });
    }
}