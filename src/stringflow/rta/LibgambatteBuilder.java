package stringflow.rta;

import java.io.IOException;

public class LibgambatteBuilder {

    public static void buildGambatte(boolean drawning, int rtcOffset) throws IOException {
        //TODO: Either enable/disable drawning through hex editing or have 2 base dlls
        byte base[] = Util.readBytesFromFile("./base.dll");
        base[0x5E200] = (byte)((rtcOffset >> 0) & 0xFF);
        base[0x5E201] = (byte)((rtcOffset >> 8) & 0xFF);
        base[0x5E202] = (byte)((rtcOffset >> 16) & 0xFF);
        base[0x5E203] = (byte)((rtcOffset >> 24) & 0xFF);
        Util.writeBytesToFile("./libgambatte/cygambatte.dll", base);
    }
}