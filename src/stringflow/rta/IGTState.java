package stringflow.rta;

import stringflow.rta.util.IGTTimeStamp;

public class IGTState {

	private IGTTimeStamp igt;
	private byte state[];
	
	public IGTState(IGTTimeStamp igt, byte[] state) {
		this.igt = igt;
		this.state = state;
	}
	
	public IGTTimeStamp getIgt() {
		return igt;
	}
	
	public byte[] getState() {
		return state;
	}
}