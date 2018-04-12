package lt.raimond.laivu_musis.services;

import lt.raimond.laivu_musis.Exceptions.InvalidCoordinateException;
import lt.raimond.laivu_musis.entities.*;
import lt.raimond.laivu_musis.interfaces.BotServiceInterface;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SimpleBotService implements BotServiceInterface {

    private LocalGameService localGameService = new LocalGameService();
    private List<List<Ship>> shipListPresets = new ArrayList<>();

    private List<Coordinate> ignoreList = new ArrayList<>();
    private Queue<Coordinate> coordinateQueue = new LinkedList<>();
    private Queue<Coordinate> presetCoordinateQueue;

    //TODO?  private List<Coordinate> focusedShip = null;
    private Ship focusedShip = null;
    private Map<Integer, Integer> destroyedShips = new HashMap<>();

    public SimpleBotService() {
        for (int i = 1; i <= 4; i++) destroyedShips.put(i, 0);
        presetCoordinateQueue = getPresetQueue();

        List<String[]> shipsAsString = new ArrayList<>();
        List<Ship> preset = new ArrayList<>();

        shipsAsString.add(new String[]{"M1-M4", "O6-O8", "R1-S1", "K2-I2", "A7-S7", "T9-R9", "I8", "T5", "S9", "A3"});
        shipsAsString.add(new String[]{"I3-I6", "M0-T0", "E3-E5", "O2-O3", "E8-T8", "L9-O9", "A3", "O7", "I1", "K8"});
        shipsAsString.add(new String[]{"I0-I3", "O1-E1", "M5-M7", "I6-L6", "K8-K9", "R3-A3", "S1", "M3", "R8", "L9"});
        shipsAsString.add(new String[]{"E2-E5", "R1-S1", "I1-O1", "R7-A7", "M8-M9", "L5-L6", "O3", "A4", "L9", "K5"});
        shipsAsString.add(new String[]{"A3-A6", "L3-M3", "M7-M9", "L0-L1", "T1-R1", "I6-I7", "K0", "K4", "E5", "S1"});
        shipsAsString.add(new String[]{"S0-S3", "E0-R0", "L2-L4", "L6-O6", "O8-O9", "A6-A7", "T3", "K8", "E7", "T9"});
        shipsAsString.add(new String[]{"K3-K6", "E0-E2", "T8-A8", "M6-M7", "A2-S2", "L4-O4", "K0", "O2", "R5", "S5"});

        try {
            for (String[] set : shipsAsString) {
                for (int i = 0; i < set.length; i++) {
                    preset.add(localGameService.parseShip(set[i]));
                }
                shipListPresets.add(preset);
                preset = new ArrayList<>();
            }
        } catch (InvalidCoordinateException e) {
            //presets will always be valid ..
        }
    }

    @Override
    public List<Ship> getRandomPresetShipList() {
        int index = ThreadLocalRandom.current().nextInt(0, shipListPresets.size());
        return shipListPresets.get(index);
    }

    //TODO test and clean up ..
    private Queue<Coordinate> getPresetQueue() {
        Queue<Coordinate> presetQueue = new LinkedList<>();
        Coordinate position = new Coordinate();

        while (position != null) {
            presetQueue.add(position);
            position = position.move(Coordinate.DOWN_RIGHT);
        }

        position = new Coordinate(9, 0);
        while (position != null) {
            presetQueue.add(position);
            position = position.move(Coordinate.DOWN_LEFT);
        }

        position = new Coordinate(1, 1);
        Coordinate direction = Coordinate.DOWN;

        int rot = 8;
        for (int mod = 0; mod <= 2; mod++) {
            for (int i = 1; i < rot; i++) {
                position = position.move(direction);
                presetQueue.add(position);
            }
            direction = direction.turnLeft();
            if (mod == 2) {
                mod = 0;
                rot--;
            }
            if (rot == 6) break;
        }

        presetQueue.removeAll(Collections.singleton(null));
        return presetQueue;
    }

    private Coordinate getPresetCoordinate() {
        presetCoordinateQueue.removeAll(ignoreList);
        return presetCoordinateQueue.remove();
    }

    public Coordinate getRandomCoordinate() {
        int col = ThreadLocalRandom.current().nextInt(0, 10);
        int row = ThreadLocalRandom.current().nextInt(0, 10);
        return new Coordinate(col, row);
    }


    @Override
    public Coordinate getNextCoordinate() {
        Coordinate next;
        if (!coordinateQueue.isEmpty()) {
            next = coordinateQueue.remove();
            ignoreList.add(next);
            return next;
        } else do {
            if (presetCoordinateQueue.isEmpty()) {
                next = getRandomCoordinate();
            } else next = getPresetCoordinate();
            //also can check if I can make a needed ship from current coordinate (as in
        } while (ignoreList.contains(next));

        ignoreList.add(next);
        return next;
    }

    //Collections.binarySearch(..); need to make coordinates Comparable
    //TODO redo in a way with less iterations, most of the mess is in coordinate checks with ignored and queues, neighbours, etc. duplicates
    //TODO could this be used to check for next coordinates?

    @Override
    public void updateStrategy(GameData gameData, Coordinate lastCoordinate) {
        boolean hit = localGameService.checkHit(gameData, lastCoordinate);

        if (hit) {
            addNearDiagonalsToIgnore(ignoreList, lastCoordinate);
            addAvailableNeighbourToQueue(ignoreList, coordinateQueue, lastCoordinate);
            coordinateQueue.removeAll(ignoreList);

            if (focusedShip == null) {
                focusedShip = new Ship(lastCoordinate);
            } else {
                focusedShip = extendShip(focusedShip, lastCoordinate);
            }

            updateDestroyedShips();
        } else if (focusedShip != null) updateDestroyedShips();
    }

    private void updateDestroyedShips() {
        int size = focusedShip.getSize();
        for (int i = size; i <= 4; i++) {
            if (i == 4) {
                ignoreList.addAll(coordinateQueue);
                coordinateQueue.clear();
            } else if (destroyedShips.get(i + 1) != 4 - i) break;
        }
        if (coordinateQueue.isEmpty()) {
            focusedShip = null;
            destroyedShips.put(size, destroyedShips.get(size) + 1);
        }
    }

    private Ship extendShip(Ship ship, Coordinate extension) {
        if (extension.getCol() < ship.getFront().getCol() || extension.getRow() < ship.getFront().getRow()) {
            return new Ship(ship.getEnd(), extension);
        } else {
            return new Ship(ship.getFront(), extension);
        }
    }

    private void addAvailableNeighbourToQueue(List<Coordinate> ignore, Queue<Coordinate> queue, Coordinate coordinate) {
        for (Coordinate neighbour : localGameService.getNeighbourCoordinates(coordinate)) {
            if (!ignore.contains(neighbour)) {
                queue.add(neighbour);
            }
        }
    }

    private void addNearDiagonalsToIgnore(List<Coordinate> ignore, Coordinate coordinate) {
        for (Coordinate diagonal : localGameService.getNearDiagonalCoordinates(coordinate)) {
            if (!ignore.contains(diagonal)) {
                ignore.add(diagonal);
            }
        }
    }
}