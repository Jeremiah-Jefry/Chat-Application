package chat;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessageRenderer extends JPanel implements ListCellRenderer<String> {
    private JLabel usernameLabel;
    private JLabel messageLabel;
    private JLabel timestampLabel;

    public ChatMessageRenderer() {
        setLayout(new BorderLayout());
        setBackground(new Color(152, 251, 152)); // Mint Green
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        usernameLabel = new JLabel();
        usernameLabel.setForeground(new Color(75, 0, 130)); // Indigo
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 12));

        messageLabel = new JLabel();
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        timestampLabel = new JLabel();
        timestampLabel.setForeground(Color.DARK_GRAY);
        timestampLabel.setFont(new Font("Arial", Font.PLAIN, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(usernameLabel, BorderLayout.WEST);
        topPanel.add(timestampLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(messageLabel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        String[] parts = value.split(" ", 2);
        if (parts.length == 2) {
            timestampLabel.setText(parts[0]);
            String[] messageParts = parts[1].split(":", 2);
            if (messageParts.length == 2) {
                usernameLabel.setText(messageParts[0]);
                messageLabel.setText("<html>" + messageParts[1].trim() + "</html>");
            } else {
                usernameLabel.setText("");
                messageLabel.setText("<html>" + parts[1] + "</html>");
            }
        } else {
            usernameLabel.setText("");
            messageLabel.setText("<html>" + value + "</html>");
            timestampLabel.setText("");
        }

        if (isSelected) {
            setBackground(new Color(64, 68, 75));
        } else {
            setBackground(new Color(54, 57, 63));
        }

        return this;
    }
}