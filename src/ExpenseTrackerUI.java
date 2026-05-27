import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;


public class ExpenseTrackerUI extends JFrame {

    private static final Color BG_PRIMARY = new Color(245, 246, 250);
    private static final Color BG_SECONDARY = new Color(243, 242, 240);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(220, 218, 213);
    private static final Color TEXT_PRIMARY = new Color(35, 35, 45);
    private static final Color TEXT_MUTED = new Color(120, 125, 140);
    private static final Color ACCENT_BLUE = new Color(47, 106, 229);
    private static final Color ACCENT_GREEN = new Color(25, 135, 84);
    private static final Color ACCENT_RED = new Color(220, 53, 69);
    private static final Color ACCENT_AMBER = new Color(133, 79, 11);


    private static final Color[] CAT_COLORS = {
            new Color(29, 158, 117),   // FOOD     - teal hahahhaah
            new Color(55, 138, 221),   // TRANSPORT - blue???
            new Color(212, 83, 126),   // SHOPPING  - pink
            new Color(226, 75, 74),    // HEALTH    - red
            new Color(186, 117, 23),   // UTILITY   - amber na lng
            new Color(136, 135, 128),  // OTHER     - gray
    };

    private JTextField descField, amtField, dateField, budgetField, searchField;
    private JComboBox<ExpenseCategory> catCombo;
    private JLabel totalLabel, budgetLabel, remainingLabel, countLabel;
    private JLabel statusLabel;
    private JPanel budgetBarPanel;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private ExpenseCategory activeFilter = null; 
    private JPanel weeklyPanel;
    private DonutChartPanel donutChart;
    private Toast toast;


    private int editingIndex = -1;
    private JButton addBtn;
    private String loggedInUser = "User";

    public ExpenseTrackerUI(String username) {
        super("CentSible: Expense Tracker");
        this.loggedInUser = (username != null && !username.isBlank()) ? username : "User";
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 680));
        setBackground(BG_PRIMARY);

        toast = new Toast(this);

        setLayout(new BorderLayout());
        JPanel mainWrap = new JPanel(new BorderLayout(0, 12));
        mainWrap.setBackground(BG_PRIMARY);
        mainWrap.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(buildTopBar());
        header.add(buildMetricsBar());

        mainWrap.add(header, BorderLayout.NORTH);

        JPanel leftPanel = buildLeftPanel();
        JPanel rightPanel = buildRightPanel();

        JSplitPane mid = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        mid.setOpaque(false);
        mid.setBorder(new EmptyBorder(12, 0, 0, 0));
        mid.setContinuousLayout(true);
        mid.setResizeWeight(0.24);
        mid.setDividerSize(10);
        mid.setOneTouchExpandable(true);
        mainWrap.add(mid, BorderLayout.CENTER);

        add(mainWrap);
        setResizable(true);
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        refresh();
    }

    private JPanel buildTopBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel title = new JLabel("💰 CentSible");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton chartBtn = smallBtn("📊 View Bar Chart", Color.BLUE);
        chartBtn.addActionListener(e -> ExpenseTracker.showChart());
        right.add(chartBtn);

        JButton exportBtn = styledBtn("⬇ Export CSV", ACCENT_BLUE, Color.BLACK);
        exportBtn.addActionListener(e -> exportCSV());
        right.add(exportBtn);

        // Logged-in user label
        JLabel userLbl = new JLabel("👤 " + loggedInUser);
        userLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLbl.setForeground(TEXT_MUTED);
        right.add(userLbl);

        // Logout button
        JButton logoutBtn = styledBtn("⎋ Logout", BG_SECONDARY, ACCENT_RED);
        logoutBtn.addActionListener(e -> handleLogout());
        right.add(logoutBtn);

        p.add(title, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private JPanel buildMetricsBar() {
        JPanel p = new JPanel(new GridLayout(1, 4, 10, 0));
        p.setOpaque(false);

        totalLabel = metricLabel("Total Spent", "₱0.00", TEXT_PRIMARY);
        budgetLabel = metricLabel("Budget", "—", TEXT_PRIMARY);
        remainingLabel = metricLabel("Remaining", "—", TEXT_PRIMARY);
        countLabel = metricLabel("Expenses", "0", TEXT_PRIMARY);

        p.add(metricCard("Total Spent", totalLabel));
        p.add(metricCard("Budget", budgetLabel));
        p.add(metricCard("Remaining", remainingLabel));
        p.add(metricCard("# Expenses", countLabel));
        return p;
    }

    private JLabel metricLabel(String key, String val, Color c) {
        JLabel l = new JLabel(val);
        l.setFont(new Font("SansSerif", Font.BOLD, 22));
        l.setForeground(c);
        return l;
    }

    private JPanel metricCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 14, 12, 14)));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(TEXT_MUTED);
        card.add(lbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);
        return card;
    }

    private JPanel buildLeftPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        p.add(buildAddPanel());
        p.add(Box.createVerticalStrut(10));
        p.add(buildBudgetPanel());
        p.add(Box.createVerticalStrut(10));
        p.add(buildWeeklyPanel());
        return p;
    }

    private JPanel buildAddPanel() {
        JPanel card = card("Add Expense");
        card.setBackground(new Color(240, 245, 250));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        descField = inputField("e.g. Grocery run");
        amtField = inputField("0.00");
        dateField = inputField(java.time.LocalDate.now().toString());
        catCombo = new JComboBox<>(ExpenseCategory.values());
        styleCombo(catCombo);

        card.add(formRow("Description", descField));
        card.add(Box.createVerticalStrut(6));
        card.add(formRow("Amount (₱)", amtField));
        card.add(Box.createVerticalStrut(6));
        card.add(formRow("Date (YYYY-MM-DD)", dateField));
        card.add(Box.createVerticalStrut(6));
        card.add(formRow("Category", catCombo));
        card.add(Box.createVerticalStrut(10));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRow.setOpaque(false);
        addBtn = styledBtn("✓ Add Expense", ACCENT_BLUE, Color.BLACK);
        JButton clearBtn = styledBtn("Clear", BG_SECONDARY, TEXT_PRIMARY);
        addBtn.addActionListener(e -> submitExpense());
        clearBtn.addActionListener(e -> cancelEdit());
        btnRow.add(addBtn);
        btnRow.add(clearBtn);
        card.add(btnRow);
        return card;

    }

    private JPanel buildBudgetPanel() {
        JPanel card = card("Budget");
        card.setBackground(new Color(255, 255, 255));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        budgetField = inputField("e.g. 5000.00");
        card.add(formRow("Monthly Budget (₱)", budgetField));
        card.add(Box.createVerticalStrut(6));

        JButton setBtn = styledBtn("Set Budget", ACCENT_BLUE, Color.BLACK);
        setBtn.addActionListener(e -> setBudget());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.add(setBtn);
        card.add(btnRow);
        card.add(Box.createVerticalStrut(8));

        budgetBarPanel = new JPanel();
        budgetBarPanel.setLayout(new BoxLayout(budgetBarPanel, BoxLayout.Y_AXIS));
        budgetBarPanel.setOpaque(false);
        budgetBarPanel.setVisible(false);
        card.add(budgetBarPanel);
        return card;
    }

    private JPanel buildWeeklyPanel() {
        JPanel card = card("Weekly Breakdown");
        card.setBackground(new Color(248, 247, 244));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        weeklyPanel = new JPanel();
        weeklyPanel.setLayout(new BoxLayout(weeklyPanel, BoxLayout.Y_AXIS));
        weeklyPanel.setOpaque(false);
        card.add(weeklyPanel);
        return card;
    }

    private JPanel buildRightPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);

        JPanel topRow = new JPanel(new BorderLayout(10, 0));
        topRow.setOpaque(false);
        donutChart = new DonutChartPanel();
        donutChart.setPreferredSize(new Dimension(200, 180));
        topRow.add(donutChart, BorderLayout.WEST);
        topRow.add(buildSearchFilterPanel(), BorderLayout.CENTER);

        p.add(topRow, BorderLayout.NORTH);
        p.add(buildTablePanel(), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildSearchFilterPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(BG_CARD);
        p.setOpaque(true);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 14, 12, 14)));

        searchField = inputField("Search expenses...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                refresh();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                refresh();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                refresh();
            }
        });
        p.add(formRow("Search", searchField), BorderLayout.NORTH);


        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filterRow.setOpaque(false);
        addFilterBtn(filterRow, "All", null);
        for (ExpenseCategory cat : ExpenseCategory.values()) {
            addFilterBtn(filterRow, cat.getLabel(), cat);
        }
        p.add(filterRow, BorderLayout.CENTER);


        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        statusLabel.setForeground(TEXT_MUTED);
        p.add(statusLabel, BorderLayout.SOUTH);
        return p;
    }

    private void addFilterBtn(JPanel row, String label, ExpenseCategory cat) {
        JToggleButton btn = new JToggleButton(label);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boolean selected = (cat == null && activeFilter == null) || cat == activeFilter;
        styleFilterBtn(btn, selected);
        btn.addActionListener(e -> {
            activeFilter = cat;
            row.getComponents();
            for (Component c : row.getComponents()) {
                if (c instanceof JToggleButton tb) {
                    Object tc = tb.getClientProperty("cat");
                    boolean sel = (cat == null && tc == null) || tc == cat;
                    tb.setSelected(sel);
                    styleFilterBtn(tb, sel);
                }
            }
            refresh();
        });
        btn.putClientProperty("cat", cat);
        btn.setSelected(selected);
        row.add(btn);
    }

    private void styleFilterBtn(JToggleButton btn, boolean selected) {
        if (selected) {
            btn.setBackground(ACCENT_BLUE);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ACCENT_BLUE, 1, true),
                    new EmptyBorder(3, 10, 3, 10)));
        } else {
            btn.setBackground(BG_SECONDARY);
            btn.setForeground(TEXT_MUTED);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(3, 10, 3, 10)));
        }
    }

    private JPanel buildTablePanel() {
        String[] cols = {"#", "Description", "Category", "Date", "Amount (₱)", "Actions"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return c == 5; // only the Actions column is interactive
            }
        };
        expenseTable = new JTable(tableModel);
        expenseTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        expenseTable.setRowHeight(34);
        expenseTable.setShowGrid(false);
        expenseTable.setIntercellSpacing(new Dimension(0, 4));
        expenseTable.setBackground(BG_PRIMARY);
        expenseTable.setForeground(TEXT_PRIMARY);
        expenseTable.setSelectionBackground(new Color(230, 241, 251));
        expenseTable.setSelectionForeground(TEXT_PRIMARY);
        expenseTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        expenseTable.getTableHeader().setForeground(TEXT_PRIMARY);
        expenseTable.getTableHeader().setBackground(BG_PRIMARY);
        expenseTable.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));

        expenseTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setForeground(isSelected ? TEXT_PRIMARY : TEXT_PRIMARY);
                label.setBackground(isSelected ? table.getSelectionBackground() : BG_PRIMARY);
                return label;
            }
        });


        expenseTable.getColumnModel().getColumn(0).setMaxWidth(36);
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        expenseTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        expenseTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        expenseTable.getColumnModel().getColumn(5).setMinWidth(160);
        expenseTable.getColumnModel().getColumn(5).setMaxWidth(160);


        expenseTable.getColumnModel().getColumn(5).setCellRenderer(new ActionRenderer());
        expenseTable.getColumnModel().getColumn(5).setCellEditor(new ActionEditor());


        expenseTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int col = expenseTable.columnAtPoint(e.getPoint());
                int row = expenseTable.rowAtPoint(e.getPoint());
                if (col != 5 || row < 0) return;

                // Find which button was hit inside the rendered cell
                Rectangle cellRect = expenseTable.getCellRect(row, col, false);
                int localX = e.getX() - cellRect.x;

                Object raw = tableModel.getValueAt(row, 5);
                int realIdx = (raw instanceof Integer) ? (Integer) raw : -1;
                if (realIdx < 0) return;

                int cellW = cellRect.width;
                if (localX < cellW / 2) {
                    editExpense(realIdx);
                } else {
                    deleteExpense(realIdx);
                }
            }
        });

        DefaultTableCellRenderer amtRenderer = new DefaultTableCellRenderer();
        amtRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        amtRenderer.setForeground(TEXT_PRIMARY);
        expenseTable.getColumnModel().getColumn(4).setCellRenderer(amtRenderer);

        JScrollPane scroll = new JScrollPane(expenseTable);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(0, 0, 0, 0)));
        scroll.setBackground(BG_PRIMARY);
        scroll.getViewport().setBackground(BG_PRIMARY);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_CARD);
        wrapper.setOpaque(true);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }


    private void refresh() {
        List<Expense> all = ExpenseTracker.getExpensesList();
        double total = ExpenseTracker.calculateTotal();
        double budget = ExpenseTracker.getBudget();


        totalLabel.setText(fmt(total));
        countLabel.setText(String.valueOf(all.size()));
        if (budget > 0) {
            budgetLabel.setText(fmt(budget));
            double rem = budget - total;
            remainingLabel.setText((rem < 0 ? "−" : "") + fmt(Math.abs(rem)));
            remainingLabel.setForeground(rem < 0 ? ACCENT_RED : ACCENT_GREEN);
            statusLabel.setText(rem < 0
                    ? "⚠ Over budget by " + fmt(-rem)
                    : "✓ Within budget — " + fmt(rem) + " remaining");
            statusLabel.setForeground(rem < 0 ? ACCENT_RED : ACCENT_GREEN);
        } else {
            budgetLabel.setText("—");
            remainingLabel.setText("—");
            remainingLabel.setForeground(TEXT_MUTED);
            statusLabel.setText(" ");
        }


        refreshBudgetBar(total, budget);


        refreshWeekly();


        donutChart.setData(all);
        donutChart.repaint();


        refreshTable(all);
    }

    private void refreshBudgetBar(double total, double budget) {
        budgetBarPanel.removeAll();
        if (budget <= 0) {
            budgetBarPanel.setVisible(false);
            budgetBarPanel.revalidate();
            return;
        }
        budgetBarPanel.setVisible(true);

        double pct = Math.min(total / budget, 1.0);
        Color fillColor = pct >= 1.0 ? ACCENT_RED : pct >= 0.8 ? ACCENT_AMBER : ACCENT_GREEN;

        JPanel barTrack = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_SECONDARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                int fillW = (int) (getWidth() * pct);
                if (fillW > 0) {
                    g2.setColor(fillColor);
                    g2.fill(new RoundRectangle2D.Float(0, 0, fillW, getHeight(), 8, 8));
                }
            }
        };
        barTrack.setPreferredSize(new Dimension(260, 8));
        barTrack.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        barTrack.setOpaque(false);

        JLabel barLabel = new JLabel(String.format("%.0f%% of ₱%.2f used", pct * 100, budget));
        barLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        barLabel.setForeground(TEXT_MUTED);

        budgetBarPanel.add(barTrack);
        budgetBarPanel.add(Box.createVerticalStrut(4));
        budgetBarPanel.add(barLabel);
        budgetBarPanel.revalidate();
        budgetBarPanel.repaint();
    }

    private void refreshWeekly() {
        weeklyPanel.removeAll();
        Map<Integer, Double> weeks = ExpenseTracker.getWeeklyTotals();
        if (weeks.isEmpty()) {
            JLabel none = new JLabel("No data yet");
            none.setFont(new Font("SansSerif", Font.PLAIN, 12));
            none.setForeground(TEXT_MUTED);
            weeklyPanel.add(none);
            weeklyPanel.revalidate();
            return;
        }
        double max = weeks.values().stream().mapToDouble(Double::doubleValue).max().orElse(1);
        for (Map.Entry<Integer, Double> entry : weeks.entrySet()) {
            JPanel row = new JPanel(new BorderLayout(6, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

            JLabel wk = new JLabel("Wk " + entry.getKey());
            wk.setFont(new Font("SansSerif", Font.PLAIN, 11));
            wk.setForeground(TEXT_MUTED);
            wk.setPreferredSize(new Dimension(42, 16));

            double pct = entry.getValue() / max;
            JPanel bar = new JPanel(null) {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(BG_SECONDARY);
                    g2.fill(new RoundRectangle2D.Float(0, 2, getWidth(), 6, 4, 4));
                    g2.setColor(ACCENT_BLUE);
                    g2.fill(new RoundRectangle2D.Float(0, 2, (int) (getWidth() * pct), 6, 4, 4));
                }
            };
            bar.setOpaque(false);

            JLabel amt = new JLabel("₱" + String.format("%.0f", entry.getValue()));
            amt.setFont(new Font("SansSerif", Font.BOLD, 11));
            amt.setForeground(TEXT_PRIMARY);
            amt.setPreferredSize(new Dimension(62, 16));
            amt.setHorizontalAlignment(SwingConstants.RIGHT);

            row.add(wk, BorderLayout.WEST);
            row.add(bar, BorderLayout.CENTER);
            row.add(amt, BorderLayout.EAST);
            weeklyPanel.add(row);
            weeklyPanel.add(Box.createVerticalStrut(5));
        }
        weeklyPanel.revalidate();
        weeklyPanel.repaint();
    }

    private void refreshTable(List<Expense> all) {
        tableModel.setRowCount(0);
        String search = searchField == null ? "" : searchField.getText().trim().toLowerCase();


        List<Expense> sorted = new ArrayList<>(all);
        sorted.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        int displayIdx = 1;
        for (int i = 0; i < sorted.size(); i++) {
            Expense e = sorted.get(i);
            if (activeFilter != null && e.getCategory() != activeFilter) continue;
            if (!search.isEmpty() && !e.getDescription().toLowerCase().contains(search)
                    && !e.getDate().contains(search)) continue;
            // Store real index for edit/delete
            int realIdx = all.indexOf(e);
            tableModel.addRow(new Object[]{
                    displayIdx++,
                    e.getDescription(),
                    e.getCategory().getLabel(),
                    e.getDate(),
                    String.format("%.2f", e.getAmount()),
                    realIdx   // passed to action buttons
            });
        }

    }


    private void submitExpense() {
        String desc = descField.getText().trim();
        String amtStr = amtField.getText().trim();
        String date = dateField.getText().trim();
        ExpenseCategory cat = (ExpenseCategory) catCombo.getSelectedItem();

        if (desc.isEmpty()) {
            toast.show("Please enter a description", ACCENT_RED);
            return;
        }
        double amt;
        try {
            amt = Double.parseDouble(amtStr);
        } catch (NumberFormatException ex) {
            toast.show("Enter a valid amount", ACCENT_RED);
            return;
        }
        if (amt < 0) {
            toast.show("Amount must be positive", ACCENT_RED);
            return;
        }
        if (date.isEmpty() || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            toast.show("Use date format YYYY-MM-DD", ACCENT_RED);
            return;
        }

        if (editingIndex >= 0) {
            ExpenseTracker.updateExpense(editingIndex, desc, amt, date, cat);
            toast.show("Expense updated", ACCENT_GREEN);
        } else {
            ExpenseTracker.addExpense(desc, amt, date, cat);
            toast.show("Expense added", ACCENT_GREEN);
        }
        cancelEdit();
        refresh();
    }

    private void editExpense(int realIndex) {
        List<Expense> all = ExpenseTracker.getExpensesList();
        if (realIndex < 0 || realIndex >= all.size()) return;
        Expense e = all.get(realIndex);
        descField.setText(e.getDescription());
        amtField.setText(String.valueOf(e.getAmount()));
        dateField.setText(e.getDate());
        catCombo.setSelectedItem(e.getCategory());
        editingIndex = realIndex;
        addBtn.setText("✓ Save Changes");
        descField.requestFocus();
        toast.show("Editing — make changes and save", ACCENT_AMBER);
    }

    private void deleteExpense(int realIndex) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove this expense?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            ExpenseTracker.removeExpense(realIndex);
            refresh();
            toast.show("Expense removed", ACCENT_AMBER);
        }
    }

    private void cancelEdit() {
        editingIndex = -1;
        addBtn.setText("✓ Add Expense");
        descField.setText("");
        amtField.setText("");
        dateField.setText(java.time.LocalDate.now().toString());
        catCombo.setSelectedIndex(0);
    }

    private void setBudget() {
        String txt = budgetField.getText().trim();
        try {
            double b = Double.parseDouble(txt);
            if (b < 0) throw new NumberFormatException();
            ExpenseTracker.setBudget(b);
            toast.show("Budget set to ₱" + String.format("%.2f", b), ACCENT_GREEN);
            refresh();
        } catch (NumberFormatException ex) {
            toast.show("Enter a valid budget amount", ACCENT_RED);
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Log Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            String user = loggedInUser;
            dispose();
            SwingUtilities.invokeLater(() -> new LogoutUI(user).setVisible(true));
        }
    }

    private void exportCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("expenses_export.csv"));
        int res = fc.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                ExpenseTracker.exportToCSV(fc.getSelectedFile().getAbsolutePath());
                toast.show("Exported to " + fc.getSelectedFile().getName(), ACCENT_GREEN);
            } catch (Exception ex) {
                toast.show("Export failed: " + ex.getMessage(), ACCENT_RED);
            }
        }
    }


    private String fmt(double v) {
        return String.format("₱%.2f", v);
    }

    private JPanel card(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 14, 12, 14)));

        if (title != null && !title.isEmpty()) {
            JLabel lbl = new JLabel(title.toUpperCase());
            lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
            lbl.setForeground(TEXT_MUTED);
            card.add(lbl);
            card.add(Box.createVerticalStrut(8));
        }
        return card;
    }

    private JPanel formRow(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(TEXT_MUTED);
        p.add(lbl);
        p.add(Box.createVerticalStrut(3));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setAlignmentX(LEFT_ALIGNMENT);
        p.add(field);
        return p;
    }

    private JTextField inputField(String placeholder) {
        JTextField f = new JTextField(18);
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBackground(BG_SECONDARY);
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(4, 8, 4, 8)));
        f.setToolTipText(placeholder);
        return f;
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cb.setBackground(BG_SECONDARY);
        cb.setForeground(TEXT_PRIMARY);
        cb.setBorder(new LineBorder(BORDER_COLOR, 1, true));
    }

    private JButton styledBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                new EmptyBorder(6, 14, 6, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    // actionnnn

    class ActionRenderer implements TableCellRenderer {
        private final JPanel p    = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        private final JButton edit = smallBtn("✎ Edit", ACCENT_BLUE);
        private final JButton del  = smallBtn("✕ Delete", ACCENT_RED);

        ActionRenderer() {
            p.setOpaque(true);
            p.add(edit);
            p.add(del);
        }

        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            p.setBackground(sel ? new Color(230, 241, 251) : BG_PRIMARY);
            edit.setBackground(BG_SECONDARY);
            del.setBackground(BG_SECONDARY);
            return p;
        }
    }


    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel   p    = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        private final JButton  edit = smallBtn("✎ Edit",   ACCENT_BLUE);
        private final JButton  del  = smallBtn("✕ Delete", ACCENT_RED);
        private int realIndex = -1;

        ActionEditor() {
            p.setOpaque(true);
            p.setBackground(new Color(230, 241, 251));
            // Buttons delegate to outer class methods via the stored realIndex.
            edit.addActionListener(e -> {
                int idx = realIndex;
                stopCellEditing();
                editExpense(idx);
            });
            del.addActionListener(e -> {
                int idx = realIndex;
                stopCellEditing();
                deleteExpense(idx);
            });
            p.add(edit);
            p.add(del);
        }

        public Component getTableCellEditorComponent(
                JTable t, Object v, boolean sel, int row, int col) {
            // Read the real list-index stored in the hidden column 5 value.
            Object raw = tableModel.getValueAt(row, 5);
            realIndex  = (raw instanceof Integer) ? (Integer) raw : -1;
            return p;
        }

        public Object getCellEditorValue() { return null; }

        /** Allow editing to start on the very first click (default needs two). */
        public boolean isCellEditable(java.util.EventObject e) { return true; }
        public boolean shouldSelectCell(java.util.EventObject e) { return true; }
    }

    private JButton smallBtn(String text, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 11));
        b.setForeground(fg);
        b.setBackground(BG_SECONDARY);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(2, 6, 2, 6)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }


    static class DonutChartPanel extends JPanel {
        private List<Expense> expenses = new ArrayList<>();

        DonutChartPanel() {
            setOpaque(false);
        }

        void setData(List<Expense> data) {
            this.expenses = data;
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
            if (total == 0) {
                g2.setColor(new Color(220, 218, 213));
                g2.setStroke(new BasicStroke(18, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(18, 18, getWidth() - 56, getHeight() - 36);
                return;
            }

            Map<ExpenseCategory, Double> totals = new LinkedHashMap<>();
            for (ExpenseCategory cat : ExpenseCategory.values()) totals.put(cat, 0.0);
            for (Expense e : expenses) totals.merge(e.getCategory(), e.getAmount(), Double::sum);

            int size = Math.min(getWidth() - 40, getHeight() - 20);
            int x = (getWidth() - size) / 2, y = (getHeight() - size) / 2;
            double start = -90;
            ExpenseCategory[] cats = ExpenseCategory.values();
            g2.setStroke(new BasicStroke(18, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < cats.length; i++) {
                double pct = totals.get(cats[i]) / total;
                if (pct < 0.001) continue;
                double sweep = pct * 360;
                g2.setColor(CAT_COLORS[i]);
                g2.drawArc(x + 9, y + 9, size - 18, size - 18, (int) start, (int) sweep);
                start += sweep;
            }


            g2.setColor(TEXT_PRIMARY);
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            String totalStr = String.format("₱%.0f", total);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(totalStr, getWidth() / 2 - fm.stringWidth(totalStr) / 2,
                    getHeight() / 2 + fm.getAscent() / 2 - 2);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(TEXT_MUTED);
            g2.drawString("total", getWidth() / 2 - g2.getFontMetrics().stringWidth("total") / 2,
                    getHeight() / 2 + fm.getAscent() / 2 + 12);

            int lx = 4, ly = getHeight() - 14 - (cats.length * 14);
            for (int i = 0; i < cats.length; i++) {
                if (totals.get(cats[i]) < 0.01) continue;
                g2.setColor(CAT_COLORS[i]);
                g2.fillRoundRect(lx, ly + i * 14, 8, 8, 2, 2);
                g2.setColor(TEXT_MUTED);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.drawString(cats[i].getLabel(), lx + 11, ly + i * 14 + 8);
            }
        }
    }

    static class Toast {
        private final JWindow window;
        private final JLabel label;
        private final JFrame parent;
        private javax.swing.Timer hideTimer;

        Toast(JFrame parent) {
            this.parent = parent;
            window = new JWindow(parent);
            label = new JLabel();
            label.setFont(new Font("SansSerif", Font.BOLD, 12));
            label.setForeground(Color.WHITE);
            label.setBorder(new EmptyBorder(8, 16, 8, 16));
            window.add(label);
            window.setOpacity(0.92f);
        }

        void show(String msg, Color bg) {
            label.setText(msg);
            label.setBackground(bg);
            window.getContentPane().setBackground(BG_PRIMARY);
            window.pack();
            Point loc = parent.getLocation();
            Dimension pSize = parent.getSize();
            window.setLocation(
                    loc.x + pSize.width - window.getWidth() - 24,
                    loc.y + pSize.height - window.getHeight() - 24);
            window.setVisible(true);
            if (hideTimer != null) hideTimer.stop();
            hideTimer = new javax.swing.Timer(2200, e -> window.setVisible(false));
            hideTimer.setRepeats(false);
            hideTimer.start();
        }
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}
