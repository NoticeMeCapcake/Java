package AOP;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class Test2 {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class)) {
            University university = context.getBean("university", University.class);
            university.addStudents();
            try {
                List<Student> studentList = university.getStudents();
                System.out.println("Info from getStudents: " + studentList);
            }
            catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
