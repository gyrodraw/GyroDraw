package ch.epfl.sweng.SDP.matchmaking;

import com.google.android.gms.tasks.Task;

public interface MatchmakingInterface {

    public void leaveRoom(String roomId);

    public Task<String> joinRoom();

}
