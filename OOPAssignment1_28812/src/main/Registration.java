package com.auca.main;

import com.auca.abstracts.Person;
import com.auca.administrator.Administrator;
import com.auca.assessment.Assessment;
import com.auca.classroom.Classroom;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.auca.main.Forms.*;

final class Registration {

    private final Main app;

    Registration(Main app) {
        this.app = app;
    }

    void registerDepartment() throws Cancelled {
        Form form = app.forms.new Form("Department", 2, 4);
        Field<String> id = addId(form, "Department ID");
        Field<String> name = form.add("Department name (2-60 characters)", app.consoleInput.name(60));
        Field<String> code = form.add("Department code", app.consoleInput.required());
        Field<String> office = form.add("Office location", app.consoleInput.required());
        Field<String> email = form.add("Contact email", app.consoleInput.email());
        form.fixed("Head teacher", "Unassigned");

        Department department = form.complete(() -> new Department(
                id.value, name.value, code.value, office.value, email.value, null));
        app.records.departments.add(department);
        saved(department);
    }

    void registerStudent() throws Cancelled {
        while (true) {
            Form form = app.forms.new Form("Student", 6, 12);
            PersonFields person = new PersonFields(form);
            AcademicFields academic = new AcademicFields(form);

            Field<String> number = form.add("Student number (4-30 characters)", input -> {
                String value = app.consoleInput.idValue(input);
                for (Student student : app.records.students) {
                    app.records.unique(value, student.getStudentNumber(), "Student number");
                }
                return value;
            });
            Field<String> program = form.add("Program name (2-120 characters)", app.consoleInput.name(120));
            Field<LocalDate> admission = form.add("Admission date (YYYY-MM-DD)", app.consoleInput.date(true, false));
            Field<Integer> year = form.add("Study year (" + Student.MIN_STUDY_YEAR + "-"
                    + Student.MAX_STUDY_YEAR + ")",
                    app.consoleInput.integer(Student.MIN_STUDY_YEAR, Student.MAX_STUDY_YEAR));
            Field<String> guardian = form.add("Guardian phone (10-15 digits)", app.consoleInput.phone());
            Field<Boolean> scholarship = form.add("Scholarship (1 YES / 2 NO)", app.consoleInput.yesNo());

            Student student = form.complete(() -> new Student(
                    person.id.value, person.first.value, person.last.value,
                    person.email.value, person.phone.value, person.birth.value,
                    academic.number.value, academic.department.value, academic.joined.value,
                    academic.status.value, academic.email.value, academic.advisor.value,
                    number.value, program.value, admission.value, year.value,
                    guardian.value, scholarship.value));

            app.records.students.add(student);
            System.out.println("Student " + student.getStudentNumber() + " registered successfully.");

            while (true) {
                String next = app.consoleInput.read("1. Register another  2. View student  0. Main menu: ");
                if (next.equals("1")) {
                    break;
                }
                if (next.equals("0") || app.consoleInput.isCancel(next)) {
                    return;
                }
                if (next.equals("2")) {
                    student.printDetails();
                } else {
                    System.out.println("Choose 1, 2, or 0.");
                }
            }
        }
    }

    void registerTeacher() throws Cancelled {
        Form form = app.forms.new Form("Teacher", 6, 12);
        PersonFields person = new PersonFields(form);
        AcademicFields academic = new AcademicFields(form);

        Field<String> employee = form.add("Employee number", input -> {
            String value = app.consoleInput.required().parse(input);
            for (Teacher teacher : app.records.teachers) {
                app.records.unique(value, teacher.getEmployeeNumber(), "Employee number");
            }
            return value;
        });
        Field<String> specialization = form.add("Specialization", app.consoleInput.required());
        Field<LocalDate> hired = form.add("Hire date (YYYY-MM-DD)", app.consoleInput.date(true, false));
        Field<BigDecimal> salary = form.add("Salary (positive decimal)", app.consoleInput.money());
        Field<String> office = form.add("Office number", app.consoleInput.required());
        Field<String> employment = form.add("Employment type", app.consoleInput.required());

        Teacher teacher = form.complete(() -> new Teacher(
                person.id.value, person.first.value, person.last.value,
                person.email.value, person.phone.value, person.birth.value,
                academic.number.value, academic.department.value, academic.joined.value,
                academic.status.value, academic.email.value, academic.advisor.value,
                employee.value, specialization.value, hired.value,
                salary.value, office.value, employment.value));
        app.records.teachers.add(teacher);
        saved(teacher);
    }

    void registerAdministrator() throws Cancelled {
        Form form = app.forms.new Form("Administrator", 4, 8);
        PersonFields person = new PersonFields(form);
        Field<String> staff = form.add("Staff number", input -> {
            String value = app.consoleInput.required().parse(input);
            for (Administrator administrator : app.records.administrators) {
                app.records.unique(value, administrator.getStaffNumber(), "Staff number");
            }
            return value;
        });
        Field<String> job = form.add("Job title", app.consoleInput.required());
        Field<String> office = form.add("Assigned office", app.consoleInput.required());
        Field<LocalDate> employed = form.add("Employment date (YYYY-MM-DD)", app.consoleInput.date(true, false));
        Field<Integer> access = form.add("Access level (positive integer)", app.consoleInput.integer(1, Integer.MAX_VALUE));
        Field<Boolean> active = form.add("Active (1 YES / 2 NO)", app.consoleInput.yesNo());

        Administrator administrator = form.complete(() -> new Administrator(
                person.id.value, person.first.value, person.last.value,
                person.email.value, person.phone.value, person.birth.value,
                staff.value, job.value, office.value, employed.value, access.value, active.value));
        app.records.administrators.add(administrator);
        saved(administrator);
    }

    void registerCourse() throws Cancelled {
        Form form = app.forms.new Form("Course", 2, 4);
        Field<String> id = addId(form, "Course ID");
        Field<String> code = form.add("Course code", input -> {
            String value = app.consoleInput.required().parse(input);
            for (Course course : app.records.courses) {
                app.records.unique(value, course.getCourseCode(), "Course code");
            }
            return value;
        });
        Field<String> title = form.add("Course title", app.consoleInput.required());
        Field<Integer> credits = form.add("Credit hours (1-6)", app.consoleInput.integer(1, 6));
        Field<Department> department = form.select("Department", app.records.departments, this::registerDepartment);
        Field<Teacher> teacher = form.select("Assigned teacher", app.records.teachers, this::registerTeacher);

        Course course = form.complete(() -> new Course(id.value, code.value, title.value,
                credits.value, department.value, teacher.value));
        app.records.courses.add(course);
        saved(course);
    }

    void registerClassroom() throws Cancelled {
        Form form = app.forms.new Form("Classroom", 2, 4);
        Field<String> id = addId(form, "Classroom ID");
        Field<String> building = form.add("Building name", app.consoleInput.required());
        Field<String> room = form.add("Room number", app.consoleInput.required());
        Field<Integer> capacity = form.add("Capacity (positive integer)", app.consoleInput.integer(1, Integer.MAX_VALUE));
        Field<Integer> floor = form.add("Floor number (0 or greater)", app.consoleInput.integer(0, Integer.MAX_VALUE));
        Field<Boolean> available = form.add("Available (1 YES / 2 NO)", app.consoleInput.yesNo());

        Classroom classroom = form.complete(() -> new Classroom(id.value, building.value,
                room.value, capacity.value, floor.value, available.value));
        app.records.classrooms.add(classroom);
        saved(classroom);
    }

    void enrollStudent() throws Cancelled {
        Form form = app.forms.new Form("Enrollment", 2, 4);
        Field<String> id = addId(form, "Enrollment ID");
        Field<Student> student = form.select("Student", app.records.students, this::registerStudent);
        Field<Course> course = form.select("Course", app.records.courses, this::registerCourse);
        Field<LocalDate> enrolled = form.add("Enrollment date (YYYY-MM-DD)", app.consoleInput.date(true, false));
        Field<String> semester = form.add("Semester (1 SEMESTER 1 / 2 SEMESTER 2)",
                app.consoleInput.choices("SEMESTER 1", "SEMESTER 2"));
        Field<String> status = form.add("Status (1 ACTIVE / 2 INACTIVE)", app.consoleInput.status());

        Enrollment enrollment = form.complete(() -> {
            app.records.checkEnrollment(null, student.value, course.value, semester.value, status.value);
            return new Enrollment(id.value, student.value, course.value, enrolled.value,
                    semester.value, status.value);
        });
        app.records.enrollments.add(enrollment);
        saved(enrollment);
    }

    void recordAssessment() throws Cancelled {
        Form form = app.forms.new Form("Assessment", 2, 4);
        Field<String> id = addId(form, "Assessment ID");
        Field<Enrollment> enrollment = form.add("Active enrollment",
                () -> app.recordSelection.selectActiveEnrollment(this::enrollStudent));
        Field<String> type = form.add("Type (1 COURSEWORK / 2 MIDTERM / 3 FINAL EXAM)",
                app.consoleInput.assessmentTypes());
        Field<Double> maximum = form.add("Maximum score (positive decimal)", app.consoleInput.positiveScore());
        Field<Double> obtained = form.add("Obtained score", input -> app.consoleInput.score(input, maximum.value));
        Field<LocalDate> assessed = form.add("Assessment date (YYYY-MM-DD)", app.consoleInput.date(false, false));

        Assessment assessment = form.complete(() -> new Assessment(id.value, enrollment.value,
                type.value, maximum.value, obtained.value, assessed.value));
        app.records.assessments.add(assessment);
        saved(assessment);
    }

    void recordPayment() throws Cancelled {
        Form form = app.forms.new Form("Fee Payment", 2, 4);
        Field<String> id = addId(form, "Payment ID");
        Field<Student> student = form.select("Student", app.records.students, this::registerStudent);
        Field<BigDecimal> amount = form.add("Amount (positive decimal)", app.consoleInput.money());
        Field<LocalDate> paid = form.add("Payment date (YYYY-MM-DD)", app.consoleInput.date(false, false));
        Field<String> method = form.add("Payment method (1 CASH / 2 BANK TRANSFER / 3 MOBILE MONEY)",
                app.consoleInput.paymentMethods());
        Field<String> receipt = form.add("Receipt number", input -> app.records.receipt(input, null));

        FeePayment payment = form.complete(() -> new FeePayment(id.value, student.value,
                amount.value, paid.value, method.value, receipt.value));
        app.records.payments.add(payment);
        saved(payment);
    }

    void registerBook() throws Cancelled {
        Form form = app.forms.new Form("Library Book", 2, 4);
        Field<String> id = addId(form, "Book ID");
        Field<String> isbn = form.add("ISBN", app.consoleInput.required());
        Field<String> title = form.add("Title", app.consoleInput.required());
        Field<String> author = form.add("Author", app.consoleInput.required());
        Field<Integer> total = form.add("Total copies", app.consoleInput.integer(0, Integer.MAX_VALUE));
        Field<Integer> available = form.add("Available copies", input -> app.consoleInput.integer(0, total.value).parse(input));

        LibraryBook book = form.complete(() -> new LibraryBook(id.value, isbn.value, title.value,
                author.value, total.value, available.value));
        app.records.books.add(book);
        saved(book);
    }

    class PersonFields {
        final Field<String> id;
        final Field<String> first;
        final Field<String> last;
        final Field<String> email;
        final Field<String> phone;
        final Field<LocalDate> birth;

        PersonFields(Form form) {
            id = addId(form, "Person ID");
            first = form.add("First name (2-60 characters)", app.consoleInput.name(60));
            last = form.add("Last name (2-60 characters)", app.consoleInput.name(60));
            email = form.add("Personal email", input -> app.records.personalEmail(input, null));
            phone = form.add("Phone (10-15 digits, optional leading +)", app.consoleInput.phone());
            birth = form.add("Date of birth (YYYY-MM-DD)", app.consoleInput.date(true, true));
        }
    }

    class AcademicFields {
        final Field<String> number;
        final Field<Department> department;
        final Field<LocalDate> joined;
        final Field<String> status;
        final Field<String> email;
        final Field<String> advisor;

        AcademicFields(Form form) {
            number = form.add("Member number (4-30 characters)", input -> app.records.memberNumber(input, null));
            department = form.select("Department", app.records.departments, Registration.this::registerDepartment);
            joined = form.add("Join date (YYYY-MM-DD)", app.consoleInput.date(false, false));
            status = form.add("Academic status (1 ACTIVE / 2 INACTIVE)", app.consoleInput.status());
            email = form.add("Institutional email", input -> app.records.institutionalEmail(input, null));
            advisor = form.add("Advisor name (2-120 characters)", app.consoleInput.name(120));
        }
    }

    Field<String> addId(Form form, String label) {
        return form.add(label + " (4-30 characters)", input -> {
            String value = app.consoleInput.idValue(input);
            app.records.checkNewId(value);
            return value;
        });
    }

    void saved(Identifiable record) {
        System.out.println("Saved: " + record.getId() + " - " + record.getDisplayName());
    }

}
