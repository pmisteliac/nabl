package mb.statix.sequences;

import java.util.NoSuchElementException;


/**
 * Base class for sequences.
 *
 * @param <T> the type of elements in the sequence
 */
public abstract class SequenceBase<T> implements Sequence<T> {

    @SuppressWarnings("rawtypes")
    static final Sequence EMPTY_SEQUENCE = new SequenceBase() {
        @Override
        protected boolean doHasNext() {
            return false;
        }

        @Override
        protected Object doNext() {
            throw new NoSuchElementException();
        }
    };

    private boolean cut = false;
    private boolean requestedNext = false;

    @Override
    public void cut() {
        this.cut = true;
    }

    @Override
    public boolean hasNext() {
        if (this.cut && !this.requestedNext) return false;
        boolean hasNext = doHasNext();
        this.requestedNext = hasNext;
        return hasNext;
    }

    @Override
    public T next() {
        if (this.cut && !this.requestedNext) throw new NoSuchElementException();
        this.requestedNext = false;
        return doNext();
    }

    protected abstract boolean doHasNext();
    protected abstract T doNext();

}
