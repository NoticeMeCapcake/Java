package Spring1;

import java.security.PublicKey;

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

    private void init() {
        System.out.println("Dog bean initialized");
    }
    private void destroy() {
        System.out.println("Dog bean destroyed");
    }
}
