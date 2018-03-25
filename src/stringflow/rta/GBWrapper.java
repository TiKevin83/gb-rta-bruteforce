package stringflow.rta;

import mrwint.gbtasgen.Gb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GBWrapper {

    private Gb gb;
    private HashMap<String, Integer> addressMap;
    private int joypadAddress;
    private int heldJoypad;

    public GBWrapper(Gb gb, String symFile, int joypadAddress) throws IOException {
        this.gb = gb;
        this.addressMap = new HashMap<String, Integer>();
        this.joypadAddress = joypadAddress;
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
        gb.writeMemory(joypadAddress, joypad);
        gb.step(joypad | heldJoypad);
    }

    public void advanceFrame() {
        advance(1);
    }

    public void advance(int amount) {
        gb.writeMemory(joypadAddress, heldJoypad);
        for(int i = 0; i < amount; i++) {
            gb.step(heldJoypad);
        }
    }

    public Address advanceTo(String addressName) {
        return advanceTo(getAddress(addressName));
    }

    public Address advanceTo(Object... addresses) {
        int addressesAsInts[] = new int[addresses.length];
        for(int i = 0; i < addresses.length; i++) {
            if(addresses[i] instanceof String) {
                addressesAsInts[i] = getAddress(String.valueOf(addresses[i]));
            } else if(addresses[i] instanceof Integer) {
                addressesAsInts[i] = Integer.valueOf(String.valueOf(addresses[i]));
            } else {
                throw new RuntimeException("Advancement address at index " + i + " is an invalid type.");
            }
        }
        int result = 0;
        gb.writeMemory(joypadAddress, heldJoypad);
        while(result == 0) {
            if(addresses.length == 0) {
                result = gb.step(heldJoypad);
            } else {
                result = gb.step(heldJoypad, addressesAsInts);
            }
        }
        return new Address(getAddressName(result), result);
    }

    public int read(String addressName) {
        return gb.readMemory(getAddress(addressName));
    }

    public int read(int address) {
        return gb.readMemory(address);
    }

    public void write(String addressName, int value) {
        gb.writeMemory(getAddress(addressName), value);
    }

    public void write(int address, int value) {
        gb.writeMemory(address, value);
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
        } return "NOT FOUND";
    }

    public Gb getGb() {
        return gb;
    }
}
