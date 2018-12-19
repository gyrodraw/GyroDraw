package ch.epfl.sweng.SDP.matchmaking;

import com.google.android.gms.tasks.Task;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.FbDatabase;
import ch.epfl.sweng.SDP.firebase.FbFunctions;

/**
 * Singleton class that represents the matchmaker.
 */
public final class Matchmaker implements MatchmakingInterface {

    private static Matchmaker instance = null;

    private final Account account;

    private Matchmaker(Account account) {
        if (instance != null) {
            throw new IllegalStateException("Already instantiated");
        }

        this.account = account;
    }

    /**
     * Gets (eventually creates) the instance.
     *
     * @return the unique instance.
     */
    public static Matchmaker getInstance(Account account) {
        if (instance == null) {
            instance = new Matchmaker(account);
        }

        return instance;
    }

    @Override
    public Task<String> joinRoom(int gameMode) {
        return FbFunctions.joinRoom(account, gameMode);
    }

    @Override
    public void leaveRoom(String roomId) {
        if (!account.getUsername().isEmpty()) {
            FbDatabase.removeUserFromRoom(roomId, account);
        }
    }
}
