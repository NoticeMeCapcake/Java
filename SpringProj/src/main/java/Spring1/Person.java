package Spring1;

public class Person {
    private Pet pet;
    private String name;
    private int age;
    public Person() {
        System.out.println("Person bean created");
    }
    public Person(Pet pet) {
        System.out.println("Person bean created (with Pet)");
        this.pet = pet;
    }
    public void setPet(Pet pet) {
        System.out.println("Setting pet");
        this.pet = pet;
    }

    public void setName(String name) {
        System.out.println("Setting name");
        this.name = name;
    }
    public void setAge(int age) {
        System.out.println("Setting age");
        this.age = age;
    }
    public String getName() {
        System.out.println("Getting name");
        return name;
    }
    public int getAge() {
        System.out.println("Getting age");
        return age;
    }
    public void callToPet() {
        System.out.println("Calling pet");
        pet.say();
    }
}
