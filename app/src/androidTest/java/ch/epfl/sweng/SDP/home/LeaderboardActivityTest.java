package ch.epfl.sweng.SDP.home;


@RunWith(AndroidJUnit4.class)
public class LeaderboardActivityTest {

    private static final String USER_ID = "123456789";
    private static final String TEST_EMAIL = "testEmail";
    private static final String USERNAME = "username";

    @Rule
    public final ActivityTestRule<LeaderboardActivity> activityRule =
            new ActivityTestRule<>(LeaderboardActivity.class);

    private Account account;

    @Before
    public void init() {
        account = Account.getInstance(activityRule.getActivity());
        account.setUserId(USER_ID);
        account.setUsername(USERNAME);
        account.setEmail(TEST_EMAIL);
    }

    @Test
    public void testSearchFieldClickable() {
        onView(withId(R.id.searchField))
                .perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.searchField)).check(matches(isClickable()));
    }

    @Test
    public void testClickOnExitButtonOpensHomeActivity() {
        testExitButtonBody();
    }

    @Test
    public void testFriendsButtonsClickable() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        SystemClock.sleep(1000);
        onView(withTagValue(is((Object) "friendsButton0"))).perform(click());
        SystemClock.sleep(1000);
        onView(withTagValue(is((Object) "friendsButton0"))).perform(click());
    }

    @Test
    public void testFilterButtonBehavesCorrectly() {
        onView(withId(R.id.friendsFilter)).perform(click());
        SystemClock.sleep(1000);
        onView(withId(R.id.searchField)).perform(typeText("P"));
        onView(withId(R.id.friendsFilter)).perform(click());
        SystemClock.sleep(1000);
        TextView filterText = activityRule.getActivity().findViewById(R.id.friendsFilterText);
        assertThat(filterText.getText().toString(),
                is(activityRule.getActivity().getResources().getString(R.string.friendsFilter)));
    }

    @Test
    public void testLeaderboardIsSearchable() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        onView(withId(R.id.searchField)).perform(typeText("PICASSO"));
        SystemClock.sleep(1000);
        assertThat(((LinearLayout) activityRule.getActivity().findViewById(R.id.leaderboard))
                .getChildCount(), greaterThanOrEqualTo(1));
    }

    @Test
    public void testFriendsAreSearchable() {
        friendsTest(FriendsRequestState.FRIENDS.ordinal(), 1);
    }

    private void friendsTest(int state, int expected) {
        Database.getReference("users."
                + USER_ID + ".friends.HFNDgmFKQPX92nmfmi2qAUfTzxJ3")
                .setValue(state);
        SystemClock.sleep(2000);
        onView(withId(R.id.friendsFilter)).perform(click());
        SystemClock.sleep(2000);
        onView(withId(R.id.searchField)).perform(typeText("PICASSO"));
        SystemClock.sleep(2000);
        assertThat(expected, is(((LinearLayout) activityRule.getActivity()
                .findViewById(R.id.leaderboard)).getChildCount()));
        account.removeFriend("HFNDgmFKQPX92nmfmi2qAUfTzxJ3");
    }

    /**
     * Body of a test that tests if an exit button opens the home page.
     */
    public static void testExitButtonBody() {
        Intents.init();
        onView(ViewMatchers.withId(R.id.exitButton)).perform(click());
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
    }
}
