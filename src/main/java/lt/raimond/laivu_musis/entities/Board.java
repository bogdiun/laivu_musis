package lt.raimond.laivu_musis.entities;

import lt.raimond.laivu_musis.interfaces.BoardInterface;

public class Board implements BoardInterface {
    private final String[] header;     //Header dalykas susitvarkyti, nes dabar cia, localgame ir gamedata
    private String[][] field;


    public Board(String[] header, String value) {
        int size = header.length;

        this.header = header;
        field = new String[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                field[i][j] = value;
            }
        }
    }

    public void setValue(Coordinate coord, String value) {
        field[coord.getRow()][coord.getCol()] = value;
    }

    public String getValue(Coordinate coord) {
        return field[coord.getRow()][coord.getCol()];
    }

    public int size(){
        return field.length;
    }

    public String[] getRow(int index) {
        return field[index];
    }

    public String[] getHeader() {
        return header;
    }

}