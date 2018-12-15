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
import static ch.epfl.sweng.SDP.firebase.RoomAttributes.attributeToPath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RoomAttributesUnitTest {

    @Test
    public void testAttributeToPathReturnsCorrectPath() {
        assertThat(attributeToPath(FINISHED), is(equalTo("finished")));
        assertThat(attributeToPath(GAME_MODE), is(equalTo("gameMode")));
        assertThat(attributeToPath(ONLINE_STATUS), is(equalTo("onlineStatus")));
        assertThat(attributeToPath(PLAYING), is(equalTo("playing")));
        assertThat(attributeToPath(RANKING), is(equalTo("ranking")));
        assertThat(attributeToPath(STATE), is(equalTo("state")));
        assertThat(attributeToPath(TIMER), is(equalTo("timer.observableTime")));
        assertThat(attributeToPath(UPLOAD_DRAWING), is(equalTo("uploadDrawing")));
        assertThat(attributeToPath(USERS), is(equalTo("users")));
        assertThat(attributeToPath(WORDS), is(equalTo("words")));
    }

}