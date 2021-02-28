package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.WcException;

import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;


public class WcArguments {

    public static final char CHAR_BYTES_OPTION = 'c';
    public static final char CHAR_LINES_OPTION = 'l';
    public static final char CHAR_WORDS_OPTION = 'w';
    private final List<String> files;
    private boolean lines, words, bytes;

    public WcArguments() {
        this.lines = false;
        this.words = false;
        this.bytes = false;
        this.files = new ArrayList<>();
    }

    /**
     * Handles argument list parsing for the `wc` application.
     *
     * @param args Array of arguments to parse
     */
    public void parse(String... args) throws WcException {
        if (args == null) {
            throw new WcException(ERR_NULL_ARGS);
        }
        if (args.length < 1) {
            throw new WcException(ERR_NO_REGEX);
        }

        boolean parsingFlag = true;
        // Parse arguments=
        for (String arg : args) {
            if (arg.isEmpty()) {
                continue;
            }
            // `parsingFlag` is to ensure all flags come first, followed by files.
            if (parsingFlag && arg.charAt(0) == CHAR_FLAG_PREFIX) {
                if (arg.equals(CHAR_FLAG_PREFIX + "" + CHAR_BYTES_OPTION)) {
                    this.bytes = true;
                    continue;
                }
                if (arg.equals(CHAR_FLAG_PREFIX + "" + CHAR_WORDS_OPTION)) {
                    this.words = true;
                    continue;
                }
                if (arg.equals(CHAR_FLAG_PREFIX + "" + CHAR_LINES_OPTION)) {
                    this.lines = true;
                    continue;
                }

                boolean isValid = true;

                for (int i = 1; i < arg.toCharArray().length; i++) {
                    char argChar = arg.toCharArray()[i];
                    if (argChar == CHAR_BYTES_OPTION) {
                        this.bytes = true;
                        continue;
                    }
                    if (argChar == CHAR_WORDS_OPTION) {
                        this.words = true;
                        continue;
                    }
                    if (argChar == CHAR_LINES_OPTION) {
                        this.lines = true;
                        continue;
                    }

                    isValid = false;
                }

                if (isValid) {
                    continue;
                }

                throw new WcException(ERR_INVALID_FLAG);
            } else {
                parsingFlag = false;
                this.files.add(arg.trim());
            }
        }
    }

    public boolean isLines() {
        return lines;
    }

    public boolean isWords() {
        return words;
    }

    public boolean isBytes() {
        return bytes;
    }

    public List<String> getFiles() {
        return files;
    }
}
