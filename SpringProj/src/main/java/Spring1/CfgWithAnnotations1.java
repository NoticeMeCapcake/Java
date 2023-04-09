package Spring1;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CfgWithAnnotations1 {
    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext3.xml")) {
//            Cat cat = context.getBean("catBean", Cat.class);
//            cat.say();
            Person person = context.getBean("personBean", Person.class);
            person.callToPet();
            System.out.println(person.getName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Done");
    }
}
