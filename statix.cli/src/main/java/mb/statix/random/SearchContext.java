package mb.statix.random;

import java.util.Random;

public interface SearchContext {

    public Random rnd();

    public void addFailed(SearchNode<SearchState> node);

}