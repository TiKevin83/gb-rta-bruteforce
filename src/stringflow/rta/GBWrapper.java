package stringflow.rta;

import mrwint.gbtasgen.Gb;

import java.io.IOException;
import java.util.HashMap;

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

    public int advanceTo(String addressName) {
        return advanceTo(getAddress(addressName));
    }

    public int advanceTo(String... addresses) {
        int addressesAsInts[] = new int[addresses.length];
        for(int i = 0; i < addresses.length; i++) {
            addressesAsInts[i] = getAddress(addresses[i]);
        }
        return advanceTo(addressesAsInts);
    }

    public int advanceTo(int... addresses) {
        int result = 0;
        gb.writeMemory(joypadAddress, heldJoypad);
        while(result == 0) {
            if(addresses.length == 0) {
                result = gb.step(heldJoypad);
            } else {
                result = gb.step(heldJoypad, addresses);
            }
        }
        return result;
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

    public Gb getGb() {
        return gb;
    }
}
