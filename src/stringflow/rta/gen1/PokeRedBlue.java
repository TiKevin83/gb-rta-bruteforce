package stringflow.rta.gen1;

import static stringflow.rta.Joypad.*;

public class PokeRedBlue {

    public static final int igtInjectAddr = 0x7366A;
    public static final int biosReadKeypad = 0x021D;

    public static final int hRandomAdd = 0xFFD3;
    public static final int hRandomSub = 0xFFD4;
    public static final int hJoypad = 0xFFF8;

    public static Strat pal = new Strat("_pal", 0, new Object[] { biosReadKeypad }, new Integer[] { UP }, new Integer[] { 1 });
    public static Strat nopal = new Strat("_nopal", 0, new Object[] { biosReadKeypad }, new Integer[] { NO_INPUT }, new Integer[] { 1 });   
    public static Strat abss = new Strat("_nopal(ab)", 0, new Object[] { biosReadKeypad, "Init" }, new Integer[] { A, A }, new Integer[] { 0, 0 });
    public static Strat holdpal = new Strat("_pal(hold)", 0, new Object[] { biosReadKeypad, "Init" }, new Integer[] { UP, UP }, new Integer[] { 0, 0 });
    public static Strat cheatpal = new Strat("_pal(ab)", 0, new Object[] { biosReadKeypad, biosReadKeypad, "Init" }, new Integer[] { UP, UP | A, UP | A }, new Integer[] { 70, 0, 0 });

    public static Strat gfSkip = new Strat("", 0, new Object[] { "joypad" }, new Integer[] { UP | SELECT | B }, new Integer[] { 1 });
    public static Strat nido0 = new Strat("_hop0", 0, new Object[] { "joypad" }, new Integer[] { UP | SELECT | B }, new Integer[] { 1 });
    public static Strat title = new Strat("", 0, new Object[] { "joypad" }, new Integer[] { START }, new Integer[] { 1 });
    public static Strat cont = new Strat("", 0, new Object[] { "joypad" }, new Integer[] { A }, new Integer[] { 1 });
}
