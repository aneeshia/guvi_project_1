package ManageStudentRecords.helper;

import java.time.LocalDate;

public final class StudentValidator {
    private StudentValidator() { }

    public static boolean isValidName(String value) {
        return value != null && !value.trim().isEmpty() && !value.contains(",");
    }

    public static boolean isValidDob(LocalDate dob) {
        if (dob == null || dob.isAfter(LocalDate.now())) {
            return false;
        }
        LocalDate earliestDob = LocalDate.now().minusYears(150);
        LocalDate latestDob = LocalDate.now().minusYears(3);

        return !dob.isBefore(earliestDob)
                && !dob.isAfter(latestDob);
    }

    public static boolean isValidGrade(String value) {
        return value != null && !value.trim().isEmpty() && !value.contains(",");
    }

    public static boolean isValidSection(String value) {
        return value != null && !value.trim().isEmpty() && !value.contains(",");
    }

    public static boolean areValidScores(Integer[] scores, int subjectCount) {
        if (scores == null || scores.length != subjectCount) return false;
        for (Integer score : scores) {
            if (score == null || score < 0 || score > 100) return false;
        }
        return true;
    }

    public static boolean isValidStudent(Student student, int subjectCount) {
        return student != null
                && student.getId() != null && !student.getId().trim().isEmpty()
                && isValidName(student.getName())
                && isValidDob(student.getDob())
                && isValidGrade(student.getGrade())
                && isValidSection(student.getSection())
                && areValidScores(student.getScores(), subjectCount);
    }
}
