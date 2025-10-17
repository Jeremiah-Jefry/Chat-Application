package chat;

import com.formdev.flatlaf.FlatDarkLaf;
import java.net.URI;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
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
            String userList = message.substring(9);
            if (userList.isEmpty()) {
                chatGUI.updateUserList(new String[0]);
            } else {
                String[] users = userList.split(",");
                chatGUI.updateUserList(users);
            }
        } else if (message.startsWith("CHANNELLIST:")) {
            String[] channels = message.substring(12).split(",");
            chatGUI.updateChannelList(channels);
        } else {
            chatGUI.appendMessage(message);
        }
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
                if (message.startsWith("/msg")) {
                    client.sendMessage(message);
                } else {
                    client.sendMessage(message);
                }
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