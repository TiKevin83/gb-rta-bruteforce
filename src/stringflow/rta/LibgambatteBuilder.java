package stringflow.rta;

import java.io.IOException;

public class LibgambatteBuilder {

    public static void buildGambatte(boolean drawing, int rtcOffset) throws IOException {
        //TODO: Fix this
        /*byte base[] = Util.readBytesFromFile("./base.dll");
        int drawVar = drawing ? 0x11223344 : 0x44332211;
        base[0x5E400] = (byte)((rtcOffset >> 0) & 0xFF);
        base[0x5E401] = (byte)((rtcOffset >> 8) & 0xFF);
        base[0x5E402] = (byte)((rtcOffset >> 16) & 0xFF);
        base[0x5E403] = (byte)((rtcOffset >> 24) & 0xFF);

        base[0x5E414] = (byte)((drawVar >> 0) & 0xFF);
        base[0x5E415] = (byte)((drawVar >> 8) & 0xFF);
        base[0x5E416] = (byte)((drawVar >> 16) & 0xFF);
        base[0x5E417] = (byte)((drawVar >> 24) & 0xFF);

        Util.writeBytesToFile("./libgambatte/cyggambatte.dll", base);*/
    }
}