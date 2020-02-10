package mb.statix.search.strategies;

import mb.statix.search.strategies.teststrategies.ChangeCaseStrategy;
import mb.statix.search.strategies.teststrategies.DataStrategy;
import mb.statix.search.strategies.teststrategies.TestStrategy;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mb.statix.search.strategies.Strategies.seq;
import static org.junit.Assert.assertEquals;


/**
 * Tests the {@link DebugStrategy} class.
 */
public final class DebugStrategyTests {

    /**
     * The debug(s) strategy should apply its results to <em>all</em>
     * elements returned by strategy s, even if some of those are not actually
     * used in a computation further down the line.
     *
     * Therefore, in this test we evaluate:
     * <pre>
     *     limit(1, debug(![a, b, c, d, e]; change-case))
     * </pre>
     * By limiting the result of the computation, normally {@code change-case} would
     * be evaluated only once, for the first element. But using debug, it is evaluated
     * for all elements instead.
     */
    @Test
    public void evaluatesAllComputations() throws InterruptedException {
        // Arrange
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random(12345);
        List<String> inputs = Arrays.asList("a", "b", "c", "d", "e");
        TestStrategy<String, String, Object> testStr = new TestStrategy<>(new ChangeCaseStrategy<>());
        LimitStrategy<Object, String, Object> finalStr = new LimitStrategy<>(1,
                new DebugStrategy<>(seq(new DataStrategy<>(inputs)).$(testStr).$(), sb::append));

        // Act
        Stream<String> outputStream = finalStr.apply(null, null);
        List<String> outputs = outputStream.collect(Collectors.toList());

        // Assert
        List<String> expected = Collections.singletonList("A");
        assertEquals(expected, outputs);
        assertEquals("ABCDE", sb.toString());
        assertEquals(5, testStr.getEvalCount());
    }
}
