package lt.raimond.laivu_musis;

import lt.raimond.laivu_musis.Exceptions.InvalidCoordinateException;
import lt.raimond.laivu_musis.Exceptions.InvalidShipPlaceException;
import lt.raimond.laivu_musis.entities.*;
import lt.raimond.laivu_musis.services.GameDataService;
import lt.raimond.laivu_musis.services.LocalGameService;
import lt.raimond.laivu_musis.services.UserService;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class ConsoleUI {
    private static Scanner scanner = new Scanner(System.in);
    private static UserService userServerService = new UserService();
    private static GameDataService gameDataService = new GameDataService();
    private static LocalGameService localGameService = new LocalGameService();

    private static List<User> userRepository = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Welcome to BattleShip Game!");
        boolean appRun = true;
        int menuOption;

        while (appRun) {
            System.out.println("1. New Game");  //New User, Continue, New Bot
            System.out.println("0. Exit Game");
            menuOption = scanner.nextInt();

            switch (menuOption) {
                case 0:
                    appRun = false;
                    System.out.println("Exiting game..");
                    break;
                case 1:
                    try {
                        newGameSession();

                        //TODO HANDLE EXCEPTIONS, THREADING

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
            }
        }
        scanner.close();
    }

    private static void newGameSession() throws IOException, ParseException, InterruptedException {
        User user = readUserFromConsole();
        userServerService.requestCreateUser(user);

        GameData gameData = requestGameData(user);
        //TODO nusikelti gameRun cikla jau nuo cia,

        //game setup - check if READY_FOR_SHIPS

        Board myBoard = localGameService.createGameBoard(gameData);
        printGameBoard("This is your game board:", myBoard);

        Board enemyBoard = localGameService.createGameBoard(gameData);

        List<Ship> myShips = shipSetupHelper(myBoard);
        gameData = gameDataService.requestSetup(gameData, user, myShips);

        while (!gameDataService.hasWinner(gameData)) {
            if (gameDataService.isReadyToPlay(gameData)) {
                if (gameDataService.isMyTurn(gameData, user)) {

                    printGameBoard("My board", myBoard);

                    Coordinate turnCoordinate = readCoordinateFromConsole();
                    gameData = gameDataService.requestTurn(gameData, user, turnCoordinate);
                    localGameService.updateGameBoards(gameData, user, myBoard, enemyBoard);  // return Boolean on Hit?

                    printGameBoard("Enemy Board", enemyBoard);
                } else {
                    System.out.println("Waiting for enemy to make his turn...");
                    Thread.sleep(300);

                    gameData = gameDataService.requestStatus(gameData);
                    localGameService.updateGameBoards(gameData, user, myBoard, enemyBoard);
                }
            } else {
                System.out.println("Waiting for enemy...");
                Thread.sleep(300);

                gameData = gameDataService.requestStatus(gameData);
                localGameService.updateGameBoards(gameData, user, myBoard, enemyBoard);
            }
        }

        //Could also end if gameTerminated, or errors?

        if (gameData.getWinnerUserId().equals(user.getId())) {
            System.out.println(user.getName() + ", You are a winner baby!");
        } else {
            System.out.println("You lost.");
        }
    }

    private static GameData requestGameData(User user) throws IOException, ParseException {
        System.out.print("Connecting to a game..");
        GameData gameData = gameDataService.requestJoin(user);
        System.out.printf("%s joined a game [id = %s].\n", user.getName(), gameData.getGameId());
        return gameData;
    }

    //TODO I do not like how this is //isskaidyti i aiskius zingsnius
    private static List<Ship> shipSetupHelper(Board myBoard) {
        System.out.print("Input your ships, by providing a start and end points (ie. A1-A5).\n");

        List<Ship> ships = new ArrayList<>();
        int shipSize = 4;

        for (int i = 1; i <= 4; i++) {

            for (int j = 1; j <= i; j++) {
                Ship ship = readShipFromConsole(String.format("Enter ship nr.%d coordinates (shipsize=%d): ", j, shipSize));
                try {
                    localGameService.placeShipOnBoard(ship, myBoard);
                } catch (InvalidShipPlaceException e) {
                    System.out.println("try again..");
                }
                ships.add(ship);
                printGameBoard("Board update..", myBoard);
            }
            shipSize--;
        }
        return ships;
    }

//    private static List<Ship> readShipsFromConsole(){
//        List<Ship> myShips = shipSetupHelper(myBoard);
//
//                        List<Ship> myShips = readShipsFromConsole();
//
//                        localGameService.placeShipsOnBoard(myShips, myBoard); //Here I already now that the ships are safe to pass on
//                        gameData = gameDataService.requestSetup(gameData, user, myShips);
//
//                          -- continue --
//
//        gameData = gameDataService.requestSetup(gameData, user, myShips);
//
//    }

    private static User readUserFromConsole() {
        System.out.println("1. New User");
        System.out.println("2. Choose User");   //pvz. bota pasirinkti
        int menuOption = scanner.nextInt();

        switch (menuOption) {
            case 1:
                System.out.print("Enter user name: ");
                String name = scanner.nextLine();

                System.out.print("Enter user email: ");
                String email = scanner.nextLine();

                return new User("", name, email);
            case 2:
                System.out.println("DEFAULT user: Test, test@mail.com");
                return new User("", "Test", "test@mail.com");
            //pickBot(); //pickAvailableUser();
            default:
                System.out.println("Invalid input!");
                return readUserFromConsole();
        }
    }

    private static Ship readShipFromConsole(String title) {
        System.out.print(title);
        String shipAsString = scanner.next();
        try {
            return localGameService.createShip(shipAsString);
        } catch (InvalidCoordinateException e) {
            System.out.println("Invalid coordinate input: " + e.getMessage());
            return readShipFromConsole(title); //recursively
        }
        //TODO valid ship = not bigger then specified shipSize
        //this code will actually help botService, because he will need to understand how big are the ships.
        //add to ship class:   int size = 1..4;
        //set ship size in createShip with another method?
    }

    private static Coordinate readCoordinateFromConsole() {
        System.out.print("Input coordinate (ie. A1 or k8): ");
        String coordAsString = scanner.next();
        try {
            return localGameService.parseCoordinate(coordAsString);
        } catch (InvalidCoordinateException e) {
            System.out.println(e.getMessage());
            return readCoordinateFromConsole();
        }
    }

    private static void printGameBoard(String title, Board board) {
        System.out.printf("%s\n\n", title);
        printBoardRow(board.getHeader(), " ");

        for (int i = 0; i < board.size(); i++) {
            printSeparator(board.size());
            printBoardRow(board.getRow(i), "" + i);
        }
        System.out.println();
    }

    private static void printSeparator(int size) {
        StringBuilder separator = new StringBuilder();
        for (int i = 0; i <= size; i++) {
            separator.append("--- ");
        }
        System.out.println(separator.toString());
    }

    private static void printBoardRow(String[] row, String indexAsString) {
        StringBuilder template = new StringBuilder("%2s |");
        List<String> values = new LinkedList<>();

        values.add(indexAsString);
        values.addAll(Arrays.asList(row));
        for (int i = 0; i < row.length; i++) template.append("%2s |");

        System.out.println(String.format(template.toString(), values.toArray()));
    }

//    TODO get rid of it, or redo as bot;
//    private static User pickAvailableUser(String name, String email) {
//        for (User user : userRepository) {
//            if (user.getName().equals(name) && user.getEmail().equals(email)) {
//                return user;
//            }
//        }
//        return null;
//    }
}
