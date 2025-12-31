package com.expensetracker.service;

import com.expensetracker.dao.TransactionDAO;
import com.expensetracker.model.Transaction;
import com.expensetracker.model.TransactionType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TransactionService {

    private final TransactionDAO transactionDAO;

    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
    }

    // ================== ADD ==================
    public void addTransaction(Transaction transaction) throws SQLException {
        validateTransaction(transaction);
        transactionDAO.addTransaction(transaction);
    }

    // ================== UPDATE ==================
    public void updateTransaction(Transaction transaction) throws SQLException {
        if (transaction.getId() <= 0) {
            throw new IllegalArgumentException("Invalid transaction ID");
        }
        validateTransaction(transaction);
        transactionDAO.updateTransaction(transaction);
    }

    // ================== DELETE ==================
    public void deleteTransaction(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid transaction ID");
        }
        transactionDAO.deleteTransaction(id);
    }

    // ================== GET ALL ==================
    public List<Transaction> getAllTransactions() throws SQLException {
        return transactionDAO.getAllTransactions();
    }

    // ================== FILTER ==================
    public List<Transaction> filterTransactions(
            LocalDate startDate,
            LocalDate endDate,
            String category,
            TransactionType type) throws SQLException {

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return transactionDAO.filterTransactions(startDate, endDate, category, type);
    }

    // ================== SUMMARY ==================
    public Map<String, Double> getSummary() throws SQLException {

        List<Transaction> transactions = transactionDAO.getAllTransactions();

        double totalIncome = 0;
        double totalExpense = 0;

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                totalIncome += t.getAmount();
            } else if (t.getType() == TransactionType.EXPENSE) {
                totalExpense += t.getAmount();
            }
        }

        double balance = totalIncome - totalExpense;

        Map<String, Double> summary = new HashMap<>();
        summary.put("income", totalIncome);
        summary.put("expense", totalExpense);
        summary.put("balance", balance);

        return summary;
    }

    // ================== VALIDATION ==================
    private void validateTransaction(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        if (transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (transaction.getDate() == null) {
            throw new IllegalArgumentException("Date is required");
        }

        if (transaction.getCategory() == null || transaction.getCategory().isBlank()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }

        if (transaction.getType() == null) {
            throw new IllegalArgumentException("Transaction type is required");
        }
    }
}
