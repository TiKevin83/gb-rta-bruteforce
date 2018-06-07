package stringflow.rta.gen1;

import stringflow.rta.GBWrapper;
import stringflow.rta.Location;
import stringflow.rta.Util;

public class Itemball {

    private Location possiblePickupLocations[];
    private int addressIndex;

    public Itemball( int addressIndex, Location... possiblePickupLocations) {
        this.possiblePickupLocations = possiblePickupLocations;
        this.addressIndex = addressIndex;
    }

    public boolean canBePickedUp(GBWrapper wrap) {
        for(Location pickupLocation : possiblePickupLocations) {
            if(wrap.read("wCurMap") == pickupLocation.map && wrap.read("wXCoord") == pickupLocation.x && wrap.read("wYCoord") == pickupLocation.y) {
                return true;
            }
        }
        return false;
    }

    public boolean isPickedUp(GBWrapper wrap) {
        return wrap.read("wSprite" + Util.getSpriteAddressIndexString(addressIndex) + "SpriteImageIdx") == 0xFF;
    }
}