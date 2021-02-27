package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.Integer.parseInt;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class SplitArgsParser extends ArgsParser{
    private final static char FLAG_IS_SPLIT_BY_LINE = 'l';
    private final static char FLAG_IS_SPLIT_BY_BYTES = 'b';

    private int splitSize = -1;

    private Path fileName = null;
    private String prefix = null;
    private boolean standardInput = false;

    public SplitArgsParser() {
        super();
        legalFlags.add(FLAG_IS_SPLIT_BY_LINE);
        legalFlags.add(FLAG_IS_SPLIT_BY_BYTES);
    }

    public void processArguments() throws InvalidArgsException {
        if (splitByBothBytesAndLines()) {
            throw new InvalidArgsException(ERR_TOO_MANY_ARGS);
        }

        if (noFlagsGiven()) {
            if (nonFlagArgs.isEmpty()) {
                standardInput = true;
                flags.add(FLAG_IS_SPLIT_BY_LINE);
                splitSize = 1000;
                return;
            } else if (nonFlagArgs.size() == 1) {
                fileName = Paths.get(Environment.currentDirectory, nonFlagArgs.get(0));
            } else if (nonFlagArgs.size() == 2) {
                fileName = Paths.get(Environment.currentDirectory, nonFlagArgs.get(0));
                prefix = nonFlagArgs.get(1);
            }
        } else if (isSplitByBytes() || isSplitByLines()) {
            if (nonFlagArgs.isEmpty()) {
                throw new InvalidArgsException(ERR_MISSING_ARG);
            } else {
                try {
                    splitSize = parseInt(nonFlagArgs.get(0));
                } catch (NumberFormatException numberFormatException) {
                    throw new InvalidArgsException(ERR_INVALID_ARG);
                }

                if (nonFlagArgs.size() == 1) {
                    standardInput = true;
                }

                if (nonFlagArgs.size() >= 2) {
                    fileName = Paths.get(Environment.currentDirectory, nonFlagArgs.get(1));
                }

                if (nonFlagArgs.size() >= 3) {
                    prefix = nonFlagArgs.get(2);
                }
            }
        }

        // Validate filename
        if (fileName!= null && !Files.exists(fileName)) {
            throw new InvalidArgsException(ERR_FILE_NOT_FOUND);
        }

        // Validate prefix
        if (prefixPresent() && prefix.charAt(0) == '/') {
            throw new InvalidArgsException(ERR_INVALID_ARG);
        }
    }

    public boolean splitByBothBytesAndLines() {
        return flags.contains(FLAG_IS_SPLIT_BY_BYTES) && flags.contains(FLAG_IS_SPLIT_BY_LINE);
    }

    public boolean noFlagsGiven() {
        return !flags.contains(FLAG_IS_SPLIT_BY_BYTES) && !flags.contains(FLAG_IS_SPLIT_BY_LINE);
    }

    public Boolean isSplitByBytes() {
        return flags.contains(FLAG_IS_SPLIT_BY_BYTES);
    }

    public boolean isSplitByLines() {
        return flags.contains(FLAG_IS_SPLIT_BY_LINE);
    }

    public int getSplitSize() {
        return splitSize;
    }

    public boolean prefixPresent() {
        return prefix != null;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean fileInput() {
        return !standardInput;
    }

    public String fileName() {
        if (fileName != null) {
            return fileName.toString();
        } else {
            return null;
        }
    }
}
