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
        chatPane.setBackground(Color.BLACK);
        JScrollPane chatScrollPane = new JScrollPane(chatPane);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());
        chatScrollPane.getViewport().setBackground(Color.BLACK);

        // Message Field and Send Button
        messageField = new JTextField();
        messageField.setBackground(new Color(30, 30, 30));
        messageField.setForeground(Color.WHITE);
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(67, 58, 85), 1), // Purple border
            BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding
        ));
        
        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(50, 50, 50));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        sendButton.setFocusPainted(false);
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        discordLayout.getChatPanel().setLayout(new BorderLayout());
        discordLayout.getChatPanel().add(chatScrollPane, BorderLayout.CENTER);
        discordLayout.getChatPanel().add(messagePanel, BorderLayout.SOUTH);


        // User List
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setBackground(Color.BLACK);
        userList.setForeground(Color.WHITE);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setBorder(BorderFactory.createTitledBorder(null, "Users", 
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
            javax.swing.border.TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 12),
            Color.WHITE));
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