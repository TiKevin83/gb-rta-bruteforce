package stringflow.rta.encounterigt;

public interface IFilterVisitor {
	
	public boolean onVisit(EncounterIGTResult result, EncounterIGTMap map);
}
