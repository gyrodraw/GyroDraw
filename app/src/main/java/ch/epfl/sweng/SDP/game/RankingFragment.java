package ch.epfl.sweng.SDP.game;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;


/**
 * A custom {@link ListFragment} used for displaying the final ranking at the end of the game.
 */
public class RankingFragment extends ListFragment {

    private static final String RANKING_KEY = "Ranking";
    private static final String TOP_ROOM_NODE_ID = "realRooms";

    private String roomID;

    private DatabaseReference rankingRef;
    private DatabaseReference finishedRef;

    private Map<String, Integer> finalRanking;

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
        rankingRef = Database.INSTANCE.getReference(TOP_ROOM_NODE_ID + "." + roomID + ".ranking");
        finishedRef = Database.INSTANCE.getReference(TOP_ROOM_NODE_ID + "." + roomID + ".finished");
        retrieveFinalRanking();
    }

    private void retrieveFinalRanking() {
        rankingRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                finalRanking = new HashMap<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    finalRanking.put(ds.getKey(), ds.getValue(Integer.class));
                }

                List<String> rankingUsernames = sortByValue(finalRanking);
                ArrayAdapter<String> adapter = new RankingAdapter(getActivity(),
                        rankingUsernames.toArray(new String[rankingUsernames.size()]));
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
        finishedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                if(value != null) {
                    finishedRef.setValue(++value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * @see "https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values"
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> List<K> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Object>() {
            @SuppressWarnings("unchecked")
            public int compare(Object o1, Object o2) {
                return -((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue()).compareTo(((Map.Entry<K, V>) (o2)).getValue());
            }
        });

        List<K> result = new ArrayList<>();
        for (Iterator<Map.Entry<K, V>> it = list.iterator(); it.hasNext();) {
            Map.Entry<K, V> entry = it.next();
            result.add(entry.getKey());
        }

        return result;
    }

    private class RankingAdapter extends ArrayAdapter<String> {

        String[] players;

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
