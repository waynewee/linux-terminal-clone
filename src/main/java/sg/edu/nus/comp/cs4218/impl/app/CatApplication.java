package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CatInterface;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.WcException;
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
            if (!catArgs.getNonInputFiles().isEmpty() && catArgs.getFiles().contains("-")) {
                result = catFileAndStdin(catArgs.isLineNumber(), stdin, catArgs.getFiles().toArray(new String[0]));
            } else if (catArgs.getNonInputFiles().isEmpty()) {
                result = catStdin(catArgs.isLineNumber(), stdin);
            } else {
                result = catFiles(catArgs.isLineNumber(), catArgs.getFiles().toArray(new String[0]));
            }
        } catch (Exception e) {
            throw new CatException(ERR_GENERAL, e);
        }
        try {
            stdout.write(result.getBytes());
            stdout.write(STRING_NEWLINE.getBytes());
        } catch (IOException e) {
            throw new CatException(ERR_WRITE_STREAM, e);
        }
    }

    @Override
    public String catFiles(Boolean isLineNumber, String... fileName) throws Exception {
        if (fileName == null) {
            throw new CatException(ERR_GENERAL);
        }
        StringJoiner result = new StringJoiner(STRING_NEWLINE);
        Vector<InputStream> inputStreams = new Vector<>();
        for (String file : fileName) {
            File node;
            try {
                node = IOUtils.resolveFilePath(file).toFile();
            } catch (Exception e) {
                result.add(new CatException(ERR_FILE_NOT_FOUND).getMessage());
                continue;
            }
            if (file.isEmpty() || !node.exists()) {
                result.add(new CatException(ERR_FILE_NOT_FOUND).getMessage());
                continue;
            }
            if (node.isDirectory()) {
                result.add(new CatException(ERR_IS_DIR).getMessage());
                continue;
            }

            InputStream input = IOUtils.openInputStream(file);
            inputStreams.add(input);
        }
        String catResult = getCatResult(isLineNumber, inputStreams);
        if (!catResult.isEmpty()) {
            result.add(catResult);
        }
        for (InputStream inputStream: inputStreams) {
            IOUtils.closeInputStream(inputStream);
        }
        return result.toString();
    }

    @Override
    public String catStdin(Boolean isLineNumber, InputStream stdin) throws CatException {
        if (stdin == null) {
            throw new CatException(ERR_NULL_STREAMS);
        }
        List<InputStream> inputStreams = new ArrayList<>();
        inputStreams.add(stdin);
        return getCatResult(isLineNumber, inputStreams);
    }

    public String getCatResult(Boolean isLineNumber, List<InputStream> inputStreams) {
        int lineNumber = 1;
        StringJoiner result = new StringJoiner(STRING_NEWLINE);
        for (InputStream inputStream: inputStreams) {
            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine()) {
                String line = "";
                if (isLineNumber) {
                    line += lineNumber + " ";
                }
                line += scanner.nextLine();
                result.add(line);
                lineNumber += 1;
            }
        }
        return result.toString().trim();
    }

    @Override
    public String catFileAndStdin(Boolean isLineNumber, InputStream stdin, String... fileName) throws Exception {
        if (fileName == null || stdin == null) {
            throw new CatException(ERR_GENERAL);
        }
        StringJoiner result = new StringJoiner(STRING_NEWLINE);
        Vector<InputStream> inputStreams = new Vector<>();
        for (String file : fileName) {
            if (file.equals("-")) {
                inputStreams.add(stdin);
            } else {
                File node = IOUtils.resolveFilePath(file).toFile();
                if (file.isEmpty() || !node.exists()) {
                    result.add(new CatException(ERR_FILE_NOT_FOUND).getMessage());
                    continue;
                }
                if (node.isDirectory()) {
                    result.add(new CatException(ERR_IS_DIR).getMessage());
                    continue;
                }

                InputStream input = IOUtils.openInputStream(file);
                inputStreams.add(input);
            }
        }

        String catResult = getCatResult(isLineNumber, inputStreams);
        if (!catResult.isEmpty()) {
            result.add(catResult);
        }
        for (InputStream inputStream: inputStreams) {
            IOUtils.closeInputStream(inputStream);
        }

        return result.toString();
    }
}
