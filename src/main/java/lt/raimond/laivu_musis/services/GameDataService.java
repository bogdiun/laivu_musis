package lt.raimond.laivu_musis.services;

import lt.raimond.laivu_musis.Exceptions.InvalidCoordinateException;
import lt.raimond.laivu_musis.entities.*;
import lt.raimond.laivu_musis.interfaces.GameStatusInterface;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.LinkedList;
import java.util.Date;
import java.util.List;

public class GameDataService extends WebService implements GameStatusInterface {
    public static final String READY_FOR_SECOND_PLAYER = "READY_FOR_SECOND_PLAYER";
    public static final String READY_FOR_SHIPS = "READY_FOR_SHIPS";
    public static final String READY_TO_PLAY = "READY_TO_PLAY";
    public static final String FINISHED = "FINISHED";

    public static final String JOIN_METHOD = "join?";
    public static final String SETUP_METHOD = "setup?";
    public static final String TURN_METHOD = "turn?";
    public static final String STATUS_METHOD = "status?";

    private final LocalGameService localGameService = new LocalGameService();

    public GameData requestJoin(User user) throws Exception {
        StringBuilder request = new StringBuilder(JOIN_METHOD);
        request.append("user_id=").append(user.getId());

        String response = getHttpResponseAsString(request.toString());
        return convertJsonToGameData(response);
    }

    public GameData requestSetup(GameData gameData, User user, List<Ship> ships) throws Exception {
        StringBuilder request = new StringBuilder(SETUP_METHOD);
        request.append("game_id=").append(gameData.getGameId()).append("&");
        request.append("user_id=").append(user.getId()).append("&");
        request.append("data=").append(localGameService.convertShipsToString(ships));

        String response = getHttpResponseAsString(request.toString());
        return convertJsonToGameData(response);
    }

    public GameData requestTurn(GameData gameData, User user, Coordinate coordinate) throws Exception {
        StringBuilder request = new StringBuilder(TURN_METHOD);
        request.append("game_id=").append(gameData.getGameId()).append("&");
        request.append("user_id=").append(user.getId()).append("&");
        request.append("data=").append(localGameService.convertCoordinateToString(coordinate));

        String response = getHttpResponseAsString(request.toString());
        return convertJsonToGameData(response);
    }

    public GameData requestStatus(GameData gameData) throws Exception {
        StringBuilder request = new StringBuilder(STATUS_METHOD);
        request.append("game_id=").append(gameData.getGameId());

        String response = getHttpResponseAsString(request.toString());
        return convertJsonToGameData(response);
    }

    private GameData convertJsonToGameData(String response) throws Exception {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonGame = (JSONObject) parser.parse(response);

            String gameId = (String) jsonGame.get("id");
            String status = (String) jsonGame.get("status");
            String nextTurnForUserId = (String) jsonGame.get("nextTurnForUserId");
            String winnerUserId = (String) jsonGame.get("winnerUserId");
            LinkedList<Event> events = convertJsonArrayToEventList((JSONArray) jsonGame.get("events"));

            return new GameData(gameId, status, nextTurnForUserId, winnerUserId, events);
        } catch (ParseException e) {
            //TODO actual exception handling on possible errors from server response
            throw new Exception(response);
        }
    }

    private LinkedList<Event> convertJsonArrayToEventList(JSONArray jsonArray) {
        LinkedList<Event> result = new LinkedList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject jsonEvent = (JSONObject) jsonArray.get(i);
                result.add(convertJsonToEvent(jsonEvent));
            } catch (InvalidCoordinateException e) {} // this should never happen.
        }
        return result;
    }

    private Event convertJsonToEvent(JSONObject jsonObject) throws InvalidCoordinateException {
        Long date = (Long) jsonObject.get("date");
        String userId = (String) jsonObject.get("userId");
        Boolean hit = (Boolean) jsonObject.get("hit");

        JSONObject jsonCoord = (JSONObject) jsonObject.get("coordinate");
        String col = (String) jsonCoord.get("column");
        Long row = (Long) jsonCoord.get("row");
        Coordinate coordinate = localGameService.parseCoordinate(col + row);

        return new Event(new Date(date), coordinate, userId, hit);
    }

    @Override
    public boolean isReadyForSecondPlayer(GameData gameData) {
        return READY_FOR_SECOND_PLAYER.equals(gameData.getStatus());
    }

    @Override
    public boolean isReadyForShips(GameData gameData) {
        return READY_FOR_SHIPS.equals(gameData.getStatus());
    }

    @Override
    public boolean isReadyToPlay(GameData gameData) {
        return READY_TO_PLAY.equals(gameData.getStatus());
    }

    @Override
    public boolean hasWinner(GameData gameData) {
        return FINISHED.equals(gameData.getStatus());
    }

    @Override
    public boolean isMyTurn(GameData gameData, User user) {
        return user.getId().equals(gameData.getNextUserId());
    }
}