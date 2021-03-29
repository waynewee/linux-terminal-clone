package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.EnvironmentUtil;
import sg.edu.nus.comp.cs4218.app.GrepInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.impl.app.args.GrepArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
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

    @Override
    public String grepFromFiles(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, String... fileNames) throws Exception {
        if (fileNames == null || pattern == null) {
            throw new GrepException(ERR_NULL_STREAMS);
        }

        boolean isSingleFile = fileNames.length == 1;
        ArrayList<GrepResults> grepResultsList = grepResultsMultiple(pattern, isCaseInsensitive, null, fileNames);
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
    private ArrayList<GrepResults> grepResultsMultiple(String pattern, Boolean isCaseInsensitive, InputStream stdin, String... fileNames) throws Exception {

        ArrayList<GrepResults> grepResultsList = new ArrayList<>();

        for (String f : fileNames) {
            InputStream inputStream;
            GrepResults grepResults = new GrepResults();
            String fileName = f;
            if (fileName.equals("-") && stdin != null) {
                inputStream = stdin;
                fileName = "(standard input)";
            } else {
                File file = IOUtils.resolveFilePath(fileName).toFile();
                if (!file.exists()) {
                    String exceptionMessage = new GrepException(fileName + ": " + ERR_FILE_NOT_FOUND).getMessage();
                    grepResults.exceptionMessages.add(exceptionMessage);
                    grepResultsList.add(grepResults);
                    continue;

                }
                if (file.isDirectory()) { // ignore if it's a directory
                    String exceptionMessage = new GrepException(fileName + ": " + ERR_IS_DIR).getMessage();
                    grepResults.exceptionMessages.add(exceptionMessage);
                    grepResultsList.add(grepResults);
                    continue;
                }
                inputStream = IOUtils.openInputStream(fileName);
            }
            grepResults = grepResults(pattern, isCaseInsensitive, inputStream);
            IOUtils.closeInputStream(inputStream);
            grepResults.file = fileName;
            grepResultsList.add(grepResults);
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
        } catch (PatternSyntaxException pse) {
            throw new GrepException(ERR_INVALID_REGEX, pse);
        }

        return grepResults;

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
            GrepArguments grepArguments = new GrepArguments();
            grepArguments.parse(args);
            ArrayList<String> inputFiles = (ArrayList<String>) grepArguments.getFiles();
            String pattern = grepArguments.getPattern();
            String result = "";

            if (stdin == null && inputFiles.isEmpty()) {
                throw new Exception(ERR_NO_INPUT);
            }

            if (!grepArguments.getNonInputFiles().isEmpty() && grepArguments.getFiles().contains("-")) {
                result = grepFromFileAndStdin(pattern, grepArguments.isCaseInsensitive(), grepArguments.isCountOfLinesOnly(), grepArguments.isPrefixFileName(), stdin, grepArguments.getFiles().toArray(new String[0]));
            } else if (grepArguments.getNonInputFiles().isEmpty()) {
                result = grepFromStdin(pattern, grepArguments.isCaseInsensitive(), grepArguments.isCountOfLinesOnly(), grepArguments.isPrefixFileName(), stdin);
            } else {
                result = grepFromFiles(pattern, grepArguments.isCaseInsensitive(), grepArguments.isCountOfLinesOnly(), grepArguments.isPrefixFileName(), grepArguments.getFiles().toArray(new String[0]));
            }

            stdout.write(result.getBytes());
        } catch (GrepException grepException) {
            throw grepException;
        } catch (Exception e) {
            throw new GrepException(e.getMessage(), e);
        }
    }

    @Override
    public String grepFromFileAndStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, InputStream stdin, String... fileNames) throws Exception {
        if (fileNames == null || pattern == null) {
            throw new GrepException(ERR_NULL_STREAMS);
        }
        if (stdin == null) {
            throw new GrepException(ERR_NULL_STREAMS);
        }
        ArrayList<GrepResults> grepResultsList = grepResultsMultiple(pattern, isCaseInsensitive, stdin, fileNames);
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
            String filename = file;
            if (filename == null) {
                filename = "(standard input)";
            }
            stringJoiner.add(filename + ": " + count);
        }

        public void getLines(boolean isPrefixFileName, StringJoiner stringJoiner) {
            for (String line: lines) {
                if (isPrefixFileName) {
                    String filename = file;
                    if (filename == null) {
                        filename = "(standard input)";
                    }
                    stringJoiner.add(filename + ": " + line);
                } else {
                    stringJoiner.add(line);
                }
            }
        }
    }
}