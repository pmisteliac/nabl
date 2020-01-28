package mb.statix.search;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

import static mb.statix.search.SequenceBase.EMPTY_SEQUENCE;


/**
 * A lazy iterator.
 *
 * @param <T> the type of elements in the iterator
 */
public interface Sequence<T> extends Iterator<T> {

    // Cuts the sequence short: it returns no elements any more.
    void cut();





    // ------------------------------------------------------------------ //

    /**
     * Returns an empty sequence.
     *
     * @param <T> the type of elements in the sequence
     * @return the new sequence
     */
    static <T> Sequence<T> empty() {
        //noinspection unchecked
        return (Sequence<T>)EMPTY_SEQUENCE;
    }

    /**
     * Returns an empty sequence.
     *
     * @param <T> the type of elements in the sequence
     * @return the new sequence
     */
    static <T> Sequence<T> of() {
        return empty();
    }

    /**
     * Returns the specified elements as a sequence.
     *
     * @param es the elements in the sequence
     * @param <T> the type of elements in the sequence
     * @return the new sequence
     */
    @SafeVarargs    // Safe?
    static <T> Sequence<T> of(T... es) {
        if (es.length == 0) return empty();
        return new SequenceBase<T>() {

            private int index = 0;
            @Override
            protected boolean doHasNext() {
                return index < es.length;
            }

            @Override
            protected T doNext() {
                T e = es[index];
                index += 1;
                return e;
            }
        };
    }

    /**
     * Returns the specified iterable as a sequence.
     *
     * @param iterable the iterable
     * @param <T> the type of elements in the sequence
     * @return the new sequence
     */
    static <T> Sequence<T> from(Iterable<T> iterable) {
        return new SequenceBase<T>() {
            private final Iterator<T> iterator = iterable.iterator();

            @Override
            protected boolean doHasNext() {
                return iterator.hasNext();
            }

            @Override
            protected T doNext() {
                return iterator.next();
            }
        };
    }

    // ------------------------------------------------------------------ //

    /**
     * Concatenates the specific sequence after this sequence.
     *
     * @param seq the sequence to concatenate after this one
     * @return the new sequence
     */
    default Sequence<T> concatWith(Sequence<T> seq) {
        return flatten(Sequence.of(Sequence.this, seq));
    }

    /**
     * Flattens the given sequences.
     *
     * @param seqs the sequences to flatten
     * @param <T> the type of elements in the sequences
     * @return the new sequence
     */
    static <T> Sequence<T> flatten(Sequence<Sequence<T>> seqs) {
        return new SequenceBase<T>() {
            @Nullable
            private Sequence<T> currentSequence;

            @Override
            protected boolean doHasNext() {
                ensureSequence();
                return this.currentSequence != null && this.currentSequence.hasNext();
            }

            @Override
            protected T doNext() {
                ensureSequence();
                if (this.currentSequence == null || !this.currentSequence.hasNext())
                    throw new NoSuchElementException();
                return this.currentSequence.next();
            }

            /**
             * Ensures that either {@link #currentSequence} is non-null if there is an available element;
             * or that {@link #currentSequence} is null if we've reached the end of the sequences.
             */
            private void ensureSequence() {
                while (this.currentSequence == null || !this.currentSequence.hasNext()) {
                    if (!seqs.hasNext()) {
                        this.currentSequence = null;
                        break;
                    }
                    this.currentSequence = seqs.next();
                }
            }
        };
    }

    /**
     * Applies a function to each element of the sequence.
     *
     * @param f the function to apply
     * @param <R> the type of element resulting from the function
     * @return the new sequence
     */
    default <R> Sequence<R> map(Function<T, R> f) {
        return new SequenceBase<R>() {
            @Override
            protected boolean doHasNext() {
                return Sequence.this.hasNext();
            }

            @Override
            protected R doNext() {
                T next = Sequence.this.next();
                return f.apply(next);
            }
        };
    }

    default <R> Sequence<R> flatMap(Function<T, Sequence<R>> f) {
        return flatten(Sequence.this.map(f));
    }

    /**
     * Applies a side-effectful action to each element of the sequence.
     *
     * @param a the action
     * @return the unaltered sequence
     */
    default Sequence<T> forEach(Consumer<T> a) {
        return new SequenceBase<T>() {
            @Override
            protected boolean doHasNext() {
                return Sequence.this.hasNext();
            }

            @Override
            protected T doNext() {
                T next = Sequence.this.next();
                a.accept(next);
                return next;
            }
        };
    }

    /**
     * Buffers the input sequence.
     *
     * @return a new sequence
     */
    default Sequence<T> buffer() {
        return new SequenceBase<T>() {
            @Nullable private List<T> buffer = null;

            private int index = 0;

            @Override
            protected boolean doHasNext() {
                List<T> buffer = getBuffer();
                return index < buffer.size();
            }

            @Override
            protected T doNext() {
                List<T> buffer = getBuffer();
                T e = buffer.get(index);
                index += 1;
                return e;
            }

            private List<T> getBuffer() {
                if (this.buffer == null) {
                    this.buffer = Sequence.this.toList();
                }
                return this.buffer;
            }
        };
    }

    /**
     * Coerces the sequence into a list.
     *
     * @return the list of elements
     */
    default List<T> toList() {
        ArrayList<T> list = new ArrayList<>();
        while (Sequence.this.hasNext()) {
            list.add(Sequence.this.next());
        }
        return list;
    }

}
