package fundraising.donation;

public class Donation {

    private Donor donor;
    private double amount;

    public Donation(Donor donor, double amount) {
        this.donor = donor;
        this.amount = amount;
    }

    public Donor getDonor() {
        return this.donor;
    }

    public double getAmount() {
        return this.amount;
    }


    public boolean equals(Object o) {
        if (! (o instanceof Donation)) 
                return false;
        Donation other = (Donation) o;
        if (this.donor == null) {
	    return other.donor == null;
	}
	else {
	    return this.donor.equals(other.donor) &&
		this.amount == other.amount;
	}
    }

    
}
