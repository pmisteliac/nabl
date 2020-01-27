package mb.statix.generator;

import mb.statix.generator.nodes.SearchNode;
import mb.statix.generator.nodes.SearchNodes;
import mb.statix.generator.search.SStrategy;
import mb.statix.generator.search.StrategyNode;
import mb.statix.solver.IConstraint;

public interface SearchLogger {

    void init(long seed, SearchStrategy<?, ?> strategy, Iterable<IConstraint> constraint);

    void init(long seed, SStrategy strategy, Iterable<IConstraint> constraint);

    void success(SearchNode<SearchState> n);

    void success(StrategyNode n);

    void failure(SearchNodes<?> nodes);

    void failure(StrategyNode n);

    final SearchLogger NOOP = new SearchLogger() {

        @Override public void init(long seed, SearchStrategy<?, ?> strategy, Iterable<IConstraint> constraint) {
        }


        @Override public void init(long seed, SStrategy strategy, Iterable<IConstraint> constraint) {
        }

        @Override public void success(SearchNode<SearchState> n) {
        }

        @Override public void success(StrategyNode n) {
        }

        @Override public void failure(SearchNodes<?> nodes) {
        }

        @Override public void failure(StrategyNode n) {
        }

    };

}