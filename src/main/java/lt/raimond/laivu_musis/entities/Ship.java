package lt.raimond.laivu_musis.entities;

public class Ship {
    private Coordinate start;
    private Coordinate end;

    public Ship(Coordinate start, Coordinate end) {
        this.start = start;
        this.end = end;
    }

    public Coordinate getStartCoordinate() {
        return start;
    }

    public Coordinate getEndCoordinate() {
        return end;
    }
}
