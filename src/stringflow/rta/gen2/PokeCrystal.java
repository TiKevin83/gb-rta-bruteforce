package stringflow.rta.gen2;

import stringflow.rta.Address;

public class PokeCrystal extends Gen2Game {
	
	public PokeCrystal() {
		super("/pokecrystal.sym", 0xFFE1, 0xFFE2);
		addressList.add(new Address("joypadCall", 0x098F));
		addressList.add(new Address("titleScreenJoypad", 0x49DF5));
	}
	
	public void setCsum(byte target[]) {
		int csum1 = 0;
		for(int i = 0x2009; i <= 0x2B82; i++) {
			csum1 += target[i] & 0xFF;
		}
		csum1 = (csum1 & 0xFFFF) ^ 0xFFFF;
		target[0x2D0E] = (byte)((csum1 / 256 & 0xFF) ^ 0xFF);
		target[0x2D0D] = (byte)((csum1 % 256 & 0xFF) ^ 0xFF);
		int csum2 = 0;
		for(int j = 0x1209; j <= 0x1D82; j++) {
			csum2 += target[j] & 0xFF;
		}
		csum2 = (csum2 & 0xFFFF) ^ 0xFFFF;
		target[0x1F0E] = (byte)((csum2 / 256 & 0xFF) ^ 0xFF);
		target[0x1F0D] = (byte)((csum2 % 256 & 0xFF) ^ 0xFF);
	}
}