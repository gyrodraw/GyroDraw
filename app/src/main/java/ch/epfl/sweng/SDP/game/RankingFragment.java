package ch.epfl.sweng.SDP.game;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.utils.SortUtils;


/**
 * A custom {@link ListFragment} used for displaying the final ranking at the end of the game.
 */
public class RankingFragment extends ListFragment {

    private static final String TOP_ROOM_NODE_ID = "realRooms";

    private String roomID;

    private DatabaseReference rankingRef;
    private DatabaseReference finishedRef;

    private Map<String, Integer> finalRanking;
    private List<String> rankedUsernames;

    public RankingFragment() {
        // Empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Retrieve the ranking array, passed as argument on instantiation of the class
        roomID = getArguments().getString("roomID");
        return inflater.inflate(R.layout.ranking_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rankingRef = Database.getReference(TOP_ROOM_NODE_ID + "." + roomID + ".ranking");
        finishedRef = Database.getReference(TOP_ROOM_NODE_ID + "." + roomID + ".finished");
        retrieveFinalRanking();
    }

    public List<String> getRankedUsernames() {
        return rankedUsernames;
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

                rankedUsernames = SortUtils.sortByValue(finalRanking);
                ArrayAdapter<String> adapter = new RankingAdapter(getActivity(),
                        rankedUsernames.toArray(new String[rankedUsernames.size()]));
                setListAdapter(adapter);
                setFinishedCollectingRanking();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void setFinishedCollectingRanking() {
        finishedRef.child(Account.getInstance(getActivity()
                .getApplicationContext()).getUsername()).setValue(1);
    }

    private class RankingAdapter extends ArrayAdapter<String> {

        private final String[] players;

        private RankingAdapter(Context context, String[] players) {
            super(context, 0, players);
            this.players = players;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.ranking_item, parent, false);
            }

            TextView pos = convertView.findViewById(R.id.position);
            TextView name = convertView.findViewById(R.id.playerName);

            pos.setText(String.format(Locale.getDefault(), "%d. ", position + 1));
            name.setText(players[position]);

            // Return the completed view to render on screen
            return convertView;
        }
    }
}
