package com.expensetracker.ui;

import com.expensetracker.model.Transaction;
import com.expensetracker.model.TransactionType;
import com.expensetracker.service.TransactionService;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;

public class TransactionFormPanel extends JPanel {

    private JTextField txtAmount;
    private JTextField txtCategory;
    private JTextArea txtNotes;
    private JComboBox<TransactionType> cmbType;
    private JFormattedTextField txtDate;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnClear;

    private final TransactionService transactionService;
    private int selectedTransactionId = -1;

    public TransactionFormPanel() {
        this.transactionService = new TransactionService();
        initializeUI();
    }

    // ================= UI SETUP =================
    private void initializeUI() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Add / Edit Transaction"));
        setPreferredSize(new Dimension(320, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Amount
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1;
        txtAmount = new JTextField();
        formPanel.add(txtAmount, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        txtDate = createDateField();
        formPanel.add(txtDate, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        txtCategory = new JTextField();
        formPanel.add(txtCategory, gbc);

        // Type
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Type:"), gbc);

        gbc.gridx = 1;
        cmbType = new JComboBox<>(TransactionType.values());
        formPanel.add(cmbType, gbc);

        // Notes
        gbc.gridx = 0; gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("Notes:"), gbc);

        gbc.gridx = 1;
        txtNotes = new JTextArea(3, 20);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(txtNotes), gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnClear = new JButton("Clear");

        btnUpdate.setEnabled(false);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnClear);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        registerActions();
    }

    // ================= DATE FIELD =================
    private JFormattedTextField createDateField() {
        try {
            MaskFormatter mask = new MaskFormatter("####-##-##");
            mask.setPlaceholderCharacter('_');
            return new JFormattedTextField(mask);
        } catch (ParseException e) {
            return new JFormattedTextField();
        }
    }

    // ================= ACTIONS =================
    private void registerActions() {
        btnAdd.addActionListener(e -> addTransaction());
        btnUpdate.addActionListener(e -> updateTransaction());
        btnClear.addActionListener(e -> clearForm());
    }

    // ================= ADD =================
    private void addTransaction() {
        try {
            Transaction t = buildTransactionFromForm();
            transactionService.addTransaction(t);
            JOptionPane.showMessageDialog(this, "Transaction added successfully");
            clearForm();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // ================= UPDATE =================
    private void updateTransaction() {
        try {
            Transaction t = buildTransactionFromForm();
            t.setId(selectedTransactionId);
            transactionService.updateTransaction(t);
            JOptionPane.showMessageDialog(this, "Transaction updated successfully");
            clearForm();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // ================= FORM → MODEL =================
    private Transaction buildTransactionFromForm() {

        double amount = Double.parseDouble(txtAmount.getText().trim());
        String category = txtCategory.getText().trim();
        TransactionType type = (TransactionType) cmbType.getSelectedItem();
        String notes = txtNotes.getText().trim();
        String dateText = txtDate.getText().trim();

        if (dateText.contains("_")) {
            throw new IllegalArgumentException("Enter date in yyyy-MM-dd format");
        }

        LocalDate date = LocalDate.parse(dateText);

        return new Transaction(amount, date, category, type, notes);
    }

    // ================= CLEAR =================
    private void clearForm() {
        txtAmount.setText("");
        txtCategory.setText("");
        txtNotes.setText("");
        txtDate.setValue(null);
        cmbType.setSelectedIndex(0);

        selectedTransactionId = -1;
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
    }

    // ================= LOAD FOR EDIT =================
    public void loadTransactionForEdit(Transaction t) {

        selectedTransactionId = t.getId();

        txtAmount.setText(String.valueOf(t.getAmount()));
        txtCategory.setText(t.getCategory());
        txtNotes.setText(t.getNotes());
        cmbType.setSelectedItem(t.getType());
        txtDate.setText(t.getDate().toString());

        btnAdd.setEnabled(false);
        btnUpdate.setEnabled(true);
    }
    private MainUI mainUI;

    public TransactionFormPanel(MainUI mainUI) {
        this.mainUI = mainUI;
        this.transactionService = new TransactionService();
        initializeUI();
    }

    

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
