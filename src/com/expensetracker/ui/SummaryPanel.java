package com.expensetracker.ui;

import com.expensetracker.model.Transaction;
import com.expensetracker.model.TransactionType;
import com.expensetracker.service.TransactionService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SummaryPanel extends JPanel {

    private JLabel lblIncome;
    private JLabel lblExpense;
    private JLabel lblBalance;

    private PieChartPanel pieChartPanel;

    private final TransactionService transactionService;

    public SummaryPanel() {
        this.transactionService = new TransactionService();
        initializeUI();
        refreshSummary();
    }

    // ================= UI SETUP =================
    private void initializeUI() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Summary & Reports"));

        JPanel summaryBox = new JPanel(new GridLayout(1, 3, 15, 5));

        lblIncome = createSummaryLabel("Total Income: ₹0.00", new Color(0, 128, 0));
        lblExpense = createSummaryLabel("Total Expense: ₹0.00", Color.RED);
        lblBalance = createSummaryLabel("Balance: ₹0.00", Color.BLUE);

        summaryBox.add(lblIncome);
        summaryBox.add(lblExpense);
        summaryBox.add(lblBalance);

        pieChartPanel = new PieChartPanel();

        add(summaryBox, BorderLayout.NORTH);
        add(pieChartPanel, BorderLayout.CENTER);
    }

    // ================= LABEL STYLE =================
    private JLabel createSummaryLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(color);
        return label;
    }

    // ================= REFRESH =================
    public void refreshSummary() {
        try {
            updateSummaryValues();
            updateChart();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load summary",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= SUMMARY VALUES =================
    private void updateSummaryValues() throws Exception {

        Map<String, Double> summary = transactionService.getSummary();

        double income = summary.get("income");
        double expense = summary.get("expense");
        double balance = summary.get("balance");

        lblIncome.setText(String.format("Total Income: ₹%.2f", income));
        lblExpense.setText(String.format("Total Expense: ₹%.2f", expense));
        lblBalance.setText(String.format("Balance: ₹%.2f", balance));
    }

    // ================= PIE CHART DATA =================
    private void updateChart() throws Exception {

        List<Transaction> transactions = transactionService.getAllTransactions();
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.EXPENSE) {
                categoryTotals.merge(t.getCategory(), t.getAmount(), Double::sum);
            }
        }

        pieChartPanel.setData(categoryTotals);
    }

    // ================= INNER PIE CHART PANEL =================
    private static class PieChartPanel extends JPanel {

        private Map<String, Double> data = new HashMap<>();

        public void setData(Map<String, Double> data) {
            this.data = data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (data == null || data.isEmpty()) {
                g.drawString("No expense data to display", 20, 20);
                return;
            }

            double total = data.values().stream().mapToDouble(Double::doubleValue).sum();

            int x = 50;
            int y = 20;
            int width = 250;
            int height = 250;

            int startAngle = 0;

            Color[] colors = {
                    Color.RED, Color.BLUE, Color.GREEN,
                    Color.ORANGE, Color.MAGENTA, Color.CYAN
            };

            int i = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                int arcAngle = (int) Math.round((entry.getValue() / total) * 360);
                g.setColor(colors[i % colors.length]);
                g.fillArc(x, y, width, height, startAngle, arcAngle);
                startAngle += arcAngle;
                i++;
            }

            // Legend
            int legendX = 320;
            int legendY = 40;
            i = 0;

            for (String category : data.keySet()) {
                g.setColor(colors[i % colors.length]);
                g.fillRect(legendX, legendY, 15, 15);
                g.setColor(Color.BLACK);
                g.drawString(category, legendX + 20, legendY + 12);
                legendY += 25;
                i++;
            }
        }
    }
}
