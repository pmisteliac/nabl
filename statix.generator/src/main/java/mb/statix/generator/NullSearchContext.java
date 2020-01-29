package mb.statix.generator;

import java.util.Random;

import mb.statix.generator.nodes.SearchNodes;
import mb.statix.spec.Spec;


public class NullSearchContext implements SearchContext {

    private final Random rnd;

    public NullSearchContext(Random rnd) {
        this.rnd = rnd;
    }

    @Override public int nextNodeId() {
        return 0;
    }

    @Override public Random rnd() {
        return rnd;
    }

    @Override public void failure(SearchNodes<?> nodes) {
    }

	@Override
	public Spec spec() {
		return null;
	}

//	@Override
//	public SetMultimap<String, Rule> getUnorderedRules() {
//		return null;
//	}

}