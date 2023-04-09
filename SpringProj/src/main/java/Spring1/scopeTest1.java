package Spring1;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class scopeTest1 {
    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext3.xml")) {
//            Person person = context.getBean("personBean", Person.class);
//            System.out.println(person.getName());
            Dog myDog = context.getBean("dog", Dog.class);
            Dog yourDog = context.getBean("dog", Dog.class);
            System.out.println(myDog == yourDog);

        }
    }
}
