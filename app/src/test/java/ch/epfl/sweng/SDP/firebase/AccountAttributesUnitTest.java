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
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.attributeToPath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AccountAttributesUnitTest {

    @Test
    public void testAttributeToPathReturnsCorrectPath() {
        assertThat(attributeToPath(USER_ID), is(equalTo("userId")));
        assertThat(attributeToPath(USERNAME), is(equalTo("username")));
        assertThat(attributeToPath(EMAIL), is(equalTo("email")));
        assertThat(attributeToPath(STARS), is(equalTo("stars")));
        assertThat(attributeToPath(TROPHIES), is(equalTo("trophies")));
        assertThat(attributeToPath(LEAGUE), is(equalTo("currentLeague")));
        assertThat(attributeToPath(MATCHES_WON), is(equalTo("matchesWon")));
        assertThat(attributeToPath(MATCHES_TOTAL), is(equalTo("totalMatches")));
        assertThat(attributeToPath(AVERAGE_RATING), is(equalTo("averageRating")));
        assertThat(attributeToPath(MAX_TROPHIES), is(equalTo("maxTrophies")));
        assertThat(attributeToPath(FRIENDS), is(equalTo("friends")));
        assertThat(attributeToPath(STATUS), is(equalTo("online")));
        assertThat(attributeToPath(BOUGHT_ITEMS), is(equalTo("boughtItems")));
    }
}