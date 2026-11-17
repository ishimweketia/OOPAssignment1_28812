package com.auca.main;

import com.auca.enrollment.Enrollment;
import com.auca.exceptions.PrerequisiteMissingException;
import com.auca.interfaces.Identifiable;

import java.util.ArrayList;
import java.util.List;

import com.auca.main.Forms.*;

final class RecordSelection {

    private final Main app;

    RecordSelection(Main app) {
        this.app = app;
    }

    Enrollment selectActiveEnrollment(Action register)
            throws Cancelled, Back, PrerequisiteMissingException {
        while (true) {
            List<Enrollment> active = new ArrayList<>();
            for (Enrollment enrollment : app.records.enrollments) {
                if (enrollment.isActive()) {
                    active.add(enrollment);
                }
            }

            Enrollment selected = select("active Enrollment", active, null, register);
            if (selected != null) {
                return selected;
            }
        }
    }

    <T extends Identifiable> T select(String label, List<T> records, Action register)
            throws Cancelled, Back, PrerequisiteMissingException {
        return select(label, records, register, null);
    }

    <T extends Identifiable> T select(String label, List<T> records,
            Action register, Action registerAndRefresh)
            throws Cancelled, Back, PrerequisiteMissingException {
        Action registration = register != null ? register : registerAndRefresh;
        while (true) {
            try {
                requireRecords(label, records);
            } catch (PrerequisiteMissingException exception) {
                if (registration == null) {
                    throw exception;
                }
                System.out.println(exception.getMessage());
            }

            for (int i = 0; i < records.size(); i++) {
                T record = records.get(i);
                String description = label.equals("Department")
                        ? "[" + record.getId() + "] " + record.getDisplayName()
                        : app.consoleInput.display(record);
                System.out.println((i + 1) + ". " + description);
            }
            if (registration != null) {
                String action = label.equals("Department")
                        ? "[+] Register a new Department" : "Register " + label + " now";
                System.out.println((records.size() + 1) + ". " + action);
            }

            String input = app.consoleInput.read("Select " + label + ", BACK, or 0 to cancel: ");
            if (app.consoleInput.isCancel(input) || input.equals("0"))
                throw new Cancelled();
            if (app.consoleInput.isBack(input))
                throw new Back();

            int selection = app.consoleInput.menuNumber(input);
            if (selection >= 1 && selection <= records.size()) {
                return records.get(selection - 1);
            }
            if (selection == records.size() + 1 && registration != null) {
                addPrerequisite(registration);
                if (registerAndRefresh != null) {
                    return null;
                }
            } else {
                System.out.println("Choose a number from the list.");
            }
        }
    }

    private void requireRecords(String label, List<?> records)
            throws PrerequisiteMissingException {
        if (records.isEmpty()) {
            throw new PrerequisiteMissingException("No " + label + " is registered.");
        }
    }

    void addPrerequisite(Action register) {
        try {
            register.run();
        } catch (Cancelled exception) {
            System.out.println("Related registration cancelled.");
        }
        System.out.println("Returning to the selection.");
    }

}
