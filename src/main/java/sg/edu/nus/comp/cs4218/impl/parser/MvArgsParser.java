package main.java.sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import main.java.sg.edu.nus.comp.cs4218.impl.app.args.MvArguments;
import sg.edu.nus.comp.cs4218.impl.parser.ArgsParser;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_ARGS;

public class MvArgsParser extends ArgsParser {

    private final static char FLAG_NO_OVERWRITE = 'n';
    private MvArguments arguments = null;

    public MvArgsParser() {
        super();
        legalFlags.add(FLAG_NO_OVERWRITE);
    }

    /**
     * Separates command flags from non-flag arguments given a tokenized command.
     *
     * @param args
     */
    @Override
    public void parse(String... args) throws InvalidArgsException {
        super.parse(args);
        // Check if there is enough parameter
        if (nonFlagArgs.size() < 2) {
            throw new InvalidArgsException(ERR_NO_ARGS);
        }
        arguments = new MvArguments(
                nonFlagArgs.subList(0, nonFlagArgs.size() - 1).toArray(new String[0]),
                nonFlagArgs.get(nonFlagArgs.size() - 1),
                !flags.contains(FLAG_NO_OVERWRITE));
    }

    public MvArguments getArguments() {
        return arguments;
    }
}
