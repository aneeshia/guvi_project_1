package ManageStudentRecords;

import ManageStudentRecords.helper.ConsoleStudentInputScanner;
import ManageStudentRecords.helper.StudentInputScanner;
import ManageStudentRecords.helper.StudentService;

import java.util.Scanner;

class Main {

    private static final Scanner scan = new Scanner(System.in);
    private static final StudentInputScanner inputScanner = new ConsoleStudentInputScanner(scan);
    private static final String[] SUBJECTS = {"English","Math","Biology","Chemistry","Physics"};
    private static final String FILE_PATH = "studentRecords.csv";

    static void main(String[] args) {


        int userChoice;
        do {
            System.out.print("""
                Manage Student Records:
                1. Add New Student
                2. Update Student Details
                3. Delete Student
                4. View Student Details
                5. Exit
                
                """);
            userChoice = inputScanner.scanChoice(
                    "Choose an option", 1, 5
            );
            checkUserChoice(userChoice);
        } while (userChoice != 5);
    }

    public static void checkUserChoice(int userChoice) {
        StudentService studentService = new StudentService(FILE_PATH, SUBJECTS, inputScanner);

        switch (userChoice) {
            case 1:
                studentService.addStudent();
                break;
            case 2:
                studentService.updateStudent();
                break;
            case 3:
                studentService.deleteStudent();
                break;
            case 4:
                studentService.viewStudent();
                break;
            case 5:
                System.out.println("Exiting program");
                return;
            default:
                System.out.println("Incorrect input. Please try again.\n");
        }
    }

}
