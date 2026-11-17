package com.auca.main;

import com.auca.abstracts.Person;
import com.auca.assessment.Assessment;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.PrerequisiteMissingException;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;

import java.math.BigDecimal;
import java.util.List;

import com.auca.main.Forms.*;

final class RecordActions {

    private final Main app;

    RecordActions(Main app) {
        this.app = app;
    }

    void viewAllRecords() {
        showGroup("Departments", app.records.departments);
        showGroup("Students", app.records.students);
        showGroup("Teachers", app.records.teachers);
        showGroup("Administrators", app.records.administrators);
        showGroup("Courses", app.records.courses);
        showGroup("Classrooms", app.records.classrooms);
        showGroup("Enrollments", app.records.enrollments);
        showGroup("Assessments", app.records.assessments);
        showGroup("Payments", app.records.payments);
        showGroup("Library Books", app.records.books);
        showGroup("Book Loans", app.records.loans);
    }

    void showGroup(String heading, List<? extends Registrable> records) {
        System.out.println("\n===== " + heading + " =====");
        if (records.isEmpty()) {
            System.out.println("No records.");
        }
        for (Registrable record : records) {
            record.printDetails();
            System.out.println();
        }
    }

    void searchRecord() throws Cancelled, RecordNotFoundException {
        Identifiable record = app.records.findRecord(app.consoleInput.readCommand("Enter record ID"));
        ((Registrable) record).printDetails();
    }

    void academicSummary() throws Cancelled, PrerequisiteMissingException {
        Student student = selectReportStudent();
        if (student == null)
            return;

        System.out.println("\nAcademic summary for " + student.getDisplayName());
        boolean found = false;
        for (Enrollment enrollment : app.records.enrollments) {
            if (enrollment.getStudent() == student) {
                found = true;
                System.out.println(enrollment.getRegistrationSummary()
                        + " | " + enrollment.getEnrollmentStatus());
                boolean hasAssessments = false;
                for (Assessment assessment : app.records.assessments) {
                    if (assessment.getEnrollment() == enrollment) {
                        System.out.println("  " + assessment.getRegistrationSummary());
                        hasAssessments = true;
                    }
                }
                if (!hasAssessments) {
                    System.out.println("  No assessments recorded.");
                }
            }
        }
        if (!found) {
            System.out.println("No enrollments recorded.");
        }
    }

    void paymentHistory() throws Cancelled, PrerequisiteMissingException {
        Student student = selectReportStudent();
        if (student == null)
            return;

        System.out.println("\nPayment history for " + student.getDisplayName());
        BigDecimal total = BigDecimal.ZERO;
        boolean found = false;
        for (FeePayment payment : app.records.payments) {
            if (payment.getStudent() == student) {
                payment.printDetails();
                System.out.println();
                total = total.add(payment.getAmount());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No payments recorded.");
        }
        System.out.println("Total paid: " + total.toPlainString());
    }

    void viewAvailableBooks() {
        boolean found = false;
        for (LibraryBook book : app.records.books) {
            if (book.getAvailableCopies() > 0) {
                System.out.println(book.getRegistrationSummary());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No books have available copies.");
        }
    }

    void demonstratePolymorphism() {
        System.out.println("\nSuperclass polymorphism using List<Person>");
        List<Person> people = app.records.people();
        if (people.isEmpty()) {
            System.out.println("Register a student, teacher, or administrator first.");
        }
        for (Person person : people) {
            System.out.println(person.getDisplayName() + " - " + person.getRoleDescription());
        }

        System.out.println("\nInterface polymorphism using List<Identifiable>");
        List<Identifiable> records = app.records.allRecords();
        if (records.isEmpty()) {
            System.out.println("Register records first.");
        }
        for (Identifiable record : records) {
            System.out.println(record.getId() + " - " + record.getDisplayName()
                    + " | Active: " + record.isActive());
        }
    }

    Student selectReportStudent() throws Cancelled, PrerequisiteMissingException {
        try {
            return app.recordSelection.select("Student", app.records.students, null);
        } catch (Back exception) {
            return null;
        }
    }

}
