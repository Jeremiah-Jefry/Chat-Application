package chat;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class ChatGUI {
    private JFrame frame;
    private JTextPane chatPane;
    private JTextField messageField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JList<String> channelList;
    private DefaultListModel<String> channelListModel;
    private JButton sendButton;

    public ChatGUI() {
        frame = new JFrame("Chat Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Channel List
        channelListModel = new DefaultListModel<>();
        channelList = new JList<>(channelListModel);
        JScrollPane channelScrollPane = new JScrollPane(channelList);
        channelScrollPane.setBorder(BorderFactory.createTitledBorder("Channels"));

        // Chat Pane and Message Field
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPanel.add(new JScrollPane(chatPane), BorderLayout.CENTER);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        this.sendButton = new JButton("Send");
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(this.sendButton, BorderLayout.EAST);
        chatPanel.add(messagePanel, BorderLayout.SOUTH);

        // User List
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setBorder(BorderFactory.createTitledBorder("Users"));

        // Main Split Pane
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatPanel, userScrollPane);
        rightSplitPane.setResizeWeight(0.8);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, channelScrollPane, rightSplitPane);
        mainSplitPane.setResizeWeight(0.2);

        frame.add(mainSplitPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void appendMessage(String message) {
        StyledDocument doc = chatPane.getStyledDocument();
        Style userStyle = chatPane.addStyle("UserStyle", null);
        StyleConstants.setForeground(userStyle, Color.ORANGE);

        Style regularStyle = chatPane.addStyle("RegularStyle", null);
        StyleConstants.setForeground(regularStyle, Color.WHITE);

        try {
            if (message.startsWith("[")) {
                doc.insertString(doc.getLength(), message + "\n", regularStyle);
            } else {
                String[] parts = message.split(":", 2);
                doc.insertString(doc.getLength(), parts[0] + ":", userStyle);
                doc.insertString(doc.getLength(), parts[1] + "\n", regularStyle);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void clearChat() {
        chatPane.setText("");
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
        channelListModel.clear();
        for (String channel : channels) {
            channelListModel.addElement(channel);
        }
    }

    public void addChannelSelectionListener(java.util.function.Consumer<String> listener) {
        channelList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedChannel = channelList.getSelectedValue();
                if (selectedChannel != null) {
                    clearChat();
                    listener.accept(selectedChannel);
                }
            }
        });
    }
}