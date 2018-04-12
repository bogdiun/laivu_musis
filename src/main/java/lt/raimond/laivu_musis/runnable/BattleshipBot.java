package lt.raimond.laivu_musis.runnable;

import lt.raimond.laivu_musis.entities.*;
import lt.raimond.laivu_musis.interfaces.BotServiceInterface;
import lt.raimond.laivu_musis.services.GameDataService;
import lt.raimond.laivu_musis.services.LocalGameService;
import lt.raimond.laivu_musis.services.SimpleBotService;
import lt.raimond.laivu_musis.services.UserService;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class BattleshipBot implements Runnable {

    private final UserService userServerService = new UserService();
    private final GameDataService gameDataService = new GameDataService();
    private final BotServiceInterface botService = new SimpleBotService();
    private final LocalGameService localGameService = new LocalGameService();
    private final CountDownLatch latch;

    private static String email = "SilentBot%d@gmail.com";
    private static String name = "SilentBot%d";
    private static int botNr;

    public BattleshipBot(CountDownLatch latch, int botNr) {
        this.latch = latch;
        this.botNr = botNr;
    }

    public void run() {
        try {
            User bot = new User("", String.format(name, botNr), String.format(email, botNr));
            bot = userServerService.requestCreateUser(bot);

            latch.countDown();
            Thread.sleep(100);
            GameData gameData = gameDataService.requestJoin(bot);
            System.out.printf("%s joined a game [id = %s].\n\n", bot.getName(), gameData.getGameId());

            while (!gameDataService.isReadyForShips(gameData) && gameDataService.isReadyForSecondPlayer(gameData)) {
                Thread.sleep(3000);
                gameData = gameDataService.requestStatus(gameData);
            }

            List<Ship> ships = botService.getRandomPresetShipList();
            gameData = gameDataService.requestSetup(gameData, bot, ships);

            while (!gameDataService.isReadyToPlay(gameData)) {
                Thread.sleep(3000);
                gameData = gameDataService.requestStatus(gameData);
            }

            while (!gameDataService.hasWinner(gameData)) {

                if (gameDataService.isReadyToPlay(gameData) && gameDataService.isMyTurn(gameData, bot)) {
                    Coordinate turnCoordinate = botService.getNextCoordinate();
                    gameData = gameDataService.requestTurn(gameData, bot, turnCoordinate);
                    botService.updateStrategy(gameData, turnCoordinate);
                } else {
                    Thread.sleep(500);
                    gameData = gameDataService.requestStatus(gameData);
                }
            }

        } catch (Exception e) {
            System.out.printf("Bot error: %s.", e.getMessage());
            //e.printStackTrace();
        }
    }
}
