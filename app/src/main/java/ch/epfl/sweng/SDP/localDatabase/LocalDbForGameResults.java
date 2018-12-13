package ch.epfl.sweng.SDP.localDatabase;

import android.content.Context;
import ch.epfl.sweng.SDP.home.battlelog.GameResult;
import java.util.List;

/**
 * Interface representing a generic handler for the local database, responsible of operations
 * involving {@link GameResult}.
 */
public interface LocalDbForGameResults {

    /**
     * Adds a game result to the local db.
     *
     * @param gameResult to insert
     */
    void addGameResultToDb(GameResult gameResult);

    /**
     * Retrieves the 10th most recent game results from the table.
     *
     * @return the newest game result
     */
    List<GameResult> getGameResultsFromDb(Context context);
}
