package exception;


public class DataAccessException extends RuntimeException {
    public DataAccessException(Exception e) {
        super(e);
    }
}
