package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.EnvironmentUtil;
import sg.edu.nus.comp.cs4218.app.LsInterface;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_CURR_DIR;

public class LsApplication implements LsInterface {

    private final static String PATH_CURR_DIR = STRING_CURR_DIR + CHAR_FILE_SEP;

    @Override
    public String listFolderContent(Boolean isFoldersOnly, Boolean isRecursive, Boolean isSortByExt,
                                    String... folderNames) throws LsException {
        if (folderNames.length == 0 && !isRecursive) {
            return listCwdContent(isFoldersOnly, isSortByExt);
        }

        List<Path> paths;
        if (folderNames.length == 0 && isRecursive) {
            String[] directories = new String[1];
            directories[0] = EnvironmentUtil.currentDirectory;
            paths = resolvePaths(directories);
        } else {
            paths = resolvePaths(folderNames);
        }

        return buildResult(paths, isFoldersOnly, isRecursive, isSortByExt);
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws LsException {
        if (args == null) {
            throw new LsException(ERR_NULL_ARGS);
        }

        if (stdout == null) {
            throw new LsException(ERR_NO_OSTREAM);
        }

        LsArgsParser parser = new LsArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new LsException(e.getMessage(), e);
        }

        Boolean foldersOnly = parser.isFoldersOnly();
        Boolean recursive = parser.isRecursive();
        Boolean sortByExt = parser.isSortByExt();
        String[] files = parser.getFiles().toArray(new String[parser.getFiles().size()]);
        String[] directories = parser.getDirectories()
                .toArray(new String[parser.getDirectories().size()]);

        String result1;
        String result2;
        result1 = listFileContent(files);
        result2 = listFolderContent(foldersOnly, recursive, sortByExt, directories);

        try {
            if (!parser.getFiles().isEmpty()) {
                stdout.write(result1.getBytes());
            }

            if (parser.getFiles().isEmpty() || !parser.getDirectories().isEmpty()) {
                stdout.write(result2.getBytes());
            }
        } catch (Exception e) {
            throw new LsException(ERR_WRITE_STREAM, e);
        }
    }

    public String listFileContent(String... filename) throws LsException {
        List<Path> paths;
        paths = resolvePaths(filename);
        StringBuilder result = new StringBuilder();
        for (Path path : paths) {
            String relativePath = getRelativeToCwd(path).toString();
            result.append(StringUtils.isBlank(relativePath) ? PATH_CURR_DIR : relativePath);
            result.append(StringUtils.STRING_NEWLINE);
        }
        if (result.length() == 0) {
            return "";
        } else {
            return result.toString().trim() + StringUtils.STRING_NEWLINE;
        }
    }
    /**
     * Lists only the current directory's content and RETURNS. This does not account for recursive
     * mode in cwd.
     *
     * @param isFoldersOnly
     * @param isSortByExt
     * @return
     */
    private String listCwdContent(Boolean isFoldersOnly, Boolean isSortByExt) throws LsException {
        String cwd = EnvironmentUtil.currentDirectory;
        try {
            return formatContents(getContents(Paths.get(cwd), isFoldersOnly), isSortByExt);
        } catch (InvalidDirectoryException e) {
            throw new LsException("Unexpected error occurred!", e);
        }
    }

    /**
     * Builds the resulting string to be written into the output stream.
     * <p>
     * NOTE: This is recursively called if user wants recursive mode.
     *
     * @param paths         - list of java.nio.Path objects to list
     * @param isFoldersOnly - only list the folder contents
     * @param isRecursive   - recursive mode, repeatedly ls the child directories
     * @param isSortByExt - sorts folder contents alphabetically by file extension (characters after the last ???.??? (without quotes)). Files with no extension are sorted first.
     * @return String to be written to output stream.
     */
    private String buildResult(List<Path> paths, Boolean isFoldersOnly, Boolean isRecursive, Boolean isSortByExt) {
        StringBuilder result = new StringBuilder();
        for (Path path : paths) {
            try {
                List<Path> contents = getContents(path, isFoldersOnly);
                String formatted = formatContents(contents, isSortByExt);
                String relativePath = getRelativeToCwd(path).toString();
                result.append(StringUtils.isBlank(relativePath) ? PATH_CURR_DIR : relativePath);
                result.append(':');
                result.append(StringUtils.STRING_NEWLINE);
                result.append(formatted);

                if (formatted.isEmpty()) {
                    result = new StringBuilder();
                } else {
                    // Empty directories should not have an additional new line
                    result.append(StringUtils.STRING_NEWLINE);
                }

                // RECURSE!
                if (isRecursive) {
                    boolean containsFolders = false;
                    for (Path filepath: contents) {
                        if (filepath.toFile().isDirectory()) {
                            containsFolders = true;
                            break;
                        }
                    }
                    if (containsFolders) {
                        String recursiveResult = buildResult(contents, isFoldersOnly, isRecursive, isSortByExt);
                        result.append(recursiveResult);
                        if (!recursiveResult.equals("")) {
                            // Empty directories should not have an additional new line
                            result.append(StringUtils.STRING_NEWLINE);
                        }
                    }
                }
            } catch (InvalidDirectoryException e) {
                // NOTE: This is pretty hackish IMO - we should find a way to change this
                // If the user is in recursive mode, and if we resolve a file that isn't a directory
                // we should not spew the error message.
                //
                // However the user might have written a command like `ls invalid1 valid1 -R`, what
                // do we do then?
                if (!isRecursive) {
                    result.append(e.getMessage());
                    result.append(StringUtils.STRING_NEWLINE);
                }
            }
        }
        if (result.length() == 0) {
            return "";
        } else {
            return result.toString().trim() + StringUtils.STRING_NEWLINE;
        }
    }

    /**
     * Formats the contents of a directory into a single string.
     *
     * @param contents    - list of items in a directory
     * @param isSortByExt - sorts folder contents alphabetically by file extension (characters after the last ???.??? (without quotes)). Files with no extension are sorted first.
     * @return
     */
    private String formatContents(List<Path> contents, Boolean isSortByExt) {
        // TODO: To implement sorting by extension
        List<String> fileNames = new ArrayList<>();
        for (Path path : contents) {
            fileNames.add(path.getFileName().toString());
        }

        if (isSortByExt) {
            List<String> filesWithoutExt = new ArrayList<>();
            List<String> filesWithExt = new ArrayList<>();
            for (String filename: fileNames) {
                if (filename.contains(".")) {
                    filesWithExt.add(filename);
                } else {
                    filesWithoutExt.add(filename);
                }
            }

            // Sort files without extensions
            Collections.sort(filesWithoutExt);
            // Sort files with extensions
            filesWithExt.sort(new ExtensionComparator());
            // Combine both the results
            fileNames = new ArrayList<>(filesWithoutExt);
            fileNames.addAll(filesWithExt);
        }

        StringBuilder result = new StringBuilder();
        for (String fileName : fileNames) {
            result.append(fileName);
            result.append(StringUtils.STRING_NEWLINE);
        }

        if (result.length() == 0) {
            return "";
        } else {
            return result.toString().trim() + StringUtils.STRING_NEWLINE;
        }
    }

    /**
     * Gets the contents in a single specified directory.
     *
     * @param directory
     * @return List of files + directories in the passed directory.
     */
    private List<Path> getContents(Path directory, Boolean isFoldersOnly)
            throws InvalidDirectoryException {
        if (!Files.exists(directory)) {
            throw new InvalidDirectoryException(getRelativeToCwd(directory).toString());
        }

        if (!Files.isDirectory(directory)) {
            throw new InvalidDirectoryException(getRelativeToCwd(directory).toString());
        }

        List<Path> result = new ArrayList<>();
        File pwd = directory.toFile();
        for (File f : pwd.listFiles()) {
            if ((isFoldersOnly && !f.isDirectory()) || f.isHidden()) {
                continue;
            }

            result.add(f.toPath());
        }

        Collections.sort(result);

        return result;
    }

    /**
     * Resolve all paths given as arguments into a list of Path objects for easy path management.
     *
     * @param directories
     * @return List of java.nio.Path objects
     */
    private List<Path> resolvePaths(String... directories) {
        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < directories.length; i++) {
            paths.add(resolvePath(directories[i]));
        }

        return paths;
    }

    /**
     * Converts a String into a java.nio.Path objects. Also resolves if the current path provided
     * is an absolute path.
     *
     * @param directory
     * @return
     */
    private Path resolvePath(String directory) {

        if (new File(directory).isAbsolute()) {
            // This is an absolute path
            return Paths.get(directory).normalize();
        }

        return Paths.get(EnvironmentUtil.currentDirectory, directory).normalize();
    }

    /**
     * Converts a path to a relative path to the current directory.
     *
     * @param path
     * @return
     */
    private Path getRelativeToCwd(Path path) {
        return Paths.get(EnvironmentUtil.currentDirectory).relativize(path);
    }

    private class InvalidDirectoryException extends Exception {
        InvalidDirectoryException(String directory) {
            super(String.format("ls: cannot access '%s': No such file or directory", directory));
        }
    }

    // Utils
    static class ExtensionComparator implements Comparator<String> {
        @Override
        public int compare(String file1, String file2) {
            String ext1 = file1.substring(file1.lastIndexOf('.') + 1);
            String ext2 = file2.substring(file2.lastIndexOf('.') + 1);
            return ext1.compareTo(ext2);
        }
    }
}
