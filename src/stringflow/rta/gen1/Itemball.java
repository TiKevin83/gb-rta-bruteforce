package stringflow.rta.gen1;

import stringflow.rta.GBWrapper;
import stringflow.rta.Location;
import stringflow.rta.Util;

public class Itemball {

    public static final Itemball WATER_GUN = new Itemball(0xD, new Location(59, 0x5, 0x1F), new Location(59, 0x6, 0x20));
    public static final Itemball RARE_CANDY = new Itemball(0xA, new Location(59, 0x23, 0x20), new Location(59, 0x22, 0x1F));
    public static final Itemball ESCAPE_ROPE = new Itemball(0xB, new Location(59, 0x24, 0x18));
    public static final Itemball MOON_STONE = new Itemball(0x9, new Location(59, 0x3, 0x2), new Location(59, 0x2, 0x3));
    public static final Itemball MEGA_PUNCH = new Itemball(0x9, new Location(61, 0x1C, 0x5));

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

    public int getAddressIndex() {
        return addressIndex;
    }
}