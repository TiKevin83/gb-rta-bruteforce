package stringflow.rta;

import stringflow.rta.libgambatte.Gb;
import stringflow.rta.util.NamedDataType;

public class Strat extends NamedDataType {

	protected String logStr;
    protected int cost;
    protected Object[] addr;
    protected Integer[] input;
    protected Integer[] advanceFrames;

    public Strat(String name, String logStr, int cost, Object[] addr, Integer[] input, Integer[] advanceFrames) {
    	super(name);
    	this.logStr = logStr;
        this.addr = addr;
        this.cost = cost;
        this.input = input;
        this.advanceFrames = advanceFrames;
    }

    public void execute(Gb gb) {
        for(int i = 0; i < addr.length; i++) {
            gb.runUntil(addr[i]);
            gb.hold(input[i]);
            gb.frameAdvance(advanceFrames[i]);
        }
		gb.hold(0);
    }

    public String toString() {
        return logStr;
    }

    public int getCost() {
        return cost;
    }
}