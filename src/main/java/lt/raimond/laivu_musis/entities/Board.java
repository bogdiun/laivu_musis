package lt.raimond.laivu_musis.entities;

//could make iterable, or use a map of coordinates? instead?
public class Board {
    private final String[] header;
    private String[][] field;

    public Board(String[] header, String value) {
        this.header = header;

        int size = header.length;
        field = new String[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                field[i][j] = value;
            }
        }
    }

    public void put(Coordinate coordinate, String value) {
        field[coordinate.getRow()][coordinate.getCol()] = value;
    }

    public String get(Coordinate coordinate) {
        return field[coordinate.getRow()][coordinate.getCol()];
    }

    public int size() {
        return field.length;
    }

    public String[] getRow(int row) {
        return field[row];
    }

    public String[] getHeader() {
        return header;
    }

}