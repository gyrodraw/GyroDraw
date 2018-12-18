package ch.epfl.sweng.SDP.firebase;

import org.junit.Test;

import static ch.epfl.sweng.SDP.firebase.AccountAttributes.AVERAGE_RATING;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.BOUGHT_ITEMS;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.EMAIL;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.FRIENDS;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.LEAGUE;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.MATCHES_TOTAL;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.MATCHES_WON;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.MAX_TROPHIES;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.STARS;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.STATUS;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.TROPHIES;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.USERNAME;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.USER_ID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AccountAttributesUnitTest {

    @Test
    public void testAttributeToPathReturnsCorrectPath() {
        assertThat(USER_ID, is(equalTo("userId")));
        assertThat(USERNAME, is(equalTo("username")));
        assertThat(EMAIL, is(equalTo("email")));
        assertThat(STARS, is(equalTo("stars")));
        assertThat(TROPHIES, is(equalTo("trophies")));
        assertThat(LEAGUE, is(equalTo("currentLeague")));
        assertThat(MATCHES_WON, is(equalTo("matchesWon")));
        assertThat(MATCHES_TOTAL, is(equalTo("totalMatches")));
        assertThat(AVERAGE_RATING, is(equalTo("averageRating")));
        assertThat(MAX_TROPHIES, is(equalTo("maxTrophies")));
        assertThat(FRIENDS, is(equalTo("friends")));
        assertThat(STATUS, is(equalTo("online")));
        assertThat(BOUGHT_ITEMS, is(equalTo("boughtItems")));
    }
}