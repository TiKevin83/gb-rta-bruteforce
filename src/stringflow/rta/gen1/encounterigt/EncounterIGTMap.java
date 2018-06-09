package stringflow.rta.gen1.encounterigt;

import stringflow.rta.GBWrapper;
import stringflow.rta.Util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class EncounterIGTMap {

	private EncounterIGTResult resultMap[];
	
	public EncounterIGTMap(int size) {
		resultMap = new EncounterIGTResult[size * 60];
	}
	
	public void addResult(GBWrapper wrap, int index, ArrayList<Integer>[] npcTimers, ByteBuffer save, boolean yoloballs[]) {
		String npcTimersString = "";
		for(int i = 1; i < npcTimers.length; i++) {
			long timerId = 0x00000000;
			int expireCounter = 0;
			for(int j = 1; j < npcTimers[i].size(); j++) {
				int previousTimer = npcTimers[i].get(j - 1);
				int currentTimer = npcTimers[i].get(j);
				if(currentTimer > previousTimer) {
					timerId |= j << expireCounter++ * 8;
				}
			}
			npcTimersString += String.format("%08X", timerId);
			if(i != npcTimers.length - 1) {
				npcTimersString += "_";
			}
		}
		int hRandom = (wrap.getRandomAdd() << 8) | wrap.getRandomSub();
		resultMap[index] = new EncounterIGTResult(wrap.read("wCurMap"), wrap.read("wXCoord"), wrap.read("wYCoord"), hRandom, npcTimersString, save, wrap.read("wEnemyMonSpecies"),
												wrap.read("wEnemyMonLevel"), (wrap.read("wEnemyMonDVs") << 8) | (wrap.read(wrap.getAddress("wEnemyMonDVs") + 1)),
											    yoloballs[0], yoloballs[1], yoloballs[2], yoloballs[3]);
	}
	
	public void save(String path) throws IOException {
		byte igtMapAsBytes[] = new byte[resultMap.length * EncounterIGTResult.SIZE + 3 + 4 + 4 + 2];
		byte header[] = "IGT".getBytes();
		int pointer = 0;
		pointer = Util.writeByteArray(igtMapAsBytes, pointer, header);
		pointer = Util.writeInt(igtMapAsBytes, pointer, EncounterIGTResult.SIZE);
		pointer = Util.writeInt(igtMapAsBytes, pointer, resultMap.length);
		pointer = Util.writeByte(igtMapAsBytes, pointer, (byte)0xFF);
		pointer = Util.writeByte(igtMapAsBytes, pointer, (byte)0xFF);
		for(int i = 0; i < resultMap.length; i++) {
			pointer = Util.writeByte(igtMapAsBytes, pointer, (byte)resultMap[i].getMap());
			pointer = Util.writeByte(igtMapAsBytes, pointer, (byte)resultMap[i].getX());
			pointer = Util.writeByte(igtMapAsBytes, pointer, (byte)resultMap[i].getY());
			pointer = Util.writeShort(igtMapAsBytes, pointer, (short)resultMap[i].getRNG());
			pointer = Util.writeIntArray(igtMapAsBytes, pointer, resultMap[i].getNpcTimersAsIntArray());
			pointer = Util.writeByte(igtMapAsBytes, pointer, (byte)resultMap[i].getSpecies());
			pointer = Util.writeByte(igtMapAsBytes, pointer, (byte)resultMap[i].getLevel());
			pointer = Util.writeShort(igtMapAsBytes, pointer, (short)resultMap[i].getDvs());
			pointer = Util.writeBoolean(igtMapAsBytes, pointer, resultMap[i].getYoloball());
			pointer = Util.writeBoolean(igtMapAsBytes, pointer, resultMap[i].getSelectYoloball());
			pointer = Util.writeBoolean(igtMapAsBytes, pointer, resultMap[i].getRedbarYoloball());
			pointer = Util.writeBoolean(igtMapAsBytes, pointer, resultMap[i].getRedbarSelectYoloball());
		}
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
		stream.write(igtMapAsBytes);
		stream.close();
	}
	
	public EncounterIGTResult getResult(int index) {
		return resultMap[index];
	}
	
	public int getSize() {
		return resultMap.length;
	}
}