package com.auca.main;

import com.auca.bookloan.BookLoan;
import com.auca.exceptions.PrerequisiteMissingException;
import com.auca.exceptions.ValidationException;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.auca.main.Forms.*;

final class LibraryActions {

    private final Main app;

    LibraryActions(Main app) {
        this.app = app;
    }

    void returnBook() throws Cancelled, ValidationException, PrerequisiteMissingException {
        List<BookLoan> active = new ArrayList<>();
        for (BookLoan loan : app.records.loans) {
            if (loan.isActive()) {
                active.add(loan);
            }
        }

        BookLoan loan;
        try {
            loan = app.recordSelection.select("active Book Loan", active, null);
        } catch (Back exception) {
            return;
        }

        LocalDate returned;
        try {
            returned = app.consoleInput.ask("Return date (YYYY-MM-DD)", input -> {
                LocalDate value = app.consoleInput.parseDate(input);
                if (value.isBefore(loan.getBorrowedDate())) {
                    throw new ValidationException("Return date cannot be before borrowing.");
                }
                return value;
            });
        } catch (Back exception) {
            return;
        }

        LibraryBook book = loan.getBook();
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new ValidationException("The book's stock counts do not allow this return.");
        }
        System.out.println(loan.getRegistrationSummary() + " | Return date: " + returned);
        app.consoleInput.confirm("Confirm return");

        loan.setReturnedDate(returned);
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        System.out.println("Book returned. Available copies: " + book.getAvailableCopies());
    }

    void borrowBook() throws Cancelled {
        Form form = app.forms.new Form("Book Loan", 2, 4);
        Field<String> id = app.registration.addId(form, "Loan ID");
        Field<Student> student = form.select("Student", app.records.students, app.registration::registerStudent);
        Field<LibraryBook> book = form.add("Book", () -> {
            LibraryBook selected = app.recordSelection.select("Library Book", app.records.books, app.registration::registerBook);
            app.records.checkAvailable(selected);
            return selected;
        });
        Field<LocalDate> borrowed = form.add("Borrowed date (YYYY-MM-DD)", app.consoleInput.date(false, false));
        Field<LocalDate> due = form.add("Due date (YYYY-MM-DD)",
                input -> app.consoleInput.dueDate(input, borrowed.value));
        form.fixed("Returned date", "Not yet returned");

        BookLoan loan = form.complete(() -> {
            app.records.checkAvailable(book.value);
            return new BookLoan(id.value, student.value, book.value, borrowed.value, due.value, null);
        });
        app.records.loans.add(loan);
        book.value.setAvailableCopies(book.value.getAvailableCopies() - 1);
        app.registration.saved(loan);
        System.out.println("Available copies: " + book.value.getAvailableCopies());
    }

}
