package mb.statix.search.strategies.teststrategies;

import mb.statix.search.Strategy;

import java.util.stream.Stream;


/**
 * Test strategy that increments an integer,
 * and counts how many times it has been invoked.
 *
 * @param <CTX> the type of context
 */
public final class ChangeCaseStrategy<CTX> implements Strategy<String, String, CTX> {

    @Override
    public Stream<String> apply(CTX ctx, String input) throws InterruptedException {
        return Stream.of(stringChangeCase(input));
    }

    private String stringChangeCase(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            sb.append(charChangeCase(s.charAt(i)));
        }
        return sb.toString();
    }

    private char charChangeCase(char c) {
        if (Character.isUpperCase(c)) {
            return Character.toLowerCase(c);
        } else {
            return Character.toUpperCase(c);
        }
    }

}
