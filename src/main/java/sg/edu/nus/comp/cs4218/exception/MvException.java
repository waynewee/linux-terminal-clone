package main.java.sg.edu.nus.comp.cs4218.exception;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class MvException extends AbstractApplicationException {
    public MvException(String message) {
        super("mv: " + message);
    }
}
