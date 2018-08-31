package stringflow.rta.libgambatte;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface Libgambatte extends Library {
	
	public static final Libgambatte INSTANCE = (Libgambatte)Native.loadLibrary("libgambatte/libgambatte.dll", Libgambatte.class);
	
	Pointer gambatte_create();
	int gambatte_load(Pointer gb, String path, int flags);
	int gambatte_loadbios(Pointer gb, String path, int size, int crc);
	int gambatte_cpuread(Pointer gb, int addr);
	int gambatte_cpuwrite(Pointer gb, int addr, int val);
	int gambatte_runfor(Pointer gb, Memory videoBuf, int pitch, Memory audioBuf, Memory samples);
	int gambatte_gethitinterruptaddress(Pointer gb);
	void gambatte_setinterruptaddresses(Pointer gb, Memory addrs, int numAddrs);
	int gambatte_savestate(Pointer gb, Memory videoBuffer, int pitch, Memory statBuf);
	boolean gambatte_loadstate(Pointer gb, Memory saveState, int size);
	void gambatte_setinputgetter(Pointer gb, IInputCallback getInput, Pointer p);
}
