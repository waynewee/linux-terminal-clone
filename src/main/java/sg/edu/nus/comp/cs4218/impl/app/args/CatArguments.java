package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;


public class CatArguments {

    public static final char CHAR_LINES_OPTION = 'n';

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
        boolean parsingFLag = true, lineNumber = false;
        // Parse arguments
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (arg.isEmpty()) {
                    continue;
                }
                if (!arg.equals("-")  && parsingFLag && arg.charAt(0) == CHAR_FLAG_PREFIX) {
                    if (arg.equals(CHAR_FLAG_PREFIX + "" + CHAR_LINES_OPTION)) {
                        lineNumber = true;
                        continue;
                    }
                    throw new CatException(ERR_INVALID_FLAG);
                } else {
                    parsingFLag = false;
                    String filename = arg.trim();
                    this.files.add(filename);
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

    public List<String> getNonInputFiles() {
        return files.stream().filter( f -> !f.equals("-")).collect(Collectors.toList());
    }
}
