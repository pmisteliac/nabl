package mb.statix.search.strategies;

import mb.statix.search.SearchComputation;
import mb.statix.search.SearchContext;
import mb.statix.search.SearchNode;
import mb.statix.search.SearchStrategy;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;


///**
// * The toString strategy.
// */
//public final class ToStringStrategy implements SearchStrategy<Integer> {
//
//    @Override
//    public List<SearchNode<Integer>> eval(SearchContext ctx, SearchNode<Integer> input, @Nullable SearchComputation<Integer> next) {
//        if (input.getValue() % 2 == 0) {
//            return Collections.singletonList(new SearchNode<>(input.getValue(), next));
//        } else {
//            return Collections.emptyList();
//        }
//    }
//
//    @Override
//    public String toString() {
//        return "isEven";
//    }
//
//}
