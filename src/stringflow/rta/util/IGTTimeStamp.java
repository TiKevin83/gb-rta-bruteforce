package stringflow.rta.util;

import stringflow.rta.libgambatte.Gb;

public class IGTTimeStamp {

	private int hours;
	private int minutes;
	private int seconds;
	private int frames;
	
	public IGTTimeStamp(Gb gb) {
		this(gb.read("w" + gb.getGame().getIgtPrefix() + "Hours"),
			 gb.read("w" + gb.getGame().getIgtPrefix() + "Minutes"),
			 gb.read("w" + gb.getGame().getIgtPrefix() + "Seconds"),
			 gb.read("w" + gb.getGame().getIgtPrefix() + "Frames"));
	}
	
	public IGTTimeStamp(int hours, int minutes, int seconds, int frames) {
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.frames = frames;
	}
	
	public int getHours() {
		return hours;
	}
	
	public int getMinutes() {
		return minutes;
	}
	
	public int getSeconds() {
		return seconds;
	}
	
	public int getFrames() {
		return frames;
	}
	
	public int getTotalFrames() {
		return hours * 216000 + minutes * 3600 + seconds * 60 + frames;
	}
	
	public boolean equals(Object o) {
		IGTTimeStamp other = (IGTTimeStamp) o;
		return getTotalFrames() == other.getTotalFrames();
	}
	
	public int hashCode() {
		return getTotalFrames();
	}
}