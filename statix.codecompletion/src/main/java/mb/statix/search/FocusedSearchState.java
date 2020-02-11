package mb.statix.search;

import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.unification.UnifierFormatter;
import mb.nabl2.terms.unification.Unifiers;
import mb.nabl2.terms.unification.ud.IUniDisunifier;
import mb.statix.solver.Delay;
import mb.statix.solver.IConstraint;
import org.metaborg.util.functions.Action1;
import org.metaborg.util.functions.Function2;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * A focused search state.
 *
 * @param <C> the type of constraint to focus on
 */
public final class FocusedSearchState<C extends IConstraint> {

    private final SearchState searchState;

    private final C focus;
    private final Set.Immutable<IConstraint> unfocused;

    public FocusedSearchState(SearchState searchState, C focus) {
        this.searchState = searchState;
        Set.Immutable<IConstraint> constraints = searchState.getConstraints();
        if(!constraints.contains(focus)) {
            throw new IllegalArgumentException("The focus constraint is not one of the constraints in the state.");
        }
        this.focus = focus;
        this.unfocused = constraints.__remove(focus);
    }

    public SearchState getSearchState() {
        return searchState;
    }

    public C getFocus() {
        return focus;
    }

    public Set<IConstraint> getUnfocused() {
        return unfocused;
    }

    @Override public String toString() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        try {
            writer.println("FocusedSearchState:");
            write(writer, (t, u) -> new UnifierFormatter(u, 2).format(t));
        } catch (IOException e) {
            // This can never happen.
            throw new RuntimeException(e);
        }
        return out.toString();
    }

    public void write(PrintWriter writer, Function2<ITerm, IUniDisunifier, String> prettyprinter) throws IOException {
        final IUniDisunifier unifier = getSearchState().getState().unifier();
        writer.println("| focus:");
        writer.println("|   " + focus.toString(t -> prettyprinter.apply(t, unifier)));
        this.getSearchState().write(writer, prettyprinter);
    }

}
