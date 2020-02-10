package mb.statix.search.strategies;

import mb.statix.search.strategies.teststrategies.IncrementStrategy;
import mb.statix.search.strategies.teststrategies.TestStrategy;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;


/**
 * Tests the {@link RepeatStrategy} class.
 */
public final class RepeatStrategyTests {

    @Test
    public void succeedsWhenSCannotBeApplied() throws InterruptedException {
        // Arrange
        int input = 10;
        TestStrategy<Integer, Integer, Object> testStr = new TestStrategy<>(new IncrementStrategy<>(), 0);
        RepeatStrategy<Integer, Object> str = new RepeatStrategy<>(testStr);

        // Act
        Stream<Integer> outputStream = str.apply(null, input);
        List<Integer> outputs = outputStream.collect(Collectors.toList());

        // Assert
        List<Integer> expected = Collections.singletonList(10);
        assertEquals(expected, outputs);
        assertEquals(0, testStr.getEvalCount());
    }

    @Test
    public void succeedsWhenSCanBeAppliedOnce() throws InterruptedException {
        // Arrange
        int input = 10;
        TestStrategy<Integer, Integer, Object> testStr = new TestStrategy<>(new IncrementStrategy<>(), 1);
        RepeatStrategy<Integer, Object> str = new RepeatStrategy<>(testStr);

        // Act
        Stream<Integer> outputStream = str.apply(null, input);
        List<Integer> outputs = outputStream.collect(Collectors.toList());

        // Assert
        List<Integer> expected = Collections.singletonList(11);
        assertEquals(expected, outputs);
        assertEquals(1, testStr.getEvalCount());
    }

    @Test
    public void succeedsWhenSCanBeAppliedTwice() throws InterruptedException {
        // Arrange
        int input = 10;
        TestStrategy<Integer, Integer, Object> testStr = new TestStrategy<>(new IncrementStrategy<>(), 2);
        RepeatStrategy<Integer, Object> str = new RepeatStrategy<>(testStr);

        // Act
        Stream<Integer> outputStream = str.apply(null, input);
        List<Integer> outputs = outputStream.collect(Collectors.toList());

        // Assert
        List<Integer> expected = Collections.singletonList(12);
        assertEquals(expected, outputs);
        assertEquals(2, testStr.getEvalCount());
    }

    @Test
    public void succeedsWhenSCanBeAppliedTenTimes() throws InterruptedException {
        // Arrange
        int input = 10;
        TestStrategy<Integer, Integer, Object> testStr = new TestStrategy<>(new IncrementStrategy<>(), 10);
        RepeatStrategy<Integer, Object> str = new RepeatStrategy<>(testStr);

        // Act
        Stream<Integer> outputStream = str.apply(null, input);
        List<Integer> outputs = outputStream.collect(Collectors.toList());

        // Assert
        List<Integer> expected = Collections.singletonList(20);
        assertEquals(expected, outputs);
        assertEquals(10, testStr.getEvalCount());
    }

}
