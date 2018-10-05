package ch.epfl.sweng.SDP;

public class Account implements java.io.Serializable {
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

    public void changeUsername(String newName) throws IllegalArgumentException {
        //check for availability
        //try to update database
        this.username = newName;
    }

    public void changeTrophies(int a) throws Throwable{
        int newRating = Math.max(0, rating + a);
        //try to update database
        this.rating = newRating;
    }

    public void addStars(int a) throws IllegalArgumentException {
        if (a < 0) {
            throw new IllegalArgumentException();
        }
        //try to update database
        this.currency += a;
    }

    public void subtractStars(int a) throws IllegalArgumentException {
        if (a < 0 || this.currency - a < 0) {
            throw new IllegalArgumentException();
        }
        this.currency -= a;
    }



}
