import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.JFrame;

public class ExpenseTracker {
    private static final Map<String, Transaction> userTransactions = new HashMap<>();
    private static final Map<String, Double> userBudgets = new HashMap<>();

    private static String currentUser;

    public static double getBudget() {
        return userBudgets.getOrDefault(currentUser, 0.0);
    }

    public static void setCurrentUser(String username) {
        currentUser = username;

        userTransactions.putIfAbsent(username, new Transaction(username));
        userBudgets.putIfAbsent(username, 0.0);
    }

    private static Transaction getTransaction() {
        return userTransactions.get(currentUser);
    }

    public static void addExpense(String description, double amount, String date, ExpenseCategory category) {
        getTransaction().addExpense(new Expense(description, amount, date, category));
    }

    public static boolean removeExpense(int index) {
        return getTransaction().removeExpense(index);
    }

    public static boolean updateExpense(int index, String desc, double amt, String date, ExpenseCategory cat) {
        return getTransaction().updateExpense(index, new Expense(desc, amt, date, cat));
    }


    public static void setBudget(double b) {
        userBudgets.put(currentUser, b);
    }

    public static double calculateTotal() {
        return getTransaction().getTotal();
    }



    public static List<Expense> getExpensesList() {
        return getTransaction().getExpenses();
    }


    public static Map<Integer, Double> getWeeklyTotals() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        WeekFields wf = WeekFields.of(Locale.getDefault());
        Map<Integer, Double> result = new TreeMap<>();
        for (Expense e : getTransaction().getExpenses()) {
            try {
                LocalDate d = LocalDate.parse(e.getDate(), fmt);
                int week = d.get(wf.weekOfWeekBasedYear());
                result.merge(week, e.getAmount(), Double::sum);
            } catch (Exception ignored) {}
        }
        return result;
    }

    public static void showChart() {
        if (getTransaction().getExpenses().isEmpty()) {
            System.out.println("No expenses to chart.");
            return;
        }
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Expense e : getTransaction().getExpenses()) {
            dataset.addValue(e.getAmount(), "Expenses", e.getDescription() + " (" + e.getDate() + ")");
        }
        JFreeChart barChart = ChartFactory.createBarChart(
                "Expenses Chart",
                "Expense",
                "Amount",
                dataset
        );
        JFrame frame = new JFrame("Expenses Chart");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 400));
        frame.add(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void exportToCSV(String path) throws java.io.IOException {
        getTransaction().exportToCSV(path);
    }
}