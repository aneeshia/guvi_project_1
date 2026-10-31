package ManageStudentRecords.helper;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StudentFileHandler {
    private final String filePath;
    private final int subjectCount;

    public StudentFileHandler(String filePath, int subjectCount) {
        this.filePath = filePath;
        this.subjectCount = subjectCount;
    }

    public void saveStudents(List<Student> students) {
        saveToCsvFile(filePath, students);
        // later incase a different file per grade or subject required.
    }

    public List <Student> loadStudents() {
        return loadFromCsvFile(filePath);
    }


    public List <Student> loadFromCsvFile(String filePath) {
        List <Student> studentsFromCSV = new ArrayList<>();
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
                            || scoreTokens.length != this.subjectCount
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
    public void saveToCsvFile(String filePath, List <Student> students) {

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

}
