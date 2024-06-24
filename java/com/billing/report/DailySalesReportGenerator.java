package SupermarketBillingSystem.java.com.billing.report;

import SupermarketBillingSystem.java.com.billing.dao.PurchaseDetailsDAO;
import SupermarketBillingSystem.java.com.billing.model.PurchaseDetail;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailySalesReportGenerator {

    private PurchaseDetailsDAO purchaseDetailsDAO;

    public DailySalesReportGenerator(PurchaseDetailsDAO purchaseDetailsDAO) {
        this.purchaseDetailsDAO = purchaseDetailsDAO;
    }

    public void generateDailySalesReport(String date) {
        List<PurchaseDetail> purchaseDetails = purchaseDetailsDAO.getPurchaseDetailsByDate(date);

        Map<String, Double> productSales = new HashMap<>();
        for (PurchaseDetail detail : purchaseDetails) {
            productSales.merge(detail.getProductName(), detail.getTotalPrice(), Double::sum);
        }

        System.out.println("*******Daily Sales Report******** - " + LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        System.out.println("---------------------------------------------------");
        System.out.printf("%-20s %-20s\n", "Product Name", "Total Sales Amount");
        System.out.println("---------------------------------------------------");

        for (Map.Entry<String, Double> entry : productSales.entrySet()) {
            System.out.printf("%-20s Rs.%-20.2f\n", entry.getKey(), entry.getValue());
        }

        System.out.println("---------------------------------------------------");
    }
}
