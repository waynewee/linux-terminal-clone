package sg.edu.nus.comp.cs4218.impl.parser;

public class RmArgsParser extends ArgsParser {
    private final static char FLAG_RECURSIVE = 'r';
    private final static char FLAG_DIRECTORY = 'd';

    public RmArgsParser() {
        super();
        legalFlags.add(FLAG_RECURSIVE);
        legalFlags.add(FLAG_DIRECTORY);
    }

    /**
     * Converts the non-flag arguments into a string array for ease of use.
     *
     * @return A string array of file path arguments
     */
    public String[] getFilesPaths() {
        return nonFlagArgs.toArray(new String[0]);
    }

    public boolean isDirectory() {
        return flags.contains(FLAG_DIRECTORY);
    }

    public boolean isRecursive() {
        return flags.contains(FLAG_RECURSIVE);
    }

}
