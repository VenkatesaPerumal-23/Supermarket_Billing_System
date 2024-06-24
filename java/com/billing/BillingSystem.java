package SupermarketBillingSystem.java.com.billing;

import SupermarketBillingSystem.java.com.billing.dao.*;
import SupermarketBillingSystem.java.com.billing.model.*;
import SupermarketBillingSystem.java.com.billing.report.DailySalesReportGenerator;
import SupermarketBillingSystem.java.com.billing.report.CustomerReportGenerator;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class BillingSystem {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        CustomerDAO customerDAO = new CustomerDAO();
        ProductDAO productDAO = new ProductDAO();
        CouponDAO couponDAO = new CouponDAO();
        PurchaseDetailsDAO purchaseDetailsDAO = new PurchaseDetailsDAO();
        PaymentsDAO paymentsDAO = new PaymentsDAO();

        DailySalesReportGenerator dailySalesReportGenerator = new DailySalesReportGenerator(purchaseDetailsDAO);
        CustomerReportGenerator customerReportGenerator = new CustomerReportGenerator(purchaseDetailsDAO, paymentsDAO);

        System.out.println("Welcome to the Supermarket Billing System!");

        // Simple login system
        System.out.println("Enter user type (admin/customer):");
        String userType = scanner.nextLine();

        while (true) {
            if (userType.equalsIgnoreCase("admin")) {
                showAdminMenu(customerDAO, productDAO, dailySalesReportGenerator, customerReportGenerator);
            } else if (userType.equalsIgnoreCase("customer")) {
                showCustomerMenu(customerDAO, productDAO, couponDAO, purchaseDetailsDAO, paymentsDAO);
            } else {
                System.out.println("Invalid user type. Please enter 'admin' or 'customer'.");
                userType = scanner.nextLine();
            }
        }
    }

    private static void showAdminMenu(CustomerDAO customerDAO, ProductDAO productDAO, DailySalesReportGenerator dailySalesReportGenerator, CustomerReportGenerator customerReportGenerator) {
        while (true) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Add Customer");
            System.out.println("2. Add Product");
            System.out.println("3. Generate Daily Sales Report");
            System.out.println("4. Generate Customer Report");
            System.out.println("5. Exit");
            System.out.println("Enter your choice:");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    addCustomer(customerDAO);
                    break;
                case 2:
                    addProduct(productDAO);
                    break;
                case 3:
                    dailySalesReportGenerator.generateDailySalesReport(getCurrentDate());
                    break;
                case 4:
                    customerReportGenerator.generateCustomerReport(getCurrentDate());
                    break;
                case 5:
                    System.out.println("Exiting Supermarket Billing System. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static void showCustomerMenu(CustomerDAO customerDAO, ProductDAO productDAO, CouponDAO couponDAO, PurchaseDetailsDAO purchaseDetailsDAO, PaymentsDAO paymentsDAO) {
        while (true) {
            System.out.println("\nCustomer Menu:");
            System.out.println("1. Buy Products");
            System.out.println("2. Exit");
            System.out.println("Enter your choice:");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    buyProducts(customerDAO, productDAO, couponDAO, purchaseDetailsDAO, paymentsDAO);
                    break;
                case 2:
                    System.out.println("Exiting Supermarket Billing System. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static void addCustomer(CustomerDAO customerDAO) {
        System.out.println("Enter customer name:");
        String name = scanner.nextLine();
        System.out.println("Enter customer email:");
        String email = scanner.nextLine();
        System.out.println("Enter customer phone:");
        String phone = scanner.nextLine();
        System.out.println("Enter customer address:");
        String address = scanner.nextLine();

        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(address);

        customerDAO.addCustomer(customer);
    }

    private static void addProduct(ProductDAO productDAO) {
        System.out.println("Enter product name:");
        String name = scanner.nextLine();
        System.out.println("Enter product price:");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.println("Enter product stock quantity:");
        int stockQuantity = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter product GST percentage:");
        double gstPercentage = Double.parseDouble(scanner.nextLine());

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setGstPercentage(gstPercentage);

        productDAO.addProduct(product);
    }

    private static void buyProducts(CustomerDAO customerDAO, ProductDAO productDAO, CouponDAO couponDAO, PurchaseDetailsDAO purchaseDetailsDAO, PaymentsDAO paymentsDAO) {
        System.out.println("Enter customer email:");
        String email = scanner.nextLine();
        Customer customer = customerDAO.getCustomerByEmail(email);
        if (customer == null) {
            System.out.println("Customer not found. Please add customer first.");
            return;
        }

        double totalBill = 0;
        double totalDiscount = 0;
        while (true) {
            System.out.println("Enter product name to buy or 'done' to finish:");
            String productName = scanner.nextLine();
            if (productName.equalsIgnoreCase("done")) {
                break;
            }

            Product product = productDAO.getProductByName(productName);
            if (product == null) {
                System.out.println("Product not found.");
                continue;
            }

            System.out.println("Enter quantity:");
            int quantity = Integer.parseInt(scanner.nextLine());
            if (quantity > product.getStockQuantity()) {
                System.out.println("Insufficient stock.");
                continue;
            }

            product.setStockQuantity(product.getStockQuantity() - quantity);
            productDAO.updateProduct(product);

            double totalPrice = product.getPrice() * quantity;
            System.out.println("Enter coupon code (if any) or press Enter to skip:");
            String couponCode = scanner.nextLine();
            double discount = 0;
            if (!couponCode.isEmpty()) {
                Coupon coupon = couponDAO.getCouponByCode(couponCode);
                if (coupon != null) {
                    discount = totalPrice * (coupon.getDiscount() / 100);
                    System.out.println("Discount applied: " + discount);
                } else {
                    System.out.println("Invalid coupon code.");
                }
            }

            double finalPrice = totalPrice - discount;
            totalBill += finalPrice;
            totalDiscount += discount;

            PurchaseDetail purchaseDetail = new PurchaseDetail();
            purchaseDetail.setCustomerId(customer.getId());
            purchaseDetail.setCustomerName(customer.getName());
            purchaseDetail.setProductId(product.getId());
            purchaseDetail.setProductName(product.getName());
            purchaseDetail.setQuantity(quantity);
            purchaseDetail.setTotalPrice(finalPrice);
            purchaseDetail.setDiscountApplied(discount);
            purchaseDetail.setPurchaseDate(new Timestamp(System.currentTimeMillis()));
            purchaseDetailsDAO.addPurchaseDetail(purchaseDetail);
        }
        System.out.println("\n-------------------------------------------");
        System.out.println("\nGenerating Invoice...");
        System.out.println("\n-------------------------------------------");
        System.out.println("Customer: " + customer.getName());
        System.out.println("Total Bill: Rs." + totalBill);
        System.out.println("Total Discount: Rs." + totalDiscount);
        System.out.println("Final Amount: Rs." + (totalBill - totalDiscount));
        System.out.println("\n-------------------------------------------");

        // Payment
        System.out.println("\nSelect Payment Method:");
        System.out.println("1. Cash");
        System.out.println("2. UPI");
        System.out.println("3. Debit Card");
        System.out.println("Enter your choice:");
        String mode = "";
        int paymentChoice = Integer.parseInt(scanner.nextLine());
        switch (paymentChoice) {
            case 1:
                mode = "Cash";
                System.out.println("Payment successful! Thank you for shopping with us!");
                break;
            case 2:
                mode = "UPI";
                System.out.println("Enter UPI ID:");
                String upiId = scanner.nextLine();
                if (upiId.contains("@")) {
                    System.out.println("Payment successful! Thank you for shopping with us!");
                } else {
                    System.out.println("Invalid UPI ID. Payment failed.");
                }
                break;
            case 3:
                mode = "Debit Card";
                System.out.println("Enter Debit Card Number:");
                String debitCardNumber = scanner.nextLine();
                if (debitCardNumber.matches("\\d{12}")) {
                    System.out.println("Payment successful! Thank you for shopping with us!");
                } else {
                    System.out.println("Invalid Debit Card Number. Payment failed.");
                }
                break;
            default:
                System.out.println("Invalid payment method.");
        }

        Payment payment = new Payment();
        payment.setCustomerId(customer.getId());
        payment.setCustomerName(customer.getName());
        payment.setPaymentAmount(totalBill - totalDiscount);
        payment.setPaymentMode(mode);
        payment.setPaymentTime(new Timestamp(System.currentTimeMillis()));
        paymentsDAO.addPayment(payment);
    }

    private static String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return currentDate.format(formatter);
    }
}
