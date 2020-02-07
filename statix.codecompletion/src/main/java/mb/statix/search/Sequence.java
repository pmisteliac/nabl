package mb.statix.search;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
     * This is an initial operation.
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
     * This is an initial operation.
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
     * This is an initial operation.
     *
     * @param es the elements in the sequence
     * @param <T> the type of elements in the sequence
     * @return the new sequence
     */
    @SafeVarargs
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
     * This is an initial operation.
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

    /**
     * Returns a range of integers.
     *
     * This is an initial operation.
     *
     * @param inclusiveStart the start value, inclusive
     * @param exclusiveEnd the end value, exclusive
     * @return the sequence of integers
     */
    static Sequence<Integer> range(int inclusiveStart, int exclusiveEnd) {
        return new SequenceBase<Integer>() {
            private int i = inclusiveStart;

            @Override
            protected boolean doHasNext() {
                return i < exclusiveEnd;
            }

            @Override
            protected Integer doNext() {
                int next = i;
                i += 1;
                return next;
            }
        };
    }

    /**
     * Generates a sequence of values from the specified generator function.
     *
     * This is an initial operation.
     *
     * @param generator the generator function which returns a value; or {@code null} to terminate
     * @param <T> the type of elements in the sequence
     * @return the generated sequence
     */
    static <T> Sequence<T> generate(Supplier<T> generator) {
        return new SequenceBase<T>() {
            private T next = generator.get();

            @Override
            protected boolean doHasNext() {
                return next != null;
            }

            @Override
            protected T doNext() {
                if (next == null) throw new NoSuchElementException();
                T current = next;
                next = generator.get();
                return current;
            }
        };
    }

    // ------------------------------------------------------------------ //

    /**
     * Concatenates the specific sequence after this sequence.
     *
     * This is an intermediate operation.
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
     * This is an intermediate operation.
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
     * This is an intermediate operation.
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

    /**
     * Skips the elements of the sequence for which the predicate returns false.
     *
     * This is an intermediate operation.
     *
     * @param f the function to apply
     * @return the new sequence
     */
    default Sequence<T> filter(Predicate<T> f) {
        return new SequenceBase<T>() {
            private T next = getNext();

            @Override
            protected boolean doHasNext() {
                return this.next != null;
            }

            @Override
            protected T doNext() {
                T current = this.next;
                this.next = getNext();
                return current;
            }

            private T getNext() {
                if (!Sequence.this.hasNext()) return null;
                T next = Sequence.this.next();
                while (!f.test(next) && Sequence.this.hasNext()) {
                    next = Sequence.this.next();
                }
                if (!Sequence.this.hasNext()) return null;
                return next;
            }
        };
    }

    /**
     * Applies a function to each element of the sequence, and flattens the resulting sequences.
     *
     * This is an intermediate operation.
     *
     * @param f the function to apply
     * @param <R> the type of sequence elements resulting from the function
     * @return the new sequence
     */
    default <R> Sequence<R> flatMap(Function<T, Sequence<R>> f) {
        return flatten(Sequence.this.map(f));
    }

    /**
     * Applies a side-effectful action to each element of the sequence.
     *
     * This is an intermediate operation.
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
     * Takes a number of elements from the sequence.
     *
     * This is an intermediate operation.
     *
     * @param amount the number of elements to take at maximum
     * @return the new sequence
     */
    default Sequence<T> take(int amount) {
       return new SequenceBase<T>() {

           int remaining = amount;

           @Override
           protected boolean doHasNext() {
               return remaining > 0 && Sequence.this.hasNext();
           }

           @Override
           protected T doNext() {
               if (remaining <= 0) throw new NoSuchElementException();
               T next = Sequence.this.next();
               remaining -= 1;
               return next;
           }
       };
    }

    /**
     * Drops a number of elements from the sequence.
     *
     * This is an intermediate operation, but it evaluates the elements that are dropped.
     *
     * @param amount the number of elements to skip
     * @return the new sequence
     */
    default Sequence<T> drop(int amount) {
        return new SequenceBase<T>() {

            boolean skipped = false;

            @Override
            protected boolean doHasNext() {
                skipAll();
                return Sequence.this.hasNext();
            }

            @Override
            protected T doNext() {
                skipAll();
                return Sequence.this.next();
            }

            private void skipAll() {
                if (skipped) return;
                int toSkip = amount;
                while (toSkip > 0 && Sequence.this.hasNext()) {
                    // We don't care about the result
                    Sequence.this.next();
                    toSkip -= 1;
                }
                skipped = true;
            }
        };
    }

    // ------------------------------------------------------------------ //


    /**
     * Buffers the input sequence.
     *
     * This is a terminal intermediate operation.
     *
     * @return a new sequence
     */
    default Sequence<T> buffer() {
        return Sequence.from(Sequence.this.toList());
    }

    /**
     * Applies a function to the buffered sequence.
     *
     * This is a terminal intermediate operation.
     *
     * @param f the function to apply
     * @param <R> the type of elements in the resulting sequence
     * @return the new sequence
     */
    default <R> Sequence<R> transform(Function<List<T>, Iterable<R>> f) {
        return Sequence.from(f.apply(Sequence.this.toList()));
    }

    /**
     * Coerces the sequence into a list.
     *
     * This is a terminal operation.
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

    /**
     * Produces the powerset of the elements in the sequence.
     *
     * This is a terminal intermediate operation.
     *
     * @return the powerset
     */
    default Sequence<Collection<T>> powersetOf() {
        final List<T> list = this.toList();
        final int[] size = {0};
        return Sequence.generate(() -> size[0]++).take(list.size()).flatMap( s -> new SubsetSequence<>(list, s));
    }

    /**
     * Produces the subsets of a specific cardinality of the elements in the sequence.
     *
     * This is a terminal intermediate operation.
     *
     * @param size the cardinality of the sets to return; must be less than or equal to the size of the set
     * @return the sequence of sets with the same cardinality
     */
    default Sequence<Collection<T>> subsetsOfSize(int size) {
        return new SubsetSequence<>(Sequence.this.toList(), size);
    }

//    /**
//     * Produces the subsets of a specific cardinality of the elements in the sequence.
//     *
//     * This is a terminal intermediate operation.
//     *
//     * @param size the cardinality of the sets to return; must be less than or equal to the size of the set
//     * @return the sequence of sets with the same cardinality
//     */
//    static Sequence<Set<T>> subsetsOfSize(int size) {
//        return new SequenceBase<Set<T>>() {
//            private final List<T> list = Sequence.this.toList();
//            private BigInteger nextSubset = BigInteger.ONE.shiftLeft(size).subtract(BigInteger.ONE);
//
//            @Override
//            protected boolean doHasNext() {
//                return nextSubset.bitLength() <= list.size();
//            }
//
//            @Override
//            protected Set<T> doNext() {
//                if (!doHasNext()) throw new NoSuchElementException();
//                Set<T> subset = new Subset<>(list, nextSubset, size);
//                this.nextSubset = snoob(this.nextSubset);
//                return subset;
//            }
//
//            /**
//             * Gets the next higher integer with the same number of bits set.
//             *
//             * @param x a value that is not 0
//             * @return the next higher integer with the same number of bits set
//             */
//            private BigInteger snoob(BigInteger x) {
//                // Inspired by: Hacker's Delight
//                BigInteger s = x.and(x.negate());           // get the right-most set bit
//                BigInteger r = x.add(s);                    // get the bit set to the left of the right-most pattern
//                BigInteger m = x.xor(r);                    // isolate the pattern from the rest
//                BigInteger a = m.shiftRight(2).divide(s);   // adjust and correct the pattern
//                return r.or(a);                             // integrate the new pattern
//            }
//        };
//    }

}
