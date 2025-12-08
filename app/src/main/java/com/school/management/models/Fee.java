package com.school.management.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Fee {
    private String feeId;
    private String studentId;
    private String feeType; // TUITION, BUS, LIBRARY, EXAM, MISC
    private double amount;
    private double discount;
    private double totalAmount;
    private double paidAmount;
    private double balanceAmount;
    private String status; // PAID, PARTIAL, PENDING, OVERDUE
    private Date dueDate;
    private Date paidDate;
    private String paymentMethod; // CASH, CARD, ONLINE, CHEQUE
    private String transactionId;
    private String receiptNumber;
    private String academicYear;
    private String term;
    private String remarks;

    public Fee() {
        // Required for Firebase
    }

    public Fee(String feeId, String studentId, String feeType, double amount) {
        this.feeId = feeId;
        this.studentId = studentId;
        this.feeType = feeType;
        this.amount = amount;
        this.totalAmount = amount;
        this.balanceAmount = amount;
        this.status = "PENDING";
    }

    // Getters and Setters
    public String getFeeId() { return feeId; }
    public void setFeeId(String feeId) { this.feeId = feeId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getFeeType() { return feeType; }
    public void setFeeType(String feeType) { this.feeType = feeType; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    public double getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(double balanceAmount) { this.balanceAmount = balanceAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public Date getPaidDate() { return paidDate; }
    public void setPaidDate(Date paidDate) { this.paidDate = paidDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("feeId", feeId);
        map.put("studentId", studentId);
        map.put("feeType", feeType);
        map.put("amount", amount);
        map.put("discount", discount);
        map.put("totalAmount", totalAmount);
        map.put("paidAmount", paidAmount);
        map.put("balanceAmount", balanceAmount);
        map.put("status", status);
        map.put("dueDate", dueDate);
        map.put("paidDate", paidDate);
        map.put("paymentMethod", paymentMethod);
        map.put("transactionId", transactionId);
        map.put("receiptNumber", receiptNumber);
        map.put("academicYear", academicYear);
        map.put("term", term);
        map.put("remarks", remarks);
        return map;
    }
}
