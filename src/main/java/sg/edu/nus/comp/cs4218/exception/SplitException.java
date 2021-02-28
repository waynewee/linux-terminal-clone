package sg.edu.nus.comp.cs4218.exception;

public class SplitException extends AbstractApplicationException{

    private static final long serialVersionUID = -5845672222072101576L;

    public SplitException(String message) {
        super("split: " + message);
    }

}
