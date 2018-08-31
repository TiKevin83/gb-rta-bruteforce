package stringflow.rta.gen2;

import stringflow.rta.Address;

public class PokeGoldSilver extends Gen2Game {
	
	public PokeGoldSilver() {
		super("", 0xFFA9, 0xFFAA, 0xFFA6, 0xFFE3, 0xFFE4);
		addressList.add(new Address("ChooseWildEncounter.startwildbattle", "0A:6725"));
		addressList.add(new Address("OWPlayerInput", "25:68A7"));
		addressList.add(new Address("LoadEnemyMon.UpdateDVs", "0F:6800"));
		addressList.add(new Address("CountStep", "25:6AAC"));
		addressList.add(new Address("joypadCall", 0x0940));
		addressList.add(new Address("ButtonSound", 0x0A60));
		
		//wram
		addressList.add(new Address("wMapGroup", 0xDA00));
		addressList.add(new Address("wYCoord", 0xDA02));
		addressList.add(new Address("wXCoord", 0xDA03));
		addressList.add(new Address("wEnemyMonSpecies", 0xD0EF));
		addressList.add(new Address("wEnemyMonLevel", 0xD0FC));
		addressList.add(new Address("wEnemyMonDVs", 0xD0F5));
		addressList.add(new Address("wGameTimeFrames", 0xD1EF));
	}
	
	public void writeChecksum(byte sram[]) {
		int checksum = 0;
		for(int i = 0x2009; i < 0x2D69; i++) {
			checksum += (sram[i] & 0xFF);
		}
		sram[0x2D69] = (byte)((checksum >> 0) & 0xFF);
		sram[0x2D6A] = (byte)((checksum >> 8) & 0xFF);
	}
}