package com.auca.main;

import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import com.auca.main.Forms.*;

final class ConsoleInput {

    private final Scanner scanner;

    ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    String read(String prompt) {
        System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "CANCEL";
    }

    String readCommand(String label) throws Cancelled {
        while (true) {
            String input = read(label + " (or CANCEL): ");
            if (isCancel(input) || isBack(input))
                throw new Cancelled();
            if (!input.isEmpty())
                return input;
            System.out.println("Enter a value.");
        }
    }

    boolean isCancel(String input) {
        return input.equalsIgnoreCase("CANCEL");
    }

    boolean isBack(String input) {
        return input.equalsIgnoreCase("BACK");
    }

    int menuNumber(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    void confirm(String action) throws Cancelled {
        while (true) {
            String input = read("1. " + action + "  0. Cancel: ");
            if (input.equals("1"))
                return;
            if (input.equals("0") || isCancel(input) || isBack(input))
                throw new Cancelled();
            System.out.println("Choose 1 or 0.");
        }
    }

    <T> T ask(String label, Parser<T> parser)
            throws Cancelled, Back {
        while (true) {
            String input = read(label + ": ");
            if (isCancel(input))
                throw new Cancelled();
            if (isBack(input))
                throw new Back();

            try {
                return parser.parse(input);
            } catch (ValidationException | DuplicateRecordException exception) {
                System.out.println(exception.getMessage());
            } catch (NumberFormatException exception) {
                System.out.println("Enter a number in the format shown.");
            } catch (DateTimeParseException exception) {
                System.out.println("Enter a valid calendar date in YYYY-MM-DD format.");
            }
        }
    }

    Parser<String> required() {
        return input -> {
            if (input.isEmpty())
                throw new ValidationException("This field is required.");
            return input;
        };
    }

    String idValue(String input) throws ValidationException {
        return checkLength(input, Identifiable.MIN_ID_LENGTH, Identifiable.MAX_ID_LENGTH);
    }

    Parser<String> name(int maximum) {
        return input -> checkLength(input, Registrable.MIN_NAME_LENGTH, maximum);
    }

    String checkLength(String value, int minimum, int maximum) throws ValidationException {
        if (value.length() < minimum || value.length() > maximum) {
            throw new ValidationException("Use " + minimum + " to " + maximum + " characters.");
        }
        return value;
    }

    Parser<String> email() {
        return input -> {
            if (input.length() > Registrable.MAX_EMAIL_LENGTH
                    || !input.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
                throw new ValidationException("Enter a valid email address.");
            }
            return input;
        };
    }

    Parser<String> phone() {
        return input -> {
            if (!input.matches("\\+?[0-9]{10,15}")) {
                throw new ValidationException("Enter 10 to 15 digits, with an optional leading +.");
            }
            return input;
        };
    }

    LocalDate parseDate(String input) {
        if (!input.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            throw new DateTimeParseException("Use YYYY-MM-DD.", input, 0);
        }
        return LocalDate.parse(input);
    }

    Parser<LocalDate> date(boolean noFuture, boolean pastOnly) {
        return input -> {
            LocalDate value = parseDate(input);
            LocalDate today = LocalDate.now();
            if (pastOnly && !value.isBefore(today)) {
                throw new ValidationException("Date of birth must be in the past.");
            }
            if (noFuture && value.isAfter(today)) {
                throw new ValidationException("This date cannot be in the future.");
            }
            return value;
        };
    }

    LocalDate dueDate(String input, LocalDate borrowed) throws ValidationException {
        LocalDate value = parseDate(input);
        if (!value.isAfter(borrowed)) {
            throw new ValidationException("Due date must be after the borrowed date.");
        }
        return value;
    }

    Parser<Integer> integer(int minimum, int maximum) {
        return input -> {
            int value = Integer.parseInt(input);
            if (value < minimum || value > maximum) {
                throw new ValidationException("Enter a whole number from " + minimum + " to " + maximum + ".");
            }
            return value;
        };
    }

    Parser<BigDecimal> money() {
        return input -> {
            if (input.length() > 40 || !input.matches("-?(?:[0-9]+(?:\\.[0-9]+)?|\\.[0-9]+)")) {
                throw new ValidationException("Enter a decimal amount without exponent notation (up to 40 characters).");
            }
            BigDecimal amount = new BigDecimal(input);
            if (amount.signum() <= 0)
                throw new ValidationException("Amount must be greater than zero.");
            return amount;
        };
    }

    Parser<Double> positiveScore() {
        return input -> {
            double value = Double.parseDouble(input);
            if (!Double.isFinite(value) || value <= 0) {
                throw new ValidationException("Maximum score must be greater than zero.");
            }
            return value;
        };
    }

    double score(String input, double maximum) throws ValidationException {
        double value = Double.parseDouble(input);
        if (!Double.isFinite(value) || value < 0 || value > maximum) {
            throw new ValidationException("Enter a score between zero and " + maximum + ".");
        }
        return value;
    }

    Parser<String> choices(String... options) {
        return input -> {
            for (int i = 0; i < options.length; i++) {
                if (input.equals(Integer.toString(i + 1)) || input.equalsIgnoreCase(options[i])) {
                    return options[i];
                }
            }
            throw new ValidationException("Choose one of the listed options.");
        };
    }

    Parser<Boolean> yesNo() {
        return input -> choices("YES", "NO").parse(input).equals("YES");
    }

    Parser<String> status() {
        return choices("ACTIVE", "INACTIVE");
    }

    Parser<String> assessmentTypes() {
        return choices("COURSEWORK", "MIDTERM", "FINAL EXAM");
    }

    Parser<String> paymentMethods() {
        return choices("CASH", "BANK TRANSFER", "MOBILE MONEY");
    }

    String display(Object value) {
        if (value == null)
            return "Unassigned";
        if (value instanceof Registrable)
            return ((Registrable) value).getRegistrationSummary();
        if (value instanceof Boolean)
            return (Boolean) value ? "YES" : "NO";
        if (value instanceof BigDecimal)
            return ((BigDecimal) value).toPlainString();
        return value.toString();
    }

}
