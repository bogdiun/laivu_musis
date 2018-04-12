package lt.raimond.laivu_musis.entities;

import java.util.LinkedList;

public class GameData {
    private String gameId;
    private String status;
    private String nextUserId;
    private String winnerUserId;
    private LinkedList<Event> events;

    public GameData(String gameId, String status, String nextUserId, String winnerUserId, LinkedList<Event> events) {
        this.gameId = gameId;
        this.nextUserId = nextUserId;
        this.winnerUserId = winnerUserId;
        this.status = status;
        this.events = events;
    }

    public String getGameId() {
        return gameId;
    }

    public String getStatus() {
        return status;
    }

    public String getNextUserId() {
        return nextUserId;
    }

    public String getWinnerUserId() {
        return winnerUserId;
    }

    public LinkedList<Event> getEventsList() {
        return events;
    }
}