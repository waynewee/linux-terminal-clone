package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CatInterface;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.impl.app.args.CatArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class CatApplication implements CatInterface {

    /**
     * Runs the cat application with the specified arguments.
     *
     * @param args   Array of arguments for the application. Each array element is the path to a
     *               file. If no files are specified stdin is used.
     * @param stdin  An InputStream. The input for the command is read from this InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     * @throws CatException If the file(s) specified do not exist or are unreadable.
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws CatException {

        if (stdout == null) {
            throw new CatException(ERR_NULL_STREAMS);
        }
        CatArguments catArgs = new CatArguments();
        catArgs.parse(args);
        String result;
        try {
            if (catArgs.getFiles().isEmpty()) {
                result = catStdin(catArgs.isLineNumber(), stdin);
            } else {
                result = catFiles(catArgs.isLineNumber(), catArgs.getFiles().toArray(new String[0]));
            }
        } catch (Exception e) {
            throw new CatException(ERR_GENERAL);
        }
        try {
            stdout.write(result.getBytes());
            stdout.write(STRING_NEWLINE.getBytes());
        } catch (IOException e) {
            throw new CatException(ERR_WRITE_STREAM);
        }
    }

    @Override
    public String catFiles(Boolean isLineNumber, String... fileName) throws Exception {
        if (fileName == null) {
            throw new CatException(ERR_GENERAL);
        }
        List<String> result = new ArrayList<>();
        Vector<InputStream> inputStreams = new Vector<>();
        for (String file : fileName) {
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                result.add(new CatException(ERR_FILE_NOT_FOUND).getMessage());
                continue;
            }
            if (node.isDirectory()) {
                result.add(new CatException(ERR_IS_DIR).getMessage());
                continue;
            }
            if (!node.canRead()) {
                result.add(new CatException(ERR_NO_PERM).getMessage());
                continue;
            }

            InputStream input = IOUtils.openInputStream(file);
            inputStreams.add(input);
        }

        Enumeration<InputStream> inputStreamEnum = inputStreams.elements();
        SequenceInputStream seqInputStream = new SequenceInputStream(inputStreamEnum);
        String catResult = getCatResult(isLineNumber, seqInputStream);
        result.add(catResult);

        return String.join(STRING_NEWLINE, result);
    }

    @Override
    public String catStdin(Boolean isLineNumber, InputStream stdin) throws Exception {
        return getCatResult(isLineNumber, stdin);
    }

    public String getCatResult(Boolean isLineNumber, InputStream input) throws Exception {
        if (input == null) {
            throw new CatException(ERR_NULL_STREAMS);
        }
        StringBuilder result = new StringBuilder();

        Scanner scanner = new Scanner(input);
        int lineNumber = 1;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (isLineNumber) {
                result.append(lineNumber).append(" ");
            }
            result.append(line);
            if (scanner.hasNextLine()) {
                result.append(STRING_NEWLINE);
            }
            lineNumber += 1;
        }

        return result.toString();
    }

    @Override
    public String catFileAndStdin(Boolean isLineNumber, InputStream stdin, String... fileName) throws Exception {
        if (fileName == null || stdin == null) {
            throw new CatException(ERR_GENERAL);
        }
        List<String> result = new ArrayList<>();
        Vector<InputStream> inputStreams = new Vector<>();
        for (String file : fileName) {
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                result.add(new CatException(ERR_FILE_NOT_FOUND).getMessage());
                continue;
            }
            if (node.isDirectory()) {
                result.add(new CatException(ERR_IS_DIR).getMessage());
                continue;
            }
            if (!node.canRead()) {
                result.add(new CatException(ERR_NO_PERM).getMessage());
                continue;
            }

            InputStream input = IOUtils.openInputStream(file);
            inputStreams.add(input);
        }

        inputStreams.add(stdin);

        Enumeration<InputStream> inputStreamEnum = inputStreams.elements();
        SequenceInputStream seqInputStream = new SequenceInputStream(inputStreamEnum);
        String catResult = getCatResult(isLineNumber, seqInputStream);
        result.add(catResult);

        return String.join(STRING_NEWLINE, result);
    }
}
