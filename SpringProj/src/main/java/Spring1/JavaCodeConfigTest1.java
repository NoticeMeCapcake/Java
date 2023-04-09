package Spring1;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class JavaCodeConfigTest1 {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class)) {
            Person person = context.getBean("personBean", Person.class);
            person.callToPet();
            System.out.println(person.getName());
//            Pet cat1 = context.getBean("catBean", Pet.class);
//            Pet cat2 = context.getBean("catBean", Pet.class); // если singleton то одинаковые объекты
//            cat1.say();
        }
    }
}
