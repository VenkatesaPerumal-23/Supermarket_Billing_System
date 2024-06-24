package SupermarketBillingSystem.java.com.billing.dao;

import SupermarketBillingSystem.java.com.billing.model.PurchaseDetail;
import SupermarketBillingSystem.java.com.billing.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PurchaseDetailsDAO {
    public void addPurchaseDetail(PurchaseDetail purchaseDetail) {
        String query = "INSERT INTO purchase_details (customer_id, customer_name, product_id, product_name, quantity, total_price, discount_applied, purchase_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, purchaseDetail.getCustomerId());
            preparedStatement.setString(2, purchaseDetail.getCustomerName());
            preparedStatement.setInt(3, purchaseDetail.getProductId());
            preparedStatement.setString(4, purchaseDetail.getProductName());
            preparedStatement.setInt(5, purchaseDetail.getQuantity());
            preparedStatement.setDouble(6, purchaseDetail.getTotalPrice());
            preparedStatement.setDouble(7, purchaseDetail.getDiscountApplied());
            preparedStatement.setTimestamp(8, purchaseDetail.getPurchaseDate());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<PurchaseDetail> getPurchaseDetailsByDate(String date) {
        List<PurchaseDetail> purchaseDetails = new ArrayList<>();
        String query = "SELECT * FROM purchase_details WHERE DATE(purchase_date) = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                PurchaseDetail purchaseDetail = new PurchaseDetail();
                purchaseDetail.setId(resultSet.getInt("id"));
                purchaseDetail.setCustomerId(resultSet.getInt("customer_id"));
                purchaseDetail.setCustomerName(resultSet.getString("customer_name"));
                purchaseDetail.setProductId(resultSet.getInt("product_id"));
                purchaseDetail.setProductName(resultSet.getString("product_name"));
                purchaseDetail.setQuantity(resultSet.getInt("quantity"));
                purchaseDetail.setTotalPrice(resultSet.getDouble("total_price"));
                purchaseDetail.setDiscountApplied(resultSet.getDouble("discount_applied"));
                purchaseDetail.setPurchaseDate(resultSet.getTimestamp("purchase_date"));
                purchaseDetails.add(purchaseDetail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchaseDetails;
    }

    public List<PurchaseDetail> getAllPurchaseDetails() {
        List<PurchaseDetail> purchaseDetails = new ArrayList<>();
        String query = "SELECT * FROM purchase_details";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                PurchaseDetail detail = new PurchaseDetail();
                detail.setId(rs.getInt("id"));
                detail.setCustomerId(rs.getInt("customer_id"));
                detail.setCustomerName(rs.getString("customer_name"));
                detail.setProductId(rs.getInt("product_id"));
                detail.setProductName(rs.getString("product_name"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setTotalPrice(rs.getDouble("total_price"));
                detail.setDiscountApplied(rs.getDouble("discount_applied"));
                detail.setPurchaseDate(rs.getTimestamp("purchase_date"));
                purchaseDetails.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchaseDetails;
    }
}
