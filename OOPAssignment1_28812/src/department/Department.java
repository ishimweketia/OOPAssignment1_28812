package com.auca.department;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Department implements Identifiable, Registrable {

    private final String departmentId;
    private String departmentName;
    private String departmentCode;
    private String officeLocation;
    private String contactEmail;
    private Teacher headTeacher;

    public Department(String departmentId, String departmentName,
            String departmentCode, String officeLocation,
            String contactEmail, Teacher headTeacher) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.officeLocation = officeLocation;
        this.contactEmail = contactEmail;
        this.headTeacher = headTeacher;
    }

    @Override
    public String getId() {
        return departmentId;
    }

    @Override
    public String getDisplayName() {
        return departmentName;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Teacher getHeadTeacher() {
        return headTeacher;
    }

    public void setHeadTeacher(Teacher headTeacher) {
        this.headTeacher = headTeacher;
    }

    @Override
    public void validate() throws ValidationException {
        if (departmentId == null
                || departmentId.trim().length() < MIN_ID_LENGTH
                || departmentId.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Department ID must have 4 to 30 characters.");
        }

        if (departmentName == null
                || departmentName.trim().length() < MIN_NAME_LENGTH
                || departmentName.trim().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Department name must have 2 to 60 characters.");
        }

        if (departmentCode == null || departmentCode.isBlank()) {
            throw new ValidationException("Department code is required.");
        }

        if (officeLocation == null || officeLocation.isBlank()) {
            throw new ValidationException("Office location is required.");
        }

        if (contactEmail == null
                || contactEmail.length() > MAX_EMAIL_LENGTH
                || !contactEmail.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new ValidationException("Enter a valid contact email.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println(this);
    }

    @Override
    public String getRegistrationSummary() {
        return departmentId + " - " + departmentName;
    }

    @Override
    public String toString() {
        String headTeacherName = "Unassigned";

        if (headTeacher != null) {
            headTeacherName = headTeacher.getDisplayName();
        }

        return "Department ID: " + departmentId
                + "\nDepartment name: " + departmentName
                + "\nDepartment code: " + departmentCode
                + "\nOffice location: " + officeLocation
                + "\nContact email: " + contactEmail
                + "\nHead teacher: " + headTeacherName;
    }
}