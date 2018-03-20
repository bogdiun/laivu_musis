package lt.raimond.laivu_musis.entities;

import java.util.Date;

public class Event {
    Date date; //long?
    Coordinate coordinate;
    String userId;
    boolean hit;

    public Event(Date date, Coordinate coordinate, String userId, boolean hit) {
        this.date = date;
        this.coordinate = coordinate;
        this.userId = userId;
        this.hit = hit;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isHit() {
        return hit;
    }

    public Date getDate() {
        return date;
    }
}
