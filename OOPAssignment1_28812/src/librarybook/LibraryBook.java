package com.auca.librarybook;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

public class LibraryBook implements Identifiable, Registrable {

    private final String bookId;
    private String isbn;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;

    public LibraryBook(String bookId, String isbn, String title,
            String author, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    @Override
    public String getId() {
        return bookId;
    }

    @Override
    public String getDisplayName() {
        return title;
    }

    @Override
    public boolean isActive() {
        return availableCopies > 0;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    @Override
    public void validate() throws ValidationException {
        if (bookId == null
                || bookId.trim().length() < MIN_ID_LENGTH
                || bookId.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Book ID must have 4 to 30 characters.");
        }

        if (isbn == null || isbn.isBlank()) {
            throw new ValidationException("ISBN is required.");
        }

        if (title == null || title.isBlank()) {
            throw new ValidationException("Book title is required.");
        }

        if (author == null || author.isBlank()) {
            throw new ValidationException("Author is required.");
        }

        if (totalCopies < 0) {
            throw new ValidationException("Total copies cannot be negative.");
        }

        if (availableCopies < 0 || availableCopies > totalCopies) {
            throw new ValidationException(
                    "Available copies must be between zero and total copies.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println(this);
    }

    @Override
    public String getRegistrationSummary() {
        return bookId + " - " + title + " (" + availableCopies + " available)";
    }

    @Override
    public String toString() {
        return "Book ID: " + bookId
                + "\nISBN: " + isbn
                + "\nTitle: " + title
                + "\nAuthor: " + author
                + "\nTotal copies: " + totalCopies
                + "\nAvailable copies: " + availableCopies;
    }
}