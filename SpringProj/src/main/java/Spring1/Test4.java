package Spring1;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test4 {
    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context =
                     new ClassPathXmlApplicationContext("applicationContext2.xml")) {
            Dog dog = context.getBean("myPet", Dog.class);
            Dog yourDog = context.getBean("myPet", Dog.class);

            System.out.println(dog == yourDog);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
