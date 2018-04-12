package lt.raimond.laivu_musis.interfaces;

import lt.raimond.laivu_musis.Exceptions.InvalidCoordinateException;
import lt.raimond.laivu_musis.entities.Board;
import lt.raimond.laivu_musis.entities.GameData;
import lt.raimond.laivu_musis.entities.Ship;
import lt.raimond.laivu_musis.entities.User;

import java.util.List;

public interface BoardServiceInterface {

    Board createGameBoard(GameData gameData);

    void updateGameBoards(GameData gameData, User user, Board myBoard, Board enemyBoard);

    Ship parseShip(String shipAsString) throws InvalidCoordinateException;

    String convertShipsToString(List<Ship> ships);

    Board drawShipsOnBoard(List<Ship> ships, Board board);

    Board drawShipOnBoard(Ship ship, Board board);
}


