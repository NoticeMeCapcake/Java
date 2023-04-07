package Spring1;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test5 {
    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext2.xml")) {
            Dog myDog = context.getBean("myPet", Dog.class);
            myDog.say();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Done");
    }
}
