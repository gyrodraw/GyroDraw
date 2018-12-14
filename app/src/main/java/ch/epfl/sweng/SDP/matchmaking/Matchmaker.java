package ch.epfl.sweng.SDP.matchmaking;

import static java.lang.String.format;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.FbDatabase;
import ch.epfl.sweng.SDP.firebase.FbFunctions;
import com.google.android.gms.tasks.Task;

/**
 * Singleton class that represents the matchmaker.
 */
public final class Matchmaker implements MatchmakingInterface {

    private static Matchmaker instance = null;

    private final Account account;

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

    private Matchmaker(Account account) {
        if (instance != null) {
            throw new IllegalStateException("Already instantiated");
        }

        this.account = account;
    }

    @Override
    public Task<String> joinRoom(int gameMode) {
        return FbFunctions.joinRoom(account, gameMode);
    }

    @Override
    public void leaveRoom(String roomId) {
        FbDatabase.getReference(format("realRooms.%s.users.%s", roomId, account.getUserId()))
                .removeValue();

        if (!account.getUsername().isEmpty()) {
            FbDatabase
                    .getReference(format("realRooms.%s.ranking.%s", roomId, account.getUsername()))
                    .removeValue();
            FbDatabase
                    .getReference(format("realRooms.%s.finished.%s", roomId, account.getUsername()))
                    .removeValue();
            FbDatabase.getReference(
                    format("realRooms.%s.uploadDrawing.%s", roomId, account.getUsername()))
                    .removeValue();
        }
    }
}
