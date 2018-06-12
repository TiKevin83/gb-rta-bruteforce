package stringflow.rta;

import java.util.ArrayList;
import java.util.Arrays;

public class IntroSequence extends ArrayList<Strat> implements Comparable<IntroSequence> {

    private static final long serialVersionUID = -7505108790448829235L;

    public IntroSequence(Strat... strats) {
        super(Arrays.asList(strats));
    }

    public IntroSequence(IntroSequence other) {
        super(other);
    }

    public String getName(String gameName) {
        String ret = gameName;
        for(Strat s : this) {
            ret += s.name;
        }
        return ret;
    }

    public void execute(GBWrapper wrap) {
        for(Strat s : this) {
            s.execute(wrap);
        }
    }

    public int cost() {
        return this.stream().mapToInt((Strat s) -> s.cost).sum();
    }

    @Override
    public int compareTo(IntroSequence o) {
        return this.cost() - o.cost();
    }
}