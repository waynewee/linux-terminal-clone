package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.WcInterface;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.app.args.WcArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class WcApplication implements WcInterface {

    private static final String NUMBER_FORMAT = " %7d";
    private static final String NUMBER_FORMAT_1 = " %d";
    private static final int LINES_INDEX = 0;
    private static final int WORDS_INDEX = 1;
    private static final int BYTES_INDEX = 2;

    /**
     * Runs the wc application with the specified arguments.
     *
     * @param args   Array of arguments for the application. Each array element is the path to a
     *               file. If no files are specified stdin is used.
     * @param stdin  An InputStream. The input for the command is read from this InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     * @throws WcException
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws WcException {
        if (stdout == null) {
            throw new WcException(ERR_NULL_STREAMS);
        }
        WcArguments wcArgs = new WcArguments();
        wcArgs.parse(args);
        String result;
        try {
            if (!wcArgs.getNonInputFiles().isEmpty() && wcArgs.getFiles().contains("-")) {
                result = countFromFileAndStdin(wcArgs.isBytes(), wcArgs.isLines(), wcArgs.isWords(), stdin, wcArgs.getFiles().toArray(new String[0]));
            } else if (wcArgs.getNonInputFiles().isEmpty()) {
                result = countFromStdin(wcArgs.isBytes(), wcArgs.isLines(), wcArgs.isWords(), stdin);
            } else {
                result = countFromFiles(wcArgs.isBytes(), wcArgs.isLines(), wcArgs.isWords(), wcArgs.getFiles().toArray(new String[0]));
            }
        } catch (Exception e) {
            // Will never happen
            throw new WcException(ERR_GENERAL); //NOPMD
        }
        try {
            stdout.write(result.getBytes());
            stdout.write(STRING_NEWLINE.getBytes());
        } catch (IOException e) {
            throw new WcException(ERR_WRITE_STREAM);//NOPMD
        }
    }

    /**
     * Returns string containing the number of lines, words, and bytes in input files
     *
     * @param isBytes  Boolean option to count the number of Bytes
     * @param isLines  Boolean option to count the number of lines
     * @param isWords  Boolean option to count the number of words
     * @param fileName Array of String of file names
     * @throws Exception
     */
    @Override
    public String countFromFiles(Boolean isBytes, Boolean isLines, Boolean isWords, //NOPMD
                                 String... fileName) throws Exception {
        if (fileName == null) {
            throw new WcException(ERR_GENERAL);
        }
        List<String> result = new ArrayList<>();
        long totalBytes = 0, totalLines = 0, totalWords = 0;
        boolean displayAll = !isLines && !isWords && !isBytes;
        for (String file : fileName) {
            File node;
            try {
                node = IOUtils.resolveFilePath(file).toFile();
            } catch (Exception e) {
                result.add(new WcException(ERR_FILE_NOT_FOUND).getMessage());
                continue;
            }
            if (file.isEmpty() || !node.exists()) {
                result.add(new WcException(ERR_FILE_NOT_FOUND).getMessage());
                continue;
            }
            if (node.isDirectory()) {
                result.add(new WcException(ERR_IS_DIR).getMessage());
                continue;
            }

            InputStream input = IOUtils.openInputStream(file);
            long[] count = getCountReport(input); // lines words bytes
            IOUtils.closeInputStream(input);

            // Update total count
            totalLines += count[0];
            totalWords += count[1];
            totalBytes += count[2];

            String numberFormat = fileName.length > 1 ? NUMBER_FORMAT : NUMBER_FORMAT_1;

            // Format all output: " %7d %7d %7d %s"
            // Output in the following order: lines words bytes filename
            StringBuilder sb = new StringBuilder(); //NOPMD
            if (isLines || displayAll) {
                sb.append(String.format(numberFormat, count[0]));
            }
            if (isWords || displayAll) {
                sb.append(String.format(numberFormat, count[1]));
            }
            if (isBytes || displayAll) {
                sb.append(String.format(numberFormat, count[2]));
            }
            sb.append(String.format(" %s", file));
            result.add(sb.toString());
        }

        // Print cumulative counts for all the files
        if (fileName.length > 1) {
            StringBuilder sb = new StringBuilder(); //NOPMD
            if (isLines || displayAll) {
                sb.append(String.format(NUMBER_FORMAT, totalLines));
            }
            if (isWords || displayAll) {
                sb.append(String.format(NUMBER_FORMAT_1, totalWords));
            }
            if (isBytes || displayAll) {
                sb.append(String.format(NUMBER_FORMAT, totalBytes));
            }
            sb.append(" total");
            result.add(sb.toString());
        }

        return String.join(STRING_NEWLINE, result);
    }

    /**
     * Returns string containing the number of lines, words, and bytes in standard input
     *
     * @param isBytes Boolean option to count the number of Bytes
     * @param isLines Boolean option to count the number of lines
     * @param isWords Boolean option to count the number of words
     * @param stdin   InputStream containing arguments from Stdin
     * @throws Exception
     */
    @Override
    public String countFromStdin(Boolean isBytes, Boolean isLines, Boolean isWords,
                                 InputStream stdin) throws Exception {
        if (stdin == null) {
            throw new WcException(ERR_NULL_STREAMS);
        }
        long[] count = getCountReport(stdin); // lines words bytes;
        boolean displayAll = !isLines && !isWords && !isBytes;
        StringBuilder sb = new StringBuilder(); //NOPMD
        if (isLines || displayAll) {
            sb.append(' ').append(count[0]);
        }
        if (isWords || displayAll) {
            sb.append(' ').append(count[1]);
        }
        if (isBytes || displayAll) {
            sb.append(' ').append(count[2]);
        }

        return sb.toString();
    }

    @Override
    public String countFromFileAndStdin(Boolean isBytes, Boolean isLines, Boolean isWords, InputStream stdin, String... fileName) throws Exception {
        if (fileName == null) {
            throw new WcException(ERR_GENERAL);
        }
        if (stdin == null) {
            throw new WcException(ERR_NULL_STREAMS);
        }
        List<String> result = new ArrayList<>();
        long totalBytes = 0, totalLines = 0, totalWords = 0;
        boolean displayAll = !isLines && !isWords && !isBytes;
        for (String file : fileName) {
            String fName = file;
            long[] count;
            if (file.equals("-")) {
                fName = "";
                count = getCountReport(stdin);
            } else {
                File node = IOUtils.resolveFilePath(file).toFile();
                if (!node.exists()) {
                    result.add(new WcException(ERR_FILE_NOT_FOUND).getMessage());
                    continue;
                }
                if (node.isDirectory()) {
                    result.add(new WcException(ERR_IS_DIR).getMessage());
                    continue;
                }
                InputStream input = IOUtils.openInputStream(file);
                count = getCountReport(input); // lines words bytes
                IOUtils.closeInputStream(input);
            }
            // Update total count
            totalLines += count[0];
            totalWords += count[1];
            totalBytes += count[2];
            StringBuilder sb = new StringBuilder(); //NOPMD
            if (isLines || displayAll) {
                sb.append(String.format(NUMBER_FORMAT, count[0]));
            }
            if (isWords || displayAll) {
                sb.append(String.format(NUMBER_FORMAT, count[1]));
            }
            if (isBytes || displayAll) {
                sb.append(String.format(NUMBER_FORMAT, count[2]));
            }
            sb.append(String.format(" %s", fName));
            result.add(sb.toString());
        }

        if (fileName.length > 1) {
            StringBuilder sbTotal = new StringBuilder();
            if (isLines || displayAll) {
                sbTotal.append(String.format(NUMBER_FORMAT, totalLines));
            }
            if (isWords || displayAll) {
                sbTotal.append(String.format(NUMBER_FORMAT_1, totalWords));
            }
            if (isBytes || displayAll) {
                sbTotal.append(String.format(NUMBER_FORMAT, totalBytes));
            }
            sbTotal.append(" total");
            result.add(sbTotal.toString());
        }

        return String.join(STRING_NEWLINE, result);
    }

    /**
     * Returns array containing the number of lines, words, and bytes based on data in InputStream.
     *
     * @param input An InputStream
     * @throws IOException
     */
    public long[] getCountReport(InputStream input) throws Exception {

        long[] result = new long[3]; // lines, words, bytes

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int inRead = 0;
        boolean inWord = false;
        while ((inRead = input.read(data, 0, data.length)) != -1) {
            for (int i = 0; i < inRead; ++i) {
                if (Character.isWhitespace(data[i])) {
                    // Use <newline> character here. (Ref: UNIX)
                    if (data[i] == '\n') {
                        ++result[LINES_INDEX];
                    }
                    if (inWord) {
                        ++result[WORDS_INDEX];
                    }

                    inWord = false;
                } else {
                    inWord = true;
                }
            }
            result[BYTES_INDEX] += inRead;
            buffer.write(data, 0, inRead);
        }
        buffer.flush();
        if (inWord) {
            ++result[WORDS_INDEX]; // To handle last word
        }

        return result;
    }
}
