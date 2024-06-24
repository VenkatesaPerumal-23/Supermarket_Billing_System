package SupermarketBillingSystem.java.com.billing.dao;

import SupermarketBillingSystem.java.com.billing.model.Payment;
import SupermarketBillingSystem.java.com.billing.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentsDAO {
    public static void addPayment(Payment payment) {
        String query = "INSERT INTO user_payments (customer_id, customer_name, payment_amount, payment_mode, payment_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, payment.getCustomerId());
            preparedStatement.setString(2, payment.getCustomerName());
            preparedStatement.setDouble(3, payment.getPaymentAmount());
            preparedStatement.setString(4, payment.getPaymentMode());
            preparedStatement.setTimestamp(5, payment.getPaymentTime());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT * FROM user_payments";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setCustomerId(rs.getInt("customer_id"));
                payment.setCustomerName(rs.getString("customer_name"));
                payment.setPaymentAmount(rs.getDouble("payment_amount"));
                payment.setPaymentMode(rs.getString("payment_mode"));
                payment.setPaymentTime(rs.getTimestamp("payment_time"));
                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }
}
