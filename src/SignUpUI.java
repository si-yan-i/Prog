import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


public class SignUpUI extends JFrame {


    private final Color BG_PRIMARY;
    private final Color BG_SECONDARY;
    private final Color BG_CARD;
    private final Color BORDER_COLOR;
    private final Color TEXT_PRIMARY;
    private final Color TEXT_MUTED;
    private static final Color ACCENT_BLUE  = new Color(24, 95, 165);
    private static final Color ACCENT_GREEN = new Color(15, 110, 86);
    private static final Color ACCENT_RED   = new Color(163, 45, 45);

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JLabel         errorLabel;

    public SignUpUI() {
        super("CentSible — Create Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(380, 500));
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

        // ── Logo ─────────────────────────────────────────────────────────────
        JLabel logo = new JLabel("💰 CentSible", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 28));
        logo.setForeground(TEXT_PRIMARY);
        gbc.gridy  = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        root.add(logo, gbc);

        JLabel tagline = new JLabel("Create your account", SwingConstants.CENTER);
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tagline.setForeground(TEXT_MUTED);
        gbc.gridy  = 1;
        gbc.insets = new Insets(0, 0, 24, 0);
        root.add(tagline, gbc);


        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(24, 24, 24, 24)));
        gbc.gridy  = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        root.add(card, gbc);

        // Username
        card.add(fieldLabel("Username"));
        card.add(Box.createVerticalStrut(4));
        usernameField = styledTextField("Choose a username");
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));

        // Password
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        passwordField = styledPasswordField("Choose a password (min. 6 characters)");
        card.add(passwordField);
        card.add(Box.createVerticalStrut(14));

        // Confirm password
        card.add(fieldLabel("Confirm Password"));
        card.add(Box.createVerticalStrut(4));
        confirmField = styledPasswordField("Re-enter your password");
        card.add(confirmField);
        card.add(Box.createVerticalStrut(18));

        // Password rules hint
        JLabel rulesHint = new JLabel("Password must be at least 6 characters.");
        rulesHint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        rulesHint.setForeground(TEXT_MUTED);
        rulesHint.setAlignmentX(LEFT_ALIGNMENT);
        card.add(rulesHint);
        card.add(Box.createVerticalStrut(8));

        // Error / success label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setForeground(ACCENT_RED);
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(6));

        // Sign-up button
        JButton signUpBtn = styledButton("Create Account", ACCENT_GREEN, Color.WHITE);
        signUpBtn.setAlignmentX(LEFT_ALIGNMENT);
        signUpBtn.addActionListener(e -> handleSignUp());
        card.add(signUpBtn);
        card.add(Box.createVerticalStrut(12));

        // Divider
        card.add(divider());
        card.add(Box.createVerticalStrut(12));

        // Back to login link
        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        backRow.setOpaque(false);
        backRow.setAlignmentX(CENTER_ALIGNMENT);
        JLabel alreadyLbl = new JLabel("Already have an account?");
        alreadyLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        alreadyLbl.setForeground(TEXT_MUTED);
        JButton backBtn = linkButton("Log in");
        backBtn.addActionListener(e -> goBackToLogin(null));
        backRow.add(alreadyLbl);
        backRow.add(backBtn);
        card.add(backRow);

        // Enter key on confirm field submits
        confirmField.addActionListener(e -> handleSignUp());

        pack();
        setLocationRelativeTo(null);
        setResizable(true);
    }


    private void handleSignUp() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmField.getPassword());

        // Validation
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("Please fill in all fields.", ACCENT_RED);
            return;
        }
        if (username.length() < 3) {
            showError("Username must be at least 3 characters.", ACCENT_RED);
            return;
        }
        if (!username.matches("[a-zA-Z0-9_]+")) {
            showError("Username: letters, numbers, and _ only.", ACCENT_RED);
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters.", ACCENT_RED);
            return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match.", ACCENT_RED);
            confirmField.setText("");
            confirmField.requestFocus();
            return;
        }
        if (UserStore.exists(username)) {
            showError("Username \"" + username + "\" is already taken.", ACCENT_RED);
            usernameField.requestFocus();
            return;
        }

        // Register
        UserStore.register(username, password);
        showError("✓ Account created! Redirecting to login…", ACCENT_GREEN);


        javax.swing.Timer delay = new javax.swing.Timer(1200, e -> goBackToLogin(username));
        delay.setRepeats(false);
        delay.start();
    }

    /** Closes this window and opens LoginUI, optionally pre-filling the username. */
    private void goBackToLogin(String prefillUsername) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            LoginUI login = new LoginUI();
            if (prefillUsername != null && !prefillUsername.isBlank()) {
                login.prefillUsername(prefillUsername);
            }
            login.setVisible(true);
        });
    }

    private void showError(String message, Color color) {
        errorLabel.setText(message);
        errorLabel.setForeground(color);
    }



    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField styledTextField(String placeholder) {
        JTextField f = new JTextField(20);
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBackground(BG_SECONDARY);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        f.setToolTipText(placeholder);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        f.setAlignmentX(LEFT_ALIGNMENT);
        return f;
    }

    private JPasswordField styledPasswordField(String placeholder) {
        JPasswordField f = new JPasswordField(20);
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBackground(BG_SECONDARY);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        f.setToolTipText(placeholder);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        f.setAlignmentX(LEFT_ALIGNMENT);
        return f;
    }

    private JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                new EmptyBorder(9, 20, 9, 20)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private JButton linkButton(String text) {
        JButton btn = new JButton("<html><u>" + text + "</u></html>");
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(ACCENT_BLUE);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 0, 0, 0));
        return btn;
    }

    private JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
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