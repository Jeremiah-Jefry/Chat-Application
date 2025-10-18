package chat;

import javax.swing.*;
import java.awt.*;

public class ServerListPanel extends JPanel {
    private DefaultListModel<String> serverListModel;
    private JList<String> serverList;

    public ServerListPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(45, 52, 71)); // Deep Indigo

        serverListModel = new DefaultListModel<>();
        serverList = new JList<>(serverListModel);
        serverList.setBackground(new Color(45, 52, 71)); // Deep Indigo
        serverList.setForeground(new Color(220, 220, 255)); // Soft White
        serverList.setCellRenderer(new ServerListRenderer());
        serverList.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Add some dummy servers for now
        serverListModel.addElement("Server 1");
        serverListModel.addElement("Server 2");
        serverListModel.addElement("Server 3");

        add(new JScrollPane(serverList), BorderLayout.CENTER);
    }

    private static class ServerListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            if (isSelected) {
                label.setBackground(new Color(54, 57, 63));
            } else {
                label.setBackground(new Color(32, 34, 37));
            }
            return label;
        }
    }
}