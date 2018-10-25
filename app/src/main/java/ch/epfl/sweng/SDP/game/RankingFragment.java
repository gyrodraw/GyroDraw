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

import java.util.Locale;

import ch.epfl.sweng.SDP.R;

/**
 * A custom {@link ListFragment} used for displaying the final ranking at the end of the game.
 */
public class RankingFragment extends ListFragment {

    private static final String RANKING_KEY = "Ranking";

    private String[] ranking;

    public RankingFragment() {
        // Empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Retrieve the ranking array, passed as argument on instantiation of the class
        ranking = getArguments().getStringArray(RANKING_KEY);
        return inflater.inflate(R.layout.ranking_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<String> adapter = new RankingAdapter(getActivity(), ranking);
        setListAdapter(adapter);
    }


    private class RankingAdapter extends ArrayAdapter<String> {

        private RankingAdapter(Context context, String[] players) {
            super(context, 0, players);
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
            name.setText(ranking[position]);

            // Return the completed view to render on screen
            return convertView;
        }
    }
}
