package ch.epfl.sweng.SDP.matchmaking;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {

    private String name;
    private String id;
    private Boolean playing;
    private ArrayList<String> inRoom;

    public Room() {}

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

    public Boolean getPlaying() {
        return playing;
    }

    public void setPlaying(Boolean playing) {
        this.playing = playing;
    }

    public ArrayList<String> getInRoom() {
        return inRoom;
    }

    public void setInRoom(ArrayList<String> inRoom) {
        this.inRoom = inRoom;
    }

}