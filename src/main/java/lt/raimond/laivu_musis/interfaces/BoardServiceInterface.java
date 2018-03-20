package lt.raimond.laivu_musis.interfaces;

import lt.raimond.laivu_musis.Exceptions.InvalidCoordinateException;
import lt.raimond.laivu_musis.Exceptions.InvalidShipPlaceException;
import lt.raimond.laivu_musis.entities.Board;
import lt.raimond.laivu_musis.entities.GameData;
import lt.raimond.laivu_musis.entities.Ship;
import lt.raimond.laivu_musis.entities.User;

import java.util.List;

public interface BoardServiceInterface {

    Board createGameBoard(GameData gameData);

    void updateGameBoards(GameData gameData, User user, Board myBoard, Board enemyBoard);

    Ship createShip(String shipAsString) throws InvalidCoordinateException; //

    String convertShipsToString(List<Ship> ships);

    void placeShipsOnBoard(List<Ship> ships, Board board) throws InvalidShipPlaceException;

    void placeShipOnBoard(Ship ship, Board board) throws InvalidShipPlaceException;
}


