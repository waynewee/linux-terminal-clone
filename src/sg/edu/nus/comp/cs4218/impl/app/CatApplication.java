package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CatInterface;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.impl.app.args.CatArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class CatApplication implements CatInterface {
    public static final String ERR_IS_DIR = "This is a directory";
    public static final String ERR_READING_FILE = "Could not read file";
    public static final String ERR_WRITE_STREAM = "Could not write to output stream";
    public static final String ERR_NULL_STREAMS = "Null Pointer Exception";
    public static final String ERR_GENERAL = "Exception Caught";

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
            throw new Exception(ERR_GENERAL);
        }
        List<String> result = new ArrayList<>();
        Vector<InputStream> inputStreams = new Vector<>();
        for (String file : fileName) {
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                result.add("cat: " + ERR_FILE_NOT_FOUND);
                continue;
            }
            if (node.isDirectory()) {
                result.add("cat: " + ERR_IS_DIR);
                continue;
            }
            if(!node.canRead()) {
                result.add("cat: " + ERR_NO_PERM);
                continue;
            }

            InputStream input = IOUtils.openInputStream(file);
            inputStreams.add(input);
        }

        Enumeration<InputStream> inputStreamEnumeration = inputStreams.elements();
        SequenceInputStream sequenceInputStream = new SequenceInputStream(inputStreamEnumeration);
        String catResult = getCatResult(isLineNumber, sequenceInputStream);
        result.add(catResult);

        return String.join(STRING_NEWLINE, result);
    }

    @Override
    public String catStdin(Boolean isLineNumber, InputStream stdin) throws Exception {
        return getCatResult(isLineNumber, stdin);
    }

    public String getCatResult(Boolean isLineNumber, InputStream input) throws Exception {
        if (input == null) {
            throw new Exception(ERR_NULL_STREAMS);
        }
        StringBuilder result = new StringBuilder();

        byte[] data = new byte[1024];
        int inRead = 0;

        while ((inRead = input.read(data, 0, data.length)) != -1) {
            for (int i = 0; i < inRead; ++i) {
                try {
                    String s = Character.toString(data[i]);
                    result.append(s);
                } catch (IllegalArgumentException e) {
                    throw new CatException(ERR_INVALID_ARG);
                }
            }
        }

        return result.toString();
    }

    @Override
    public String catFileAndStdin(Boolean isLineNumber, InputStream stdin, String... fileName) throws Exception {
        return null;
    }
}
