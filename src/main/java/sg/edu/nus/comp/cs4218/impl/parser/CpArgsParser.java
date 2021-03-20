package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_MISSING_ARG;

public class CpArgsParser extends ArgsParser{
    private final static char COPY_RECURSIVELY_LOWERCASE_R = 'r';
    private final static char COPY_RECURSIVELY_UPPERCASE_R = 'R';

    public ArrayList<Path> sourceFiles = new ArrayList<>();
    public boolean destinationIsFolder;
    public Path destinationFolder;

    public CpArgsParser() {
        super();
        legalFlags.add(COPY_RECURSIVELY_LOWERCASE_R);
        legalFlags.add(COPY_RECURSIVELY_UPPERCASE_R);
    }

    public void processArguments() throws InvalidArgsException, IOException {
        if (nonFlagArgs.isEmpty()) {
            throw new InvalidArgsException(ERR_MISSING_ARG);
        } else if (nonFlagArgs.size() == 1) {
            throw new InvalidArgsException(ERR_MISSING_ARG);
        } else if (nonFlagArgs.size() == 2) {
            if (lastFilePathIsExistingFolder(nonFlagArgs)) {
                destinationIsFolder = true;
                destinationFolder = getPathOfRawFileName(nonFlagArgs.get(1));
            } else {
                destinationIsFolder = false;
                sourceFiles.add(getPathOfRawFileName(nonFlagArgs.get(1)));
            }
        } else {
            for (int i = 0; i < nonFlagArgs.size() - 1; i++) {
                validateFileName(nonFlagArgs.get(i));
                sourceFiles.add(getPathOfRawFileName(nonFlagArgs.get(i)));
            }
            destinationIsFolder = true;
            if (lastFilePathIsExistingFolder(nonFlagArgs)) {
                destinationFolder = getPathOfRawFileName(nonFlagArgs.get(nonFlagArgs.size() - 1));
            } else {
                createDestinationFolder(nonFlagArgs.get(nonFlagArgs.size() - 1));
            }
        }
    }

    public Path getPathOfRawFileName(String rawFileName) {
        if (Files.exists(Paths.get(rawFileName))) {
            return Paths.get(rawFileName);
        } else {
            return Paths.get(Environment.currentDirectory, rawFileName);
        }
    }

    private void createDestinationFolder(String rawFileName) throws IOException {
        Files.createDirectory(Paths.get(rawFileName));
    }

    private boolean lastFilePathIsExistingFolder(List<String> nonFlagArgs) {
        String rawFileName = nonFlagArgs.get(nonFlagArgs.size() - 1);
        return Files.isDirectory(Paths.get(rawFileName)) || Files.isDirectory(Paths.get(Environment.currentDirectory, rawFileName));
    }

    public void validateFileName(String rawFileName) throws InvalidArgsException {
        if (!Files.exists(Paths.get(rawFileName)) && !Files.exists(Paths.get(Environment.currentDirectory, rawFileName))) {
            throw new InvalidArgsException(ERR_FILE_NOT_FOUND);
        }
    }

    public boolean isRecursive() {
        return legalFlags.contains(COPY_RECURSIVELY_LOWERCASE_R) || legalFlags.contains(COPY_RECURSIVELY_UPPERCASE_R);
    }
}
