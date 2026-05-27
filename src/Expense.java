public class Expense {
    private String description;
    private double amount;
    private String date;
    private ExpenseCategory category;

    public Expense(String description, double amount, String date, ExpenseCategory category) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public ExpenseCategory getCategory() { return category; }


    @Override
    public String toString() {
        return String.format("%-20s %-10.2f %-12s %-15s",
                description, amount, date, category.getLabel());
    }
}