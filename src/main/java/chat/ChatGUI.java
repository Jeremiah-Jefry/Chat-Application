package chat;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class ChatGUI {
    private DiscordLayout discordLayout;
    private JList<String> chatPane;
    private DefaultListModel<String> chatListModel;
    private JTextField messageField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private ChannelListPanel channelListPanel;
    private JButton sendButton;

    public ChatGUI() {
        discordLayout = new DiscordLayout();
        channelListPanel = (ChannelListPanel) discordLayout.getChannelListPanel();

        // Chat Pane
        chatListModel = new DefaultListModel<>();
        chatPane = new JList<>(chatListModel);
        chatPane.setCellRenderer(new ChatMessageRenderer());
        chatPane.setBackground(new Color(54, 57, 63));
        JScrollPane chatScrollPane = new JScrollPane(chatPane);
        chatScrollPane.setBorder(null);

        // Message Field and Send Button
        messageField = new JTextField();
        messageField.setBackground(new Color(64, 68, 75));
        messageField.setForeground(Color.WHITE);
        sendButton = new JButton("Send");

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        discordLayout.getChatPanel().setLayout(new BorderLayout());
        discordLayout.getChatPanel().add(chatScrollPane, BorderLayout.CENTER);
        discordLayout.getChatPanel().add(messagePanel, BorderLayout.SOUTH);


        // User List
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setBackground(new Color(47, 49, 54));
        userList.setForeground(Color.WHITE);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setBorder(BorderFactory.createTitledBorder("Users"));
        discordLayout.getUserListPanel().setLayout(new BorderLayout());
        discordLayout.getUserListPanel().add(userScrollPane, BorderLayout.CENTER);
    }

    public void appendMessage(String message) {
        chatListModel.addElement(message);
    }

    public void clearChat() {
        chatListModel.clear();
    }

    public JTextField getMessageField() {
        return messageField;
    }

    public void addSendButtonListener(Runnable listener) {
        messageField.addActionListener(e -> listener.run());
        sendButton.addActionListener(e -> listener.run());
    }

    public void updateUserList(String[] users) {
        userListModel.clear();
        for (String user : users) {
            userListModel.addElement(user);
        }
    }

    public void updateChannelList(String[] channels) {
        channelListPanel.updateChannelList(channels);
    }

    public void addChannelSelectionListener(java.util.function.Consumer<String> listener) {
        channelListPanel.addChannelSelectionListener(listener);
    }

    public JFrame getFrame() {
        return discordLayout.getFrame();
    }
}