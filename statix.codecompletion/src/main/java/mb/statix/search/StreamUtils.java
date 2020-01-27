package mb.statix.search;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * Utility methods for working with streams.
 */
public final class StreamUtils {

    /**
     * Returns a stream that performs its default computation until it can determine whether there are any results.
     * If there are none, the stream returns the stream from the supplier instead.
     *
     * @param stream the input stream
     * @param supplier the supplier of an alternative stream
     * @param <T> the type of elements in the stream
     * @return the resulting stream
     */
    public static <T> Stream<T> orIfEmpty(Stream<T> stream, Supplier<Stream<T>> supplier) {
        // Inspired by: https://stackoverflow.com/a/26659413/146622
        Iterator<T> iterator = stream.iterator();
        if (iterator.hasNext()) {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
        } else {
            return supplier.get();
        }
    }

    private static class EmptySpliterator<T> implements Spliterator<T> {

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            return false;
        }

        @Override
        public Spliterator<T> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return 0;
        }

        @Override
        public int characteristics() {
            return 0;
        }

    }

}
