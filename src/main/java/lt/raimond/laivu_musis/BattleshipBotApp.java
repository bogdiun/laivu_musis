package lt.raimond.laivu_musis;

import lt.raimond.laivu_musis.entities.*;
import lt.raimond.laivu_musis.interfaces.BotServiceInterface;
import lt.raimond.laivu_musis.runnable.BattleshipBot;
import lt.raimond.laivu_musis.services.GameDataService;
import lt.raimond.laivu_musis.services.LocalGameService;
import lt.raimond.laivu_musis.services.SimpleBotService;
import lt.raimond.laivu_musis.services.UserService;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class BattleshipBotApp {

    private static Scanner scanner = new Scanner(System.in);
    private static UserService userServerService = new UserService();
    private static GameDataService gameDataService = new GameDataService();
    private static LocalGameService localGameService = new LocalGameService();
    private static BotServiceInterface botService = new SimpleBotService();

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        Thread secondaryBot = new Thread(new BattleshipBot(latch, 1));
        boolean botRun = false;

        System.out.println("Running: Battleship MainBot");
        boolean appRun = true;
        int timeOut;

        while (appRun) {
            System.out.println("1. New Game");
            System.out.println("2. New Game with bot");
            System.out.println("0. Exit Game");
            switch (scanner.nextInt()) {
                case 2:
                    botRun = true;
                    //intentional fall-through
                case 1:
                    try {
                        if (botRun) secondaryBot.start();
                        User bot = new User("", "MainBot", "bot@mail.com");
                        bot = userServerService.requestCreateUser(bot);

                        if (botRun) latch.await();
                        GameData gameData = gameDataService.requestJoin(bot);

                        System.out.printf("%s joined a game [id = %s].\n\n", bot.getName(), gameData.getGameId());
                        Board botBoard = localGameService.createGameBoard(gameData);
                        Board enemyBoard = localGameService.createGameBoard(gameData);

                        System.out.println("Waiting for the second player ...");
                        timeOut = 0;    //temp, must be a better way to do this
                        while (!gameDataService.isReadyForShips(gameData) && gameDataService.isReadyForSecondPlayer(gameData)) {
                            Thread.sleep(1000);
                            gameData = gameDataService.requestStatus(gameData);

                            if (timeOut == 10) {
                                System.out.printf("TIME_OUT(%d000ms): no response from second player.\nGame exit.", timeOut);
                                return;
                            } else timeOut++;
                        }

                        System.out.println("Setting up random ships.");
                        List<Ship> ships = botService.getRandomPresetShipList();
                        localGameService.drawShipsOnBoard(ships, botBoard);
                        gameData = gameDataService.requestSetup(gameData, bot, ships);

                        timeOut = 0;
                        while (!gameDataService.isReadyToPlay(gameData)) {
                            Thread.sleep(1000);
                            gameData = gameDataService.requestStatus(gameData);
                            if (timeOut == 15) {
                                System.out.printf("TIME_OUT(%d000ms): Player is taking too long to set up ships.\nGame exit.", timeOut);
                                return;
                            } else timeOut++;
                        }

                        int turnCount = 0;
                        while (!gameDataService.hasWinner(gameData)) {
                            if (gameDataService.isMyTurn(gameData, bot)) {
                                Coordinate turnCoordinate = botService.getNextCoordinate();
                                gameData = gameDataService.requestTurn(gameData, bot, turnCoordinate);
                                localGameService.updateGameBoards(gameData, bot, botBoard, enemyBoard);

                                printGameBoard("Update: mainbot board.", botBoard);
                                printGameBoard("Update: enemy board.", enemyBoard);

                                botService.updateStrategy(gameData, turnCoordinate);
                                turnCount++;
                                timeOut = 0;
                            } else {
                                System.out.println("Waiting for enemy to make a turn..");
                                Thread.sleep(1000);
                                gameData = gameDataService.requestStatus(gameData);
                                if (++timeOut == 20) {
                                    System.out.printf("TIME_OUT(%d000ms): Second player not responding.\nGame exit.", timeOut);
                                    return;
                                }
                            }
                        }

                        //Game finish code
                        if (gameData.getWinnerUserId().equals(bot.getId())) {
                            printGameBoard(String.format("Game finished in %d turns: %s, is so so smart!\n", turnCount, bot.getName()), enemyBoard);
                        } else {
                            printGameBoard(String.format("Game finished in %d turns: %s lost.\n", turnCount, bot.getName()), botBoard);
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                        System.out.printf("Error: %s.", e.getMessage());
                        appRun = false;
                        break;
                    }
                case 0:
                    System.out.println("Exiting game..");
                    appRun = false;
                    break;
            }
        }
        scanner.close();
    }

    private static void printGameBoard(String title, Board board) {
        System.out.printf("%s\n\n", title);
        String template = "%2s";

        printBoardRow(board.getHeader(), " ", template);

        for (int i = 0; i < board.size(); i++) {
            printBoardRow(board.getRow(i), "" + i, template);
        }
        System.out.println();
    }

    private static void printBoardRow(String[] row, String indexAsString, String temp) {
        StringBuilder template = new StringBuilder(temp);
        List<String> values = new LinkedList<>();

        values.add(indexAsString);
        values.addAll(Arrays.asList(row));
        for (int i = 0; i < row.length; i++) template.append(temp);

        System.out.println(String.format(template.toString(), values.toArray()));
    }
}
