package mb.statix.search;

import org.junit.Test;

import java.util.List;

import static mb.statix.search.strategies.Strategies.*;

public class SomeTests {


    @Test
    public void f() {
//        SearchStrategy<Integer, Integer> strategy = or(inc(), id());
        Strategy<Integer, Integer, Object> strategy = seq(or(inc(), id()), isEven());

//        SearchNode<Integer> rootNode = new SearchNode<>(10);
        Sequence<Integer> seq = strategy.apply(null, 10);
        List<Integer> results = seq.toList();

        System.out.println("SUCCEEDS:");
        if (results.isEmpty()) {
            System.out.println("  <none>");
        } else {
            for (Integer succeed : results) {
                System.out.println("  " + succeed);
            }
        }

        System.out.println("Done");
    }

//    @Test
//    public void f() {
//        List<Integer> results = collect(null, 10, new SeqStrategy<>(new OrStrategy<>(new IncStrategy(), new IdStrategy<>()), new NotStrategy<>(new IsEvenStrategy())));
////        List<Integer> results = collect(null, 10, new SeqStrategy<>(new IncStrategy(), new SeqStrategy<>(new SplitStrategy<>(), new IncStrategy())));
//
//        System.out.println("Done");
//    }

//
//    public static <T> List<T> collect(SearchContext ctx, T value, SearchStrategy<T> strategy) {
//        ArrayList<T> succeeds = new ArrayList<>();
//        ArrayList<T> fails = new ArrayList<>();
//        ArrayList<T> cuts = new ArrayList<>();
//
//        SearchEventListener<T> listener = new SearchEventListener<T>() {
//
//            @Override
//            public void step(SearchNode<T> node) {
//            }
//
//            @Override
//            public void cut(SearchNode<T> node) {
//                cuts.add(node.getValue());
//            }
//
//            @Override
//            public void fail(SearchNode<T> node) {
//                fails.add(node.getValue());
//            }
//
//            @Override
//            public void done(SearchNode<T> node) {
//                succeeds.add(node.getValue());
//            }
//        };
//
//        SearchNode<T> rootNode = new SearchNode<>(value, new SearchComputation<>(strategy, null));
//
////        // Add an extra operation that collects the results.
////        SearchNode<T> rootNode = new SearchNode<>(value, new SearchComputation<>(strategy, new SearchComputation<>((c, n, next) -> {
////            succeeds.add(n.getValue());
////            // Return nothing: done!
////            return Collections.emptyList();
////        }, null)));
//
//        // Evaluate all nodes
//        Stack<SearchNode<T>> stack = new Stack<>();
//        stack.push(rootNode);
//        while (!stack.isEmpty()) {
//            SearchNode<T> node = stack.pop();
//            List<SearchNode<T>> newNodes = node.step(ctx, listener);
//            for (SearchNode<T> newNode : newNodes) {
//                stack.push(newNode);
//            }
//        }
//
//        System.out.println("FAILS:");
//        if (fails.isEmpty()) {
//            System.out.println("  <none>");
//        } else {
//            for (T fail : fails) {
//                System.out.println("  " + fail);
//            }
//        }
//        System.out.println("CUTS:");
//        if (cuts.isEmpty()) {
//            System.out.println("  <none>");
//        } else {
//            for (T cut : cuts) {
//                System.out.println("  " + cut);
//            }
//        }
//        System.out.println("SUCCEEDS:");
//        if (succeeds.isEmpty()) {
//            System.out.println("  <none>");
//        } else {
//            for (T succeed : succeeds) {
//                System.out.println("  " + succeed);
//            }
//        }
//
//        return succeeds;
//    }

}
