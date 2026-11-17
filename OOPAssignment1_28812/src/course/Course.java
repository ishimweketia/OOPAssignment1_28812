package com.auca.course;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Course implements Identifiable, Registrable {

    private final String courseId;
    private String courseCode;
    private String courseTitle;
    private int creditHours;
    private Department department;
    private Teacher assignedTeacher;

    public Course(String courseId, String courseCode, String courseTitle,
            int creditHours, Department department, Teacher assignedTeacher) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditHours = creditHours;
        this.department = department;
        this.assignedTeacher = assignedTeacher;
    }

    @Override
    public String getId() {
        return courseId;
    }

    @Override
    public String getDisplayName() {
        return courseTitle;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Teacher getAssignedTeacher() {
        return assignedTeacher;
    }

    public void setAssignedTeacher(Teacher assignedTeacher) {
        this.assignedTeacher = assignedTeacher;
    }

    @Override
    public void validate() throws ValidationException {
        if (courseId == null
                || courseId.trim().length() < MIN_ID_LENGTH
                || courseId.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Course ID must have 4 to 30 characters.");
        }

        if (courseCode == null || courseCode.isBlank()) {
            throw new ValidationException("Course code is required.");
        }

        if (courseTitle == null || courseTitle.isBlank()) {
            throw new ValidationException("Course title is required.");
        }

        if (creditHours < 1 || creditHours > 6) {
            throw new ValidationException("Credit hours must be between 1 and 6.");
        }

        if (department == null) {
            throw new ValidationException("Select a registered department.");
        }

        if (assignedTeacher == null) {
            throw new ValidationException("Select a registered teacher.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println(this);
    }

    @Override
    public String getRegistrationSummary() {
        return courseId + " - " + courseCode + " - " + courseTitle;
    }

    @Override
    public String toString() {
        String departmentName = department == null
                ? "Unassigned"
                : department.getDisplayName();

        String teacherName = assignedTeacher == null
                ? "Unassigned"
                : assignedTeacher.getDisplayName();

        return "Course ID: " + courseId
                + "\nCourse code: " + courseCode
                + "\nCourse title: " + courseTitle
                + "\nCredit hours: " + creditHours
                + "\nDepartment: " + departmentName
                + "\nAssigned teacher: " + teacherName;
    }
}