package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class CpArgsParser extends ArgsParser{
    private final static char COPY_RECURSIVELY_LOWERCASE_R = 'r';
    private final static char COPY_RECURSIVELY_UPPERCASE_R = 'R';

    public String[] sourceFiles;
    public boolean destinationIsFolder;
    public String destination;

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
            sourceFiles = new String[nonFlagArgs.size() - 1];
            validateFileName(nonFlagArgs.get(0));
            sourceFiles[0] = getPathOfRawFileName(nonFlagArgs.get(0));
            destination = getPathOfRawFileName(nonFlagArgs.get(1));

            if (Files.isDirectory(Path.of(sourceFiles[0]))) {
                destinationIsFolder = true;
                if (!lastFilePathIsExistingFolder(nonFlagArgs)) {
                    createDestinationFolder(nonFlagArgs.get(nonFlagArgs.size() - 1));
                }
            } else {
                destinationIsFolder = lastFilePathIsExistingFolder(nonFlagArgs);
            }
        } else {
            sourceFiles = new String[nonFlagArgs.size() - 1];
            for (int i = 0; i < nonFlagArgs.size() - 1; i++) {
                validateFileName(nonFlagArgs.get(i));
                validateIsNotDirectory(nonFlagArgs.get(i));
                sourceFiles[i] = getPathOfRawFileName(nonFlagArgs.get(i));
            }
            destinationIsFolder = true;
            if (lastFilePathIsExistingFolder(nonFlagArgs)) {
                destination = getPathOfRawFileName(nonFlagArgs.get(nonFlagArgs.size() - 1));
            } else {
                createDestinationFolder(nonFlagArgs.get(nonFlagArgs.size() - 1));
            }
        }
    }

    private void validateIsNotDirectory(String rawFileName) throws InvalidArgsException {
        if (Files.isDirectory(Paths.get(Environment.currentDirectory, rawFileName))) {
            throw new InvalidArgsException(ERR_TOO_MANY_FOLDER_ARGUMENTS);
        }
    }

    public String getPathOfRawFileName(String rawFileName) {
        return Paths.get(Environment.currentDirectory, rawFileName).toString();
    }

    private void createDestinationFolder(String rawFileName) throws IOException {
        Files.createDirectory(Paths.get(Environment.currentDirectory, rawFileName));
    }

    private boolean lastFilePathIsExistingFolder(List<String> nonFlagArgs) {
        String rawFileName = nonFlagArgs.get(nonFlagArgs.size() - 1);
        return Files.isDirectory(Paths.get(Environment.currentDirectory, rawFileName));
    }

    public void validateFileName(String rawFileName) throws InvalidArgsException {
        if (!Files.exists(Paths.get(Environment.currentDirectory, rawFileName))) {
            throw new InvalidArgsException(ERR_FILE_NOT_FOUND);
        }
    }

    public boolean isRecursive() {
        return legalFlags.contains(COPY_RECURSIVELY_LOWERCASE_R) || legalFlags.contains(COPY_RECURSIVELY_UPPERCASE_R);
    }
}
