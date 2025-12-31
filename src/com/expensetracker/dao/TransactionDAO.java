package com.expensetracker.dao;

import com.expensetracker.db.DBConnection;
import com.expensetracker.model.Transaction;
import com.expensetracker.model.TransactionType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // ================== INSERT ==================
    public void addTransaction(Transaction transaction) throws SQLException {

        String sql = """
            INSERT INTO transactions (amount, date, category, type, notes)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, transaction.getAmount());
            ps.setDate(2, Date.valueOf(transaction.getDate()));
            ps.setString(3, transaction.getCategory());
            ps.setString(4, transaction.getType().name());
            ps.setString(5, transaction.getNotes());

            ps.executeUpdate();
        }
    }

    // ================== UPDATE ==================
    public void updateTransaction(Transaction transaction) throws SQLException {

        String sql = """
            UPDATE transactions
            SET amount = ?, date = ?, category = ?, type = ?, notes = ?
            WHERE id = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, transaction.getAmount());
            ps.setDate(2, Date.valueOf(transaction.getDate()));
            ps.setString(3, transaction.getCategory());
            ps.setString(4, transaction.getType().name());
            ps.setString(5, transaction.getNotes());
            ps.setInt(6, transaction.getId());

            ps.executeUpdate();
        }
    }

    // ================== DELETE ==================
    public void deleteTransaction(int id) throws SQLException {

        String sql = "DELETE FROM transactions WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ================== SELECT ALL ==================
    public List<Transaction> getAllTransactions() throws SQLException {

        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT * FROM transactions ORDER BY date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        }
        return transactions;
    }

    // ================== FILTER ==================
    public List<Transaction> filterTransactions(
            LocalDate startDate,
            LocalDate endDate,
            String category,
            TransactionType type) throws SQLException {

        List<Transaction> transactions = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM transactions WHERE 1=1 ");

        if (startDate != null) sql.append("AND date >= ? ");
        if (endDate != null) sql.append("AND date <= ? ");
        if (category != null && !category.isBlank()) sql.append("AND category = ? ");
        if (type != null) sql.append("AND type = ? ");

        sql.append("ORDER BY date DESC");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int index = 1;

            if (startDate != null)
                ps.setDate(index++, Date.valueOf(startDate));

            if (endDate != null)
                ps.setDate(index++, Date.valueOf(endDate));

            if (category != null && !category.isBlank())
                ps.setString(index++, category);

            if (type != null)
                ps.setString(index++, type.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRowToTransaction(rs));
                }
            }
        }
        return transactions;
    }

    // ================== MAPPER ==================
    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {

        return new Transaction(
                rs.getInt("id"),
                rs.getDouble("amount"),
                rs.getDate("date").toLocalDate(),
                rs.getString("category"),
                TransactionType.valueOf(rs.getString("type")),
                rs.getString("notes")
        );
    }
}
