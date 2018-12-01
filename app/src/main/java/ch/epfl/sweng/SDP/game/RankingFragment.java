package ch.epfl.sweng.SDP.game;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.auth.ConstantsWrapper;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.GameResult;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForGameResults;
import ch.epfl.sweng.SDP.utils.SortUtils;

/**
 * A custom {@link ListFragment} used for displaying the final ranking at the end of the game.
 */
public class RankingFragment extends ListFragment {

    private static final String TOP_ROOM_NODE_ID = "realRooms";
    private static boolean isTesting = false;
    private static final int RANK = 10;
    private String roomId;

    private DatabaseReference rankingRef;
    private DatabaseReference finishedRef;

    private Map<String, Integer> finalRanking;

    private List<String> rankedUsernames;

    private Bitmap[] drawings;
    private String[] playerNames;

    private Account account;

    public RankingFragment() {
        // Empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ranking_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rankingRef = Database.getReference(TOP_ROOM_NODE_ID + "." + roomId + ".ranking");
        finishedRef = Database.getReference(TOP_ROOM_NODE_ID + "." + roomId + ".finished");
        Typeface typeMuro = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Muro.otf");
        Button button = getActivity().findViewById(R.id.homeButton);
        button.setTypeface(typeMuro);

        if(!isTesting) {
            account = Account.getInstance(getActivity().getApplicationContext());
        } else {
            createTestAccount();
        }

        retrieveFinalRanking();
    }

    /**
     * Sets the attributes of this class.
     * @param roomId the id of the room.
     * @param drawings the users drawings.
     * @param playernames the usernames of the players.
     */
    public void putExtra(String roomId, Bitmap[] drawings, String[] playernames) {
        this.roomId = roomId;
        this.drawings = drawings;
        this.playerNames = playernames;
    }

    /**
     * Retrieves the usernames.
     * @return a list of user names.
     */
    public List<String> getRankedUsernames() {
        return rankedUsernames;
    }

    private int getIndexForUserName(String username) {
        for (int i = 0; i < this.playerNames.length; i++) {
            if (username.equals(playerNames[i])) {
                return i;
            }
        }
        throw new IllegalArgumentException("Index not found");
    }

    private void retrieveFinalRanking() {

        rankingRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                finalRanking = new HashMap<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue(Integer.class) != null && ds.getKey() != null) {
                        finalRanking.put(ds.getKey(), ds.getValue(Integer.class));
                    }
                }

                String userName = account.getUsername();

                // Sort the rankings
                Integer[] tmp = new Integer[finalRanking.values().size()];
                Integer[] rankings = (finalRanking.values().toArray(tmp));
                Arrays.sort(rankings, Collections.reverseOrder());

                int rankForUser = 0;

                if(dataSnapshot.child(userName).getValue(int.class) != null) {
                    rankForUser = dataSnapshot.child(userName).getValue(int.class);
                }

                // Calculate trophies
                Integer[] trophies = new Integer[rankings.length];
                int lastRank = 0;
                int trophiesForUser = 0;
                int rank = RANK * 2;
                for (int i = 0; i < rankings.length; i++) {
                    if (rankForUser == rankings[i]) {
                        trophiesForUser = rank;
                    }
                    if (rankings[i] != lastRank) {
                        rank -= RANK;
                    }
                    trophies[i] = rank + RANK;
                    lastRank = rankings[i];
                }

                List<String> usernames = SortUtils.sortByValue(finalRanking);

                Boolean won = usernames.get(0).equals(userName);
                updateUserStats(rankForUser, trophiesForUser, won);
                createAndStoreGameResult(usernames, rankForUser, rankForUser, trophiesForUser);

                String[] tmpUserNames = usernames.toArray(new String[usernames.size()]);
                ArrayAdapter<String> adapter = new RankingAdapter(getActivity(),
                        tmpUserNames, rankings, trophies, drawings);
                setListAdapter(adapter);
                setFinishedCollectingRanking();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void updateUserStats(int starIncrease, int trophiesIncrease, boolean won) {
        account.changeStars(starIncrease);
        account.changeTrophies(trophiesIncrease);
        account.changeAverageRating(starIncrease);
        account.increaseTotalMatches();
        if (won) {
            account.increaseMatchesWon();
        }
    }

    /**
     * Create a game result and save the result into the local database.
     */
    public void createAndStoreGameResult(List<String> names, int rank, int stars, int trophies) {

        String userNameId = account.getUsername();

        Bitmap drawing = drawings[getIndexForUserName(userNameId)];

        if (drawing != null) {
            GameResult gameResult = new GameResult(names, rank, stars, trophies,
                    drawing, this.getActivity());
            LocalDbHandlerForGameResults localDb =
                    new LocalDbHandlerForGameResults(this.getActivity(), null, 1);
            localDb.addGameResultToDb(gameResult);
        }
    }

    private void setFinishedCollectingRanking() {
        finishedRef.child(account.getUsername()).setValue(1);
    }

    @VisibleForTesting
    public static void enableTesting() {
        isTesting = true;
    }

    public void createTestAccount() {
        Account.deleteAccount();
        Account.createAccount(getActivity().getApplicationContext(), new ConstantsWrapper(), "123456789"
                , "test@test.com");
        account = Account.getInstance(getActivity().getApplicationContext());
        account.setUsername("userA");
    }

    private class RankingAdapter extends ArrayAdapter<String> {

        private final String[] players;
        private final Integer[] rankings;
        private final Integer[] trophies;
        private final Bitmap[] drawings;
        
        private RankingAdapter(Context context, String[] players, Integer[] rankings,
                               Integer[] trophies, Bitmap[] drawings) {
            super(context, 0, players);
            this.players = players;
            this.rankings = rankings;
            this.trophies = trophies;
            this.drawings = drawings;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            // Check if an existing view is being reused, otherwise inflate the view
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.ranking_item, parent, false);

            AssetManager assets = getActivity().getAssets();
            Typeface typeMuro = Typeface.createFromAsset(assets, "fonts/Muro.otf");

            // Set the font
            TextView name = convertView.findViewById(R.id.playerName);
            name.setTypeface(typeMuro);
            TextView ranking = convertView.findViewById(R.id.starsWon);
            ranking.setTypeface(typeMuro);
            TextView trophiesText = convertView.findViewById(R.id.trophiesWon);
            trophiesText.setTypeface(typeMuro);

            // Update image
            ImageView imageview = convertView.findViewById(R.id.drawing);
            imageview.setImageBitmap(drawings[getIndexForUserName(players[position])]);

            int yellowColor = getResources().getColor(R.color.colorDrawYellow);
            int darkColor = getResources().getColor(R.color.colorPrimaryDark);

            // Set the color
            if (!players[position].equals(account.getUsername())) {
                name.setTextColor(yellowColor);
                ranking.setTextColor(yellowColor);
                trophiesText.setTextColor(yellowColor);
                convertView.setBackgroundColor(darkColor);
            }

            // set the texts
            name.setText(String.format("%d. %s", position + 1, players[position]));
            trophiesText.setText(String.valueOf(this.trophies[position]));
            ranking.setText(String.valueOf(this.rankings[position]));

            // Return the completed view to render on screen
            return convertView;
        }
    }
}
