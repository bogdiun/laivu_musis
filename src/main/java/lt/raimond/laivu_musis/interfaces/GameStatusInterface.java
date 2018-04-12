package lt.raimond.laivu_musis.interfaces;

import lt.raimond.laivu_musis.entities.GameData;
import lt.raimond.laivu_musis.entities.User;

public interface GameStatusInterface {
    boolean isReadyToPlay(GameData gameData);

    boolean isReadyForSecondPlayer(GameData gameData);

    boolean isReadyForShips(GameData gameData);

    boolean isMyTurn(GameData gameData, User user);

    boolean hasWinner(GameData gameData);
}
