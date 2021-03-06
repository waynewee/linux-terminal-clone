package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.EnvironmentUtil;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.Integer.parseInt;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class SplitArgsParser extends ArgsParser{
    private final static char LINE_FLAG = 'l';
    private final static char BYTES_FLAG = 'b';

    private int splitSize = 1000;
    private String splitSuffix = "";

    private Path fileName = null;
    private String prefix = "x";
    private boolean standardInput = false;

    public SplitArgsParser() {
        super();
        legalFlags.add(LINE_FLAG);
        legalFlags.add(BYTES_FLAG);
    }

    public void processArguments() throws InvalidArgsException {
        if (splitByBothBytesAndLines()) {
            throw new InvalidArgsException(ERR_TOO_MANY_ARGS);
        }

        if (noFlagsGiven()) {
            flags.add(LINE_FLAG);
            if (nonFlagArgs.isEmpty()) {
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
            standardInput = true;
        } else {
            validateFileName(nonFlagArgs.get(pos));
        }
    }

    private void extractSplitSize(int pos) throws InvalidArgsException {
        String temp = nonFlagArgs.get(pos);
        if (isSplitByBytes() && temp.charAt(temp.length() - 1) == 'b' || temp.charAt(temp.length() - 1) == 'k'
                || temp.charAt(temp.length() - 1) == 'm') {
            splitSuffix = String.valueOf(temp.charAt(temp.length() - 1));
            temp = temp.substring(0, temp.length() - 1);
        }
        validateSplitSize(temp);
    }

    private void validateSplitSize(String temp) throws InvalidArgsException {
        // Validate split size
        try {
            splitSize = parseInt(temp);
        } catch (NumberFormatException nfe) {
            throw new InvalidArgsException(ERR_INVALID_ARG, nfe);
        }
    }

    private void validatePrefix() throws InvalidArgsException {
        // Validate prefix
        if (prefix.charAt(0) == '/') {
            throw new InvalidArgsException(ERR_INVALID_ARG);
        }
    }

    public void validateFileName(String rawFileName) throws InvalidArgsException {
        // Validate filename
        if (Files.exists(Paths.get(rawFileName))) {
            fileName = Paths.get(rawFileName);
            return;
        } else if (Files.exists(Paths.get(EnvironmentUtil.currentDirectory, rawFileName))) {
            fileName = Paths.get(EnvironmentUtil.currentDirectory, rawFileName);
            return;
        }

        throw new InvalidArgsException(ERR_FILE_NOT_FOUND);
    }

    public boolean splitByBothBytesAndLines() {
        return flags.contains(BYTES_FLAG) && flags.contains(LINE_FLAG);
    }

    public boolean noFlagsGiven() {
        return !flags.contains(BYTES_FLAG) && !flags.contains(LINE_FLAG);
    }

    public Boolean isSplitByBytes() {
        return flags.contains(BYTES_FLAG);
    }

    public boolean isSplitByLines() {
        return flags.contains(LINE_FLAG);
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

    public String getFileName() {
        if (fileName == null) {
            return null;
        } else {
            return fileName.toString();
        }
    }
}
