package stringflow.rta.gen1;

import static stringflow.rta.Joypad.*;

public class PokeYellow {

	/* obsolete
	public static final int readJoypad = 0x01B9;
	public static final int joypadOverworld = 0x0C51;
	public static final int newBattle = 0x0480;
	public static final int enterMap = 0x01D7;
	public static final int encounterTest = 0x1388E;
	public static final int saveInject = 0x739D6;
	public static final int pikaInject = 0x739E7;
	public static final int manualTextScroll = 0x388E;
	public static final int playCry = 0x118B;
	public static final int playPikachuSoundClip = 0xF0000;
	public static final int displayListMenuId = 0x2AE0;
	public static final int igtInject = 0x739D6;
	public static final int catchSuccess = 0xD4D4;
	public static final int catchFailure = 0xD4D6;
	public static final int textJingleCommand = 0x1A0A;
	public static final int textJingleHidden = 0x75FAA;
	public static final int printLetterDelay = 0x38C8;
	public static final int displayTextBoxId = 0x3010;
    public static final int delayAtEndOfShootingStar = 0x41A74;
    public static final int softReset = 0x1D05;
    public static final int pikaIntro2 = 0xF996A;
    public static final int pikaIntro4 = 0xF9A1E;
    public static final int pikaIntro6 = 0xF9A6B;
    public static final int pikaIntro8 = 0xF9AD8;
    public static final int pikaIntro10 = 0xF9B04;
    public static final int pikaIntro12 = 0xF9CAC;
    public static final int titleScreen = 0x4171;
    public static final int damageRollCalculation = 0x3E82A;
    public static final int displayNamingScreen = 0x6307;
	*/

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