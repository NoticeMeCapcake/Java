package AOP.aspects;

import AOP.Student;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Aspect
public class UneversityLoggingAspect {

    @Before("execution(* AOP.University.getStudents())")
    public void logBeforeGetStudents() {
        System.out.println("Logging before getStudents");
    }

    @AfterReturning(pointcut = "execution(* AOP.University.getStudents())",
    returning = "students")
    public void logAfterReturningGetStudents(List<Student> students) {
        System.out.println("Logging after getStudents");
        Student student = students.get(0);
        student.setName("Mighty " + student.getName());
    }

    @AfterThrowing(pointcut = "execution(* AOP.University.getStudents())",
    throwing = "e")
    public void logAfterThrowingGetStudents(Exception e) {
        System.out.println("Logging after throwing getStudents: " + e.getMessage());
    }

    @After("execution(* AOP.University.getStudents())")
    public void logAfterGetStudents() {
        System.out.println("Logging after getStudents wether throws Exception or not");
    }

}
