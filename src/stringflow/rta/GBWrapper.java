package stringflow.rta;

import mrwint.gbtasgen.Gb;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class GBWrapper {
	
	private Gb gb;
	private HashMap<String, Integer> addressMap;
	private int hJoypad;
	private int hRandomAdd;
	private int hRandomSub;
	private int heldJoypad;
	
	public GBWrapper(Gb gb, String symFile, int hJoypad, int hRandomAdd, int hRandomSub) throws IOException {
		this.gb = gb;
		this.addressMap = new HashMap<String, Integer>();
		this.hJoypad = hJoypad;
		this.hRandomAdd = hRandomAdd;
		this.hRandomSub = hRandomSub;
		this.heldJoypad = 0;
		long startTime = System.currentTimeMillis();
		String symFileContent = Util.readTextFile(symFile);
		String lines[] = symFileContent.split("\n");
		for(int i = 0; i < lines.length; i++) {
			if(lines[i].isEmpty() || lines[i].startsWith(";")) {
				continue;
			}
			// format: bb:aaaa name
			int index = Integer.decode("0x" + lines[i].substring(0, lines[i].indexOf(":")));
			int address = Integer.decode("0x" + lines[i].substring(lines[i].indexOf(":") + 1, lines[i].indexOf(" ")));
			String name = lines[i].substring(lines[i].indexOf(" ") + 1);
			if(index == 0 && name.toLowerCase().startsWith("sprite")) {
				name = "w" + name;
			}
			addressMap.put(name.toLowerCase(), index > 1 ? index * 0x4000 + (address - 0x4000) : address);
		}
		System.out.println("Read and processed " + symFile + " in " + (System.currentTimeMillis() - startTime) + "ms");
	}
	
	public void hold(int heldJoypad) {
		this.heldJoypad = heldJoypad;
	}
	
	public void press(int joypad) {
		injectInput(joypad | heldJoypad);
		gb.step(joypad | heldJoypad);
	}
	
	public void advanceFrame() {
		advance(1);
	}
	
	public void advance(int amount) {
		injectInput(heldJoypad);
		for(int i = 0; i < amount; i++) {
			gb.step(heldJoypad);
			try {
				Thread.sleep(5);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Address advanceTo(Object... addresses) {
		int addressesAsInts[] = new int[addresses.length];
		for(int i = 0; i < addresses.length; i++) {
			addressesAsInts[i] = convertObjectToAddress(addresses[i]);
		}
		int result = 0;
		injectInput(heldJoypad);
		while(result == 0) {
			if(addresses.length == 0) {
				result = gb.step(heldJoypad);
			} else {
				result = gb.step(heldJoypad, addressesAsInts);
			}
		}
		return new Address(getAddressName(result), result);
	}
	
	public int read(Object address) {
		return gb.readMemory(convertObjectToAddress(address));
	}
	
	public void write(Object address, int value) {
		gb.writeMemory(convertObjectToAddress(address), value);
	}
	
	public int getAddress(String addressName) {
		if(!addressMap.containsKey(addressName.toLowerCase())) {
			throw new RuntimeException("Could not find " + addressName + " in the sym file!");
		}
		return addressMap.get(addressName.toLowerCase());
	}
	
	private String getAddressName(int address) {
		for(Map.Entry<String, Integer> entry : addressMap.entrySet()) {
			if(address == entry.getValue()) {
				return entry.getKey();
			}
		}
		return "NOT FOUND";
	}
	
	public Gb getGb() {
		return gb;
	}
	
	public void injectInput(int input) {
		if(read(0xFF88) != 0x3) { // hacked way to tell if in bootrom or not
			write(hJoypad, input);
		}
	}
	
	public int convertObjectToAddress(Object obj) {
		if(obj instanceof String) {
			return getAddress(String.valueOf(obj));
		} else if(obj instanceof Integer) {
			return Integer.valueOf(String.valueOf(obj));
		} else {
			throw new RuntimeException("Tried to convert an invalid type into Address!");
		}
	}
	
	public ByteBuffer saveState() {
		return gb.saveState();
	}
	
	public void loadState(ByteBuffer state) {
		gb.loadState(state);
	}
	
	public int getRandomAdd() {
		return read(hRandomAdd);
	}
	
	public int getRandomSub() {
		return read(hRandomSub);
	}
}
