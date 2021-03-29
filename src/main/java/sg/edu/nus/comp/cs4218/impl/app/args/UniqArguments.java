package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.EnvironmentUtil;
import sg.edu.nus.comp.cs4218.exception.UniqException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.nio.file.Path;
import java.util.ArrayList;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;


public class UniqArguments {

    public static final char COUNT_FLAG = 'c';
    public static final char DUPLICATE_FLAG = 'd';
    public static final char ALL_DUPLICATE_FLAG = 'D';
    private final ArrayList<String> files = new ArrayList<>();
    private boolean count, repeated, allRepeated;

    public UniqArguments() {
        this.count = false;
        this.repeated = false;
        this.allRepeated = false;
    }

    /**
     * Handles argument list parsing for the `wc` application.
     *
     * @param args Array of arguments to parse
     */
    public void parse(String... args) throws UniqException {
        if (args == null) {
            throw new UniqException(ERR_NULL_ARGS);
        }

        boolean parsingFlag = true;
        for (String arg : args) {
            // `parsingFlag` is to ensure all flags come first, followed by files.
            if (!arg.isEmpty() && parsingFlag && arg.charAt(0) == CHAR_FLAG_PREFIX && arg.length() > 1) {
                if (arg.equals(CHAR_FLAG_PREFIX + "" + COUNT_FLAG)) {
                    this.count = true;
                    continue;
                }
                if (arg.equals(CHAR_FLAG_PREFIX + "" + ALL_DUPLICATE_FLAG)) {
                    this.allRepeated = true;
                    continue;
                }
                if (arg.equals(CHAR_FLAG_PREFIX + "" + DUPLICATE_FLAG)) {
                    this.repeated = true;
                    continue;
                }

                boolean isValid = true;

                for (int i = 1; i < arg.toCharArray().length; i++) {
                    char argChar = arg.toCharArray()[i];
                    if (argChar == COUNT_FLAG) {
                        this.count = true;
                        continue;
                    }
                    if (argChar == ALL_DUPLICATE_FLAG) {
                        this.allRepeated = true;
                        continue;
                    }
                    if (argChar == DUPLICATE_FLAG) {
                        this.repeated = true;
                        continue;
                    }

                    isValid = false;
                }

                if (isValid) {
                    continue;
                }

                throw new UniqException(ERR_INVALID_FLAG);
            } else {
                parsingFlag = false;
                this.files.add(arg);
            }
        }
    }

    public boolean isCount() {
        return this.count;
    }

    public boolean isRepeated() {
        return this.repeated;
    }

    public boolean isAllRepeated() {
        return this.allRepeated;
    }

    public String getInputFile() {
        if (this.files.isEmpty() || this.files.get(0).equals("-")) {
            return null;
        }
        return this.files.get(0);
    }

    public String getOutputFile() {
        if (this.files.size() < 2) {
            return null;
        }
        return this.files.get(1);
    }

    private String convertToAbsolutePath(String fileName) {
        String home = System.getProperty("user.home").trim();
        String currentDir = EnvironmentUtil.currentDirectory.trim();
        String convertedPath = convertPathToSystemPath(fileName);

        String newPath;
        if (convertedPath.length() >= home.length() && convertedPath.substring(0, home.length()).trim().equals(home)) {
            newPath = convertedPath;
        } else {
            newPath = currentDir + CHAR_FILE_SEP + convertedPath;
        }
        return newPath;
    }

    private String convertPathToSystemPath(String path) {
        String convertedPath = path;
        String pathIdentifier = "\\" + Character.toString(CHAR_FILE_SEP);
        convertedPath = convertedPath.replaceAll("(\\\\)+", pathIdentifier);
        convertedPath = convertedPath.replaceAll("/+", pathIdentifier);

        if (convertedPath.length() != 0 && convertedPath.charAt(convertedPath.length() - 1) == CHAR_FILE_SEP) {
            convertedPath = convertedPath.substring(0, convertedPath.length() - 1);
        }

        return convertedPath;
    }
}
