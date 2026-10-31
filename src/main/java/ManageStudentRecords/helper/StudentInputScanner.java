package ManageStudentRecords.helper;

import java.time.LocalDate;

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
