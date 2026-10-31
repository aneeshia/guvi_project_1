package ManageStudentRecords.helper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class StudentService {
    private final List<Student> students;
    private final String[] subjects;
    private final StudentInputScanner inputScanner;
    private final StudentFileHandler fileHandler;


    public StudentService(String filePath, String[] subjects, StudentInputScanner inputScanner) {
        this.subjects = subjects.clone();
        this.inputScanner = inputScanner;
        this.fileHandler = new StudentFileHandler(filePath, subjects.length);
        this.students = new ArrayList<>(fileHandler.loadStudents());
    }

    private void save() {
        fileHandler.saveStudents(students);
    }

    public void addStudent() {
        // prompt student name, dob, class, section, marks(optional)

        System.out.println("1. Add Student ========");
        String name = inputScanner.scanName();
        LocalDate dob = inputScanner.scanDob();
        String grade = inputScanner.scanGrade();
        String section = inputScanner.scanSection();
        Integer[] scores = inputScanner.scanSubjectMarks(this.subjects);

        String id = String.valueOf(UUID.randomUUID());
        Student newStudent = new Student(id, name, dob, grade, section, scores);

        students.add(newStudent);
        System.out.println("Student added successfully with ID: " + id + "\n --------- \n\n");
        save();
    }
    private Student selectStudent(String search) {
        List<Student> matches = students.stream()
                .filter(s ->
                        s.getName().toLowerCase().contains(search.toLowerCase()) ||
                                s.getId().equalsIgnoreCase(search))
                .collect(Collectors.toList());

        if (matches.isEmpty()) {
            System.out.println("No student found.");
            return null;
        }

        if (matches.size() == 1) {
            return matches.get(0);
        }

        System.out.println("Multiple students found:");

        for (int i = 0; i < matches.size(); i++) {
            Student s = matches.get(i);

            System.out.printf(
                    "%d. %s | Grade: %s | Section: %s | ID: %s%n",
                    i + 1,
                    s.getName(),
                    s.getGrade(),
                    s.getSection(),
                    s.getId()
            );
        }

        int choice = inputScanner.scanChoice(
                "Choose a student",
                1,
                matches.size()
        );

        return matches.get(choice - 1);
    }
    private void printStudentTable(List<Student> records) {
        System.out.printf(
                "%-36s %-18s %-12s %-7s %-8s%n",
                "ID", "Name", "DOB", "Grade", "Section"
        );

        System.out.println("-".repeat(85));

        for (Student student : records) {
            System.out.printf(
                    "%-36s %-18s %-12s %-7s %-8s%n",
                    student.getId(),
                    student.getName(),
                    student.getDob(),
                    student.getGrade(),
                    student.getSection()
            );
        }
    }
    private void viewAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No student records available.");
            return;
        }
        int pageSize = 10;
        int currentIndex = 0;

        while (currentIndex < students.size()) {
            int endIndex = Math.min(currentIndex + pageSize, students.size());

            List<Student> page = students.subList(currentIndex, endIndex);

            System.out.println("\n--- Student Records ("
                    + (currentIndex + 1) + "-" + endIndex
                    + " of " + students.size() + ") ---");

            printStudentTable(page);

            currentIndex = endIndex;

            // No more records to display
            if (currentIndex >= students.size()) {
                System.out.println("End of student records.\n");
                return;
            }

            System.out.println("\n1. Load next 10 students");
            System.out.println("2. Return to main menu");

            int choice = inputScanner.scanChoice("Choose an option", 1, 2);

            if (choice == 2) {
                return;
            }
        }
    }

    public void updateStudent() {
        // prompt search student with id name,then which property to update
        System.out.println("2. Update Student ========");

        Student student = selectStudent(inputScanner.scanSearchTerm());

        if (student == null) {
            return;
        }

        System.out.printf("%nUpdating: %s | Grade: %s | Section: %s%n",
                student.getName(), student.getGrade(), student.getSection());
        System.out.print("""
                1. Name
                2. Date of Birth
                3. Grade
                4. Section
                5. Subject Marks
                6. Cancel
                
                """);

        int choice = inputScanner.scanChoice("Choose a property",1,6);

        switch (choice) {
            case 1:
                student.setName(inputScanner.scanName());
                break;

            case 2:
                student.setDob(inputScanner.scanDob());
                break;

            case 3:
                student.setGrade(inputScanner.scanGrade());
                break;

            case 4:
                student.setSection(inputScanner.scanSection());
                break;

            case 5:
                student.setScores(
                        inputScanner.scanSubjectMarks(this.subjects)
                );
                break;

            case 6:
                System.out.print("""
                Update cancelled.
                ---------
                
                """);
                return;
        }

        save();

        System.out.print("""
                Student updated successfully
                ---------
                
                """);
    }
    public void deleteStudent() {
        // prompt student id
        System.out.println("3. Delete Student ========");

        Student student = selectStudent(inputScanner.scanSearchTerm());

        if (student == null) {
            return;
        }

        System.out.println(
                "Delete " + student.getName() +
                        " (ID: " + student.getId() + ")?"
        );

        if (inputScanner.scanConfirmation(
                "Confirm deletion (y/n): ")) {

            students.remove(student);

            save();

            System.out.print("""
                Student deleted successfully
                ---------
                
                """);
        } else {
            System.out.print("""
                Deletion cancelled.
                ---------
                
                """);
        }
    }
    public void viewStudent() {
        // prompt student id, name => show.
        // in future may be show all - first 10 and load more or cancel or search - complicated.
        System.out.println("4. View Student ========");
        String search = inputScanner.scanSearchTerm(true);

        if (search.equalsIgnoreCase("all")) {
            viewAllStudents();
            return;
        }

        Student student = selectStudent(search);

        if (student == null) {
            return;
        }

        System.out.printf("""
            ID: %s
            Name: %s
            D.O.B: %s
            Age: %d
            Grade: %s
            Section: %s
            """,
            student.getId(),
            student.getName(),
            student.getDob(),
            student.getAge(),
            student.getGrade(),
            student.getSection()
        );

        Integer[] marks = student.getScores();

        for (int i = 0; i < this.subjects.length; i++) {
            System.out.println(
                    this.subjects[i] + ": " + marks[i]
            );
        }
        System.out.print("""
                ---------
                
                """);

    }
}
