package com.auca.abstracts;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import java.time.LocalDate;

public abstract class AcademicMember extends Person {

    private String memberNumber;
    private Department department;
    private LocalDate joinDate;
    private String academicStatus;
    private String institutionalEmail;
    private String advisorName;

    protected AcademicMember(String personId, String firstName, String lastName,
            String email, String phoneNumber, LocalDate dateOfBirth,
            String memberNumber, Department department, LocalDate joinDate,
            String academicStatus, String institutionalEmail, String advisorName) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.memberNumber = memberNumber;
        this.department = department;
        this.joinDate = joinDate;
        this.academicStatus = academicStatus;
        this.institutionalEmail = institutionalEmail;
        this.advisorName = advisorName;
    }

    public abstract String getAcademicCategory();

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public String getAcademicStatus() {
        return academicStatus;
    }

    public void setAcademicStatus(String academicStatus) {
        this.academicStatus = academicStatus;
    }

    public String getInstitutionalEmail() {
        return institutionalEmail;
    }

    public void setInstitutionalEmail(String institutionalEmail) {
        this.institutionalEmail = institutionalEmail;
    }

    public String getAdvisorName() {
        return advisorName;
    }

    public void setAdvisorName(String advisorName) {
        this.advisorName = advisorName;
    }

    @Override
    public boolean isActive() {
        return "ACTIVE".equals(academicStatus);
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();

        if (!hasValidIdLength(memberNumber)) {
            throw new ValidationException("Member number must have 4 to 30 characters.");
        }

        if (department == null) {
            throw new ValidationException("Select an existing department.");
        }

        if (joinDate == null) {
            throw new ValidationException("Join date is required.");
        }

        if (!"ACTIVE".equals(academicStatus) && !"INACTIVE".equals(academicStatus)) {
            throw new ValidationException("Academic status must be ACTIVE or INACTIVE.");
        }

        if (!isValidEmail(institutionalEmail)) {
            throw new ValidationException("Enter a valid institutional email.");
        }

        if (advisorName == null
                || advisorName.trim().length() < 2
                || advisorName.trim().length() > 120) {
            throw new ValidationException("Advisor name must have 2 to 120 characters.");
        }
    }

    @Override
    public String toString() {
        String departmentLabel = "Unassigned";
        if (department != null) {
            departmentLabel = department.getRegistrationSummary();
        }

        return super.toString()
                + "\nMember number: " + memberNumber
                + "\nDepartment: " + departmentLabel
                + "\nJoin date: " + joinDate
                + "\nAcademic status: " + academicStatus
                + "\nInstitutional email: " + institutionalEmail
                + "\nAdvisor name: " + advisorName;
    }
}