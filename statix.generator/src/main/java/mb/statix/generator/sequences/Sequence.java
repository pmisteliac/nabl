package mb.statix.generator.sequences;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;


/**
 * A lazy computation on a sequence of elements.
 * @param <T> the type of elements
 */
public interface Sequence<T> {

    /**
     * Gets the lazy iterator of the sequence.
     *
     * This operation is terminal.
     *
     * @return the iterator.
     */
    Iterator<T> iterator();

    /**
     * Returns whether all the elements in the sequence match the given predicate.
     *
     * @param predicate the predicate to use
     * @return {@code true} when all elements match the given predicate; otherwise, {@code false}
     */
    default boolean all(Predicate<T> predicate) {
        if (predicate == null) throw new IllegalArgumentException("predicate must not be null");

        Iterator<T> source = this.iterator();
        while (source.hasNext()) {
            if (!predicate.test(source.next()))
                return false;
        }
        return true;
    }

    /**
     * Returns a sequence that returns only the first n elements.
     *
     * This operation is intermediate and stateless.
     *
     * @param n the number of elements to return
     * @return the new sequence
     */
    default Sequence<T> take(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be greater than or equal to zero.");

        return () -> new Iterator<T>() {
            private final Iterator<T> source = Sequence.this.iterator();
            private int remaining = n;

            @Override
            public boolean hasNext() {
                return this.remaining > 0 && this.source.hasNext();
            }

            @Override
            public T next() {
                if (this.remaining <= 0) throw new NoSuchElementException();
                T next = this.source.next();
                this.remaining -= 1;
                return next;
            }
        };
    }

}
