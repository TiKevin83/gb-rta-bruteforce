package stringflow.rta.gen1;

import static stringflow.rta.Joypad.*;

public class PokeYellow {

	public static final int igtInjectAddr = 0x739D6;
	public static final int catchSuccess = 0xD4D4;
	public static final int catchFailure = 0xD4D6;

	public static final int hRandomAdd = 0xFFD3;
	public static final int hRandomSub = 0xFFD4;
	public static final int hJoypad = 0xFFF5;

	public static Strat gfSkip = new Strat("_gfskip", 0, new Object[] { "joypad"}, new Integer[] { START }, new Integer[] { 1 });
	public static Strat gfWait = new Strat("_gfwait", 253, new Object[] { 0x41A74}, new Integer[] { NO_INPUT }, new Integer[] { 0 });

	public static Strat intro0 = new Strat("_intro0", 0, new Object[] { "joypad"}, new Integer[] { A }, new Integer[] { 1 });
	public static Strat intro1 = new Strat("_intro1", 140, new Object[] { "YellowIntroScene2", "joypad"}, new Integer[] { NO_INPUT, A }, new Integer[] { 0, 1 });
	public static Strat intro2 = new Strat("_intro2", 275, new Object[] { "YellowIntroScene4", "joypad"}, new Integer[] { NO_INPUT, A }, new Integer[] { 0, 1 });
	public static Strat intro3 = new Strat("_intro3", 411, new Object[] { "YellowIntroScene6", "joypad"}, new Integer[] { NO_INPUT, A }, new Integer[] { 0, 1 });
	public static Strat intro4 = new Strat("_intro4", 594, new Object[] { "YellowIntroScene8", "joypad"}, new Integer[] { NO_INPUT, A }, new Integer[] { 0, 1 });
	public static Strat intro5 = new Strat("_intro5", 729, new Object[] { "YellowIntroScene10", "joypad"}, new Integer[] { NO_INPUT, A }, new Integer[] { 0, 1 });
	public static Strat intro6 = new Strat("_intro6", 864, new Object[] { "YellowIntroScene22", "joypad"}, new Integer[] { NO_INPUT, A }, new Integer[] { 0, 1 });
	public static Strat introwait = new Strat("_introwait", 147 + 1199, new Object[] { "DisplayTitleScreen"}, new Integer[] { NO_INPUT }, new Integer[] { 0 });

	public static Strat cont = new Strat("_cont", 0, new Object[] { "joypad"}, new Integer[] { A }, new Integer[] { 1 });
	public static Strat backout = new Strat("_backout", 140, new Object[] { "joypad"}, new Integer[] { B }, new Integer[] { 1 });
	public static Strat title = new Strat("_title", 0, new Object[] { "joypad"}, new Integer[] { START }, new Integer[] { 1 });


}