package AOP;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test1 {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class)) {
            Library library = context.getBean("libraryBean", Library.class);
            library.getBook();

        }
    }
}
