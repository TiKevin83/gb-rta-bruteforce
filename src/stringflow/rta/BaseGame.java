package stringflow.rta;

import stringflow.rta.util.IO;
import stringflow.rta.util.NamedList;
import stringflow.rta.util.TextFile;

public abstract class BaseGame {
	
	protected NamedList<Address> addressList;
	protected NamedList<Strat> stratList;
	protected NamedList<Species> speciesList;
	private String igtPrefix;
	private int hRandomAdd;
	private int hRandomSub;
	
	public BaseGame(String symFilePath, String speciesMapPath, String igtPrefix, int hRandomAdd, int hRandomSub) {
		this.addressList = new NamedList<>();
		this.stratList = new NamedList<>();
		this.speciesList = new NamedList<>();
		this.igtPrefix = igtPrefix;
		this.hRandomAdd = hRandomAdd;
		this.hRandomSub = hRandomSub;
		if(!symFilePath.trim().isEmpty()) {
			TextFile symFile = IO.readText(symFilePath);
			symFile.visitAll((line, lineNumber) -> {
				if(!(line.isEmpty() || line.startsWith(";"))) {
					int curBank = Integer.decode("0x" + line.substring(0, line.indexOf(":")));
					int curBankOffset = Integer.decode("0x" + line.substring(line.indexOf(":") + 1, line.indexOf(" ")));
					String name = line.substring(line.indexOf(" ") + 1);
					if(curBank == 0 && name.toLowerCase().startsWith("sprite")) {
						name = "w" + name;
					}
					addressList.add(new Address(name.toLowerCase(), curBank > 1 ? curBank * 0x4000 + (curBankOffset - 0x4000) : curBankOffset));
				}
			});
		}
		if(!speciesMapPath.trim().isEmpty()) {
			TextFile speciesMap = IO.readText(speciesMapPath);
			speciesMap.visitAll((line, lineNumber) -> {
				String splitArray[] = line.split(" ");
				int indexNumber = Integer.decode(splitArray[0]);
				String name = splitArray[1];
				String genderRatio = splitArray[2];
				speciesList.add(new Species(name, indexNumber, GenderRatio.valueOf(genderRatio)));
			});
		}
	}
	
	public abstract void setCsum(byte target[]);
	
	public Address getAddress(int addressIn) {
		for(Address address : addressList) {
			if(address.getAddress() == addressIn) {
				return address;
			}
		}
		return new Address("", addressIn);
	}
	
	public Address getAddress(String nameIn) {
		return addressList.get(nameIn);
	}
	
	public Species getSpecies(int index) {
		for(Species species : speciesList) {
			if(species.getIndexNumber() == index) {
				return species;
			}
		}
		return speciesList.get(0);
	}
	
	public Species getSpecies(String nameIn) {
		return speciesList.get(nameIn);
	}
	
	public Strat getStrat(int index) {
		return stratList.get(index);
	}
	
	public Strat getStrat(String nameIn) {
		return stratList.get(nameIn);
	}
	
	public int getRandomAdd() {
		return hRandomAdd;
	}
	
	public int getRandomSub() {
		return hRandomSub;
	}
	
	public String getIgtPrefix() {
		return igtPrefix;
	}
}
