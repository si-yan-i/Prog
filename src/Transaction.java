import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class Transaction {

    private ArrayList<Expense> expenses;
    private final String DATA_FILE;

    public Transaction(String username) {
        expenses = new ArrayList<>();
        DATA_FILE = username + "_expenses_data.csv";
        loadFromFile();
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        saveToFile();
    }

    public boolean removeExpense(int index) {
        if (index < 0 || index >= expenses.size()) return false;
        expenses.remove(index);
        saveToFile();
        return true;
    }

    public boolean updateExpense(int index, Expense updated) {
        if (index < 0 || index >= expenses.size()) return false;
        expenses.set(index, updated);
        saveToFile();
        return true;
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

    public double getTotal() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    public double getTotalByCategory(ExpenseCategory cat) {
        return expenses.stream()
                .filter(e -> e.getCategory() == cat)
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE))) {
            pw.println("description,amount,date,category");
            for (Expense e : expenses) {
                pw.printf("\"%s\",%.2f,%s,%s%n",
                        e.getDescription().replace("\"", "\"\""),
                        e.getAmount(),
                        e.getDate(),
                        e.getCategory().name());
            }
        } catch (IOException ex) {
            System.err.println("Could not save expenses: " + ex.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] parts = parseCsvLine(line);
                if (parts.length < 4) continue;
                try {
                    String desc = parts[0];
                    double amt = Double.parseDouble(parts[1]);
                    String date = parts[2];
                    ExpenseCategory cat = ExpenseCategory.valueOf(parts[3]);
                    expenses.add(new Expense(desc, amt, date, cat));
                } catch (Exception ignore) {}
            }
        } catch (IOException ex) {
            System.err.println("Could not load expenses: " + ex.getMessage());
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuote = false;
        StringBuilder current = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') { inQuote = !inQuote; }
            else if (c == ',' && !inQuote) { result.add(current.toString()); current.setLength(0); }
            else { current.append(c); }
        }
        result.add(current.toString());
        return result.toArray(new String[0]);
    }

    public void exportToCSV(String path) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("Description,Amount,Date,Category");
            for (Expense e : expenses) {
                pw.printf("\"%s\",%.2f,%s,%s%n",
                        e.getDescription().replace("\"", "\"\""),
                        e.getAmount(),
                        e.getDate(),
                        e.getCategory().getLabel());
            }
        }
    }
}