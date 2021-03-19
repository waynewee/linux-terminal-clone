package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.exception.UniqException;

import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;


public class UniqArguments {

    public static final char CHAR_COUNT_OPTION = 'c';
    public static final char CHAR_REPEATED_OPTION = 'd';
    public static final char CHAR_ALL_REPEATED_OPTION = 'D';
    private ArrayList<String> files = new ArrayList<>();
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
        // Parse arguments=
        for (String arg : args) {
            if (arg.isEmpty()) {
                continue;
            }
            // `parsingFlag` is to ensure all flags come first, followed by files.
            if (parsingFlag && arg.charAt(0) == CHAR_FLAG_PREFIX) {
                if (arg.equals(CHAR_FLAG_PREFIX + "" + CHAR_COUNT_OPTION)) {
                    this.count = true;
                    continue;
                }
                if (arg.equals(CHAR_FLAG_PREFIX + "" + CHAR_ALL_REPEATED_OPTION)) {
                    this.allRepeated = true;
                    continue;
                }
                if (arg.equals(CHAR_FLAG_PREFIX + "" + CHAR_REPEATED_OPTION)) {
                    this.repeated = true;
                    continue;
                }

                throw new UniqException(ERR_INVALID_FLAG);
            } else {
                parsingFlag = false;
                this.files.add(arg.trim());
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
}
