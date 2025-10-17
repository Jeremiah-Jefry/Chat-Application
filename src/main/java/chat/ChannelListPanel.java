package chat;

import javax.swing.*;
import java.awt.*;

public class ChannelListPanel extends JPanel {
    private DefaultListModel<String> channelListModel;
    private JList<String> channelList;

    public ChannelListPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(173, 216, 230)); // Baby Blue
        setBorder(BorderFactory.createTitledBorder("Channels"));

        channelListModel = new DefaultListModel<>();
        channelList = new JList<>(channelListModel);
        channelList.setBackground(new Color(173, 216, 230)); // Baby Blue
        channelList.setForeground(Color.BLACK);
        channelList.setCellRenderer(new ChannelListRenderer());

        add(new JScrollPane(channelList), BorderLayout.CENTER);
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
                    listener.accept(selectedChannel);
                }
            }
        });
    }

    private static class ChannelListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            if (isSelected) {
                label.setBackground(new Color(54, 57, 63));
            } else {
                label.setBackground(new Color(47, 49, 54));
            }
            return label;
        }
    }
}