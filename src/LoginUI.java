import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;


public class LoginUI extends JFrame {


    private static final Color BG_PRIMARY   = new Color(250, 250, 249);
    private static final Color BG_SECONDARY = new Color(243, 242, 240);
    private static final Color BG_CARD      = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(220, 218, 213);
    private static final Color TEXT_PRIMARY = new Color(28, 28, 26);
    private static final Color TEXT_MUTED   = new Color(120, 118, 112);
    private static final Color ACCENT_BLUE  = new Color(24, 95, 165);
    private static final Color ACCENT_RED   = new Color(163, 45, 45);

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;

    public LoginUI() {
        super("CentSible — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 520);
        setMinimumSize(new Dimension(380, 460));
        setLocationRelativeTo(null);
        setResizable(false);
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
        logo.setFont(new Font("SansSerif", Font.BOLD, 28));
        logo.setForeground(TEXT_PRIMARY);
        gbc.gridy  = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        root.add(logo, gbc);

        JLabel tagline = new JLabel("Track smarter. Spend wiser.", SwingConstants.CENTER);
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
        usernameField = styledTextField("Enter your username");
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));

        // Password
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        passwordField = styledPasswordField("Enter your password");
        card.add(passwordField);
        card.add(Box.createVerticalStrut(18));

        // Error label (hidden until needed)
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setForeground(ACCENT_RED);
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(6));

        // Login button
        JButton loginBtn = styledButton("Login", ACCENT_BLUE, Color.WHITE);
        loginBtn.setAlignmentX(LEFT_ALIGNMENT);
        loginBtn.addActionListener(e -> handleLogin());
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(16));

        // Divider
        card.add(divider());
        card.add(Box.createVerticalStrut(14));

        // Sign-up link row
        JPanel signUpRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        signUpRow.setOpaque(false);
        signUpRow.setAlignmentX(CENTER_ALIGNMENT);
        JLabel noAccountLbl = new JLabel("Don't have an account?");
        noAccountLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        noAccountLbl.setForeground(TEXT_MUTED);
        JButton signUpBtn = linkButton("Create one");
        signUpBtn.addActionListener(e -> openSignUp());
        signUpRow.add(noAccountLbl);
        signUpRow.add(signUpBtn);
        card.add(signUpRow);

        // Allow Enter key to submit from either field
        ActionListener enterAction = e -> handleLogin();
        usernameField.addActionListener(enterAction);
        passwordField.addActionListener(enterAction);

    }


    public void prefillUsername(String username) {
        usernameField.setText(username);
        passwordField.requestFocus();
    }


    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in both fields.");
            return;
        }

        // changes hereee
        if (UserStore.authenticate(username, password)) {
            ExpenseTracker.setCurrentUser(username);

            dispose();
            SwingUtilities.invokeLater(() -> new ExpenseTrackerUI(username).setVisible(true));
        } else {
            showError("Invalid username or password.");
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    private void openSignUp() {
        dispose();
        SwingUtilities.invokeLater(() -> new SignUpUI().setVisible(true));
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }


    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField styledTextField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBackground(BG_SECONDARY);
        f.setForeground(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        f.setToolTipText(placeholder);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        f.setAlignmentX(LEFT_ALIGNMENT);
        return f;
    }

    private JPasswordField styledPasswordField(String placeholder) {
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBackground(BG_SECONDARY);
        f.setForeground(TEXT_PRIMARY);
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


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}