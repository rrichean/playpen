package net.rodric.collections;

import net.rodric.common.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class StreamCollectorTest {

    /**
     * Every name from the collection of people needs to be present in the list, including duplicates
     */
    @Test
    public void testCollectToList() {
        List<Person> people = getPeople();

        List<String> names = people.stream().map(Person::getName).collect(Collectors.toList());
        assertThat(names.size(), is(6));
        assertThat(names, hasItems("Bob", "Sam", "Suzy", "Zamazama", "Quosa"));
    }

    /**
     * All non-duplicated names needs to be present in the output.
     */
    @Test
    public void testCollectToSet() {
        List<Person> people = getPeople();

        Set<String> names = people.stream().map(Person::getName).collect(Collectors.toSet());
        assertThat(names.size(), is(5));
        assertThat(names, hasItems("Bob", "Sam", "Suzy", "Zamazama", "Quosa"));
    }

    /**
     * All names need to be concatenated.
     */
    @Test
    public void testConcat() {
        List<Person> people = getPeople();

        String bigName = people.stream().map(Person::getName).collect(Collectors.joining());
        assertThat(bigName, is("BobSamSuzyZamazamaQuosaQuosa"));
    }

    /**
     * Test concatenation with specified delimiter
     */
    @Test
    public void testConcatWithDelimiter() {
        String nameOut = getPeople().stream().map(Person::getName).collect(Collectors.joining(" : "));
        assertThat(nameOut, is("Bob : Sam : Suzy : Zamazama : Quosa : Quosa"));
    }

    /**
     * Test concatenation with delimiter and prefix/suffix over entire text
     */
    @Test
    public void testConcatWithDelimiterAndPfxSfx() {
        String nameOut = getPeople().stream()
                .map(Person::getName)
                .collect(Collectors.joining(" - ", "${", "}"));

        assertThat(nameOut, is("${Bob - Sam - Suzy - Zamazama - Quosa - Quosa}"));
    }

    /**
     * Test counting of elements. Seems that the whole of .collect(Collectors.counting()) can be replaced with .count().
     */
    @Test
    public void testCounting() {
        Long count = getPeople().stream().map(Person::getName).collect(Collectors.counting());
        assertThat(count, is(6L));
    }

    /**
     * Group by names length = 3, and other
     */
    @Test
    public void testGroupByNameLength() {
        Map<Integer, List<String>> byLength = getPeople().stream()
                .map(Person::getName)
                .collect(Collectors.groupingBy(String::length));

        assertThat(byLength.size(), is(4));

        assertThat(byLength.get(3).size(), is(2));
        assertThat(byLength.get(3), hasItems("Bob", "Sam"));

        assertThat(byLength.get(4).size(), is(1));
        assertThat(byLength.get(4), hasItems("Suzy"));

        assertThat(byLength.get(5).size(), is(2));
        assertThat(byLength.get(5), hasItems("Quosa"));

        assertThat(byLength.get(8).size(), is(1));
        assertThat(byLength.get(8), hasItems("Zamazama"));
    }

    /**
     *
     */
    @Test
    public void testGroupByNameLengthUsingObject() {
        Map<Integer, List<Person>> collect = getPeople().stream().collect(Collectors.groupingBy(person -> person.getName().length()));

        assertThat(collect.size(), is(4));
        assertThat(collect.keySet(), hasItem(3));
        assertThat(collect.keySet(), hasItem(4));
        assertThat(collect.keySet(), hasItem(5));
        assertThat(collect.keySet(), hasItem(8));
    }

    /**
     * Split the collection to a map containing 2 keys, true -> names start with 'S', false -> names start with anything but 'S'
     */
    @Test
    public void testPartition() {
        Map<Boolean, List<String>> startWithS = getPeople().stream().map(Person::getName).collect(Collectors.partitioningBy(name -> name.startsWith("S")));

        assertThat(startWithS.size(), is(2));
        assertThat(startWithS.get(Boolean.TRUE).size(), is(2));
        assertThat(startWithS.get(Boolean.FALSE).size(), is(4));
        assertThat(startWithS.get(Boolean.TRUE), hasItems("Sam", "Suzy"));
    }

    /**
     * Collect to a map, key defined as the first 3 letters of the name, value, the person object.
     * Removing duplicate entry Quosa as it will cause a duplicate key exception
     */
    @Test
    public void testCollectToMap() {
        List<Person> people = getPeople();
        people.remove(5);
        Map<String, Person> collect = people.stream().collect(Collectors.toMap(person -> person.getName().substring(0, 3), person -> person));

        assertThat(collect.size(), is(5));
        assertThat(collect.get("Bob"), is(Person.builder().name("Bob").build()));
        assertThat(collect.get("Sam"), is(Person.builder().name("Sam").build()));
        assertThat(collect.get("Suz"), is(Person.builder().name("Suzy").build()));
        assertThat(collect.get("Zam"), is(Person.builder().name("Zamazama").build()));
    }

    /**
     * Summarizing Int captures the min/max/sum/average values. In this test, we're using the ASCI value of the character
     */
    @Test
    public void testCollectToSummarizingInt() {
        IntSummaryStatistics summary = getPeople().stream().map(Person::getName).collect(Collectors.summarizingInt(name -> name.charAt(2)));
        assertThat(summary.getSum(), is(660L));
        assertThat(summary.getMax(), is(122));
        assertThat(summary.getMin(), is(98));
    }

    /**
     * Returning a new ArrayList as the output of Arrays.asList is an unmodifiable list
     * @return List of People objects to be used for testing
     */
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