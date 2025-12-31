package com.expensetracker.ui;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {

    private TransactionFormPanel transactionFormPanel;
    private TransactionTablePanel transactionTablePanel;
    private SummaryPanel summaryPanel;

    public MainUI() {
        setTitle("Expense Tracker");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        transactionFormPanel = new TransactionFormPanel(this);
        transactionTablePanel = new TransactionTablePanel(this);
        summaryPanel = new SummaryPanel();

        add(transactionFormPanel, BorderLayout.WEST);
        add(transactionTablePanel, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);
    }

    public TransactionFormPanel getTransactionFormPanel() {
        return transactionFormPanel;
    }

    public void refreshAll() {
        transactionTablePanel.loadAllTransactions();
        summaryPanel.refreshSummary();
    }
}
