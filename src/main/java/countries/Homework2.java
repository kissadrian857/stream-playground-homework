package countries;

import java.io.IOException;

import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.*;

import java.time.ZoneId;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Homework2 {

    private List<Country> countries;

    public Homework2() {
        countries = new CountryRepository().getAll();
    }

    /**
     * Returns the longest country name translation.
     */
    public Optional<String> streamPipeline1() {
        return countries.stream()
                .map(Country::getTranslations)
                .map(map -> {
                    return new ArrayList<String>(map.values());
                })
                .reduce((x, y) -> {
                    ArrayList<String> z = new ArrayList<String>();
                    z.addAll(x);
                    z.addAll(y);
                    return z;
                })
                .get()
                .stream()
                .reduce((x, y) -> x.length() > y.length() ? x : y);
    }

    /**
     * Returns the longest Italian (i.e., {@code "it"}) country name translation.
     */
    public Optional<String> streamPipeline2() {
        return countries.stream()
                .map(Country::getTranslations)
                .map(map -> map.get("it"))
                .filter(s -> s != null)
                .reduce((x, y) -> x.length() > y.length() ? x : y);
    }

    /**
     * Prints the longest country name translation together with its language code in the form language=translation.
     */
    public void streamPipeline3() {
        System.out.println(countries.stream()
                .map(Country::getTranslations)
                .map(Map::entrySet)
                .reduce((x, y) -> {
                    Set<Map.Entry<String, String>> newSet = new HashSet<Map.Entry<String, String>>();
                    newSet.addAll(x);
                    newSet.addAll(y);
                    return newSet;
                })
                .get()
                .stream()
                .reduce((x, y) -> x.getValue().length() > y.getValue().length() ? x : y));
    }

    /**
     * Prints single word country names (i.e., country names that do not contain any space characters).
     */
    public void streamPipeline4() {
        countries.stream()
                .map(Country::getName)
                .filter(s -> !s.contains(" "))
                .forEach(System.out::println);
    }

    /**
     * Returns the country name with the most number of words.
     */
    public Optional<String> streamPipeline5() {
        return Arrays.stream(countries.stream()
                .map(country -> country.getName().split(" "))
                .reduce((x, y) -> {
                    return x.length > y.length ? x : y;
                })
                .get())
                .reduce((x, y) -> x + " " + y);
    }

    /**
     * Returns whether there exists at least one capital that is a palindrome.
     */
    public boolean streamPipeline6() {
        return countries.stream()
                .map(country -> new StringBuilder(country.getCapital()))
                .map(capitalSb -> {
                    StringBuilder stringBuilder = new StringBuilder(capitalSb.toString());
                    return capitalSb.toString() == stringBuilder.reverse().toString();
                })
                .reduce((x, y) -> x || y)
                .get();
    }

    /**
     * Returns the country name with the most number of {@code 'e'} characters ignoring case.
     */
    public Optional<String> streamPipeline7() {
        return countries.stream()
                .map(Country::getName)
                .max(Comparator.comparing(x -> charCount(x, 'e')));
    }

    /**
     * Returns the capital with the most number of English vowels (i.e., {@code 'a'}, {@code 'e'}, {@code 'i'}, {@code 'o'}, {@code 'u'}).
     */
    public Optional<String> streamPipeline8() {
        return countries.stream()
                .map(Country::getCapital)
                .max(Comparator.comparing(x -> vowelCount(x)));
    }

    /**
     * Returns a map that contains for each character the number of occurrences in country names ignoring case.
     */
    public Map<Character, Long> streamPipeline9() {
        return countries.stream()
                .map(Country::getName)
                .reduce((x, y) -> x + y)
                .get()
                .chars()
                .mapToObj(n -> (char) n)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    /**
     * Returns a map that contains the number of countries for each possible timezone.
     */
    public Map<ZoneId, Long> streamPipeline10() {
        return countries.stream()
                .map(Country::getTimezones)
                .reduce((x, y) -> {
                    ArrayList<ZoneId> newList = new ArrayList<ZoneId>();
                    newList.addAll(x);
                    newList.addAll(y);
                    return newList;
                })
                .get()
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    /**
     * Returns the number of country names by region that starts with their two-letter country code ignoring case.
     */
    public Map<Region, Long> streamPipeline11() {
        return countries.stream()
                .filter(x -> x.getName().toUpperCase().startsWith(x.getCode()))
                .map(Country::getRegion)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    /**
     * Returns a map that contains the number of countries whose population is greater or equal than the population average versus the the number of number of countries with population below the average.
     */
    public Map<Boolean, Long> streamPipeline12() {
        var average = countries.stream()
                .map(Country::getPopulation)
                .mapToLong(x -> (long) x)
                .summaryStatistics()
                .getAverage();

        return countries.stream()
                .map(x -> x.getPopulation() >= average ? true : false)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    /**
     * Returns a map that contains for each country code the name of the corresponding country in Portuguese ({@code "pt"}).
     */
    public Map<String, String> streamPipeline13() {
        return countries.stream()
                .collect(Collectors.toMap(Country::getName, x -> x.getTranslations().get("pt")));
    }

    /**
     * Returns the list of capitals by region whose name is the same is the same as the name of their country.
     */
    public Map<Region, List<String>> streamPipeline14() {
        return countries.stream()
                .filter(x -> x.getCapital() == x.getName())
                .collect(Collectors.groupingBy(Country::getRegion, Collectors.mapping(Country::getCapital, Collectors.toList())));
    }

    /**
     * Returns a map of country name-population density pairs.
     */
    public Map<String, Double> streamPipeline15() {
        return countries.stream()
                .collect(Collectors.toMap(Country::getName, x -> {
                    if (x.getArea() == null) {
                        return Double.NaN;
                    } else {
                        return x.getPopulation() / x.getArea().doubleValue();
                    }
                }));
    }

    public int charCount(String s, char c) {
        String string = s.toLowerCase();
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    public int vowelCount(String capital) {
        char[] vowels = new char[]{'a', 'e', 'i', 'o', 'u'};
        int vowelCounter = 0;
        capital = capital.toLowerCase();
        for (int i = 0; i < capital.length(); i++) {
            for (int j = 0; j < vowels.length; j++) {
                if (capital.charAt(i) == vowels[j]) {
                    vowelCounter++;
                }
            }
        }
        return vowelCounter;
    }
}
