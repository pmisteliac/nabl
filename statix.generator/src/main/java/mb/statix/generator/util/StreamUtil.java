package mb.statix.generator.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.metaborg.util.functions.Function0;
import org.metaborg.util.functions.Function1;
import org.metaborg.util.functions.PartialFunction0;
import org.metaborg.util.functions.Predicate0;

import com.google.common.collect.Streams;

public class StreamUtil {

    @SuppressWarnings("unchecked") public static <T> Stream<T> filterInstances(Class<T> cls, Stream<? super T> stream) {
        return stream.filter(cls::isInstance).map(t -> (T) t);
    }

    public static <T, U> Stream<U> flatMap(Stream<T> stream, Function1<T, Stream<? extends U>> flatMap) {
        return Streams.stream(new Iterator<U>() {

            private final Iterator<T> it = stream.iterator();

            private boolean done;
            private Iterator<? extends U> nexts;

            @Override public boolean hasNext() {
                while(!done && (nexts == null || !nexts.hasNext())) {
                    if(!it.hasNext()) {
                        done = true;
                        break;
                    }
                    nexts = flatMap.apply(it.next()).iterator();
                }
                return !done;
            }

            @Override public U next() {
                if(!hasNext()) {
                    throw new NoSuchElementException();
                }
                return nexts.next();
            }

        });
    }

    public static <T> Stream<T> generate(PartialFunction0<T> generator) {
        return Streams.stream(new Iterator<T>() {

            private boolean done = false;
            private T next = null;

            @Override public boolean hasNext() {
                if(!done && next == null) {
                    if((next = generator.apply().orElse(null)) == null) {
                        done = true;
                    }
                }
                return !done;
            }

            @Override public T next() {
                if(!hasNext()) {
                    throw new NoSuchElementException();
                }
                T t = next;
                next = null;
                return t;
            }

        });
    }

    public static <T> Stream<T> generate(Predicate0 hasNext, Function0<T> next) {
        return Streams.stream(new Iterator<T>() {

            @Override public boolean hasNext() {
                return hasNext.test();
            }

            @Override public T next() {
                return next.apply();
            }

        });
    }

    public static <T> Stream<T> generate(EnumeratedDistribution<T> distribution) {
        return Stream.generate(distribution::sample);
    }

    /**
     * Returns a stream that returns a supplied default value when the stream is empty.
     *
     * @param stream the stream
     * @param defaultValue the supplier for the default value
     * @param <T> the type of value
     * @return the new stream
     */
    public static <T> Stream<T> defaultIfEmpty(Stream<T> stream, Supplier<T> defaultValue) {
        return orIfEmpty(stream, () -> Stream.of(defaultValue.get()));
    }

    /**
     * Returns a stream that returns a supplied default stream when the original stream is empty.
     *
     * @param stream the stream
     * @param onEmpty the supplier for the default stream
     * @param <T> the type of value
     * @return the new stream
     */
    public static <T> Stream<T> orIfEmpty(Stream<T> stream, Supplier<Stream<T>> onEmpty) {
        Iterator<T> iterator = stream.iterator();
        if (!iterator.hasNext()) {
            return onEmpty.get();
        }
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
    }

}