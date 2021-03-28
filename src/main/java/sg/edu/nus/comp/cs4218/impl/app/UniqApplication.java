package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.UniqInterface;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.exception.UniqException;
import sg.edu.nus.comp.cs4218.impl.app.args.UniqArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class UniqApplication implements UniqInterface {

    /**
     * Runs the uniq application with the specified arguments.
     *
     * @param args   Array of arguments for the application. Each array element is the path to a
     *               file. If no files are specified stdin is used.
     * @param stdin  An InputStream. The input for the command is read from this InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     * @throws UniqException If the file(s) specified do not exist or are unreadable.
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws UniqException {

        if (stdout == null) {
            throw new UniqException(ERR_NULL_STREAMS);
        }
        UniqArguments uniqArgs = new UniqArguments();
        uniqArgs.parse(args);
        String result;
        if (uniqArgs.getInputFile() == null) {
            result = uniqFromStdin(uniqArgs.isCount(), uniqArgs.isRepeated(), uniqArgs.isAllRepeated(), stdin, uniqArgs.getOutputFile());
        } else {
            result = uniqFromFile(uniqArgs.isCount(), uniqArgs.isRepeated(), uniqArgs.isAllRepeated(), uniqArgs.getInputFile(), uniqArgs.getOutputFile());
        }
        try {
            if (uniqArgs.getOutputFile() == null) {
                stdout.write(result.getBytes());
            }
        } catch (IOException e) {
            throw new UniqException(ERR_WRITE_STREAM, e);
        }
    }

    @Override
    public String uniqFromFile(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, String inputFileName, String outputFileName) throws UniqException {
        if (inputFileName == null) {
            throw new UniqException(ERR_NULL_STREAMS);
        }
        try {
            InputStream inputStream = IOUtils.openInputStream(inputFileName);
            String result = getUniqLines(isCount, isRepeated, isAllRepeated, inputStream);
            if (outputFileName != null) {
                writeToFile(outputFileName, result);
            }
            IOUtils.closeInputStream(inputStream);
            return result;
        } catch (Exception e){
            throw new UniqException(ERR_FILE_NOT_FOUND, e);
        }
    }

    @Override
    public String uniqFromStdin(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, InputStream stdin, String outputFileName) throws UniqException {
        if (stdin == null) {
            throw new UniqException(ERR_NULL_STREAMS);
        }
        String result = getUniqLines(isCount, isRepeated, isAllRepeated, stdin);
        if (outputFileName != null) {
            writeToFile(outputFileName, result);
        }
        return result;
    }

    private String getUniqLines(boolean isCount, boolean isRepeated, boolean isAllRepeated, InputStream inputStream) throws UniqException {

        StringJoiner result = new StringJoiner(STRING_NEWLINE);
        int count = 1;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String currentLine;
            String previousLine = null;
            while ((currentLine = reader.readLine()) != null) {
                if (previousLine != null) {
                    if (currentLine.equals(previousLine)) {
                        count++;
                    } else {
                        addLine(result, isCount, isRepeated, isAllRepeated, count, previousLine);
                        count = 1;
                    }
                }
                previousLine = currentLine;
            }
            //account for final line
            addLine(result, isCount, isRepeated, isAllRepeated, count, previousLine);

            if (result.toString().equals("null")) {
                return STRING_NEWLINE;
            }

            return result.toString() + STRING_NEWLINE;
        } catch (IOException e) {
            throw new UniqException(ERR_IO_EXCEPTION, e);
        }
    }

    private void addLine(StringJoiner result, boolean isCount, boolean isRepeated, boolean isAllRepeated, int count, String line) {
        if ((isRepeated || isAllRepeated) && count <= 1){
            return;
        }
        String lineToAdd = isCount ? count + " " + line : line;
        int end = isAllRepeated ? count : 1;
        for (int i = 0; i < end; i++) {
            result.add(lineToAdd);
        }
    }

    private void writeToFile(String outputFile, String result) throws UniqException {
        try {
            FileWriter fileWriter = new FileWriter(outputFile);
            fileWriter.write(result);
            fileWriter.close();
        } catch (Exception e) {
            throw new UniqException(ERR_IO_EXCEPTION, e);
        }

    }
}
