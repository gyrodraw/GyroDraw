package ch.epfl.sweng.SDP.matchmaking;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

public class RoomTest {

    private Room room;

    @Before
    public void init() {
        room = new Room();
    }

    @Test
    public void getName() {
        assertNull(room.getName());
    }

    @Test
    public void setName() {
        room.setName("TEST");
        assertThat(room.getName(), is("TEST"));
    }

    @Test
    public void getId() {
        assertNull(room.getId());
    }

    @Test
    public void setId() {
        room.setId("TEST");
        assertThat(room.getId(), is("TEST"));
    }

    @Test
    public void getPlaying() {
        assertNull(room.getId());
    }

    @Test
    public void setPlaying() {
        room.setPlaying(true);
        assertThat(room.getPlaying(), is(true));
    }

    @Test
    public void getInRoom() {
        assertNull(room.getInRoom());
    }

    @Test
    public void setInRoom() {
        room.setInRoom(new ArrayList<String>());
        assertThat(room.getInRoom(), is(new ArrayList<String>()));
    }
}