package ch.epfl.sweng.GyroDraw.localDatabase;

import android.content.Context;

import java.util.List;

import ch.epfl.sweng.GyroDraw.home.battleLog.GameResult;

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
     * @param context the context invoking this method
     * @return the newest game results
     */
    List<GameResult> getGameResultsFromDb(Context context);
}
