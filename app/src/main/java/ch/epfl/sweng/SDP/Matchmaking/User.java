package ch.epfl.sweng.SDP.Matchmaking;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String name;
    private String id;
    private int stars;
    private int trophies;
    private String userName;
    private ArrayList<String> friends;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTrophies() {
        return trophies;
    }

    public void setTrophies(int trophies) {
        this.trophies = trophies;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getStars() {
        return stars;
    }

    public void setName(int stars) {
        this.stars = stars;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }


}