package com.auca.student;

import com.auca.abstracts.AcademicMember;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import java.time.LocalDate;

public class Student extends AcademicMember {

    public static final int MIN_STUDY_YEAR = 1;
    public static final int MAX_STUDY_YEAR = 6;

    private String studentNumber;
    private String programName;
    private LocalDate admissionDate;
    private int studyYear;
    private String guardianPhone;
    private boolean scholarshipStatus;

    public Student(String personId, String firstName, String lastName,
            String email, String phoneNumber, LocalDate dateOfBirth,
            String memberNumber, Department department, LocalDate joinDate,
            String academicStatus, String institutionalEmail, String advisorName,
            String studentNumber, String programName, LocalDate admissionDate,
            int studyYear, String guardianPhone, boolean scholarshipStatus) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth,
                memberNumber, department, joinDate, academicStatus,
                institutionalEmail, advisorName);

        this.studentNumber = studentNumber;
        this.programName = programName;
        this.admissionDate = admissionDate;
        this.studyYear = studyYear;
        this.guardianPhone = guardianPhone;
        this.scholarshipStatus = scholarshipStatus;
    }

    @Override
    public String getRoleDescription() {
        return "Student";
    }

    @Override
    public String getAcademicCategory() {
        return "Student";
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }

    public int getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(int studyYear) {
        this.studyYear = studyYear;
    }

    public String getGuardianPhone() {
        return guardianPhone;
    }

    public void setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
    }

    public boolean isScholarshipStatus() {
        return scholarshipStatus;
    }

    public void setScholarshipStatus(boolean scholarshipStatus) {
        this.scholarshipStatus = scholarshipStatus;
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();

        if (!hasValidIdLength(studentNumber)) {
            throw new ValidationException("Student number must have 4 to 30 characters.");
        }

        if (programName == null || programName.trim().length() < 2
                || programName.trim().length() > 120) {
            throw new ValidationException("Program name must have 2 to 120 characters.");
        }

        if (admissionDate == null || admissionDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Admission date is required and cannot be in the future.");
        }

        if (studyYear < MIN_STUDY_YEAR || studyYear > MAX_STUDY_YEAR) {
            throw new ValidationException("Study year must be between "
                    + MIN_STUDY_YEAR + " and " + MAX_STUDY_YEAR + ".");
        }

        if (!isValidPhoneNumber(guardianPhone)) {
            throw new ValidationException("Enter a valid guardian phone number.");
        }
    }

    @Override
    public String toString() {
        return super.toString()
                + "\nStudent number: " + studentNumber
                + "\nProgram name: " + programName
                + "\nAdmission date: " + admissionDate
                + "\nStudy year: " + studyYear
                + "\nGuardian phone: " + guardianPhone
                + "\nScholarship: " + (scholarshipStatus ? "YES" : "NO");
    }
}