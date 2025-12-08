package com.school.management.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LibraryBook {
    private String bookId;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private String publisher;
    private int publicationYear;
    private int totalCopies;
    private int availableCopies;
    private String location;
    private double price;
    private String barcode;
    private boolean isAvailable;

    public LibraryBook() {
        // Required for Firebase
    }

    public LibraryBook(String bookId, String title, String author, String isbn) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isAvailable = true;
    }

    // Getters and Setters
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("bookId", bookId);
        map.put("title", title);
        map.put("author", author);
        map.put("isbn", isbn);
        map.put("category", category);
        map.put("publisher", publisher);
        map.put("publicationYear", publicationYear);
        map.put("totalCopies", totalCopies);
        map.put("availableCopies", availableCopies);
        map.put("location", location);
        map.put("price", price);
        map.put("barcode", barcode);
        map.put("isAvailable", isAvailable);
        return map;
    }
}

// Book Issue Record
class BookIssue {
    private String issueId;
    private String bookId;
    private String studentId;
    private Date issueDate;
    private Date dueDate;
    private Date returnDate;
    private String status; // ISSUED, RETURNED, OVERDUE
    private double lateFee;
    private String issuedBy;

    public BookIssue() {}

    public BookIssue(String issueId, String bookId, String studentId) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.studentId = studentId;
        this.status = "ISSUED";
    }

    // Getters and Setters
    public String getIssueId() { return issueId; }
    public void setIssueId(String issueId) { this.issueId = issueId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getLateFee() { return lateFee; }
    public void setLateFee(double lateFee) { this.lateFee = lateFee; }

    public String getIssuedBy() { return issuedBy; }
    public void setIssuedBy(String issuedBy) { this.issuedBy = issuedBy; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("issueId", issueId);
        map.put("bookId", bookId);
        map.put("studentId", studentId);
        map.put("issueDate", issueDate);
        map.put("dueDate", dueDate);
        map.put("returnDate", returnDate);
        map.put("status", status);
        map.put("lateFee", lateFee);
        map.put("issuedBy", issuedBy);
        return map;
    }
}
