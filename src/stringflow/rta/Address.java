package stringflow.rta;

import stringflow.rta.util.NamedDataType;

public class Address extends NamedDataType {
	
	private int address;
	
	public Address(String name, int address) {
		super(name);
		this.address = address;
	}
	
	public Address(String name, String address) {
		super(name);
		int bank = Integer.decode("0x" + address.substring(0, address.indexOf(":")));
		int bankOffset = Integer.decode("0x" + address.substring(address.indexOf(":") + 1));
		this.address = (bank << 16) | bankOffset;
	}
	
	public int getAddress() {
		return address;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof Integer) {
			return address == (Integer) obj;
		} else if(obj instanceof String) {
			return name.equalsIgnoreCase(String.valueOf(obj));
		}
		return super.equals(obj);
	}
}
