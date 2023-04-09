package AOP;

import org.springframework.stereotype.Component;

@Component("univerLibraryBean")
public class UniverLibrary {
    public void getBook() {
        System.out.println("Getting book from univer library");
    }

    public void getMagazine() {
        System.out.println("Getting journal from univer library");
    }

    public void returnBook() {
        System.out.println("Return book to univer library");
    }
    public void returnMagazine() {
        System.out.println("Return magazine to univer library");
    }

    public void addBook() {
        System.out.println("Adding book to univer library");
    }
    public void addMagazine() {
        System.out.println("Adding magazine to univer library");
    }
}
