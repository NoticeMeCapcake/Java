package AOP;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test1 {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class)) {
            UniverLibrary library = context.getBean("univerLibraryBean", UniverLibrary.class);
            Book book = context.getBean("bookBean", Book.class);
            library.addBook("Jekyll and Hyde", book);
            library.addMagazine();
//            library.getBook();
//            library.getMagazine();

//            library.getJournal();

//            SchoolLibrary schoolLibrary = context.getBean("schoolLibraryBean", SchoolLibrary.class);
//            schoolLibrary.getBook();

        }
    }
}
