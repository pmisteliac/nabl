package mb.statix.search.strategies;

import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;


/**
 * Tests the {@link FailStrategy} class.
 */
public final class FailStrategyTests {

    @Test
    public void returnsNothing() throws InterruptedException {
        // Arrange
        FailStrategy<String, Object> str = new FailStrategy<>();

        // Act
        Stream<String> outputStream = str.apply(null, "a");
        List<String> outputs = outputStream.collect(Collectors.toList());

        // Assert
        List<String> expected = Collections.emptyList();
        assertEquals(expected, outputs);
    }

}
