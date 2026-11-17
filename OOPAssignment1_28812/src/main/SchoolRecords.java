package com.auca.main;

import com.auca.abstracts.AcademicMember;
import com.auca.abstracts.Person;
import com.auca.administrator.Administrator;
import com.auca.assessment.Assessment;
import com.auca.bookloan.BookLoan;
import com.auca.classroom.Classroom;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.BookUnavailableException;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.exceptions.ValidationException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;

import java.util.ArrayList;
import java.util.List;

import com.auca.main.Forms.*;

final class SchoolRecords {

    private final Main app;

    SchoolRecords(Main app) {
        this.app = app;
    }

    final List<Department> departments = new ArrayList<>();
    final List<Student> students = new ArrayList<>();
    final List<Teacher> teachers = new ArrayList<>();
    final List<Administrator> administrators = new ArrayList<>();
    final List<Course> courses = new ArrayList<>();
    final List<Classroom> classrooms = new ArrayList<>();
    final List<Enrollment> enrollments = new ArrayList<>();
    final List<Assessment> assessments = new ArrayList<>();
    final List<FeePayment> payments = new ArrayList<>();
    final List<LibraryBook> books = new ArrayList<>();
    final List<BookLoan> loans = new ArrayList<>();

    void checkNewId(String id) throws DuplicateRecordException {
        for (Identifiable record : allRecords()) {
            unique(id, record.getId(), "Record ID");
        }
    }

    void checkUniqueBeforeSave(Identifiable record)
            throws ValidationException, DuplicateRecordException {
        checkNewId(record.getId());

        if (record instanceof Person) {
            personalEmail(((Person) record).getEmail(), null);
        }
        if (record instanceof AcademicMember) {
            AcademicMember member = (AcademicMember) record;
            memberNumber(member.getMemberNumber(), null);
            institutionalEmail(member.getInstitutionalEmail(), null);
        }
        if (record instanceof Student) {
            Student student = (Student) record;
            for (Student saved : students) {
                unique(student.getStudentNumber(), saved.getStudentNumber(), "Student number");
            }
        } else if (record instanceof Teacher) {
            Teacher teacher = (Teacher) record;
            for (Teacher saved : teachers) {
                unique(teacher.getEmployeeNumber(), saved.getEmployeeNumber(), "Employee number");
            }
        } else if (record instanceof Administrator) {
            Administrator administrator = (Administrator) record;
            for (Administrator saved : administrators) {
                unique(administrator.getStaffNumber(), saved.getStaffNumber(), "Staff number");
            }
        } else if (record instanceof Course) {
            Course course = (Course) record;
            for (Course saved : courses) {
                unique(course.getCourseCode(), saved.getCourseCode(), "Course code");
            }
        } else if (record instanceof FeePayment) {
            receipt(((FeePayment) record).getReceiptNumber(), null);
        }
    }

    Identifiable findRecord(String id) throws RecordNotFoundException {
        for (Identifiable record : allRecords()) {
            if (record.getId().equalsIgnoreCase(id)) {
                return record;
            }
        }
        throw new RecordNotFoundException("No record found for ID " + id + ".");
    }

    List<Identifiable> allRecords() {
        List<Identifiable> records = new ArrayList<>();
        records.addAll(departments);
        records.addAll(people());
        records.addAll(courses);
        records.addAll(classrooms);
        records.addAll(enrollments);
        records.addAll(assessments);
        records.addAll(payments);
        records.addAll(books);
        records.addAll(loans);
        return records;
    }

    List<Person> people() {
        List<Person> people = new ArrayList<>();
        people.addAll(students);
        people.addAll(teachers);
        people.addAll(administrators);
        return people;
    }

    List<AcademicMember> academicMembers() {
        List<AcademicMember> members = new ArrayList<>();
        members.addAll(students);
        members.addAll(teachers);
        return members;
    }

    void unique(String entered, String saved, String label)
            throws DuplicateRecordException {
        if (entered.equalsIgnoreCase(saved)) {
            throw new DuplicateRecordException(label + " is already registered.");
        }
    }

    String personalEmail(String input, Person original)
            throws ValidationException, DuplicateRecordException {
        String value = app.consoleInput.email().parse(input);
        for (Person person : people()) {
            if (person != original)
                unique(value, person.getEmail(), "Personal email");
        }
        return value;
    }

    String memberNumber(String input, AcademicMember original)
            throws ValidationException, DuplicateRecordException {
        String value = app.consoleInput.idValue(input);
        for (AcademicMember member : academicMembers()) {
            if (member != original)
                unique(value, member.getMemberNumber(), "Member number");
        }
        return value;
    }

    String institutionalEmail(String input, AcademicMember original)
            throws ValidationException, DuplicateRecordException {
        String value = app.consoleInput.email().parse(input);
        for (AcademicMember member : academicMembers()) {
            if (member != original)
                unique(value, member.getInstitutionalEmail(), "Institutional email");
        }
        return value;
    }

    String receipt(String input, FeePayment original)
            throws ValidationException, DuplicateRecordException {
        String value = app.consoleInput.required().parse(input);
        for (FeePayment payment : payments) {
            if (payment != original)
                unique(value, payment.getReceiptNumber(), "Receipt number");
        }
        return value;
    }

    void checkEnrollment(Enrollment original, Student student, Course course,
            String semester, String status) throws DuplicateRecordException {
        if (!"ACTIVE".equals(status))
            return;
        for (Enrollment enrollment : enrollments) {
            if (enrollment != original && enrollment.isActive()
                    && enrollment.getStudent() == student && enrollment.getCourse() == course
                    && enrollment.getSemester().equals(semester)) {
                throw new DuplicateRecordException(
                        "This student already has an active enrollment for that course and semester.");
            }
        }
    }

    void checkAvailable(LibraryBook book) throws BookUnavailableException {
        if (book.getAvailableCopies() <= 0) {
            throw new BookUnavailableException("No copies of this book are available.");
        }
    }

}
