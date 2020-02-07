package mb.statix.search;

import java.math.BigInteger;
import java.util.*;


/**
 * An immutable subset view of a given list.
 *
 * @param <T> the type of elements in the collection
 */
/* package private */ final class Subset<T> extends AbstractCollection<T> {

    private final List<T> set;
    private final BigInteger bits;
    private final int size;

    /**
     * Initializes a new instance of the {@link Subset} class.
     *
     * @param set the set for which this is a view
     * @param bits bits indicating which elements to include in the subset (MSB of index 0 is first element of list).
     *                 Trailing empty elements may be elided.
     * @param size the size of the subset, must match the number of 1s in the bits array
     */
    /* package private */ Subset(List<T> set, BigInteger bits, int size) {
        this.set = set;
        this.bits = bits;
        assert size == bits.bitCount() : "Expected " + size + " bits, got " + bits.bitCount() + ".";
        this.size = size;
    }

    @Override
    public Iterator<T> iterator() {
        return new SubsetIterator();
    }

    @Override
    public int size() {
        return this.size;
    }

    /**
     * Subset iterator.
     */
    private class SubsetIterator implements Iterator<T> {
        int index;

        public SubsetIterator() {
            this.index = bits.getLowestSetBit();
        }

        @Override
        public boolean hasNext() {
            return this.index >= 0;
        }

        @Override
        public T next() {
            if (this.index < 0) throw new NoSuchElementException();
            T value = set.get(this.index);
            this.index = computeNextIndex(this.index);
            return value;
        }

        /**
         * Computes the next index of an element to return.
         *
         * @param currentIndex the current index, which must be greater than or equal to zero
         * @return the next index; or -1 when there are none
         */
        private int computeNextIndex(int currentIndex) {
            return bits.andNot(BigInteger.ONE.shiftLeft(currentIndex).min(BigInteger.ONE)).getLowestSetBit();
        }
    }

}

