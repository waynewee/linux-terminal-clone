package sg.edu.nus.comp.cs4218.impl.parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class LsArgsParser extends ArgsParser {
    private final static char FLAG_IS_RECURSIVE = 'R';
    private final static char FLAG_IS_FOLDERS = 'd';
    private final static char FLAG_IS_SORT_EXT = 'X';

    public LsArgsParser() {
        super();
        legalFlags.add(FLAG_IS_FOLDERS);
        legalFlags.add(FLAG_IS_RECURSIVE);
        legalFlags.add(FLAG_IS_SORT_EXT);
    }

    public Boolean isFoldersOnly() {
        return flags.contains(FLAG_IS_FOLDERS);
    }

    public Boolean isRecursive() {
        return flags.contains(FLAG_IS_RECURSIVE);
    }

    public Boolean isSortByExt() {
        return flags.contains(FLAG_IS_SORT_EXT);
    }

    public List<String> getDirectories() {
        List<String> directories = new LinkedList<>();
        for (String string: nonFlagArgs) {
            if (Files.isDirectory(Paths.get(string))) {
                directories.add(string);
            }
        }
        return directories;
    }

    public List<String> getFiles() {
        List<String> files = new LinkedList<>();
        for (String string: nonFlagArgs) {
            if (!Files.isDirectory(Path.of(string))) {
                files.add(string);
            }
        }
        return files;
    }
}
