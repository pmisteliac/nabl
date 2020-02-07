package mb.statix.sequences;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * Produces the subsets of a specific cardinality of the elements in the sequence.
 */
/* package private */ class SubsetSequence<T> extends SequenceBase<Collection<T>> {

    private final List<T> list;
    private final int size;
    private BigInteger nextSubset;

    /**
     *
     * @param list the set whose elements to return, as a list
     * @param size the cardinality of the sets to return; must be less than or equal to the size of the set
     */
    /* package private */ SubsetSequence(List<T> list, int size) {
        this.list = list;
        this.size = size;
        this.nextSubset = BigInteger.ONE.shiftLeft(size).subtract(BigInteger.ONE);
    }

    @Override
    protected boolean doHasNext() {
        return nextSubset.bitLength() <= list.size();
    }

    @Override
    protected Collection<T> doNext() {
        if (!doHasNext()) throw new NoSuchElementException();
        Collection<T> subset = new Subset<>(list, nextSubset, size);
        this.nextSubset = snoob(this.nextSubset);
        return subset;
    }

    /**
     * Gets the next higher integer with the same number of bits set.
     *
     * @param x a value that is not 0
     * @return the next higher integer with the same number of bits set
     */
    private BigInteger snoob(BigInteger x) {
        // Inspired by: Hacker's Delight
        BigInteger s = x.and(x.negate());           // get the right-most set bit
        BigInteger r = x.add(s);                    // get the bit set to the left of the right-most pattern
        BigInteger m = x.xor(r);                    // isolate the pattern from the rest
        BigInteger a = m.shiftRight(2).divide(s);   // adjust and correct the pattern
        return r.or(a);                             // integrate the new pattern
    }

}
