package stringflow.rta;

import stringflow.rta.util.IGTTimeStamp;

public class IGTState {

	private IGTTimeStamp igt;
	private byte state[];
	private int enterMapIGT;
	
	public IGTState(IGTTimeStamp igt, byte[] state) {
		this(igt, state, -1);
	}
	
	public IGTState(IGTTimeStamp igt, byte[] state, int enterMapIGT) {
		this.igt = igt;
		this.state = state;
		this.enterMapIGT = enterMapIGT;
	}
	
	public IGTTimeStamp getIgt() {
		return igt;
	}
	
	public byte[] getState() {
		return state;
	}
	
	public int getEnterMapIGT() {
		return enterMapIGT;
	}
	
	public void setIgt(IGTTimeStamp igt) {
		this.igt = igt;
	}
	
	public void setState(byte[] state) {
		this.state = state;
	}
}