package stringflow.rta;

import stringflow.rta.libgambatte.Gb;
import stringflow.rta.util.NamedDataType;

public class Strat extends NamedDataType {

	protected String logStr;
	protected String joypadCommand;
    protected int cost;
    protected Object[] addr;
    protected Integer[] input;
    protected Integer[] advanceFrames;

    public Strat(String name, String logStr, String joypadCommand, int cost, Object[] addr, Integer[] input, Integer[] advanceFrames) {
    	super(name);
    	this.logStr = logStr;
    	this.joypadCommand = joypadCommand;
        this.addr = addr;
        this.cost = cost;
        this.input = input;
        this.advanceFrames = advanceFrames;
    }

    public void execute(Gb gb) {
        for(int i = 0; i < addr.length; i++) {
            gb.advanceTo(addr[i]);
            gb.hold(input[i]);
            gb.frameAdvance(advanceFrames[i]);
        }
    }

    public String toString() {
        return logStr;
    }

    public int getCost() {
        return cost;
    }
}