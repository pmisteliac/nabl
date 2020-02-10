package mb.statix.search.strategies;

import mb.statix.search.strategies.teststrategies.ChangeCaseStrategy;
import mb.statix.search.strategies.teststrategies.TestStrategy;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;


/**
 * Tests the {@link TryStrategy} class.
 */
public final class TryStrategyTests {

    @Test
    public void succeedsWhenSCanBeApplied() throws InterruptedException {
        // Arrange
        String input = "a";
        TestStrategy<String, String, Object> testStr = new TestStrategy<>(new ChangeCaseStrategy<>());
        TryStrategy<String, Object> str = new TryStrategy<>(testStr);

        // Act
        Stream<String> outputStream = str.apply(null, input);
        List<String> outputs = outputStream.collect(Collectors.toList());

        // Assert
        List<String> expected = Collections.singletonList("A");
        assertEquals(expected, outputs);
        assertEquals(1, testStr.getEvalCount());
    }

    @Test
    public void succeedsWhenSCannotBeApplied() throws InterruptedException {
        // Arrange
        String input = "a";
        TestStrategy<String, String, Object> testStr = new TestStrategy<>(new FailStrategy<>());
        TryStrategy<String, Object> str = new TryStrategy<>(testStr);

        // Act
        Stream<String> outputStream = str.apply(null, input);
        List<String> outputs = outputStream.collect(Collectors.toList());

        // Assert
        List<String> expected = Collections.singletonList(input);
        assertEquals(expected, outputs);
        assertEquals(0, testStr.getEvalCount());
    }

}
