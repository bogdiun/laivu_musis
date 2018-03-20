package lt.raimond.laivu_musis.entities;

public class Coordinate {
    int col;
    int row;

    public Coordinate(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setRow(int row) {
        this.row = row;
    }


    public Coordinate copy() {
        return new Coordinate(col, row);
    }

    public void moveHorizontal(int p) {
        this.col += p;
    }

    public void moveVertical(int p) {
        this.row += p;
    }

    public void moveDiagonalUp(int p) {
        this.row -= p;
        this.col += p;
    }

    public void moveDiagonalDown(int p) {
        this.row += p;
        this.col += p;
    }
}
