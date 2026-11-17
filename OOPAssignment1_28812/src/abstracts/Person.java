package com.auca.abstracts;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import java.time.LocalDate;

public abstract class Person implements Identifiable, Registrable {

    private final String personId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;

    protected Person(String personId, String firstName, String lastName,
            String email, String phoneNumber, LocalDate dateOfBirth) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String getId() {
        return personId;
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    public abstract String getRoleDescription();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public void validate() throws ValidationException {
        if (!hasValidIdLength(personId)) {
            throw new ValidationException("Person ID must have 4 to 30 characters.");
        }

        if (!hasValidNameLength(firstName) || !hasValidNameLength(lastName)) {
            throw new ValidationException("First and last names must each have 2 to 60 characters.");
        }

        if (!isValidEmail(email)) {
            throw new ValidationException("Enter a valid personal email.");
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            throw new ValidationException("Phone number must have 10 to 15 digits, with an optional leading +.");
        }

        if (dateOfBirth == null || !dateOfBirth.isBefore(LocalDate.now())) {
            throw new ValidationException("Date of birth must be in the past.");
        }
    }

    protected static boolean hasValidIdLength(String value) {
        return value != null
                && value.trim().length() >= MIN_ID_LENGTH
                && value.trim().length() <= MAX_ID_LENGTH;
    }

    private static boolean hasValidNameLength(String value) {
        return value != null
                && value.trim().length() >= MIN_NAME_LENGTH
                && value.trim().length() <= MAX_NAME_LENGTH;
    }

    protected static boolean isValidEmail(String value) {
        return value != null
                && value.length() <= MAX_EMAIL_LENGTH
                && value.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+");
    }

    protected static boolean isValidPhoneNumber(String value) {
        return value != null
                && value.matches("\\+?[0-9]{" + MIN_PHONE_LENGTH + "," + MAX_PHONE_LENGTH + "}");
    }

    @Override
    public void printDetails() {
        System.out.println(this);
    }

    @Override
    public String getRegistrationSummary() {
        return getId() + " - " + getDisplayName() + " (" + getRoleDescription() + ")";
    }

    @Override
    public String toString() {
        return "Person ID: " + personId
                + "\nFirst name: " + firstName
                + "\nLast name: " + lastName
                + "\nEmail: " + email
                + "\nPhone number: " + phoneNumber
                + "\nDate of birth: " + dateOfBirth;
    }
}