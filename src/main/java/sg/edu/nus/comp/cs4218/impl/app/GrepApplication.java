package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.GrepInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.nio.Buffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class GrepApplication implements GrepInterface {

    private static final int NUM_ARGUMENTS = 3;
    private static final char CASE_INSEN_IDENT = 'i';
    private static final char COUNT_IDENT = 'c';
    private static final char PREFIX_FN = 'H';
    private static final int CASE_INSEN_IDX = 0;
    private static final int COUNT_INDEX = 1;
    private static final int PREFIX_FN_IDX = 2;


    @Override
    public String grepFromFiles(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, String... fileNames) throws Exception {
        if (fileNames == null || pattern == null) {
            throw new GrepException(ERR_NULL_STREAMS);
        }

        boolean isSingleFile = fileNames.length == 1;
        ArrayList<GrepResults> grepResultsList = grepResultsFromFiles(pattern, isCaseInsensitive, isPrefixFileName, fileNames);
        StringJoiner stringJoiner = new StringJoiner(STRING_NEWLINE);
        for (GrepResults grepResults : grepResultsList) {

            if (!grepResults.exceptionMessages.isEmpty()) {
                for (String exceptionMessage: grepResults.exceptionMessages) {
                    stringJoiner.add(exceptionMessage);
                }
                continue;
            }

            if (isCountLines) {
                grepResults.getCount(isSingleFile, stringJoiner);
            } else {
                grepResults.getLines(isPrefixFileName, stringJoiner);
            }
        }

        return stringJoiner.toString() + STRING_NEWLINE;

    }

    /**
     * Extract the lines and count number of lines for grep from files and insert them into
     * lineResults and countResults respectively.
     *
     * @param pattern           supplied by user
     * @param isCaseInsensitive supplied by user
     * @param fileNames         a String Array of file names supplied by user
     */
    private ArrayList<GrepResults> grepResultsFromFiles(String pattern, Boolean isCaseInsensitive, boolean isPrefixFileName, String... fileNames) throws Exception {

        ArrayList<GrepResults> grepResultsList = new ArrayList<>();

        for (String f : fileNames) {
            try {
                String path = convertToAbsolutePath(f);
                File file = new File(path);

                GrepResults grepResults = new GrepResults();

                if (!file.exists()) {
                    String exceptionMessage = new GrepException(file.getName() + ": " + ERR_FILE_NOT_FOUND).getMessage();
                    grepResults.exceptionMessages.add(exceptionMessage);
                    grepResultsList.add(grepResults);
                    continue;
                }
                if (file.isDirectory()) { // ignore if it's a directory
                    String exceptionMessage = new GrepException(file.getName() + ": " + ERR_IS_DIR).getMessage();
                    grepResults.exceptionMessages.add(exceptionMessage);
                    grepResultsList.add(grepResults);
                    continue;
                }

                InputStream inputStream = IOUtils.openInputStream(path);
                grepResults = grepResults(pattern, isCaseInsensitive, inputStream);
                grepResults.file = file.getName();
                grepResultsList.add(grepResults);
            } catch (PatternSyntaxException pse) {
                throw new GrepException(ERR_INVALID_REGEX);
            }
        }

        return grepResultsList;
    }

    private GrepResults grepResults(String pattern, boolean isCaseInsensitive, InputStream stdin) throws Exception {

        GrepResults grepResults = new GrepResults();

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
            String line;
            Pattern compiledPattern;
            if (isCaseInsensitive) {
                compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            } else {
                compiledPattern = Pattern.compile(pattern);
            }
            while ((line = reader.readLine()) != null) {
                Matcher matcher = compiledPattern.matcher(line);
                if (matcher.find()) { // match
                    grepResults.lines.add(line);
                }
            }
            reader.close();
        } catch (PatternSyntaxException pse) {
            throw new GrepException(ERR_INVALID_REGEX);
        }

        return grepResults;

    }

    /**
     * Converts filename to absolute path, if initially was relative path
     *
     * @param fileName supplied by user
     * @return a String of the absolute path of the filename
     */
    private String convertToAbsolutePath(String fileName) {
        String home = System.getProperty("user.home").trim();
        String currentDir = Environment.currentDirectory.trim();
        String convertedPath = convertPathToSystemPath(fileName);

        String newPath;
        if (convertedPath.length() >= home.length() && convertedPath.substring(0, home.length()).trim().equals(home)) {
            newPath = convertedPath;
        } else {
            newPath = currentDir + CHAR_FILE_SEP + convertedPath;
        }
        return newPath;
    }

    /**
     * Converts path provided by user into path recognised by the system
     *
     * @param path supplied by user
     * @return a String of the converted path
     */
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

    @Override
    public String grepFromStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, InputStream stdin) throws Exception {
        if (pattern == null) {
            throw new GrepException(ERR_NULL_STREAMS);
        }
        if (stdin == null) {
            throw new GrepException(ERR_NULL_STREAMS);
        }

        GrepResults grepResults = grepResults(pattern, isCaseInsensitive, stdin);

        StringJoiner stringJoiner = new StringJoiner(STRING_NEWLINE);

        if (isCountLines) {
            grepResults.getCount(true, stringJoiner);
        } else {
            grepResults.getLines(isPrefixFileName, stringJoiner);
        }

        return stringJoiner.toString() + STRING_NEWLINE;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        try {
            boolean[] grepFlags = new boolean[NUM_ARGUMENTS];
            ArrayList<String> inputFiles = new ArrayList<>();
            String pattern = getGrepArguments(args, grepFlags, inputFiles);
            String result = "";

            if (stdin == null && inputFiles.isEmpty()) {
                throw new Exception(ERR_NO_INPUT);
            }
            if (pattern == null) {
                throw new Exception(ERR_SYNTAX);
            }

            if (pattern.isEmpty()) {
                throw new Exception(ERR_EMPTY_REGEX);
            } else {
                if (inputFiles.isEmpty()) {
                    result = grepFromStdin(pattern, grepFlags[CASE_INSEN_IDX], grepFlags[COUNT_INDEX], grepFlags[PREFIX_FN_IDX], stdin);
                } else {
                    String[] inputFilesArray = new String[inputFiles.size()];
                    inputFilesArray = inputFiles.toArray(inputFilesArray);
                    result = grepFromFiles(pattern, grepFlags[CASE_INSEN_IDX], grepFlags[COUNT_INDEX], grepFlags[PREFIX_FN_IDX], inputFilesArray);

                }
            }
            stdout.write(result.getBytes());
        } catch (GrepException grepException) {
            throw grepException;
        } catch (Exception e) {
            throw new GrepException(e.getMessage());
        }
    }

    /**
     * Separates the arguments provided by user into the flags, pattern and input files.
     *
     * @param args       supplied by user
     * @param grepFlags  a bool array of possible flags in grep
     * @param inputFiles a ArrayList<String> of file names supplied by user
     * @return regex pattern supplied by user. An empty String if not supplied.
     */
    private String getGrepArguments(String[] args, boolean[] grepFlags, ArrayList<String> inputFiles) throws Exception {
        String pattern = null;
        boolean isFile = false; // files can only appear after pattern

        for (String s : args) {
            char[] arg = s.toCharArray();
            if (isFile) {
                inputFiles.add(s);
            } else {
                if (!s.isEmpty() && arg[0] == CHAR_FLAG_PREFIX) {
                    arg = Arrays.copyOfRange(arg, 1, arg.length);
                    for (char c : arg) {
                        switch (c) {
                            case CASE_INSEN_IDENT:
                                grepFlags[CASE_INSEN_IDX] = true;
                                break;
                            case COUNT_IDENT:
                                grepFlags[COUNT_INDEX] = true;
                                break;
                            case PREFIX_FN:
                                grepFlags[PREFIX_FN_IDX] = true;
                                break;
                            default:
                                throw new GrepException(ERR_SYNTAX);
                        }
                    }
                } else { // pattern must come before file names
                    pattern = s;
                    isFile = true; // next arg onwards will be file
                }
            }
        }
        return pattern;
    }

    @Override
    public String grepFromFileAndStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, InputStream stdin, String... fileNames) throws Exception {
        if (fileNames == null || pattern == null) {
            throw new GrepException(ERR_NULL_STREAMS);
        }
        if (stdin == null) {
            throw new GrepException(ERR_NULL_STREAMS);
        }
        ArrayList<GrepResults> fileGrepResultsList = grepResultsFromFiles(pattern, isCaseInsensitive, isPrefixFileName, fileNames);
        GrepResults stdinGrepResults = grepResults(pattern, isCaseInsensitive, stdin);
        ArrayList<GrepResults> grepResultsList = new ArrayList<>(fileGrepResultsList);
        grepResultsList.add(stdinGrepResults);
        StringJoiner stringJoiner = new StringJoiner(STRING_NEWLINE);
        for (GrepResults grepResults : grepResultsList) {
            if (!grepResults.exceptionMessages.isEmpty()) {
                for (String exceptionMessage: grepResults.exceptionMessages) {
                    stringJoiner.add(exceptionMessage);
                }
                continue;
            }

            if (isCountLines) {
                grepResults.getCount(false, stringJoiner);
            } else {
                grepResults.getLines(isPrefixFileName, stringJoiner);
            }
        }

        return stringJoiner.toString() + STRING_NEWLINE;

    }

    static class GrepResults {
        protected String file;
        protected ArrayList<String> lines = new ArrayList<>();
        protected ArrayList<String> exceptionMessages = new ArrayList<>();

        public void getCount(boolean isSingleFile, StringJoiner stringJoiner) {
            String count = Integer.toString(lines.size());
            if (!isSingleFile){
                if (file == null) {
                    stringJoiner.add("stdin: " + count);
                } else {
                    stringJoiner.add(file + ": " + count);
                }
            } else {
                stringJoiner.add(count);
            }
        }

        public void getLines(boolean isPrefixFileName, StringJoiner stringJoiner) {
            for (String line: lines) {
                if (isPrefixFileName) {
                    if (file == null) {
                        stringJoiner.add("stdin: " + line);
                    } else {
                        stringJoiner.add(file + ": " + line);
                    }
                } else {
                    stringJoiner.add(line);
                }
            }
        }
    }
}