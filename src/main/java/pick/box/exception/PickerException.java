package pick.box.exception;

public class PickerException extends RuntimeException {
    public PickerException(String message, Exception cause) {
        super(message, cause);
    }
}
