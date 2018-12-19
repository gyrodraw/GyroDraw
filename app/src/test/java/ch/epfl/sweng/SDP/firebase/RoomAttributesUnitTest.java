package ch.epfl.sweng.SDP.firebase;

import org.junit.Test;

import static ch.epfl.sweng.SDP.firebase.RoomAttributes.FINISHED;
import static ch.epfl.sweng.SDP.firebase.RoomAttributes.GAME_MODE;
import static ch.epfl.sweng.SDP.firebase.RoomAttributes.ONLINE_STATUS;
import static ch.epfl.sweng.SDP.firebase.RoomAttributes.PLAYING;
import static ch.epfl.sweng.SDP.firebase.RoomAttributes.RANKING;
import static ch.epfl.sweng.SDP.firebase.RoomAttributes.STATE;
import static ch.epfl.sweng.SDP.firebase.RoomAttributes.TIMER;
import static ch.epfl.sweng.SDP.firebase.RoomAttributes.UPLOAD_DRAWING;
import static ch.epfl.sweng.SDP.firebase.RoomAttributes.USERS;
import static ch.epfl.sweng.SDP.firebase.RoomAttributes.WORDS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RoomAttributesUnitTest {

    @Test
    public void testAttributeToPathReturnsCorrectPath() {
        assertThat(FINISHED, is(equalTo("finished")));
        assertThat(GAME_MODE, is(equalTo("gameMode")));
        assertThat(ONLINE_STATUS, is(equalTo("onlineStatus")));
        assertThat(PLAYING, is(equalTo("playing")));
        assertThat(RANKING, is(equalTo("ranking")));
        assertThat(STATE, is(equalTo("state")));
        assertThat(TIMER, is(equalTo("timer.observableTime")));
        assertThat(UPLOAD_DRAWING, is(equalTo("uploadDrawing")));
        assertThat(USERS, is(equalTo("users")));
        assertThat(WORDS, is(equalTo("words")));
    }

}