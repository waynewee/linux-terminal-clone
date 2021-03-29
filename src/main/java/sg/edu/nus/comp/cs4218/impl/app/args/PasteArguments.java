package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;


public class PasteArguments {

    public static final char SERIAL_FLAG = 's';
    private final List<String> files;
    private boolean serial;

    public PasteArguments() {
        this.serial = false;
        this.files = new ArrayList<>();
    }

    /**
     * Handles argument list parsing for the `wc` application.
     *
     * @param args Array of arguments to parse
     */
    public void parse(String... args) throws PasteException {
        if (args == null) {
            throw new PasteException(ERR_NULL_ARGS);
        }

        boolean parsingFlag = true;
        // Parse arguments=
        for (String arg : args) {
            // `parsingFlag` is to ensure all flags come first, followed by files.
            if (!arg.isEmpty() && !arg.equals("-") && parsingFlag && arg.charAt(0) == CHAR_FLAG_PREFIX) {
                if (arg.equals(CHAR_FLAG_PREFIX + "" + SERIAL_FLAG)) {
                    this.serial = true;
                    continue;
                }

                throw new PasteException(ERR_INVALID_FLAG);
            } else {
                parsingFlag = false;
                this.files.add(arg);
            }
        }
    }

    public boolean isSerial() {
        return serial;
    }

    public List<String> getFiles() {
        return files;
    }

    public List<String> getNonInputFiles() {
        return files.stream().filter( f -> !f.equals("-")).collect(Collectors.toList());
    }
}
