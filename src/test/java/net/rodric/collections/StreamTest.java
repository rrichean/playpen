package net.rodric.collections;

import net.rodric.common.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class StreamTest {

    @Test
    public void listPeopleUsingForEachTest() {
        getPeople().forEach(System.out::println);

        /*for (Person person : getPeople()) {
            System.out.println(person);
        }*/
    }

    @Test
    public void filterPeopleNameStartingWithSTest() {
        List<Person> sPeople = getPeople().stream()
                .filter(person -> person.getName().toLowerCase().startsWith("s"))
                .collect(Collectors.toList());

        /*for (Person person : getPeople()) {
            if (person.getName().startsWith("s")) {
                sPeople.add(person);
            }
        }*/

        assertThat(sPeople.size(), is(equalTo(2)));
    }

    @Test
    public void giveSPeopleRaiseTest() {
        List<Person> people = getPeople();
        people.stream()
                .filter(person -> person.getName().toLowerCase().startsWith("s"))
                .forEach(person -> person.setSalary(person.getSalary() + 100));

        /*for (Person person : people) {
            if (person.getName().toLowerCase().startsWith("s")) {
                person.setSalary(person.getSalary() + 100);
            }
        }*/

        //Terrible test. Don't do this
        for (Person person : people) {
            if (person.getName().toLowerCase().startsWith("s")) {
                assertThat(person.getSalary(), is(equalTo(100.0)));
            } else {
                assertThat(person.getSalary(), is(equalTo(0.0)));
            }
        }
    }

    @Test
    public void giveBobAWifeTest() {
        List<Person> people = getPeople();

        people.stream()
                .filter(person -> person.getName().equalsIgnoreCase("Bob"))
                .forEach(person -> person.addRelative(Person.Relative.SPOUSE, Person.builder().name("Bobette").build()));

        /*for (Person person : people) {
            if (person.getName().equalsIgnoreCase("bob")) {
                person.addRelative(Person.Relative.SPOUSE, Person.builder().name("Bobette").build());
            }
        }*/

        Map<Person.Relative, List<Person>> bobFamily = people.stream()
                .filter(person -> person.getName().equalsIgnoreCase("bob"))
                .findFirst()
                .map(Person::getFamily)
                .orElse(new HashMap<>());

        assertThat(bobFamily.size(), is(equalTo(1)));
        assertThat(bobFamily.get(Person.Relative.SPOUSE).get(0).getName(), is(equalTo("Bobette")));
    }

    @Test
    public void nestedFilteringTest() {
        List<Person> people = getPeopleWithFamily();
        List<String> parents = new ArrayList<>();
        for (Person parent : people) {
            if (parent.getFamily() != null && parent.getFamily().get(Person.Relative.CHILD) != null) {
                for (Person child : parent.getFamily().get(Person.Relative.CHILD)) {
                    if (child.getName().startsWith(parent.getName().substring(0, 1))) {
                        parents.add(parent.getName());
                        break;
                    }
                }
            }
        }

        /*List<String> parents = Stream.of(kane, jimmy, rodger)
                .filter(parent -> parent.getFamily().get(Person.Relative.CHILD)
                        .stream()
                        .filter(Objects::nonNull)
                        .anyMatch(child -> child.getName().startsWith(parent.getName().substring(0, 1))))
                .map(Person::getName)
                .collect(Collectors.toList());*/


        /*List<String> parents = Stream.of(kane, jimmy, rodger)
                .filter(parent ->
                        Optional.ofNullable(parent.getFamily())
                                .map(relativeMap -> relativeMap.get(Person.Relative.CHILD))
                                .orElse(new ArrayList<>())

                                .stream()
                                .anyMatch(child -> child.getName().startsWith(parent.getName().substring(0, 1))))
                .map(Person::getName)
                .collect(Collectors.toList());*/

        assertThat(parents.size(), is(equalTo(1)));
        assertThat(parents.get(0), is("Jimmy"));

    }

    @Test
    public void testUglyIfStatement() {
        List<Person> marriedParents = getPeopleWithFamily().stream()
                .filter(person -> person.getFamily() != null)
                .filter(person -> person.getFamily().get(Person.Relative.SPOUSE) != null)
                .filter(person -> person.getFamily().get(Person.Relative.CHILD) != null)
                .collect(Collectors.toList());

        /*for (Person person : getPeopleWithFamily()) {
            Map<Person.Relative, List<Person>> family = person.getFamily();
            if (family != null && family.get(Person.Relative.CHILD) != null && family.get(Person.Relative.SPOUSE) != null) {
                marriedParents.add(person);
            }
        }*/

        assertThat(marriedParents.size(), is(equalTo(1)));
        assertThat(marriedParents.get(0).getName(), is(equalTo("Rodger")));
    }

    @Test
    public void testAllMatch() {
        boolean broke = getPeople().stream()
                .allMatch(person -> person.getSalary() == 0.0);

        assertThat(broke, is(true));
    }

    @Test
    public void testAllMatchNegative() {
        List<Person> people = getPeople();
        people.get(0).setSalary(100);

        boolean allBroke = people.stream()
                .allMatch(person -> person.getSalary() == 0.0);

        assertThat(allBroke, is(false));
    }

    @Test
    public void testDistinct() {
        long count = getPeople().stream()
                .distinct()
                .count();

        assertThat(count, is(5L));
    }

    private List<Person> getPeopleWithFamily() {
        Person kane = Person.builder().name("Kane").build();
        Person jimmy = Person.builder().name("Jimmy").build();

        jimmy.addRelative(Person.Relative.CHILD, Person.builder().name("Jane").build());
        jimmy.addRelative(Person.Relative.CHILD, Person.builder().name("Joel").build());
        jimmy.addRelative(Person.Relative.CHILD, Person.builder().name("Jackie").build());

        Person rodger = Person.builder().name("Rodger").build();

        rodger.addRelative(Person.Relative.SPOUSE, Person.builder().name("Megan Fox").build());
        rodger.addRelative(Person.Relative.CHILD, Person.builder().name("Khira").build());

        List<Person> people = Arrays.asList(jimmy, rodger, kane);
        return people;
    }

    private List<Person> getPeople() {
        return new ArrayList<>(Arrays.asList(
                Person.builder().name("Bob").build(),
                Person.builder().name("Sam").build(),
                Person.builder().name("Suzy").build(),
                Person.builder().name("Zamazama").build(),
                Person.builder().name("Quosa").build(),
                Person.builder().name("Quosa").build()
        ));
    }
}
