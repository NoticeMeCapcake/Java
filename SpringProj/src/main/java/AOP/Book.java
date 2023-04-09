package AOP;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("bookBean")
public class Book {
    @Value("Jekyll and Hyde")
    private String title;

    public String getTitle() {
        return title;
    }
}
