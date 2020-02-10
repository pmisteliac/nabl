package mb.statix.search.strategies;

import mb.statix.search.strategies.teststrategies.DataStrategy;
import mb.statix.search.strategies.teststrategies.TestStrategy;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;


/**
 * Tests the {@link ShuffleStrategy} class.
 */
public final class ShuffleStrategyTests {

    @Test
    public void shufflesTheResultingStream() throws InterruptedException {
        // Arrange
        Random rnd = new Random(12345);
        List<String> inputs = Arrays.asList("a", "b", "c", "d", "e");
        TestStrategy<Object, String, Object> testStr = new TestStrategy<>(new DataStrategy<>(inputs));
        ShuffleStrategy<Object, String, Object> str = new ShuffleStrategy<>(rnd, testStr);

        // Act
        Stream<String> outputStream = str.apply(null, null);
        List<String> outputs = outputStream.collect(Collectors.toList());

        // Assert
        List<String> expected = Arrays.asList("d", "e", "a", "c", "b");
        assertEquals(expected, outputs);
        assertEquals(1, testStr.getEvalCount());
    }

    @Test
    public void returnsASingleton() throws InterruptedException {
        // Arrange
        Random rnd = new Random(12345);
        TestStrategy<String, String, Object> testStr = new TestStrategy<>(new IdStrategy<>());
        ShuffleStrategy<String, String, Object> str = new ShuffleStrategy<>(rnd, testStr);

        // Act
        Stream<String> outputStream = str.apply(null, "a");
        List<String> outputs = outputStream.collect(Collectors.toList());

        // Assert
        List<String> expected = Collections.singletonList("a");
        assertEquals(expected, outputs);
        assertEquals(1, testStr.getEvalCount());
    }

    @Test
    public void returnsAnEmptyList() throws InterruptedException {
        // Arrange
        Random rnd = new Random(12345);
        TestStrategy<String, String, Object> testStr = new TestStrategy<>(new FailStrategy<>());
        ShuffleStrategy<String, String, Object> str = new ShuffleStrategy<>(rnd, testStr);

        // Act
        Stream<String> outputStream = str.apply(null, "a");
        List<String> outputs = outputStream.collect(Collectors.toList());

        // Assert
        List<String> expected = Collections.emptyList();
        assertEquals(expected, outputs);
        assertEquals(1, testStr.getEvalCount());
    }

}
