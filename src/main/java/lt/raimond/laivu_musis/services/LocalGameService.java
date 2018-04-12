package lt.raimond.laivu_musis.services;

import lt.raimond.laivu_musis.entities.*;
import lt.raimond.laivu_musis.Exceptions.InvalidCoordinateException;
import lt.raimond.laivu_musis.interfaces.BoardServiceInterface;

import java.util.*;

public class LocalGameService implements BoardServiceInterface {
    private static final String[] HEADER = {"K", "I", "L", "O", "M", "E", "T", "R", "A", "S"};

    public static final String EMPTY = ".";
    public static final String EMPTY_HIT = "*";
    public static final String IGNORE = "^";
    public static final String SHIP = "#";
    public static final String SHIP_HIT = "X";

    private static Date lastParsedDate = new Date();

    @Override
    public Board createGameBoard(GameData gameData) {
        return new Board(HEADER, EMPTY);
    }

    @Override
    public void updateGameBoards(GameData gameData, User user, Board myBoard, Board enemyBoard) {
        LinkedList<Event> eventsList = gameData.getEventsList();
        Iterator<Event> iterator = eventsList.descendingIterator();
        Event event = null;

        while (iterator.hasNext()) {
            event = iterator.next();
            if (event.getDate().after(lastParsedDate)) {
                if (user.getId().equals(event.getUserId())) {
                    parseEventToBoard(enemyBoard, event);
                } else parseEventToBoard(myBoard, event);
            } else break;
        }
        lastParsedDate = event.getDate();
    }

    private void parseEventToBoard(Board board, Event event) {
        Coordinate coordinate = event.getCoordinate();

        if (event.isHit()) {
            board.put(coordinate, SHIP_HIT);
            for (Coordinate diagonal : getNearDiagonalCoordinates(coordinate)) {
                if (board.get(diagonal).equals(EMPTY)) {
                    board.put(diagonal, IGNORE);
                }
            }
        } else board.put(coordinate, EMPTY_HIT);
    }

    @Override
    public Ship parseShip(String shipAsString) throws InvalidCoordinateException {
        String[] coordinates = shipAsString.toUpperCase().split("-");
        if (coordinates.length <= 2 && coordinates.length > 0) {
            Coordinate start = parseCoordinate(coordinates[0]);
            Coordinate end = start;
            if (coordinates.length == 2) end = parseCoordinate(coordinates[1]);
            return new Ship(start, end);
        } else throw new InvalidCoordinateException("incorrect number of coordinates");
    }

    @Override
    public String convertShipsToString(List<Ship> ships) {
        StringBuilder result = new StringBuilder();

        for (Ship ship : ships) {
            result.append(convertShipToString(ship)).append("!");
        }
        result.deleteCharAt(result.length() - 1);    // list.isEmpty() == false;
        return result.toString();
    }

    //TODO add tests and update algorithm
    public boolean canFitShip(Ship ship, List<Coordinate> reserved) {     //reserved list will be constructed from ships already "added"
        List<Coordinate> needed = new ArrayList<>();

        needed.addAll(getNeighbourCoordinates(ship.getFront()));
        needed.addAll(getNearDiagonalCoordinates(ship.getFront()));
        needed.addAll(getNeighbourCoordinates(ship.getEnd()));
        needed.addAll(getNearDiagonalCoordinates(ship.getEnd()));
        needed.add(ship.getFront());
        needed.add(ship.getEnd());

        for (Coordinate element : needed) {
            if (reserved.contains(element)) {
                return false;
            }
        }

        //reserved.addAll(ship.getCoordinates());
        return true;
    }

    private String convertShipToString(Ship ship) {
        StringBuilder result = new StringBuilder();

        String start = convertCoordinateToString(ship.getFront());
        String end = convertCoordinateToString(ship.getEnd());
        return result.append(start).append("-").append(end).toString();
    }

    @Override
    public Board drawShipsOnBoard(List<Ship> ships, Board board) {
        for (Ship ship : ships) board = drawShipOnBoard(ship, board);
        return board;
    }

    @Override
    public Board drawShipOnBoard(Ship ship, Board board) {
        board.put(ship.getFront(), SHIP);
        Coordinate position = ship.getFront();

        if (ship.isHorizontal()) {
            while (position.getCol() < ship.getEnd().getCol()) {
                position = position.move(Coordinate.RIGHT);
                board.put(position, SHIP);
            }
        } else while (position.getRow() < ship.getEnd().getRow()) {
            position = position.move(Coordinate.DOWN);
            board.put(position, SHIP);
        }
        return board;
    }

    public Coordinate parseCoordinate(String coordAsString) throws InvalidCoordinateException {
        coordAsString = coordAsString.toUpperCase();

        try {
            if (coordAsString.length() == 2) {
                String colString = coordAsString.substring(0, 1);
                String rowString = coordAsString.substring(1, 2);

                for (int i = 0; i < HEADER.length; i++) {
                    if (HEADER[i].equals(colString)) {
                        return new Coordinate(i, Integer.parseInt(rowString));
                    }
                }
            }
        } catch (NumberFormatException e) {
        }
        throw new InvalidCoordinateException("can't parse coordinate");
    }

    public String convertCoordinateToString(Coordinate coordinate) {
        return HEADER[coordinate.getCol()] + coordinate.getRow();
    }

    //TODO update algorithm?
    public List<Coordinate> getNearDiagonalCoordinates(Coordinate coordinate) {
        List<Coordinate> result = new ArrayList<>();
        result.add(coordinate.move(Coordinate.DOWN_LEFT));
        result.add(coordinate.move(Coordinate.DOWN_RIGHT));
        result.add(coordinate.move(Coordinate.UP_LEFT));
        result.add(coordinate.move(Coordinate.UP_RIGHT));
        result.removeAll(Collections.singleton(null));
        return result;
    }

    public List<Coordinate> getNeighbourCoordinates(Coordinate coordinate) {
        List<Coordinate> result = new ArrayList<>();
        result.add(coordinate.move(Coordinate.DOWN));
        result.add(coordinate.move(Coordinate.RIGHT));
        result.add(coordinate.move(Coordinate.LEFT));
        result.add(coordinate.move(Coordinate.UP));
        result.removeAll(Collections.singleton(null));
        return result;
    }

    public boolean checkHit(GameData gameData, Coordinate coordinate) {
        LinkedList<Event> eventsList = gameData.getEventsList();
        Iterator<Event> iterator = eventsList.descendingIterator();

        while (iterator.hasNext()) {
            Event event = iterator.next();
            if (coordinate.equals(event.getCoordinate())) {
                return event.isHit();
            }
        }
        return false;
    }
}
