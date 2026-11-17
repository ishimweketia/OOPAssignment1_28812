package com.auca.main;

import com.auca.exceptions.*;
import com.auca.main.Forms.Cancelled;
import java.util.Scanner;

public class Main {
    private final Scanner scanner;
    final ConsoleInput consoleInput;
    final Registration registration = new Registration(this);
    final LibraryActions libraryActions = new LibraryActions(this);
    final RecordActions recordActions = new RecordActions(this);
    final RecordUpdates recordUpdates = new RecordUpdates(this);
    final Forms forms = new Forms(this);
    final SchoolRecords records = new SchoolRecords(this);
    final RecordSelection recordSelection = new RecordSelection(this);

    private static final String[] MENU = {
            "Register Department", "Register Student", "Register Teacher",
            "Register Administrator", "Register Course", "Register Classroom",
            "Enroll Student", "Record Assessment", "Record Fee Payment",
            "Register Library Book", "Borrow Library Book", "Return Library Book",
            "View All Records", "Search Record by ID", "Update Existing Record",
            "Student Academic Summary", "Student Payment History",
            "View Available Books", "Demonstrate Polymorphism"
    };

    private Main(Scanner scanner) {
        this.scanner = scanner;
        this.consoleInput = new ConsoleInput(scanner);
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            new Main(scanner).run();
        }
    }

    private void run() {
        while (true) {
            showMenu();
            if (!scanner.hasNextLine()) {
                return;
            }

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        registration.registerDepartment();
                        break;
                    case "2":
                        registration.registerStudent();
                        break;
                    case "3":
                        registration.registerTeacher();
                        break;
                    case "4":
                        registration.registerAdministrator();
                        break;
                    case "5":
                        registration.registerCourse();
                        break;
                    case "6":
                        registration.registerClassroom();
                        break;
                    case "7":
                        registration.enrollStudent();
                        break;
                    case "8":
                        registration.recordAssessment();
                        break;
                    case "9":
                        registration.recordPayment();
                        break;
                    case "10":
                        registration.registerBook();
                        break;
                    case "11":
                        libraryActions.borrowBook();
                        break;
                    case "12":
                        libraryActions.returnBook();
                        break;
                    case "13":
                        recordActions.viewAllRecords();
                        break;
                    case "14":
                        recordActions.searchRecord();
                        break;
                    case "15":
                        recordUpdates.updateRecord();
                        break;
                    case "16":
                        recordActions.academicSummary();
                        break;
                    case "17":
                        recordActions.paymentHistory();
                        break;
                    case "18":
                        recordActions.viewAvailableBooks();
                        break;
                    case "19":
                        recordActions.demonstratePolymorphism();
                        break;
                    case "0":
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Choose a number from 0 to 19.");
                }
            } catch (Cancelled exception) {
                System.out.println("Action cancelled.");
            } catch (ValidationException | RecordNotFoundException | PrerequisiteMissingException exception) {
                System.out.println(exception.getMessage());
            } catch (RuntimeException exception) {
                System.out.println("An unexpected error occurred (" + exception.getClass().getSimpleName()
                        + "). Returning to the main menu; check the records before continuing.");
            }
        }
    }

    private void showMenu() {
        System.out.println("\n===== AUCA_28422 SCHOOL MANAGEMENT =====");
        for (int i = 0; i < MENU.length; i++) {
            System.out.println((i + 1) + ". " + MENU[i]);
        }
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

}
