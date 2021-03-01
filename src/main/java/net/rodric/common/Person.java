package net.rodric.common;

import java.util.*;

public class Person {
    private String name;
    private double salary;
    private Map<Relative, List<Person>> family;

    public enum Relative {
        SIBLING, ANCESTOR, CHILD, SPOUSE
    }

    private Person() {
        family = new HashMap<>();
    }

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

    public void addRelative(Relative relation, Person person) {
        List<Person> people = family.get(relation);

        if (people == null) {
            people = new ArrayList<>();
        }

        people.add(person);
        family.put(relation, people);
    }

    public Map<Relative, List<Person>> getFamily() {
        return family;
    }

    public static PersonBuilder builder() {
        return new PersonBuilder(new Person());
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) {
            return false;
        }

        return this.getName().equals(((Person) obj).getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
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
}
