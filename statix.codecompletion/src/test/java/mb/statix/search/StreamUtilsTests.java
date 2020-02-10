package mb.statix.search;

import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;


/**
 * Tests the {@link StreamUtils} class.
 */
public final class StreamUtilsTests {

    /**
     * Tests the {@link StreamUtils#transform} function.
     */
    public static final class TransformTests {

        @Test
        public void transformsOneSequenceIntoAnother() {
            // Arrange
            List<String> input = Arrays.asList("a", "b", "c", "d", "e");

            // Act
            Function<List<String>, Iterable<String>> transformation = xs -> Collections.singleton(String.join(" and ", xs));
            List<String> result = StreamUtils.transform(input.stream(), transformation).collect(Collectors.toList());

            // Assert
            List<String> expected = Collections.singletonList("a and b and c and d and e");
            assertEquals(expected, result);
        }

    }


    /**
     * Tests the {@link StreamUtils#subsetsOfSize} function.
     */
    public static final class SubsetsOfSizeTests {

        @Test
        public void returnsAllSubsetsOfSizeZero() {
            // Arrange
            List<String> input = Arrays.asList("a", "b", "c", "d", "e");

            // Act
            List<Collection<String>> results = StreamUtils.subsetsOfSize(input.stream(), 0).collect(Collectors.toList());

            // Assert
            List<List<String>> expected = Collections.singletonList(Collections.emptyList());
            assertEquals(expected, results);
        }

        @Test
        public void returnsAllSubsetsOfSizeOne() {
            // Arrange
            List<String> input = Arrays.asList("a", "b", "c", "d", "e");

            // Act
            List<Collection<String>> results = StreamUtils.subsetsOfSize(input.stream(), 1).collect(Collectors.toList());

            // Assert
            List<List<String>> expected = Arrays.asList(
                    Collections.singletonList("a"),
                    Collections.singletonList("b"),
                    Collections.singletonList("c"),
                    Collections.singletonList("d"),
                    Collections.singletonList("e")
            );
            assertEquals(expected, results);
        }

        @Test
        public void returnsAllSubsetsOfSizeTwo() {
            // Arrange
            List<String> input = Arrays.asList("a", "b", "c", "d", "e");

            // Act
            List<Collection<String>> results = StreamUtils.subsetsOfSize(input.stream(), 2).collect(Collectors.toList());

            // Assert
            List<List<String>> expected = new ArrayList<>(Arrays.asList(
                    Arrays.asList("a", "b"),
                    Arrays.asList("a", "c"),
                    Arrays.asList("b", "c"),
                    Arrays.asList("a", "d"),
                    Arrays.asList("b", "d"),
                    Arrays.asList("c", "d"),
                    Arrays.asList("a", "e"),
                    Arrays.asList("b", "e"),
                    Arrays.asList("c", "e"),
                    Arrays.asList("d", "e")
            ));
            assertEquals(expected, results);
        }

        @Test
        public void returnsAllSubsetsOfSizeThree() {
            // Arrange
            List<String> input = Arrays.asList("a", "b", "c", "d", "e");

            // Act
            List<Collection<String>> results = StreamUtils.subsetsOfSize(input.stream(), 3).collect(Collectors.toList());

            // Assert
            List<List<String>> expected = Arrays.asList(
                    Arrays.asList("a", "b", "c"),
                    Arrays.asList("a", "b", "d"),
                    Arrays.asList("a", "c", "d"),
                    Arrays.asList("b", "c", "d"),
                    Arrays.asList("a", "b", "e"),
                    Arrays.asList("a", "c", "e"),
                    Arrays.asList("b", "c", "e"),
                    Arrays.asList("a", "d", "e"),
                    Arrays.asList("b", "d", "e"),
                    Arrays.asList("c", "d", "e")
            );
            assertEquals(expected, results);
        }


        @Test
        public void returnsAllSubsetsOfSizeFour() {
            // Arrange
            List<String> input = Arrays.asList("a", "b", "c", "d", "e");

            // Act
            List<Collection<String>> results = StreamUtils.subsetsOfSize(input.stream(), 4).collect(Collectors.toList());

            // Assert
            List<List<String>> expected = Arrays.asList(
                    Arrays.asList("a", "b", "c", "d"),
                    Arrays.asList("a", "b", "c", "e"),
                    Arrays.asList("a", "b", "d", "e"),
                    Arrays.asList("a", "c", "d", "e"),
                    Arrays.asList("b", "c", "d", "e")
            );
            assertEquals(expected, results);
        }

        @Test
        public void returnsAllSubsetsOfSizeFive() {
            // Arrange
            List<String> input = Arrays.asList("a", "b", "c", "d", "e");

            // Act
            List<Collection<String>> results = StreamUtils.subsetsOfSize(input.stream(), 5).collect(Collectors.toList());

            // Assert
            List<List<String>> expected = Collections.singletonList(
                    Arrays.asList("a", "b", "c", "d", "e")
            );
            assertEquals(expected, results);
        }

        @Test
        public void returnsAllSubsetsOfSizeSix() {
            // Arrange
            List<String> input = Arrays.asList("a", "b", "c", "d", "e");

            // Act
            List<Collection<String>> results = StreamUtils.subsetsOfSize(input.stream(), 6).collect(Collectors.toList());

            // Assert
            List<List<String>> expected = Collections.emptyList();
            assertEquals(expected, results);
        }

    }

}
