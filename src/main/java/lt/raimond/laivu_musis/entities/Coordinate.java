package lt.raimond.laivu_musis.entities;

public class Coordinate {
    //Should I keep the constants?
    public static final Coordinate DOWN_RIGHT = new Coordinate(1, 1);
    public static final Coordinate DOWN_LEFT = new Coordinate(-1, 1);
    public static final Coordinate UP_RIGHT = new Coordinate(1, -1);
    public static final Coordinate UP_LEFT = new Coordinate(-1, -1);

    public static final Coordinate UP = new Coordinate(0, -1);
    public static final Coordinate LEFT = new Coordinate(-1, 0);
    public static final Coordinate RIGHT = new Coordinate(1, 0);
    public static final Coordinate DOWN = new Coordinate(0, 1);

    private final int col;
    private final int row;

    public Coordinate() {
        this.col = 0;
        this.row = 0;
    }

    public Coordinate(int col, int row) {
        this.col = col;
        this.row = row;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coordinate that = (Coordinate) obj;
        return col == that.col && row == that.row;
    }

    @Override   //this method was used only for debugging so probably need to delete
    public String toString() {
        return String.format("%d.%d", col, row);
    }

    public Coordinate move(Coordinate vector) {
        int col = this.col + vector.col;
        int row = this.row + vector.row;
        Coordinate temp = new Coordinate(col, row);

        if (temp.isInRange(0, 9)) {
            return temp;
        } else return null;
    }

    //not really needed in this form, might need something similar later to move on the board?
    public Coordinate turnLeft() {
        return new Coordinate(row * 1, col * (-1));
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    private boolean isInRange(int min, int max) {
        if (col >= min && col <= max && row >= min && row <= max) {
            return true;
        } else return false;
    }
}
