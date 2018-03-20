package lt.raimond.laivu_musis.services;

import lt.raimond.laivu_musis.entities.*;
import lt.raimond.laivu_musis.Exceptions.InvalidCoordinateException;
import lt.raimond.laivu_musis.Exceptions.InvalidShipPlaceException;
import lt.raimond.laivu_musis.interfaces.BoardServiceInterface;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class LocalGameService implements BoardServiceInterface {
    public static final String EMPTY = " ";
    public static final String EMPTY_HIT = "*";
    public static final String SHIP = "#";
    public static final String SHIP_HIT = "X";

    private String[] boardHeader;   // store gameInfo?
    private Date lastParsedDate;    // think of a different way?

    @Override
    public Board createGameBoard(GameData gameData) {
        boardHeader = gameData.getBoardHeader();
        return new Board(boardHeader, EMPTY);
    }

    @Override
    public void updateGameBoards(GameData gameData, User user, Board myBoard, Board enemyBoard) {
        //TODO code update
        LinkedList<Event> eventsList = gameData.getEventsList();
        Iterator<Event> iterator = eventsList.descendingIterator();

        while (iterator.hasNext()) {
            Event event = iterator.next();
            if (lastParsedDate == null) {
                if (user.getId().equals(event.getUserId())) {
                    parseEventToBoard(enemyBoard, event);
                } else {
                    parseEventToBoard(myBoard, event);
                }
            } else if (event.getDate().after(lastParsedDate)) {
                if (user.getId().equals(event.getUserId())) {
                    parseEventToBoard(enemyBoard, event);
                } else {
                    parseEventToBoard(myBoard, event);
                }
            } else {
                lastParsedDate = event.getDate();
                break;
            }
        }

        //TODO test?
    }

    private void parseEventToBoard(Board board, Event event) {
        if (event.isHit()) {
            board.setValue(event.getCoordinate(), SHIP_HIT);
        } else {
            board.setValue(event.getCoordinate(), EMPTY_HIT);
        }
    }

    // -------  Ship Model factory,service, parser -------
    @Override
    public Ship createShip(String shipAsString) throws InvalidCoordinateException {
        String[] coordinates = shipAsString.toUpperCase().split("-");
        Coordinate start = parseCoordinate(coordinates[0]);
        Coordinate end;

        switch (coordinates.length) {
            case 1:
                end = start;
                break;
            case 2:
                end = parseCoordinate(coordinates[1]);
                break;
            default:
                throw new InvalidCoordinateException("Invalid number of coordinates");
        }

        return new Ship(start, end);

        //TODO TEST this
    }

    @Override
    public String convertShipsToString(List<Ship> ships) {
        StringBuilder result = new StringBuilder();

        for (Ship ship : ships) {
            result.append(convertShipToString(ship)).append("!");
        }
        result.deleteCharAt(result.length() - 1); //I'll assume list is not empty
        return result.toString();
    }

    public String convertShipToString(Ship ship) {
        StringBuilder result = new StringBuilder();

        String start = convertCoordinateToString(ship.getStartCoordinate());
        String end = convertCoordinateToString(ship.getEndCoordinate());

        return result.append(start).append("-").append(end).toString();
    }

    @Override
    public void placeShipsOnBoard(List<Ship> ships, Board board) throws InvalidShipPlaceException {
        for (Ship ship : ships) placeShipOnBoard(ship, board);
    }

    //TODO check for valid ships having in mind methods for botService to make use of //to estimate possible ship locations, ignore nearby fields after ship hit, etc.

    @Override
    public void placeShipOnBoard(Ship ship, Board board) throws InvalidShipPlaceException {
        Coordinate place = ship.getStartCoordinate().copy();

        //I will have a ship.getSize();
        //I'll assume here I do not know where are other ships
        //get those locations, and feed them through    board.hasShip()? board.placeShip(coordinate, coordinate)? should I do additional methods

        if (isShipHorizontal(ship)) {
            while (place.getCol() <= ship.getEndCoordinate().getCol()) {
                board.setValue(place, SHIP);     //make coordinate immutable, and then return new Coord?
                place.moveHorizontal(1);
            }
        } else {
            while (place.getRow() <= ship.getEndCoordinate().getRow()) {
                board.setValue(place, SHIP);
                place.moveVertical(1);
            }
        }

        if (false) {
            throw new InvalidShipPlaceException("Invalid Ship");
        }

        //TODO TEST this and think of how to make this good
    }


    private boolean isShipHorizontal(Ship ship) {
        return ship.getStartCoordinate().getRow() == ship.getEndCoordinate().getRow();
    }

    // ------- Coordinate Parser -------
    public Coordinate parseCoordinate(String coordAsString) throws InvalidCoordinateException {
        coordAsString = coordAsString.toUpperCase();
        String colString = coordAsString.substring(0, 1);
        String rowString = coordAsString.substring(1, 2);

        for (int i = 0; i < boardHeader.length; i++) {
            if (boardHeader[i].equals(colString)) {
                return new Coordinate(i, Integer.parseInt(rowString));  //parseInt can call exception, also catch it
            }
        }
        //TODO extra checks || TEST this
        throw new InvalidCoordinateException("Invalid coordinate");
    }

    public String convertCoordinateToString(Coordinate coordinate) {
        return boardHeader[coordinate.getCol()] + coordinate.getRow();
    }
}
