package sg.edu.nus.comp.cs4218.exception;

public abstract class AbstractApplicationException extends Exception {

    private static final long serialVersionUID = -6276854591710517685L;

    public AbstractApplicationException(String message) {
        super(message);
    }

    public AbstractApplicationException(Throwable exception) {
        super(exception);
    }

    public AbstractApplicationException(String message, Throwable exception) {
        super(message, exception);
    }
}
