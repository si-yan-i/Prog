import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


public class LogoutUI extends JFrame {


    private final Color BG_PRIMARY;
    private final Color BG_CARD;
    private final Color BORDER_COLOR;
    private final Color TEXT_PRIMARY;
    private final Color TEXT_MUTED;
    private static final Color ACCENT_BLUE  = new Color(24, 95, 165);
    private static final Color ACCENT_RED   = new Color(163, 45, 45);
    private final Color BG_SECONDARY;


    public LogoutUI(String username) {
        super("CentSible — Logged Out");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(360, 300));
        BG_PRIMARY = uiColor("Panel.background", "control", new Color(250, 250, 249));
        BG_SECONDARY = uiColor("TextField.background", "controlHighlight", new Color(243, 242, 240));
        BG_CARD = uiColor("Panel.background", "control", Color.WHITE);
        BORDER_COLOR = uiColor("Component.borderColor", "Separator.foreground", new Color(220, 218, 213));
        TEXT_PRIMARY = uiColor("Label.foreground", "TextField.foreground", new Color(28, 28, 26));
        TEXT_MUTED = uiColor("textInactiveText", "Label.disabledForeground", new Color(120, 118, 112));
        setBackground(BG_PRIMARY);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_PRIMARY);
        root.setBorder(new EmptyBorder(30, 30, 30, 30));
        add(root);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;


        JLabel logo = new JLabel("💰 CentSible", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 26));
        logo.setForeground(TEXT_PRIMARY);
        gbc.gridy  = 0;
        gbc.insets = new Insets(0, 0, 24, 0);
        root.add(logo, gbc);


        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(24, 24, 24, 24)));
        gbc.gridy  = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        root.add(card, gbc);


        JLabel icon = new JLabel("👋", SwingConstants.CENTER);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 36));
        icon.setAlignmentX(CENTER_ALIGNMENT);
        card.add(icon);
        card.add(Box.createVerticalStrut(10));

        String name = (username != null && !username.isBlank()) ? username : "User";
        JLabel heading = new JLabel("Goodbye, " + name + "!", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(TEXT_PRIMARY);
        heading.setAlignmentX(CENTER_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(6));

        JLabel sub = new JLabel("You have been successfully logged out.", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(CENTER_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(22));


        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(CENTER_ALIGNMENT);

        JButton loginAgainBtn = styledButton("Log back in", ACCENT_BLUE, Color.WHITE);
        loginAgainBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
        });

        JButton exitBtn = styledButton("Exit", BG_SECONDARY, TEXT_PRIMARY);
        exitBtn.addActionListener(e -> System.exit(0));

        btnRow.add(loginAgainBtn);
        btnRow.add(exitBtn);
        card.add(btnRow);

        pack();
        setLocationRelativeTo(null);
        setResizable(true);
    }


    private JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                new EmptyBorder(8, 18, 8, 18)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private static Color uiColor(String key1, String key2, Color fallback) {
        Color color = UIManager.getColor(key1);
        if (color != null) {
            return color;
        }
        color = UIManager.getColor(key2);
        return color != null ? color : fallback;
    }
}