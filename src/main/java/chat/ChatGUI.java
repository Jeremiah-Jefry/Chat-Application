package chat;

import javax.swing.*;
import java.awt.*;

public class ChatGUI {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;

    public ChatGUI() {
        frame = new JFrame("Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        // Chat Area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(new JScrollPane(chatArea), gbc);

        // User List
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(new JScrollPane(userList), gbc);

        // Message Field
        messageField = new JTextField();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.add(messageField, gbc);

        // Send Button
        JButton sendButton = new JButton("Send");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        frame.add(sendButton, gbc);

        frame.setVisible(true);
    }

    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }

    public JTextField getMessageField() {
        return messageField;
    }

    public void addSendButtonListener(Runnable listener) {
        messageField.addActionListener(e -> listener.run());
    }

    public void updateUserList(String[] users) {
        userListModel.clear();
        for (String user : users) {
            userListModel.addElement(user);
        }
    }
}