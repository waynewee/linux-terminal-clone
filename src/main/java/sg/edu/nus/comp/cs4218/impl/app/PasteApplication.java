package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.PasteInterface;
import sg.edu.nus.comp.cs4218.exception.*;
import sg.edu.nus.comp.cs4218.impl.app.args.PasteArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;
import java.util.*;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class PasteApplication implements PasteInterface {

    /**
     * Runs the paste application with the specified arguments.
     *
     * @param args   Array of arguments for the application. Each array element is the path to a
     *               file. If no files are specified stdin is used.
     * @param stdin  An InputStream. The input for the command is read from this InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     * @throws PasteException If the file(s) specified do not exist or are unreadable.
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws PasteException {
        if (stdout == null) {
            throw new PasteException(ERR_NULL_STREAMS);
        }

        PasteArguments pasteArguments = new PasteArguments();
        pasteArguments.parse(args);
        String result;
        try {
            List<String> files = pasteArguments.getFiles();
            if (!pasteArguments.getNonInputFiles().isEmpty() && pasteArguments.getFiles().contains("-")) {
                result = mergeFileAndStdin(pasteArguments.isSerial(), stdin, files.toArray(new String[0]));
            } else if (pasteArguments.getNonInputFiles().isEmpty()) {
                result = mergeStdin(pasteArguments.isSerial(), stdin);
            } else {
                result = mergeFile(pasteArguments.isSerial(), files.toArray(new String[0]));
            }
        } catch (Exception e) {
            throw new PasteException(ERR_GENERAL, e);
        }
        try {
            stdout.write(result.getBytes());
        } catch (IOException e) {
            throw new PasteException(ERR_WRITE_STREAM, e);
        }
    }

    @Override
    public String mergeStdin(Boolean isSerial, InputStream stdin) throws Exception {
        if (stdin == null) {
            throw new PasteException(ERR_NULL_STREAMS);
        }
        if (isSerial) {
            return mergeSerial(stdin, "-");
        }
        return mergeNonSerial(stdin, "-");
    }

    @Override
    public String mergeFile(Boolean isSerial, String... fileName) throws Exception {
        if (fileName == null) {
            throw new PasteException(ERR_NULL_STREAMS);
        }
        if (isSerial) {
            return mergeSerial(null, fileName);
        }
        return mergeNonSerial(null, fileName);
    }

    @Override
    public String mergeFileAndStdin(Boolean isSerial, InputStream stdin, String... fileName) throws Exception {
        if (stdin == null || fileName == null) {
            throw new PasteException(ERR_NULL_STREAMS);
        }
        if (isSerial) {
            return mergeSerial(stdin, fileName);
        }
        return mergeNonSerial(stdin, fileName);
    }

    public String mergeSerial(InputStream stdin, String... fileName) throws PasteException {
        BufferedReader inputStreamReader = null;
        if (stdin != null) {
            inputStreamReader = new BufferedReader(new InputStreamReader(stdin));
        }
        HashMap<String, BufferedReader> fileReaders = new HashMap<>();
        for (String file: fileName) {
            if (file.equals("-") && inputStreamReader != null) {
                fileReaders.put(file, inputStreamReader);
            } else {
                try {
                    File node = IOUtils.resolveFilePath(file).toFile();
                    fileReaders.put(file, new BufferedReader(new FileReader(node)));
                } catch (FileNotFoundException e) {
                    throw new PasteException(ERR_FILE_NOT_FOUND, e);
                }
            }
        }
        StringBuilder result = new StringBuilder();

        for (String file : fileName) {
            BufferedReader fileReader = fileReaders.get(file);
            String nextLine;
            StringJoiner lineResult = new StringJoiner("\t");
            try {
                while ((nextLine = fileReader.readLine()) != null) {
                    lineResult.add(nextLine);
                }
            } catch (IOException e) {
                throw new PasteException(ERR_IO_EXCEPTION, e);
            }
            result.append(lineResult.toString());
            result.append(STRING_NEWLINE);
        }
        return result.toString();
    }

    public String mergeNonSerial(InputStream stdin, String... fileName) throws PasteException {

        HashMap<String, BufferedReader> fileReaders = new HashMap<>();
        BufferedReader inputStreamReader = null;
        if (stdin != null) {
            inputStreamReader = new BufferedReader(new InputStreamReader(stdin));
        }

        for (String file: fileName) {
            if (file.equals("-") && inputStreamReader != null) {
                fileReaders.put(file, inputStreamReader);
            } else {
                try {
                    File node = IOUtils.resolveFilePath(file).toFile();
                    fileReaders.put(file, new BufferedReader(new FileReader(node)));
                } catch (FileNotFoundException e) {
                    throw new PasteException(ERR_FILE_NOT_FOUND, e);
                }
            }
        }


        StringBuilder result = new StringBuilder();
        StringJoiner lineResult = new StringJoiner("\t");
        int fileIndex = 0;
        boolean nothingWasRead = true;

        while (true) {
            if (fileIndex == 0) {
                nothingWasRead = true;
            }
            String file = fileName[fileIndex];
            String nextLine;
            try {
                if (file.equals("-") && inputStreamReader != null) {
                    nextLine = inputStreamReader.readLine();
                } else {
                    nextLine = fileReaders.get(file).readLine();
                }
            } catch (IOException e) {
                throw new PasteException(ERR_IO_EXCEPTION, e);
            }
            if (nextLine == null) {
                lineResult.add("");
            } else {
                lineResult.add(nextLine);
                nothingWasRead = false;
            }

            // if final input stream, check if nothing was read previously
            if (fileIndex == fileName.length - 1) {
                if (nothingWasRead) {
                    break;
                } else {
                    result.append(lineResult.toString()).append(STRING_NEWLINE);
                    lineResult = new StringJoiner("\t");
                }
            }
            fileIndex = (fileIndex + 1) % fileName.length;
        }

        return result.toString();

    }
}
