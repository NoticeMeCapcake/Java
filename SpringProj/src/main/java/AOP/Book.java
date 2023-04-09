package AOP;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("bookBean")
@Getter
@Setter
@Log
public class Book {
    @Value("Jekyll and Hyde")
    private String title;
    @Value("1886")
    private int yearOfPublication;
    public Book() {
        log.info("Book constructor");
    }
}
