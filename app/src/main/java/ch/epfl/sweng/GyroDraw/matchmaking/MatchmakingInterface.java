package ch.epfl.sweng.GyroDraw.matchmaking;

import com.google.android.gms.tasks.Task;

/**
 * Interface offering matchmaking-related methods.
 */
public interface MatchmakingInterface {

    /**
     * Leaves a room.
     *
     * @param roomId the id of the room.
     */
    void leaveRoom(String roomId);

    /**
     * Joins a room by calling a FirebaseFunction that will handle which particular room a player
     * should join.
     *
     * @param gameMode the game mode for which the player is looking a room
     * @return a {@link Task} wrapping the result
     */
    Task<String> joinRoom(int gameMode);
}

