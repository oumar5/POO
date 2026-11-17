package fundraising.donation;

public class Donor {

    private String name;
    public Donor(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (! (o instanceof Donor)) 
                return false;
        Donor other = (Donor) o;
        return other.name.equals(this.name);
    }
}
