package stringflow.rta;

public class Address {

    private String name;
    private int address;

    public Address(String name, int address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public int getAddress() {
        return address;
    }

    public boolean equals(Object obj) {
        if(obj instanceof Integer) {
            return address == (Integer) obj;
        } else if(obj instanceof String) {
            return name.equalsIgnoreCase(String.valueOf(obj));
        }
        return super.equals(obj);
    }
}
