package com.auca.bookloan;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import java.time.LocalDate;

public class BookLoan implements Identifiable, Registrable {

    private final String loanId;
    private Student student;
    private LibraryBook book;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;

    public BookLoan(String loanId, Student student, LibraryBook book,
            LocalDate borrowedDate, LocalDate dueDate,
            LocalDate returnedDate) {
        this.loanId = loanId;
        this.student = student;
        this.book = book;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnedDate = returnedDate;
    }

    @Override
    public String getId() {
        return loanId;
    }

    @Override
    public String getDisplayName() {
        String studentName = student == null ? "Unassigned" : student.getDisplayName();
        String bookTitle = book == null ? "Unassigned" : book.getDisplayName();
        return studentName + " - " + bookTitle;
    }

    @Override
    public boolean isActive() {
        return returnedDate == null;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LibraryBook getBook() {
        return book;
    }

    public void setBook(LibraryBook book) {
        this.book = book;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDate borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDate returnedDate) {
        this.returnedDate = returnedDate;
    }

    @Override
    public void validate() throws ValidationException {
        if (loanId == null
                || loanId.trim().length() < MIN_ID_LENGTH
                || loanId.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Loan ID must have 4 to 30 characters.");
        }

        if (student == null) {
            throw new ValidationException("Select a registered student.");
        }

        if (book == null) {
            throw new ValidationException("Select a registered library book.");
        }

        if (borrowedDate == null) {
            throw new ValidationException("Borrowed date is required.");
        }

        if (dueDate == null || !dueDate.isAfter(borrowedDate)) {
            throw new ValidationException("Due date must be after the borrowed date.");
        }

        if (returnedDate != null && returnedDate.isBefore(borrowedDate)) {
            throw new ValidationException("Return date cannot be before the borrowed date.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println(this);
    }

    @Override
    public String getRegistrationSummary() {
        String status = isActive() ? "ON LOAN" : "RETURNED";
        return loanId + " - " + getDisplayName() + " - " + status;
    }

    @Override
    public String toString() {
        String studentLabel = student == null
                ? "Unassigned"
                : student.getStudentNumber() + " - " + student.getDisplayName();

        String bookLabel = book == null
                ? "Unassigned"
                : book.getId() + " - " + book.getDisplayName();

        return "Loan ID: " + loanId
                + "\nStudent: " + studentLabel
                + "\nBook: " + bookLabel
                + "\nBorrowed date: " + borrowedDate
                + "\nDue date: " + dueDate
                + "\nReturned date: " + (returnedDate == null ? "Not yet returned" : returnedDate);
    }
}