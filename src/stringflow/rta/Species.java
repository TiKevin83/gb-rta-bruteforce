package stringflow.rta;

import stringflow.rta.GenderRatio;
import stringflow.rta.util.NamedDataType;

public class Species extends NamedDataType {

	private int indexNumber;
	private GenderRatio genderRatio;

	public Species(String name, int indexNumber, GenderRatio genderRatio) {
		super(name);
		this.indexNumber = indexNumber;
		this.genderRatio = genderRatio;
	}

	public String getName() {
		return name;
	}

	public int getIndexNumber() {
		return indexNumber;
	}
	
	public GenderRatio getGenderRatio() {
		return genderRatio;
	}
}