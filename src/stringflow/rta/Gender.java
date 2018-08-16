package stringflow.rta;

public enum Gender {

	GENDERLESS(""), MALE("Male"), FEMALE("Female");
	
	private String name;
	
	private Gender(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}