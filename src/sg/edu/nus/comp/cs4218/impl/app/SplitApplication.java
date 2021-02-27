package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.SplitInterface;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.SplitException;
import sg.edu.nus.comp.cs4218.impl.parser.SplitArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class SplitApplication implements SplitInterface {

    private SplitArgsParser parser;

    private int splitSize;
    private boolean isSplitByBytes;
    private boolean isSplitByLines;
    private String prefix;
    private int asciiFirstLetter = 97;
    private int asciiSecondLetter = 96;

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws Exception {
        if (args == null) {
            throw new SplitException(ERR_NULL_ARGS);
        }

        if (stdout == null) {
            throw new SplitException(ERR_NO_OSTREAM);
        }

        SplitArgsParser parser = new SplitArgsParser();

        try {
            parser.parse(args);
            parser.processArguments();
        } catch (InvalidArgsException e) {
            throw new SplitException(e.getMessage());
        }

        isSplitByBytes = parser.isSplitByBytes();
        isSplitByLines = parser.isSplitByLines();
        splitSize = parser.getSplitSize();
        prefix = parser.getPrefix();

        if (parser.fileInput()) {
            System.out.println("file input");
            if (isSplitByLines) {
                splitFileByLines(parser.fileName(), prefix, parser.getSplitSize());
            } else if (isSplitByBytes) {
                splitFileByBytes(parser.fileName(), prefix, parser.getSplitSize() + parser.getSplitSuffix());
            }
        } else {
            System.out.println("standard input");
            if (isSplitByLines) {
                splitStdinByLines(stdin, prefix, parser.getSplitSize());
            } else if (isSplitByBytes) {
                splitStdinByBytes(stdin, prefix, parser.getSplitSize() + parser.getSplitSuffix());
            }

        }
    }

    @Override
    public void splitFileByLines(String fileName, String prefix, int linesPerFile) throws Exception {
        System.out.println("splitFileByLines");
        System.out.println(fileName);
        System.out.println(prefix);
        System.out.println(linesPerFile);

        File file = new File(fileName);
        List<String> lines = Files.readAllLines(file.toPath());

        if (lines.isEmpty()) {
            return;
        }

        FileWriter fileWriter = new FileWriter(Paths.get(Environment.currentDirectory, prefix + getOutputFileName()).toString());
        int counter = 0;
        for (String line: lines) {
            if (counter == linesPerFile) {
                counter = 0;
                fileWriter.close();
                fileWriter = new FileWriter(Paths.get(Environment.currentDirectory, prefix + getOutputFileName()).toString());
            }
            counter += 1;
            fileWriter.write(line);
            fileWriter.write(StringUtils.STRING_NEWLINE);
        }
        fileWriter.close();
    }

    private String getOutputFileName() {
        asciiSecondLetter += 1;
        if (asciiSecondLetter == 124) {
            asciiFirstLetter += 1;
            asciiSecondLetter = 96;
        }

        if (asciiFirstLetter == 124) {
            parser.updatePrefix();
            asciiFirstLetter = 97;
        }

        return (char) asciiFirstLetter + Character.toString((char) asciiSecondLetter);
    }

    @Override
    public void splitFileByBytes(String fileName, String prefix, String bytesPerFile) throws Exception {
        System.out.println("splitFileByBytes");
        System.out.println(fileName);
        System.out.println(prefix);
        System.out.println(bytesPerFile);

    }

    @Override
    public void splitStdinByLines(InputStream stdin, String prefix, int linesPerFile) throws Exception {
        System.out.println("splitStdinByLines");
        System.out.println(prefix);
        System.out.println(linesPerFile);

    }

    @Override
    public void splitStdinByBytes(InputStream stdin, String prefix, String bytesPerFile) throws Exception {
        System.out.println("splitStdinByBytes");
        System.out.println(prefix);
        System.out.println(bytesPerFile);
    }
}
