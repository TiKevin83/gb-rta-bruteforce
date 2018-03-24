package stringflow.rta;

import java.io.IOException;

public class LibgambatteBuilder {

    public static void buildGambatte(boolean drawing, int rtcOffset) throws IOException {
        byte base[] = Util.readBytesFromFile("./libgambatte/cyggambatte.dll");
        int drawVar = drawing ? 0x11223344 : 0x44332211;

        base[0x5E200] = (byte)((rtcOffset >> 0) & 0xFF);
        base[0x5E201] = (byte)((rtcOffset >> 8) & 0xFF);
        base[0x5E202] = (byte)((rtcOffset >> 16) & 0xFF);
        base[0x5E203] = (byte)((rtcOffset >> 24) & 0xFF);

        base[0x5E214] = (byte)((drawVar >> 0) & 0xFF);
        base[0x5E215] = (byte)((drawVar >> 8) & 0xFF);
        base[0x5E216] = (byte)((drawVar >> 16) & 0xFF);
        base[0x5E217] = (byte)((drawVar >> 24) & 0xFF);

        Util.writeBytesToFile("./libgambatte/cyggambatte.dll", base);
    }
}