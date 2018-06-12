package stringflow.rta.gen1;

import stringflow.rta.GBWrapper;

public class Strat {

    protected String name;
    protected int cost;
    protected Object[] addr;
    protected Integer[] input;
    protected Integer[] advanceFrames;

    public Strat(String name, int cost, Object[] addr, Integer[] input, Integer[] advanceFrames) {
        this.addr = addr;
        this.cost = cost;
        this.name = name;
        this.input = input;
        this.advanceFrames = advanceFrames;
    }

    public void execute(GBWrapper wrap) {
        for(int i = 0; i < addr.length; i++) {
            wrap.advanceTo(wrap.convertObjectToAddress(addr[i]));
            wrap.hold(input[i]);
            wrap.advance(advanceFrames[i]);
        }
        wrap.hold(0);
    }

    public String toString() {
        return name;
    }

    public int getCost() {
        return cost;
    }
}