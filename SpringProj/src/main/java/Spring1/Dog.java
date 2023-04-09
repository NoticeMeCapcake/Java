package Spring1;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.security.PublicKey;
//@Component
@Scope("prototype")
public class Dog implements Pet {
    public String name;
    public Dog() {
        System.out.println("Dog bean created");
    }
    @Override
    public void say() {
        System.out.println("Woof!");
    }

    public void setName(String name) {
        System.out.println("Setting name");
        this.name = name;
    }
    @PostConstruct
    private void init() {
        System.out.println("Dog bean initialized");
    }
    @PreDestroy
    private void destroy() {
        System.out.println("Dog bean destroyed");
    }
}
