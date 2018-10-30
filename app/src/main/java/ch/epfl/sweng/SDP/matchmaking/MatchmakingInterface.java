package ch.epfl.sweng.SDP.matchmaking;

public interface MatchmakingInterface {
    Boolean joinRoom();

    Boolean leaveRoom(String roomId);
}

