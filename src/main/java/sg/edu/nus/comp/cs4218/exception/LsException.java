package sg.edu.nus.comp.cs4218.exception;

import java.io.IOException;

public class LsException extends AbstractApplicationException {

    private static final long serialVersionUID = -731736942454546043L;

    public LsException(String message) {
        super("ls: " + message);
    }
}
