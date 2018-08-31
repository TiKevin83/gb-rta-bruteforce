package stringflow.rta.gen2;

import stringflow.rta.Address;

public class PokeCrystal extends Gen2Game {
	
	public PokeCrystal() {
		super("roms/pokecrystal.sym", 0xFFA7, 0xFFA8, 0xFFA4, 0xFFE1, 0xFFE2);
		addressList.add(new Address("joypadCall", 0x098F));
		addressList.add(new Address("titleScreenJoypad", 0x436D67));
	}
	
	public void writeChecksum(byte sram[]) {
		int checksum = 0;
		for(int i = 0x2009; i < 0x2B83; i++) {
			checksum += sram[i] & 0xFF;
		}
		sram[0x2D0D] = (byte)((checksum >> 0) & 0xFF);
		sram[0x2D0E] = (byte)((checksum >> 8) & 0xFF);
	}
}