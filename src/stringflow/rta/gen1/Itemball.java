package stringflow.rta.gen1;

import stringflow.rta.Location;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.util.StringUtils;

public class Itemball {
	
	private Location possiblePickupLocations[];
	private int addressIndex;
	
	public Itemball( int addressIndex, Location... possiblePickupLocations) {
		this.possiblePickupLocations = possiblePickupLocations;
		this.addressIndex = addressIndex;
	}
	
	public boolean canBePickedUp(Gb gb) {
		for(Location pickupLocation : possiblePickupLocations) {
			if(gb.read("wCurMap") == pickupLocation.map && gb.read("wXCoord") == pickupLocation.x && gb.read("wYCoord") == pickupLocation.y) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isPickedUp(Gb gb) {
		return gb.read("wSprite" + StringUtils.getSpriteAddressIndexString(addressIndex) + "SpriteImageIdx") == 0xFF;
	}
}
