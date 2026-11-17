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
import com.auca.exceptions.PrerequisiteMissingException;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.exceptions.ValidationException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.auca.main.Forms.*;

final class RecordUpdates {

    private final Main app;

    RecordUpdates(Main app) {
        this.app = app;
    }

    void updateRecord() throws Cancelled, RecordNotFoundException {
        Identifiable record = app.records.findRecord(app.consoleInput.readCommand("Enter the ID of the record to update"));
        ((Registrable) record).printDetails();

        UpdateForm update = new UpdateForm();
        if (record instanceof Person) {
            addPersonUpdates(update, (Person) record);
        }
        if (record instanceof AcademicMember) {
            addAcademicUpdates(update, (AcademicMember) record);
        }

        if (record instanceof Department) {
            Department d = (Department) record;
            update.add("Department name", app.consoleInput.name(60), d::setDepartmentName);
            update.add("Department code", app.consoleInput.required(), d::setDepartmentCode);
            update.add("Office location", app.consoleInput.required(), d::setOfficeLocation);
            update.add("Contact email", app.consoleInput.email(), d::setContactEmail);
            update.add("Head teacher", this::selectHeadTeacher, d::setHeadTeacher);
        } else if (record instanceof Student) {
            Student s = (Student) record;
            update.add("Student number", input -> {
                String value = app.consoleInput.idValue(input);
                for (Student other : app.records.students) {
                    if (other != s)
                        app.records.unique(value, other.getStudentNumber(), "Student number");
                }
                return value;
            }, s::setStudentNumber);
            update.add("Program name", app.consoleInput.name(120), s::setProgramName);
            update.add("Admission date (YYYY-MM-DD)", app.consoleInput.date(true, false), s::setAdmissionDate);
            update.add("Study year", app.consoleInput.integer(Student.MIN_STUDY_YEAR, Student.MAX_STUDY_YEAR), s::setStudyYear);
            update.add("Guardian phone", app.consoleInput.phone(), s::setGuardianPhone);
            update.add("Scholarship (1 YES / 2 NO)", app.consoleInput.yesNo(), s::setScholarshipStatus);
        } else if (record instanceof Teacher) {
            Teacher t = (Teacher) record;
            update.add("Employee number", input -> {
                String value = app.consoleInput.required().parse(input);
                for (Teacher other : app.records.teachers) {
                    if (other != t)
                        app.records.unique(value, other.getEmployeeNumber(), "Employee number");
                }
                return value;
            }, t::setEmployeeNumber);
            update.add("Specialization", app.consoleInput.required(), t::setSpecialization);
            update.add("Hire date (YYYY-MM-DD)", app.consoleInput.date(true, false), t::setHireDate);
            update.add("Salary", app.consoleInput.money(), t::setSalary);
            update.add("Office number", app.consoleInput.required(), t::setOfficeNumber);
            update.add("Employment type", app.consoleInput.required(), t::setEmploymentType);
        } else if (record instanceof Administrator) {
            Administrator a = (Administrator) record;
            update.add("Staff number", input -> {
                String value = app.consoleInput.required().parse(input);
                for (Administrator other : app.records.administrators) {
                    if (other != a)
                        app.records.unique(value, other.getStaffNumber(), "Staff number");
                }
                return value;
            }, a::setStaffNumber);
            update.add("Job title", app.consoleInput.required(), a::setJobTitle);
            update.add("Assigned office", app.consoleInput.required(), a::setAssignedOffice);
            update.add("Employment date (YYYY-MM-DD)", app.consoleInput.date(true, false), a::setEmploymentDate);
            update.add("Access level", app.consoleInput.integer(1, Integer.MAX_VALUE), a::setAccessLevel);
            update.add("Active (1 YES / 2 NO)", app.consoleInput.yesNo(), a::setActive);
        } else if (record instanceof Course) {
            Course c = (Course) record;
            update.add("Course code", input -> {
                String value = app.consoleInput.required().parse(input);
                for (Course other : app.records.courses) {
                    if (other != c)
                        app.records.unique(value, other.getCourseCode(), "Course code");
                }
                return value;
            }, c::setCourseCode);
            update.add("Course title", app.consoleInput.required(), c::setCourseTitle);
            update.add("Credit hours (1-6)", app.consoleInput.integer(1, 6), c::setCreditHours);
            update.add("Department", () -> app.recordSelection.select("Department", app.records.departments, app.registration::registerDepartment),
                    c::setDepartment);
            update.add("Assigned teacher", () -> app.recordSelection.select("Teacher", app.records.teachers, app.registration::registerTeacher),
                    c::setAssignedTeacher);
        } else if (record instanceof Classroom) {
            Classroom c = (Classroom) record;
            update.add("Building name", app.consoleInput.required(), c::setBuildingName);
            update.add("Room number", app.consoleInput.required(), c::setRoomNumber);
            update.add("Capacity", app.consoleInput.integer(1, Integer.MAX_VALUE), c::setCapacity);
            update.add("Floor number", app.consoleInput.integer(0, Integer.MAX_VALUE), c::setFloorNumber);
            update.add("Available (1 YES / 2 NO)", app.consoleInput.yesNo(), c::setAvailable);
        } else if (record instanceof Enrollment) {
            addEnrollmentUpdates(update, (Enrollment) record);
        } else if (record instanceof Assessment) {
            Assessment a = (Assessment) record;
            update.add("Active enrollment", () -> app.recordSelection.selectActiveEnrollment(app.registration::enrollStudent), a::setEnrollment);
            update.add("Type (1 COURSEWORK / 2 MIDTERM / 3 FINAL EXAM)", app.consoleInput.assessmentTypes(), a::setAssessmentType);
            update.add("Maximum score", input -> {
                double value = app.consoleInput.positiveScore().parse(input);
                if (value < a.getObtainedScore()) {
                    throw new ValidationException("Maximum score cannot be below the obtained score.");
                }
                return value;
            }, a::setMaximumScore);
            update.add("Obtained score", input -> app.consoleInput.score(input, a.getMaximumScore()), a::setObtainedScore);
            update.add("Assessment date (YYYY-MM-DD)", app.consoleInput.date(false, false), a::setAssessmentDate);
        } else if (record instanceof FeePayment) {
            FeePayment p = (FeePayment) record;
            update.add("Student", () -> app.recordSelection.select("Student", app.records.students, app.registration::registerStudent), p::setStudent);
            update.add("Amount", app.consoleInput.money(), p::setAmount);
            update.add("Payment date (YYYY-MM-DD)", app.consoleInput.date(false, false), p::setPaymentDate);
            update.add("Method (1 CASH / 2 BANK TRANSFER / 3 MOBILE MONEY)", app.consoleInput.paymentMethods(), p::setPaymentMethod);
            update.add("Receipt number", input -> app.records.receipt(input, p), p::setReceiptNumber);
        } else if (record instanceof LibraryBook) {
            LibraryBook b = (LibraryBook) record;
            int onLoan = 0;
            for (BookLoan loan : app.records.loans) {
                if (loan.isActive() && loan.getBook() == b) {
                    onLoan++;
                }
            }
            update.add("ISBN", app.consoleInput.required(), b::setIsbn);
            update.add("Title", app.consoleInput.required(), b::setTitle);
            update.add("Author", app.consoleInput.required(), b::setAuthor);
            update.add("Total copies", app.consoleInput.integer(b.getAvailableCopies() + onLoan, Integer.MAX_VALUE), b::setTotalCopies);
            update.add("Available copies", app.consoleInput.integer(0, b.getTotalCopies() - onLoan), b::setAvailableCopies);
        } else if (record instanceof BookLoan) {
            BookLoan loan = (BookLoan) record;
            update.add("Student", () -> app.recordSelection.select("Student", app.records.students, app.registration::registerStudent), loan::setStudent);
            update.add("Borrowed date (YYYY-MM-DD)", input -> {
                LocalDate value = app.consoleInput.parseDate(input);
                if (!value.isBefore(loan.getDueDate())
                        || (loan.getReturnedDate() != null && value.isAfter(loan.getReturnedDate()))) {
                    throw new ValidationException(
                            "Borrowed date must be before the due date and on or before the return date.");
                }
                return value;
            }, loan::setBorrowedDate);
            update.add("Due date (YYYY-MM-DD)", input -> app.consoleInput.dueDate(input, loan.getBorrowedDate()), loan::setDueDate);
        }

        update.run();
    }

    void addPersonUpdates(UpdateForm update, Person person) {
        update.add("First name", app.consoleInput.name(60), person::setFirstName);
        update.add("Last name", app.consoleInput.name(60), person::setLastName);
        update.add("Email", input -> app.records.personalEmail(input, person), person::setEmail);
        update.add("Phone", app.consoleInput.phone(), person::setPhoneNumber);
        update.add("Date of birth (YYYY-MM-DD)", app.consoleInput.date(true, true), person::setDateOfBirth);
    }

    void addAcademicUpdates(UpdateForm update, AcademicMember member) {
        update.add("Member number", input -> app.records.memberNumber(input, member), member::setMemberNumber);
        update.add("Department", () -> app.recordSelection.select("Department", app.records.departments, app.registration::registerDepartment),
                member::setDepartment);
        update.add("Join date (YYYY-MM-DD)", app.consoleInput.date(false, false), member::setJoinDate);
        update.add("Academic status (1 ACTIVE / 2 INACTIVE)", app.consoleInput.status(), member::setAcademicStatus);
        update.add("Institutional email", input -> app.records.institutionalEmail(input, member), member::setInstitutionalEmail);
        update.add("Advisor name", app.consoleInput.name(120), member::setAdvisorName);
    }

    void addEnrollmentUpdates(UpdateForm update, Enrollment enrollment) {
        update.add("Student", () -> {
            Student student = app.recordSelection.select("Student", app.records.students, app.registration::registerStudent);
            app.records.checkEnrollment(enrollment, student, enrollment.getCourse(),
                    enrollment.getSemester(), enrollment.getEnrollmentStatus());
            return student;
        }, enrollment::setStudent);
        update.add("Course", () -> {
            Course course = app.recordSelection.select("Course", app.records.courses, app.registration::registerCourse);
            app.records.checkEnrollment(enrollment, enrollment.getStudent(), course,
                    enrollment.getSemester(), enrollment.getEnrollmentStatus());
            return course;
        }, enrollment::setCourse);
        update.add("Enrollment date (YYYY-MM-DD)", app.consoleInput.date(true, false), enrollment::setEnrollmentDate);
        update.add("Semester (1 SEMESTER 1 / 2 SEMESTER 2)", input -> {
            String value = app.consoleInput.choices("SEMESTER 1", "SEMESTER 2").parse(input);
            app.records.checkEnrollment(enrollment, enrollment.getStudent(), enrollment.getCourse(),
                    value, enrollment.getEnrollmentStatus());
            return value;
        }, enrollment::setSemester);
        update.add("Status (1 ACTIVE / 2 INACTIVE)", input -> {
            String value = app.consoleInput.status().parse(input);
            if (value.equals("INACTIVE")) {
                for (Assessment assessment : app.records.assessments) {
                    if (assessment.getEnrollment() == enrollment) {
                        throw new ValidationException("This enrollment already has assessments.");
                    }
                }
            }
            app.records.checkEnrollment(enrollment, enrollment.getStudent(), enrollment.getCourse(),
                    enrollment.getSemester(), value);
            return value;
        }, enrollment::setEnrollmentStatus);
    }

    Teacher selectHeadTeacher() throws Cancelled, Back, PrerequisiteMissingException {
        while (true) {
            for (int i = 0; i < app.records.teachers.size(); i++) {
                System.out.println((i + 1) + ". " + app.consoleInput.display(app.records.teachers.get(i)));
            }
            System.out.println((app.records.teachers.size() + 1) + ". Register a Teacher now");
            System.out.println((app.records.teachers.size() + 2) + ". Leave head teacher unassigned");

            String input = app.consoleInput.read("Select a head teacher, BACK, or 0 to cancel: ");
            if (app.consoleInput.isCancel(input) || input.equals("0"))
                throw new Cancelled();
            if (app.consoleInput.isBack(input))
                throw new Back();

            int selection = app.consoleInput.menuNumber(input);
            if (selection >= 1 && selection <= app.records.teachers.size()) {
                return app.records.teachers.get(selection - 1);
            }
            if (selection == app.records.teachers.size() + 1) {
                app.recordSelection.addPrerequisite(app.registration::registerTeacher);
            } else if (selection == app.records.teachers.size() + 2) {
                return null;
            } else {
                System.out.println("Choose a number from the list.");
            }
        }
    }

    class UpdateForm {
        final List<String> labels = new ArrayList<>();
        final List<Action> changes = new ArrayList<>();

        <T> void add(String label, Parser<T> parser, Consumer<T> setter) {
            add(label, () -> app.consoleInput.ask(label, parser), setter);
        }

        <T> void add(String label, Reader<T> reader, Consumer<T> setter) {
            labels.add(label);
            changes.add(() -> {
                while (true) {
                    try {
                        T value = reader.read();
                        System.out.println(label + " -> " + app.consoleInput.display(value));
                        app.consoleInput.confirm("Save change");
                        setter.accept(value);
                        System.out.println("Record updated.");
                        return;
                    } catch (Back exception) {
                        return;
                    } catch (PrerequisiteMissingException | ValidationException
                            | DuplicateRecordException | BookUnavailableException exception) {
                        System.out.println(exception.getMessage());
                    }
                }
            });
        }

        void run() throws Cancelled {
            while (true) {
                for (int i = 0; i < labels.size(); i++) {
                    System.out.println((i + 1) + ". " + labels.get(i));
                }
                String input = app.consoleInput.read("Choose a field to update or 0 to return: ");
                if (input.equals("0") || app.consoleInput.isCancel(input) || app.consoleInput.isBack(input))
                    return;
                int choice = app.consoleInput.menuNumber(input);
                if (choice >= 1 && choice <= changes.size()) {
                    changes.get(choice - 1).run();
                    return;
                }
                System.out.println("Choose a field number from the list.");
            }
        }
    }
}
