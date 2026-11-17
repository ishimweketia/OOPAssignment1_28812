package com.auca.main;

import com.auca.exceptions.BookUnavailableException;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.PrerequisiteMissingException;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;
import com.auca.teacher.Teacher;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class Forms {

    private final Main app;

    Forms(Main app) {
        this.app = app;
    }

    interface Parser<T> {
        T parse(String input) throws ValidationException, DuplicateRecordException;
    }

    interface Reader<T> {
        T read() throws Cancelled, Back, PrerequisiteMissingException,
                ValidationException, DuplicateRecordException, BookUnavailableException;
    }

    interface Factory<T> {
        T create() throws ValidationException, DuplicateRecordException, BookUnavailableException;
    }

    interface Action {
        void run() throws Cancelled;
    }

    static class Cancelled extends Exception {
    }

    static class Back extends Exception {
    }

    static class Field<T> {
        final String label;
        final Reader<T> reader;
        T value;

        Field(String label, Reader<T> reader) {
            this.label = label;
            this.reader = reader;
        }

        void enter() throws Cancelled, Back, PrerequisiteMissingException,
                ValidationException, DuplicateRecordException, BookUnavailableException {
            value = reader.read();
        }
    }

    class Form {
        final String title;
        final int secondStep;
        final int thirdStep;
        final List<Field<?>> fields = new ArrayList<>();

        Form(String title, int secondStep, int thirdStep) {
            this.title = title;
            this.secondStep = secondStep;
            this.thirdStep = thirdStep;
        }

        <T> Field<T> add(String label, Parser<T> parser) {
            return add(label, () -> app.consoleInput.ask(label, parser));
        }

        <T> Field<T> add(String label, Reader<T> reader) {
            Field<T> field = new Field<>(label, reader);
            fields.add(field);
            return field;
        }

        <T extends Identifiable> Field<T> select(String label, List<T> records, Action register) {
            return add(label, () -> app.recordSelection.select(label, records, register));
        }

        void fixed(String label, String value) {
            Field<String> field = new Field<>(label, null);
            field.value = value;
            fields.add(field);
        }

        <T extends Identifiable & Registrable> T complete(Factory<T> factory)
                throws Cancelled {
            System.out.println("\n===== AUCA_28422 / " + title.toUpperCase(Locale.ROOT) + " REGISTRATION =====");
            System.out.println("Type BACK to go back or CANCEL to discard the form.");

            if (title.equals("Student")) {
                System.out.println("1. Personal details    2. Academic details");
                System.out.println("3. Student details     4. Review and confirm");
            }

            int index = 0;
            int displayedStep = 0;
            while (true) {
                if (index < fields.size()) {
                    Field<?> field = fields.get(index);
                    if (field.reader == null) {
                        index++;
                        continue;
                    }

                    int step = index < secondStep ? 1 : index < thirdStep ? 2 : 3;
                    String section = step == 1 ? "Details"
                            : step == 2 ? "Additional details" : "Final details";
                    if (title.equals("Student") || title.equals("Teacher")) {
                        section = step == 1 ? "Personal details"
                                : step == 2 ? "Academic details" : title + " details";
                    }

                    if (step != displayedStep) {
                        if (title.equals("Student")) {
                            System.out.println("\n+----------------------------------------------------+");
                            System.out.printf("| STUDENT REGISTRATION | Step %d of 4 | %s%n", step, section);
                            System.out.println("+----------------------------------------------------+");
                        } else {
                            System.out.printf("%n[STEP %d/4] %s%n", step, section);
                        }
                        displayedStep = step;
                    }
                    try {
                        enter(index);
                        int next = index + 1;
                        if (!title.equals("Student")
                                || (next != secondStep && next != thirdStep && next != fields.size())
                                || confirmStudentStep(next == fields.size())) {
                            index = next;
                        }
                    } catch (Back exception) {
                        int previous = previousEditable(index);
                        if (previous < 0) {
                            System.out.println("You are already at the first field.");
                        } else {
                            index = previous;
                        }
                    }
                    continue;
                }

                displayedStep = 4;
                System.out.println("\n[STEP 4/4] Review and confirm");
                for (int i = 0; i < fields.size(); i++) {
                    Field<?> field = fields.get(i);
                    System.out.printf("%02d. %s: %s%n", i + 1, fieldLabel(i), app.consoleInput.display(field.value));
                }

                String choice = app.consoleInput.read("1. Save  2. Edit field  BACK. Previous  0. Cancel: ");
                if (app.consoleInput.isCancel(choice) || choice.equals("0"))
                    throw new Cancelled();

                if (app.consoleInput.isBack(choice)) {
                    index = previousEditable(fields.size());
                } else if (choice.equals("2")) {
                    String input = app.consoleInput.read("Field number, BACK, or CANCEL: ");
                    if (app.consoleInput.isCancel(input))
                        throw new Cancelled();
                    if (app.consoleInput.isBack(input))
                        continue;
                    int number = app.consoleInput.menuNumber(input);
                    if (number < 1 || number > fields.size() || fields.get(number - 1).reader == null) {
                        System.out.println("Choose an editable field number.");
                        continue;
                    }
                    try {
                        enter(number - 1);
                    } catch (Back exception) {
                        System.out.println("Returning to the review screen.");
                    }
                } else if (choice.equals("1")) {
                    try {
                        T record = factory.create();
                        app.records.checkUniqueBeforeSave(record);
                        record.validate();
                        return record;
                    } catch (ValidationException | DuplicateRecordException
                            | BookUnavailableException exception) {
                        System.out.println(exception.getMessage());
                        System.out.println("Choose Edit field to correct the record.");
                    }
                } else {
                    System.out.println("Choose 1, 2, BACK, or 0.");
                }
            }
        }

        private boolean confirmStudentStep(boolean reviewing) throws Cancelled {
            String next = reviewing ? "Review record" : "Next step";
            while (true) {
                String choice = app.consoleInput.read(
                        "[ENTER] " + next + "  [BACK] Previous  [CANCEL] Exit: ");
                if (choice.isEmpty()) return true;
                if (app.consoleInput.isBack(choice)) return false;
                if (app.consoleInput.isCancel(choice) || choice.equals("0")) throw new Cancelled();
                System.out.println("Press Enter, type BACK, or type CANCEL.");
            }
        }

        private int previousEditable(int index) {
            for (int i = index - 1; i >= 0; i--) {
                if (fields.get(i).reader != null)
                    return i;
            }
            return -1;
        }

        private String fieldLabel(int index) {
            String label = fields.get(index).label;
            if (!title.equals("Student")) return label;

            String[] tags = {
                "String | unique | 4-30 chars", "String | 2-60 chars", "String | 2-60 chars",
                "String | unique | email", "String | 10-15 digits", "LocalDate | YYYY-MM-DD",
                "String | unique | 4-30 chars", "Department | select", "LocalDate | YYYY-MM-DD",
                "String | 1 ACTIVE / 2 INACTIVE", "String | unique | email",
                "String | 2-120 chars", "String | unique | 4-30 chars", "String | 2-120 chars",
                "LocalDate | YYYY-MM-DD", "int | see allowed range", "String | 10-15 digits",
                "boolean | 1 YES / 2 NO"
            };
            int formatStart = label.indexOf(" (");
            if (formatStart >= 0 && index != 15) label = label.substring(0, formatStart);
            return label + " [" + tags[index] + "]";
        }

        private void enter(int index) throws Cancelled, Back {
            Field<?> field = fields.get(index);
            while (true) {
                System.out.printf("%02d. %s%n", index + 1, fieldLabel(index));
                if (field.value != null) {
                    System.out.println("Current value: " + app.consoleInput.display(field.value));
                }
                try {
                    field.enter();
                    return;
                } catch (PrerequisiteMissingException | ValidationException
                        | DuplicateRecordException | BookUnavailableException exception) {
                    System.out.println(exception.getMessage());
                }
            }
        }
    }

}
