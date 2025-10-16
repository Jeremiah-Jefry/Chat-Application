package chat;

import javax.swing.*;
import java.awt.*;

public class ChatGUI {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;

    public ChatGUI() {
        frame = new JFrame("Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        messageField = new JTextField();
        frame.add(messageField, BorderLayout.SOUTH);

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
}