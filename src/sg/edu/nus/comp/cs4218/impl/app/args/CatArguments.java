package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.WcException;

import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;


public class CatArguments {

    public static final char CHAR_SHOW_LINES_OPTION = 'n';

    private final List<String> files;
    private boolean lineNumber;

    public CatArguments() {
        this.lineNumber = false;
        this.files = new ArrayList<>();
    }

    /**
     * Handles argument list parsing for the `wc` application.
     *
     * @param args Array of arguments to parse
     */
    public void parse(String... args) throws CatException {
        boolean lineNumber = false;
        // Parse arguments
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (arg.isEmpty()) {
                    continue;
                }
                // `parsingFlag` is to ensure all flags come first, followed by files.
                if (arg.charAt(0) == CHAR_FLAG_PREFIX) {
                    for (char c : arg.toCharArray()) {
                        if (c == CHAR_FLAG_PREFIX) {
                            continue;
                        }
                        if (c == CHAR_SHOW_LINES_OPTION) {
                            lineNumber = true;
                            continue;
                        }
                        throw new CatException(ERR_INVALID_FLAG);
                    }
                } else {
                    this.files.add(arg.trim());
                }
            }
        }
        this.lineNumber = lineNumber;
    }

    public boolean isLineNumber() {
        return lineNumber;
    }

    public List<String> getFiles() {
        return files;
    }
}
