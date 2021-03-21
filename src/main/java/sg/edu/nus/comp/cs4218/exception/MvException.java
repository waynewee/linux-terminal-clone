package sg.edu.nus.comp.cs4218.exception;

import java.nio.file.FileAlreadyExistsException;

public class MvException extends AbstractApplicationException {
    public MvException(String message) {
        super("mv: " + message);
    }

    public MvException(String message, Throwable exception) {
        super("mv: " + message, exception);
    }
}
