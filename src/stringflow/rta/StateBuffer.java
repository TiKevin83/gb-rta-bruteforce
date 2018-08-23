package stringflow.rta;

import stringflow.rta.util.IGTTimeStamp;

public class StateBuffer {

	private IGTTimeStamp igt;
	private byte data[];
	
	public StateBuffer(IGTTimeStamp igt, byte[] data) {
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