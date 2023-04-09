package AOP;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class Test3 {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class)) {
            UniverLibrary library = context.getBean("univerLibraryBean", UniverLibrary.class);
            String bookTitle = library.returnBook();
            System.out.println("Info from returnBook: " + bookTitle);
        }
    }
}
