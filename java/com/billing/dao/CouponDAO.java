package SupermarketBillingSystem.java.com.billing.dao;

import SupermarketBillingSystem.java.com.billing.model.Coupon;
import SupermarketBillingSystem.java.com.billing.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CouponDAO {
    public Coupon getCouponByCode(String code) {
        String query = "SELECT * FROM coupons WHERE code = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Coupon coupon = new Coupon();
                coupon.setId(resultSet.getInt("id"));
                coupon.setCode(resultSet.getString("code"));
                coupon.setDiscount(resultSet.getDouble("discount"));
                return coupon;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
