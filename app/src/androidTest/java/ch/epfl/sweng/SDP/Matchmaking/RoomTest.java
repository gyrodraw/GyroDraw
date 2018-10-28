package ch.epfl.sweng.SDP.Matchmaking;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class RoomTest {

    Room room;

    @Before
    public void init() {
        room = new Room();
    }

    @Test
    public void getName() {
        assertEquals(room.getName(), null);
    }

    @Test
    public void setName() {
        room.setName("TEST");
        assertEquals(room.getName(), "TEST");
    }

    @Test
    public void getId() {
        assertEquals(room.getId(), null);
    }

    @Test
    public void setId() {
        room.setId("TEST");
        assertEquals(room.getId(), "TEST");
    }

    @Test
    public void getPlaying() {
        assertEquals(room.getId(), null);
    }

    @Test
    public void setPlaying() {
        room.setPlaying(true);
        assertEquals(room.getPlaying(), true);
    }

    @Test
    public void getInRoom() {
        assertEquals(room.getInRoom(), null);
    }

    @Test
    public void setInRoom() {
        room.setInRoom(new ArrayList<String>());
        assertEquals(room.getInRoom(), new ArrayList<String>());
    }
}