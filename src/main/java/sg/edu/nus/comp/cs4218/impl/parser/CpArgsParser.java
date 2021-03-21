package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.EnvironmentUtil;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class CpArgsParser extends ArgsParser{
    private final static char R_FLAG_LOWER = 'r';
    private final static char R_FLAG_UPPER = 'R';

    public String[] sourceFiles;
    public boolean destIsFolder;
    public String destination;

    public CpArgsParser() {
        super();
        legalFlags.add(R_FLAG_LOWER);
        legalFlags.add(R_FLAG_UPPER);
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
                destIsFolder = true;
                if (!lastFilePathIsExistingFolder(nonFlagArgs)) {
                    createDestinationFolder(nonFlagArgs.get(nonFlagArgs.size() - 1));
                }
            } else {
                destIsFolder = lastFilePathIsExistingFolder(nonFlagArgs);
            }
        } else {
            sourceFiles = new String[nonFlagArgs.size() - 1];
            for (int i = 0; i < nonFlagArgs.size() - 1; i++) {
                validateFileName(nonFlagArgs.get(i));
                validateIsNotDirectory(nonFlagArgs.get(i));
                sourceFiles[i] = getPathOfRawFileName(nonFlagArgs.get(i));
            }
            destIsFolder = true;
            if (lastFilePathIsExistingFolder(nonFlagArgs)) {
                destination = getPathOfRawFileName(nonFlagArgs.get(nonFlagArgs.size() - 1));
            } else {
                createDestinationFolder(nonFlagArgs.get(nonFlagArgs.size() - 1));
            }
        }
    }

    private void validateIsNotDirectory(String rawFileName) throws InvalidArgsException {
        if (Files.isDirectory(Paths.get(EnvironmentUtil.currentDirectory, rawFileName))) {
            throw new InvalidArgsException(ERR_TOO_MANY_FOLDER_ARGUMENTS);
        }
    }

    public String getPathOfRawFileName(String rawFileName) {
        return Paths.get(EnvironmentUtil.currentDirectory, rawFileName).toString();
    }

    private void createDestinationFolder(String rawFileName) throws IOException {
        Files.createDirectory(Paths.get(EnvironmentUtil.currentDirectory, rawFileName));
    }

    private boolean lastFilePathIsExistingFolder(List<String> nonFlagArgs) {
        String rawFileName = nonFlagArgs.get(nonFlagArgs.size() - 1);
        return Files.isDirectory(Paths.get(EnvironmentUtil.currentDirectory, rawFileName));
    }

    public void validateFileName(String rawFileName) throws InvalidArgsException {
        if (!Files.exists(Paths.get(EnvironmentUtil.currentDirectory, rawFileName))) {
            throw new InvalidArgsException(ERR_FILE_NOT_FOUND);
        }
    }

    public boolean isRecursive() {
        return legalFlags.contains(R_FLAG_LOWER) || legalFlags.contains(R_FLAG_UPPER);
    }
}
