package mb.statix.search;

import mb.statix.sequences.Sequence;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;


/**
 * Tests the {@link Sequence} interface.
 */
public final class SequenceTests {

    /**
     * Tests the {@link Sequence#empty} function.
     */
    public static final class EmptyTests {

        @Test
        public void returnsEmptySequence() {
            // Act
            Sequence<String> seq = Sequence.empty();

            // Assert
            assertEquals(Collections.<String>emptyList(), seq.toList());
        }

        @Test
        public void returnsEmptySequenceOnReiteration() {
            // Act
            Sequence<String> seq = Sequence.empty();

            // Assert
            assertEquals(Collections.<String>emptyList(), seq.toList());
            assertEquals(Collections.<String>emptyList(), seq.toList());   // Can iterate twice
        }

        @Test
        public void returnsSameEmptySequence() {
            // Act
            Sequence<String> seq1 = Sequence.empty();
            Sequence<String> seq2 = Sequence.empty();
            Sequence<Integer> seq3 = Sequence.empty();

            // Assert
            assertSame(seq1, seq2);
            assertSame(seq2, seq3);
        }

    }


    /**
     * Tests the {@link Sequence#of} functions.
     */
    public static final class OfTests {

        @Test
        public void returnsEmptySequenceWhenNoArguments() {
            // Act
            Sequence<Object> seq = Sequence.of();

            // Assert
            assertSame(Sequence.empty(), seq);
            assertEquals(Collections.emptyList(), seq.toList());
        }

        @Test
        public void returnsSingletonSequenceWhenOneArgument() {
            // Act
            Sequence<String> seq = Sequence.of("a");

            // Assert
            assertEquals(Collections.singletonList("a"), seq.toList());
            assertEquals(Collections.singletonList("a"), seq.toList());   // Can iterate twice
        }

        @Test
        public void returnsSequenceWhenMoreThanOneArgument() {
            // Act
            Sequence<String> seq = Sequence.of("a", "b", "c");

            // Assert
            assertEquals(Arrays.asList("a", "b", "c"), seq.toList());
            assertEquals(Arrays.asList("a", "b", "c"), seq.toList());   // Can iterate twice
        }

    }


    /**
     * Tests the {@link Sequence#from} function.
     */
    public static final class FromTests {

        @Test
        public void iteratesGivenIterable() {
            // Arrange
            Iterable<String> iterable = Arrays.asList("a", "b", "c");

            // Act
            Sequence<String> seq = Sequence.from(iterable);

            // Assert
            assertEquals(Arrays.asList("a", "b", "c"), seq.toList());
            assertEquals(Arrays.asList("a", "b", "c"), seq.toList());   // Can iterate twice
        }

        @Test
        public void iteratesGivenIterableMultipleTimes() {
            // Arrange
            CountingIterable<String> iterable = new CountingIterable<>(Arrays.asList("a", "b", "c"));

            // Act
            Sequence<String> seq = Sequence.from(iterable);
            seq.toList();
            seq.toList();

            // Assert
            assertEquals(2, iterable.getIteratorCount());
        }

    }


    /**
     * An iterable wrapper that counts the number of times it has returned an iterator.
     *
     * @param <T> the type of elements in the iterable
     */
    private static class CountingIterable<T> implements Iterable<T> {

        private final AtomicInteger iteratorCount = new AtomicInteger();
        private final Iterable<T> wrappedIterable;

        /**
         * Initializes a new instance of the {@link CountingIterable} class.
         *
         * @param wrappedIterable the wrapped iterable
         */
        private CountingIterable(Iterable<T> wrappedIterable) {
            this.wrappedIterable = wrappedIterable;
        }

        /**
         * Gets the number of times the iterator has been returned.
         *
         * @return the number of times the iterator has been returned.
         */
        public int getIteratorCount() { return this.iteratorCount.get(); }

        @Override
        public Iterator<T> iterator() {
            iteratorCount.incrementAndGet();
            return wrappedIterable.iterator();
        }

    }

}
