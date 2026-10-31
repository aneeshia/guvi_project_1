package ManageStudentRecords.helper;

import java.time.LocalDate;
import java.time.Period;

public class Student {
    // props ID, name, class, age, section, marks
    // but marks can be subject wise for a class or a section with student ID
    // subject would have an ID then subId + studentID becomes index

    private final String id;
    private String name;
    private LocalDate dob;
    private String grade;
    private String section;
    private Integer[] scores;

    public Student(String id, String name,
                   LocalDate dob, String grade,
                   String section, Integer[] scores) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.grade = grade;
        this.section = section;
        this.scores = scores;
    }

    public int getAge() {
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