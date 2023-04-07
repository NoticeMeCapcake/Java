package Spring1;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test2 {
    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml")) {
            Pet pet = context.getBean("myPet", Pet.class);
            pet.say();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
