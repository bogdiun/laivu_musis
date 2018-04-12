package lt.raimond.laivu_musis.entities;

public class Ship {
    private Coordinate front;
    private Coordinate end;

    public Ship(Coordinate single) {
        this.front = single;
        this.end = single;
    }

    public Ship(Coordinate start, Coordinate end) {
        //takes two coordinates, check which is the actual starting point
        if (start.getCol() > end.getCol() || start.getRow() > end.getRow()) {
            this.front = end;
            this.end = start;
        } else {
            this.front = start;
            this.end = end;
        }
    }

    public Coordinate getFront() {
        return front;
    }

    public Coordinate getEnd() {
        return end;
    }

    public boolean isHorizontal() {
        return front.getRow() == end.getRow();
    }

    public int getSize() {
        if (!isHorizontal()) {
            return end.getRow() - front.getRow() + 1;
        } else {
            return end.getCol() - front.getCol() + 1;
        }
    }
}
