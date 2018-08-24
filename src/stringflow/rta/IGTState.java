package stringflow.rta;

import stringflow.rta.util.IGTTimeStamp;

public class IGTState {

	private IGTTimeStamp igt;
	private byte data[];
	
	public IGTState(IGTTimeStamp igt, byte[] data) {
		this.igt = igt;
		this.data = data;
	}
	
	public IGTTimeStamp getIgt() {
		return igt;
	}
	
	public byte[] getData() {
		return data;
	}
}