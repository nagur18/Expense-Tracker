package com.expensetracker.ui;

import com.expensetracker.model.Transaction;
import com.expensetracker.model.TransactionType;
import com.expensetracker.service.TransactionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TransactionTablePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    private JComboBox<TransactionType> cmbTypeFilter;
    private JTextField txtCategoryFilter;
    private JTextField txtStartDate;
    private JTextField txtEndDate;

    private JButton btnFilter;
    private JButton btnRefresh;
    private JButton btnDelete;
    private JButton btnEdit;

    private TransactionService transactionService;

    public TransactionTablePanel(MainUI mainUI) {
        this.transactionService = new TransactionService();
        initializeUI();
        loadAllTransactions();
    }

    // ================= UI SETUP =================
    private void initializeUI() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Transactions"));

        add(createFilterPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);
    }

    // ================= FILTER PANEL =================
    private JPanel createFilterPanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        txtStartDate = new JTextField(8);
        txtEndDate = new JTextField(8);
        txtCategoryFilter = new JTextField(10);
        cmbTypeFilter = new JComboBox<>();
        cmbTypeFilter.addItem(null); // All
        cmbTypeFilter.addItem(TransactionType.INCOME);
        cmbTypeFilter.addItem(TransactionType.EXPENSE);

        btnFilter = new JButton("Filter");
        btnRefresh = new JButton("Refresh");

        panel.add(new JLabel("From (yyyy-mm-dd):"));
        panel.add(txtStartDate);
        panel.add(new JLabel("To:"));
        panel.add(txtEndDate);
        panel.add(new JLabel("Category:"));
        panel.add(txtCategoryFilter);
        panel.add(new JLabel("Type:"));
        panel.add(cmbTypeFilter);
        panel.add(btnFilter);
        panel.add(btnRefresh);

        btnFilter.addActionListener(e -> applyFilter());
        btnRefresh.addActionListener(e -> loadAllTransactions());

        return panel;
    }

    // ================= TABLE PANEL =================
    private JScrollPane createTablePanel() {

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Amount", "Date", "Category", "Type", "Notes"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // JTable is read-only
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);

        return new JScrollPane(table);
    }

    // ================= ACTION PANEL =================
    private JPanel createActionPanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Delete");

        panel.add(btnEdit);
        panel.add(btnDelete);

        btnDelete.addActionListener(e -> deleteSelected());
        btnEdit.addActionListener(e -> editSelected());

        return panel;
    }

    // ================= LOAD ALL =================
    void loadAllTransactions() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            populateTable(transactions);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // ================= FILTER =================
    private void applyFilter() {

        try {
            LocalDate start = txtStartDate.getText().isBlank()
                    ? null
                    : LocalDate.parse(txtStartDate.getText().trim());

            LocalDate end = txtEndDate.getText().isBlank()
                    ? null
                    : LocalDate.parse(txtEndDate.getText().trim());

            String category = txtCategoryFilter.getText().trim();
            TransactionType type = (TransactionType) cmbTypeFilter.getSelectedItem();

            List<Transaction> result =
                    transactionService.filterTransactions(start, end, category, type);

            populateTable(result);

        } catch (Exception ex) {
            showError("Invalid filter input");
        }
    }

    // ================= TABLE POPULATION =================
    private void populateTable(List<Transaction> transactions) {

        tableModel.setRowCount(0);

        if (transactions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No records found",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getAmount(),
                    t.getDate(),
                    t.getCategory(),
                    t.getType(),
                    t.getNotes()
            });
        }
    }

    // ================= DELETE =================
    private void deleteSelected() {

        int row = table.getSelectedRow();
        if (row == -1) {
            showError("Please select a record to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this transaction?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) tableModel.getValueAt(row, 0);

        try {
            transactionService.deleteTransaction(id);
            loadAllTransactions();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // ================= EDIT =================
    private void editSelected() {

        int row = table.getSelectedRow();
        if (row == -1) {
            showError("Please select a record to edit");
            return;
        }

        Transaction transaction = new Transaction(
                (int) tableModel.getValueAt(row, 0),
                (double) tableModel.getValueAt(row, 1),
                (LocalDate) tableModel.getValueAt(row, 2),
                (String) tableModel.getValueAt(row, 3),
                (TransactionType) tableModel.getValueAt(row, 4),
                (String) tableModel.getValueAt(row, 5)
        );

        // ✅ Correct way to access MainFrame
        Window window = SwingUtilities.getWindowAncestor(this);

        if (window instanceof MainUI mainUi) {
            mainUi.getTransactionFormPanel()
                     .loadTransactionForEdit(transaction);
        }
    }


    // ================= ERROR =================
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
