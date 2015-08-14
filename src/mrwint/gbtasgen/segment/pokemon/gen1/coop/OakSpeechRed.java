package mrwint.gbtasgen.segment.pokemon.gen1.coop;

import mrwint.gbtasgen.move.Move;
import mrwint.gbtasgen.segment.pokemon.EflTextSegment;
import mrwint.gbtasgen.segment.pokemon.gen1.common.NamingSegment;
import mrwint.gbtasgen.segment.util.EflSkipTextsSegment;
import mrwint.gbtasgen.segment.util.SeqSegment;

public class OakSpeechRed extends SeqSegment {

	@Override
	public void execute() {
    seq(new EflSkipTextsSegment(13));

    seqEflButton(Move.A);
    seq(new NamingSegment("R"));
    seqEflButton(Move.START);

		seq(new EflSkipTextsSegment(5));

		seqEflButton(Move.A);
		seq(new NamingSegment("r"));
		seqEflButton(Move.START);

		seq(new EflSkipTextsSegment(7));
		seq(new EflTextSegment());
	}
}