package stringflow.rta.gen1;

import mrwint.gbtasgen.Gb;
import stringflow.rta.GBWrapper;
import stringflow.rta.LibgambatteBuilder;

import static stringflow.rta.gen1.PokeYellow.*;
import static stringflow.rta.Joypad.*;

public class Test {

    public static void main(String args[]) throws Exception {
        LibgambatteBuilder.buildGambatte(false, 100);;
        Gb.loadGambatte(1);
        Gb gb = new Gb(0, false);
        gb.startEmulator("roms/pokeyellow.gbc");
        GBWrapper wrap = new GBWrapper(gb, "roms/pokeyellow.sym", hJoypad);
        /*gfSkip.execute(wrap);
        intro0.execute(wrap);
        title.execute(wrap);
        cont.execute(wrap);
        cont.execute(wrap);*/
        wrap.advanceTo("joypad");
        wrap.press(START);
        wrap.advanceTo("joypad");
        wrap.press(A);
        wrap.advanceTo("joypad");
        wrap.press(START);
        wrap.advanceTo("joypad");
        wrap.press(A);
        wrap.advanceTo("joypad");
        wrap.press(A);
        wrap.advanceTo("joypadOverworld");
        System.out.println(wrap.read("wplaytimeseconds"));
        System.out.println(wrap.read("wplaytimeframes"));
        while(true) {
            wrap.advanceFrame();
        }
    }
}
