package chat;

import javax.swing.*;
import java.awt.*;

public class DiscordLayout {
    private JFrame frame;
    private JPanel serverListPanel;
    private JPanel channelListPanel;
    private JPanel chatPanel;
    private JPanel userListPanel;

    public DiscordLayout() {
        frame = new JFrame("Chat Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());

        // Server List Panel
        serverListPanel = new ServerListPanel();
        serverListPanel.setPreferredSize(new Dimension(70, 0));
        frame.add(serverListPanel, BorderLayout.WEST);

        // Main Content Panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        frame.add(mainContentPanel, BorderLayout.CENTER);

        // Channel List Panel
        channelListPanel = new ChannelListPanel();
        channelListPanel.setPreferredSize(new Dimension(200, 0));
        mainContentPanel.add(channelListPanel, BorderLayout.WEST);

        // Chat and User List Panel
        JPanel chatAndUserPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(chatAndUserPanel, BorderLayout.CENTER);

        // Chat Panel
        chatPanel = new JPanel();
        chatPanel.setBackground(new Color(54, 57, 63));
        chatAndUserPanel.add(chatPanel, BorderLayout.CENTER);

        // User List Panel
        userListPanel = new JPanel();
        userListPanel.setBackground(new Color(47, 49, 54));
        userListPanel.setPreferredSize(new Dimension(200, 0));
        chatAndUserPanel.add(userListPanel, BorderLayout.EAST);

        frame.setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }

    public JPanel getServerListPanel() {
        return serverListPanel;
    }

    public JPanel getChannelListPanel() {
        return channelListPanel;
    }

    public JPanel getChatPanel() {
        return chatPanel;
    }

    public JPanel getUserListPanel() {
        return userListPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DiscordLayout::new);
    }
}