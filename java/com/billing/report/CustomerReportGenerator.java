package SupermarketBillingSystem.java.com.billing.report;

import SupermarketBillingSystem.java.com.billing.dao.PurchaseDetailsDAO;
import SupermarketBillingSystem.java.com.billing.dao.PaymentsDAO;
import SupermarketBillingSystem.java.com.billing.model.PurchaseDetail;
import SupermarketBillingSystem.java.com.billing.model.Payment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomerReportGenerator {

    private PurchaseDetailsDAO purchaseDetailsDAO;
    private PaymentsDAO paymentsDAO;

    public CustomerReportGenerator(PurchaseDetailsDAO purchaseDetailsDAO, PaymentsDAO paymentsDAO) {
        this.purchaseDetailsDAO = purchaseDetailsDAO;
        this.paymentsDAO = paymentsDAO;
    }

    public void generateCustomerReport(String date) {
        List<PurchaseDetail> purchaseDetails = purchaseDetailsDAO.getPurchaseDetailsByDate(date);
        Map<Integer, Double> customerTotalPurchases = new HashMap<>();
        Map<Integer, String> customerNames = new HashMap<>();
        Map<Integer, String> customerPaymentModes = new HashMap<>();

        for (PurchaseDetail detail : purchaseDetails) {
            int customerId = detail.getCustomerId();
            double finalPrice = detail.getTotalPrice() - detail.getDiscountApplied();
            customerTotalPurchases.merge(customerId, finalPrice, Double::sum);
            customerNames.put(customerId, detail.getCustomerName());
        }

        List<Payment> payments = paymentsDAO.getAllPayments();
        for (Payment payment : payments) {
            customerPaymentModes.put(payment.getCustomerId(), payment.getPaymentMode());
        }

        System.out.println("\t***************** Customer Report *****************");
        System.out.println("\n--------------------------------------------------------------------------------------");
        for (Map.Entry<Integer, Double> entry : customerTotalPurchases.entrySet()) {
            int customerId = entry.getKey();
            String customerName = customerNames.get(customerId);
            double totalPurchaseAmount = entry.getValue();
            String paymentMode = customerPaymentModes.get(customerId);

            System.out.printf("Customer: %s | Total Purchase Amount: Rs.%.2f | Payment Mode: %s\n",
                    customerName, totalPurchaseAmount, paymentMode);
        }
        System.out.println("----------------------------------------------------------------------------------------\n");
    }
}
