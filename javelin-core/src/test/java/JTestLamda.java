import com.github.paohaijiao.lamda.JLambda;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;


@Ignore
public class JTestLamda {
    @Test
    public void length() throws IOException {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        boolean bool= JLambda.anyMatch(numbers, n -> n > 3);
        System.out.println(bool);
    }
    @Test
    public void testFilter() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        List<String> result = JLambda.filter(names, name -> name.length() > 4);
        System.out.println(result);
    }
    @Test
    public void testMap() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        List<Integer> result = JLambda.map(names, String::length);
        System.out.println(result);
    }
    @Test
    public void testForEach() {
        List<String> names = new ArrayList<>(Arrays.asList("A", "B", "C"));
        JLambda.forEach(names, name -> names.set(names.indexOf(name), name + name));
        System.out.println(names);
    }
    @Test
    public void testGroupBy() {
        List<String> names = Arrays.asList("Apple", "Banana", "Avocado", "Cherry", "Blueberry");
        Map<Character, List<String>> result = JLambda.groupBy(names, s -> s.charAt(0));
        System.out.println(result);
    }
    @Test
    public void testDistinctBy() {
        List<String> names = Arrays.asList("Alice", "Bob", "Alice", "Charlie", "Bob");
        List<String> result = JLambda.distinctBy(names, s -> s);
        System.out.println(result);
    }
    @Test
    public void testAndOrPredicate() {
        Predicate<Integer> greaterThanTwo = n -> n > 2;
        Predicate<Integer> lessThanFive = n -> n < 5;

        Predicate<Integer> combinedAnd = JLambda.and(greaterThanTwo, lessThanFive);
        System.out.println(combinedAnd.test(3));
        System.out.println(combinedAnd.test(1));
        System.out.println(combinedAnd.test(5));

        Predicate<Integer> combinedOr = JLambda.or(greaterThanTwo, lessThanFive);
        System.out.println(combinedOr.test(3));
        System.out.println(combinedOr.test(1));
        System.out.println(combinedOr.test(5));
        System.out.println(combinedOr.test(2)); // 2不大于2且不小于5(实际上是小于5，这里可能有逻辑错误)
    }

    @Test
    public void testCurry() {
        BiFunction<Integer, Integer, Integer> adder = (a, b) -> a + b;
        Function<Integer, Function<Integer, Integer>> curriedAdder = JLambda.curry(adder);

        Function<Integer, Integer> addFive = curriedAdder.apply(5);
        System.out.println(addFive.apply(3));
    }
    @Test
    public void testToBiConsumer() {
        List<String> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();

        BiConsumer<String, Integer> biConsumer = JLambda.toBiConsumer(
                s -> list1.add(s),
                i -> list2.add(i)
        );

        biConsumer.accept("test", 123);
    }


}
