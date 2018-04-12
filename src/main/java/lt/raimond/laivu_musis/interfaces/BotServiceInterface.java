package lt.raimond.laivu_musis.interfaces;

import lt.raimond.laivu_musis.entities.*;

import java.util.List;

//in case I have more bots which have differing implementations.
public interface BotServiceInterface {

    List<Ship> getRandomPresetShipList();

    Coordinate getNextCoordinate();

    void updateStrategy(GameData gameData, Coordinate turnCoordinate);
}
