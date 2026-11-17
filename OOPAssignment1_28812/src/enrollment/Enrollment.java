package com.auca.enrollment;

import com.auca.course.Course;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;

import java.time.LocalDate;

public class Enrollment implements Identifiable, Registrable {

    private final String enrollmentId;
    private Student student;
    private Course course;
    private LocalDate enrollmentDate;
    private String semester;
    private String enrollmentStatus;

    public Enrollment(String enrollmentId, Student student, Course course,
            LocalDate enrollmentDate, String semester,
            String enrollmentStatus) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.semester = semester;
        this.enrollmentStatus = enrollmentStatus;
    }

    @Override
    public String getId() {
        return enrollmentId;
    }

    @Override
    public String getDisplayName() {
        String studentName = student == null ? "Unassigned" : student.getDisplayName();
        String courseTitle = course == null ? "Unassigned" : course.getDisplayName();

        return studentName + " - " + courseTitle;
    }

    @Override
    public boolean isActive() {
        return "ACTIVE".equals(enrollmentStatus);
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setEnrollmentStatus(String enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }

    @Override
    public void validate() throws ValidationException {
        if (enrollmentId == null
                || enrollmentId.trim().length() < MIN_ID_LENGTH
                || enrollmentId.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Enrollment ID must have 4 to 30 characters.");
        }

        if (student == null) {
            throw new ValidationException("Select a registered student.");
        }

        if (course == null) {
            throw new ValidationException("Select a registered course.");
        }

        if (enrollmentDate == null || enrollmentDate.isAfter(LocalDate.now())) {
            throw new ValidationException(
                    "Enrollment date is required and cannot be in the future.");
        }

        if (!"SEMESTER 1".equals(semester) && !"SEMESTER 2".equals(semester)) {
            throw new ValidationException("Choose SEMESTER 1 or SEMESTER 2.");
        }

        if (!"ACTIVE".equals(enrollmentStatus) && !"INACTIVE".equals(enrollmentStatus)) {
            throw new ValidationException("Choose ACTIVE or INACTIVE.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println(this);
    }

    @Override
    public String getRegistrationSummary() {
        return enrollmentId + " - " + getDisplayName() + " - " + semester;
    }

    @Override
    public String toString() {
        String studentLabel = student == null
                ? "Unassigned"
                : student.getStudentNumber() + " - " + student.getDisplayName();

        String courseLabel = course == null
                ? "Unassigned"
                : course.getCourseCode() + " - " + course.getDisplayName();

        return "Enrollment ID: " + enrollmentId
                + "\nStudent: " + studentLabel
                + "\nCourse: " + courseLabel
                + "\nEnrollment date: " + enrollmentDate
                + "\nSemester: " + semester
                + "\nEnrollment status: " + enrollmentStatus;
    }
}