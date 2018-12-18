package ch.epfl.sweng.SDP.home.battleLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

/**
 * Class representing a game result.
 */
public final class GameResult {

    private final List<String> rankedUsername;
    private final int rank;
    private final int stars;
    private final int trophies;
    private final Bitmap drawing;

    /**
     * Creates a new game result.
     *
     * @param rankedUsername the list of usernames in ranking order
     * @param rank           the rank of the user
     * @param stars          the stars won during this game
     * @param trophies       the trophies won during this game
     * @param drawing        the drawing of the user
     */
    public GameResult(List<String> rankedUsername, int rank, int stars, int trophies,
                      Bitmap drawing) {
        checkPrecondition(0 <= rank && rank < rankedUsername.size(),
                "Rank is out of bounds");
        checkPrecondition(rankedUsername.size() <= 5,
                "The number of username is bigger than 5");
        this.rankedUsername = new ArrayList<>(rankedUsername);
        this.rank = rank;
        this.drawing = drawing;
        this.stars = stars;
        this.trophies = trophies;
    }

    public List<String> getRankedUsername() {
        return Collections.unmodifiableList(rankedUsername);
    }

    public int getRank() {
        return rank;
    }

    public int getStars() {
        return stars;
    }

    public int getTrophies() {
        return trophies;
    }

    public Bitmap getDrawing() {
        return drawing == null ? null : drawing.copy(Bitmap.Config.ARGB_8888, false);
    }

    /**
     * Converts this game result into a LinearLayout
     * that will be displayed in the log battle.
     *
     * @return LinearLayout that will be displayed
     */
    LinearLayout toLayout(Context context) {
        return new GameResultLayout(this, context).getLayout();
    }
}