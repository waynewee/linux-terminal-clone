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

    private int splitSize = 1000;
    private String splitSuffix = "";

    private Path fileName = null;
    private String prefix = "x";
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
                flags.add(FLAG_IS_SPLIT_BY_LINE);
                standardInput = true;
            } else if (nonFlagArgs.size() == 1) {
                extractFileName(0);
            } else if (nonFlagArgs.size() == 2) {
                extractFileName(0);
                extractPrefix(1);
            }
        } else if (isSplitByBytes() || isSplitByLines()) {
            if (nonFlagArgs.isEmpty()) {
                throw new InvalidArgsException(ERR_MISSING_ARG);
            } else {
                extractSplitSize(0);

                if (nonFlagArgs.size() == 1) {
                    standardInput = true;
                }

                if (nonFlagArgs.size() >= 2) {
                    extractFileName(1);
                }

                if (nonFlagArgs.size() >= 3) {
                    extractPrefix(2);
                }
            }
        }
    }

    private void extractPrefix(int pos) throws InvalidArgsException {
        prefix = nonFlagArgs.get(pos);
        validatePrefix();
    }

    private void extractFileName(int pos) throws InvalidArgsException {
        if (nonFlagArgs.get(pos).equals("-")) {
            flags.add(FLAG_IS_SPLIT_BY_LINE);
            standardInput = true;
        } else {
            fileName = Paths.get(Environment.currentDirectory, nonFlagArgs.get(pos));
            validateFileName();
        }
    }

    private void extractSplitSize(int pos) throws InvalidArgsException {
        String temp = nonFlagArgs.get(pos);
        if (isSplitByBytes()) {
            if (temp.charAt(temp.length() - 1) == 'b' || temp.charAt(temp.length() - 1) == 'k'
                    || temp.charAt(temp.length() - 1) == 'm') {
                splitSuffix = String.valueOf(temp.charAt(temp.length() - 1));
                temp = temp.substring(0, temp.length() - 1);
            }
        }
        validateSplitSize(temp);
    }

    private void validateSplitSize(String temp) throws InvalidArgsException {
        // Validate split size
        try {
            splitSize = parseInt(temp);
        } catch (NumberFormatException numberFormatException) {
            throw new InvalidArgsException(ERR_INVALID_ARG);
        }
    }

    private void validatePrefix() throws InvalidArgsException {
        // Validate prefix
        if (prefix.charAt(0) == '/') {
            throw new InvalidArgsException(ERR_INVALID_ARG);
        }
    }

    public void validateFileName() throws InvalidArgsException {
        // Validate filename
        if (fileName == null) {
            return;
        }

        if (Files.exists(fileName)) {
            return;
        }

        throw new InvalidArgsException(ERR_FILE_NOT_FOUND);
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

    public String getPrefix() {
        return prefix;
    }

    public String getSplitSuffix() {
        return splitSuffix;
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

    public void updatePrefix() {
        prefix = "z";
    }
}
