package ch.epfl.sweng.SDP;

public class Account {
    public String username;
    public int rating;
    public int currency;

    public Account() {

    }

    public Account(String username) {
        this.username = username;
        this.rating = 1200;
        this.currency = 0;
    }
}
