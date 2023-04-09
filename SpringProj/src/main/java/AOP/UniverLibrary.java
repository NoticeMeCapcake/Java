package AOP;

import org.springframework.stereotype.Component;

@Component("univerLibraryBean")
public class UniverLibrary {
    public void getBook() {
        System.out.println("Getting book from univer library");
        System.out.println("----------------------------------------------");
    }

    public void getMagazine() {
        System.out.println("Getting magazine from univer library");
        System.out.println("----------------------------------------------");
    }

    public String returnBook() {
        System.out.println("Return book to univer library");
        return "Jekyll and Hyde";
    }
    public void returnMagazine() {
        System.out.println("Return magazine to univer library");
        System.out.println("----------------------------------------------");
    }

    public void addBook(String personName, Book book) {
        System.out.println("Adding book to univer library");
        System.out.println("----------------------------------------------");
    }
    public void addMagazine() {
        System.out.println("Adding magazine to univer library");
        System.out.println("----------------------------------------------");
    }
}
