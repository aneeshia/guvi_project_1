import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

// Question: Build a console-based application to manage student records with features like
// add, update, delete, and view.
// Focuses on core Java, OOP, collections, and file handling

public class ManageStudentRecords {

    private static final Scanner scan = new Scanner(System.in);

    public interface StudentInputScanner {
        String scanName();
        LocalDate scanDob();
        String scanGrade();
        String scanSection();
        Integer[] scanSubjectMarks(String[] subjects);

        String scanSearchTerm();
        String scanSearchTerm(boolean allowAll);
        int scanChoice(String prompt, int min, int max);
        boolean scanConfirmation(String prompt);
    }

    public static class ConsoleStudentInputScanner implements StudentInputScanner {
        private final Scanner scanner;
        public ConsoleStudentInputScanner(Scanner scan) {
            this.scanner = scan;
        }

        private String readNonEmptyInput(String prompt) {
            while(true) {
                System.out.println(prompt);
                String input = scanner.nextLine().trim();

                // Handle empty string input
                if(input.isEmpty()) {
                    System.out.println("Input can't be empty.");
                }
                // Handle comma  (for now to avoid having issues in csv)
                else if(input.contains(",")) {
                    System.out.println("Commas are not allowed.");
                } else {
                    return input;
                }
            }
        }

        @Override
        public String scanName() {
            return readNonEmptyInput("Name: ");
        }

        @Override
        public LocalDate scanDob() {
            while (true) {
                System.out.print("D.O.B (yyyy-MM-dd): ");
                try {
                    LocalDate dob = LocalDate.parse(scanner.nextLine().trim());

                    // Handle future D.O.B
                    if(dob.isAfter(LocalDate.now())) {
                        System.out.println("Date of birth cannot be in the future.");
                        continue;
                    }
                    return dob;
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid format! Please use yyyy-MM-dd.");
                }
            }
        }

        @Override
        public String scanGrade() {
            return readNonEmptyInput("Grade: ");
        }

        @Override
        public String scanSection() {
            return readNonEmptyInput("Section: ");
        }

        @Override
        public Integer[] scanSubjectMarks(String[] subjects) {
            Integer[] scores = new Integer[subjects.length];
            System.out.println("Enter Subject Marks (Out of 100) ---");
            for (int i = 0; i < subjects.length; i++) {
                while (true) {
                    System.out.print(subjects[i] + ": ");
                    try {
                        int mark = Integer.parseInt(scanner.nextLine().trim());
                        if (mark >= 0 && mark <= 100) {
                            scores[i] = mark;
                            break;
                        }
                        System.out.println("Invalid mark! Must be 0 to 100");
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid integer");
                    }
                }
            }
            return scores;
        }

        @Override
        public String scanSearchTerm() {
            return scanSearchTerm(false);
        }

        @Override
        public String scanSearchTerm(boolean allowAll) {
            while (true) {
                String prompt = "Enter student name or ID";

                if (allowAll) {
                    prompt += "\n(or type 'all' to view all students)";
                }
                System.out.print(prompt + ": ");
                String value = scanner.nextLine().trim();

                if (!value.isEmpty()) {
                    return value;
                }

                System.out.println("Input cannot be empty.");
            }
        }

        @Override
        public int scanChoice(String prompt, int min, int max) {
            while (true) {
                System.out.print(prompt + ": ");

                try {
                    int choice = Integer.parseInt(
                            scanner.nextLine().trim()
                    );

                    if (choice >= min && choice <= max) {
                        return choice;
                    }

                    System.out.printf(
                            "Enter a number between %d and %d.%n",
                            min, max
                    );

                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer.");
                }
            }
        }

        @Override
        public boolean scanConfirmation(String prompt) {
            while (true) {
                System.out.print(prompt);
                String answer = scanner.nextLine()
                        .trim()
                        .toLowerCase();

                if (answer.equals("y") || answer.equals("yes")) {
                    return true;
                }

                if (answer.equals("n") || answer.equals("no")) {
                    return false;
                }

                System.out.println("Please enter y or n.");
            }
        }
    }

    private static final StudentInputScanner inputScanner = new ConsoleStudentInputScanner(scan);
    private static final String[] SUBJECTS = {"English","Math","Biology","Chemistry","Physics"};
    private static final String FILE_PATH = "studentRecords.csv";

    static List<Student> students = new ArrayList<> ();

    static public void InitializeStudentsData() {
        // Load existing student records from the file.
        try {
            students = loadFromCsvFile(FILE_PATH);
            if (!(students instanceof ArrayList)) {
                students = new ArrayList < > (students);
            }
        } catch (Exception e) {
            System.out.println("Starting with a blank list.");
            students = new ArrayList < > ();
        }
    }

    public static void main(String[] args) {

        // Build a console-based application to manage student records with features like
        // add, update, delete, and view.
        // Focuses on core Java, OOP, collections, and file handling

        // create an arraylist of student type and assigning from file buffer somehow
        InitializeStudentsData();

        int userChoice;
        do {
            System.out.println(
                    "Manage Student Records: \n" +
                            "1. Add New Student\n" +
                            "2. Update Student Details\n" +
                            "3. Delete Student\n" +
                            "4. View Student Details\n" +
                            "5. Exit\n\n"
            );
            userChoice = inputScanner.scanChoice(
                    "Choose an option", 1, 5
            );
            checkUserChoice(userChoice);
        } while (userChoice != 5);

    }
    public static void saveToFile(List < Student > students) {
        saveToCsvFile(FILE_PATH, students);
        // later incase a different file per grade or subject required.
    }
    public static void checkUserChoice(int userChoice) {
        switch (userChoice) {
            case 1:
                addStudent();
                break;
            case 2:
                updateStudent();
                break;
            case 3:
                deleteStudent();
                break;
            case 4:
                viewStudent();
                break;
            case 5:
                System.out.println("Exiting program");
                return;
            default:
                System.out.println("Incorrect input. Please try again.\n");
        }
    }
    public static List < Student > loadFromCsvFile(String filePath) {
        List < Student > studentsFromCSV = new ArrayList<> ();

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("No existing records. Starting fresh.\n");
            return studentsFromCSV;
        }
        int lineNumber = 0;
        int loadedCount = 0;
        int skippedCount = 0;

        try (BufferedReader bufReader = new BufferedReader(new FileReader(file))) {
            String currentLine;

            while ((currentLine = bufReader.readLine()) != null) {
                lineNumber++;
                currentLine = currentLine.trim();
                if (currentLine.isEmpty()) {
                    continue;
                }

                try {
                    String[] fields = currentLine.split(",", -1); // csv

                    // Expect 6 fields: id, name, dob, grade, section, scores
                    if (fields.length != 6) {
                        System.out.println("Skipping invalid CSV record."+ lineNumber);
                        skippedCount++;
                        continue;
                    }

                    String id = fields[0].trim();
                    String name = fields[1].trim();
                    LocalDate dob = LocalDate.parse(fields[2].trim());
                    String grade = fields[3].trim();
                    String section = fields[4].trim();

                    // Extract  "85;90;78;92;88"
                    String[] scoreTokens = fields[5].split(";", -1);
                    // Check required fields and marks
                    if (id.isEmpty() || name.isEmpty() || grade.isEmpty() || section.isEmpty()
                            || scoreTokens.length != SUBJECTS.length
                            || dob.isAfter(LocalDate.now())) {
                        System.out.println("Skipping invalid student record: " + id);
                        skippedCount++;
                        continue;
                    }

                    Integer[] scores = new Integer[scoreTokens.length];

                    for (int i = 0; i < scoreTokens.length; i++) {
                        scores[i] = Integer.parseInt(scoreTokens[i].trim());

                        if (scores[i] < 0 || scores[i] > 100) {
                            throw new IllegalArgumentException("Invalid mark");
                        }
                    }
                    studentsFromCSV.add(new Student(id, name, dob, grade, section, scores));
                    loadedCount++;

                } catch (RuntimeException  e) {
//                    System.err.println("Skipping invalid csv record: " + e.getMessage());
                    skippedCount++;
                    System.err.println(
                            "Skipping invalid CSV record at line " + lineNumber
                                    + ": " + e.getMessage()
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading csv file: " + e.getMessage());
            return studentsFromCSV;
        }

        System.out.printf("CSV Student records loaded: %d, skipped: %d%n%n",
                loadedCount, skippedCount);

        return studentsFromCSV;
    }
    public static void saveToCsvFile(String filePath, List < Student > students) {

        try (BufferedWriter bufWriter = new BufferedWriter(new FileWriter(filePath))) {
            for (Student student: students) {

                Integer[] scores = student.getScores();
                String scoreStr = Arrays.stream(scores)
                        .map(String::valueOf)
                        .collect(Collectors.joining(";"));

                String csvLine = String.join(",",
                        student.getId(),
                        student.getName(),
                        student.getDob().toString(), // Save date as yyyy-MM-dd
                        student.getGrade(),
                        student.getSection(),
                        scoreStr
                );
                bufWriter.write(csvLine);
                bufWriter.newLine(); // Move to the next line
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    public static void addStudent() {
        // prompt student name, dob, class, section, marks(optional)

        System.out.println("1. Add Student ========");
        String name = inputScanner.scanName();
        LocalDate dob = inputScanner.scanDob();
        String grade = inputScanner.scanGrade();
        String section = inputScanner.scanSection();
        Integer[] scores = inputScanner.scanSubjectMarks(SUBJECTS);

        String id = String.valueOf(UUID.randomUUID());
        Student newStudent = new Student(id, name, dob, grade, section, scores);

        students.add(newStudent);
        System.out.println("Student added successfully with ID: " + id + "\n --------- \n\n");
        saveToFile(students);
    }
    private static Student selectStudent(String search) {
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
    private static void printStudentTable(List<Student> records) {
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
    private static void viewAllStudents() {
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

    public static void updateStudent() {
        // prompt search student with id name,then which property to update
        System.out.println("2. Update Student ========");

        Student student = selectStudent(inputScanner.scanSearchTerm());

        if (student == null) {
            return;
        }

        System.out.println(
                "\nUpdating: " + student.getName() +
                        " | Grade: " + student.getGrade() +
                        " | Section: " + student.getSection()
        );

        System.out.println(
                "1. Name\n" +
                        "2. Date of Birth\n" +
                        "3. Grade\n" +
                        "4. Section\n" +
                        "5. Subject Marks\n" +
                        "6. Cancel"
        );

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
                        inputScanner.scanSubjectMarks(SUBJECTS)
                );
                break;

            case 6:
                System.out.println("Update cancelled." + "\n --------- \n\n");
                return;
        }

        saveToFile(students);

        System.out.println("Student updated successfully." + "\n --------- \n\n");
    }
    public static void deleteStudent() {
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

            saveToFile(students);

            System.out.println("Student deleted successfully." + "\n --------- \n\n");
        } else {
            System.out.println("Deletion cancelled." + "\n --------- \n\n");
        }
    }
    public static void viewStudent() {
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

        System.out.println("ID: " + student.getId());
        System.out.println("Name: " + student.getName());
        System.out.println("D.O.B: " + student.getDob());
        System.out.println("Age: " + student.getAge());
        System.out.println("Grade: " + student.getGrade());
        System.out.println("Section: " + student.getSection());

        Integer[] marks = student.getScores();

        for (int i = 0; i < SUBJECTS.length; i++) {
            System.out.println(
                    SUBJECTS[i] + ": " + marks[i]
            );
        }
        System.out.println("\n --------- \n\n");

    }

}

class Student {
    // props ID, name, class, age, section, marks
    // but marks can be subject wise for a class or a section with student ID
    // subject would have an ID then subId + studentID becomes index

    private String id;
    private String name;
    private LocalDate dob;
    private String grade;
    private String section;
    private Integer[] scores;

    Student(String id, String name, LocalDate dob, String grade, String section, Integer[] scores) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.grade = grade;
        this.section = section;
        this.scores = scores;
    }

    int getAge() {
        // Calculate age from date of birth.
        LocalDate today = LocalDate.now();
        Period age = Period.between(dob, today);
        return age.getYears();
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public LocalDate getDob() {
        return this.dob;
    }

    public String getGrade() {
        return this.grade;
    }

    public String getSection() {
        return this.section;
    }

    public Integer[] getScores() {
        return this.scores;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setScores(Integer[] scores) {
        this.scores = scores;
    }
}


