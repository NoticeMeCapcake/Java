package AOP;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component

public class University {
    private List<Student> students = new ArrayList<>();

    public void addStudents() {
        students.add(new Student("John Doe", 1, 4.5));
        students.add(new Student("Jane Doe", 2, 3.5));
        students.add(new Student("Joe Doe", 3, 2.5));
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public List<Student> getStudents() {
        System.out.println("Info from getStudents: " + students);
        students.get(3);
        return students;
    }
}
