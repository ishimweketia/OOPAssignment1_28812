package com.auca.feepayment;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FeePayment implements Identifiable, Registrable {

    private final String paymentId;
    private Student student;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String receiptNumber;

    public FeePayment(String paymentId, Student student, BigDecimal amount,
            LocalDate paymentDate, String paymentMethod,
            String receiptNumber) {
        this.paymentId = paymentId;
        this.student = student;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
    }

    @Override
    public String getId() {
        return paymentId;
    }

    @Override
    public String getDisplayName() {
        return "Fee payment - Receipt " + receiptNumber;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    @Override
    public void validate() throws ValidationException {
        if (paymentId == null
                || paymentId.trim().length() < MIN_ID_LENGTH
                || paymentId.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Payment ID must have 4 to 30 characters.");
        }

        if (student == null) {
            throw new ValidationException("Select a registered student.");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Payment amount must be greater than zero.");
        }

        if (paymentDate == null) {
            throw new ValidationException("Payment date is required.");
        }

        if (!"CASH".equals(paymentMethod)
                && !"BANK TRANSFER".equals(paymentMethod)
                && !"MOBILE MONEY".equals(paymentMethod)) {
            throw new ValidationException("Choose CASH, BANK TRANSFER, or MOBILE MONEY.");
        }

        if (receiptNumber == null || receiptNumber.isBlank()) {
            throw new ValidationException("Receipt number is required.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println(this);
    }

    @Override
    public String getRegistrationSummary() {
        String displayedAmount = amount == null ? "Not entered" : amount.toPlainString();
        return paymentId + " - " + getDisplayName() + " - " + displayedAmount;
    }

    @Override
    public String toString() {
        String studentLabel = student == null
                ? "Unassigned"
                : student.getStudentNumber() + " - " + student.getDisplayName();
        String displayedAmount = amount == null ? "Not entered" : amount.toPlainString();

        return "Payment ID: " + paymentId
                + "\nStudent: " + studentLabel
                + "\nAmount: " + displayedAmount
                + "\nPayment date: " + paymentDate
                + "\nPayment method: " + paymentMethod
                + "\nReceipt number: " + receiptNumber;
    }
}