package net.rodric.common;

public class Person {
    private String name;
    private double salary;

    private Person() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public static PersonBuilder builder() {
        return new PersonBuilder(new Person());
    }

    public static class PersonBuilder {
        private Person person;
        PersonBuilder(Person person) {
            this.person = person;
        }

        public PersonBuilder name(String name) {
            person.setName(name);
            return this;
        }

        public PersonBuilder salary(double salary) {
            person.setSalary(salary);
            return this;
        }

        public Person build() {
            return person;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) {
            return false;
        }

        return this.getName().equals(((Person) obj).getName());
    }
}
