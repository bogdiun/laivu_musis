package lt.raimond.laivu_musis.Exceptions;

public class InvalidShipPlaceException extends Throwable {
    public InvalidShipPlaceException(String invalid_ship) {
        super(invalid_ship);
    }
}
