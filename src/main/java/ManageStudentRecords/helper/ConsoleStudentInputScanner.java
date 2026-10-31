package ManageStudentRecords.helper;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ConsoleStudentInputScanner implements StudentInputScanner {
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
                // Handle valid D.O.B range
                if (!StudentValidator.isValidDob(dob)) {
                    System.out.println(
                            "Invalid DOB. Student age must be between 3 and 150."
                    );
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