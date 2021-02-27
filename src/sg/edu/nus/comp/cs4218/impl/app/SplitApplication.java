package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.SplitInterface;
import sg.edu.nus.comp.cs4218.exception.SplitException;
import sg.edu.nus.comp.cs4218.impl.parser.SplitArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.lang.Integer.parseInt;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class SplitApplication implements SplitInterface {

    private SplitArgsParser parser;

    private int splitSize;
    private boolean isSplitByBytes;
    private boolean isSplitByLines;
    private String prefix;
    private String suffix;
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

        parser = new SplitArgsParser();

        try {
            parser.parse(args);
            parser.processArguments();
        } catch (Exception e) {
            throw new SplitException(e.getMessage());
        }

        isSplitByBytes = parser.isSplitByBytes();
        isSplitByLines = parser.isSplitByLines();
        splitSize = parser.getSplitSize();
        prefix = parser.getPrefix();
        suffix = parser.getSplitSuffix();

        if (parser.fileInput()) {
            System.out.println("file input");
            if (isSplitByLines) {
                splitFileByLines(parser.fileName(), prefix, parser.getSplitSize());
            } else if (isSplitByBytes) {
                splitFileByBytes(parser.fileName(), prefix, String.valueOf(parser.getSplitSize()));
            }
        } else {
            System.out.println("standard input");
            if (isSplitByLines) {
                splitStdinByLines(stdin, prefix, parser.getSplitSize());
            } else if (isSplitByBytes) {
                splitStdinByBytes(stdin, prefix, String.valueOf(parser.getSplitSize()));
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
        String directoryPath = file.getParent();

        if (directoryPath == null) {
            directoryPath = "";
        }

        List<String> lines = Files.readAllLines(file.toPath());

        if (lines.isEmpty()) {
            return;
        }

        FileWriter fileWriter = new FileWriter(Paths.get(directoryPath, prefix + getOutputFileName()).toString());
        int counter = 0;
        for (String line: lines) {
            if (counter == linesPerFile) {
                counter = 0;
                fileWriter.close();
                fileWriter = new FileWriter(Paths.get(directoryPath, prefix + getOutputFileName()).toString());
            }
            counter += 1;
            fileWriter.write(line);
            fileWriter.write(StringUtils.STRING_NEWLINE);
        }
        fileWriter.close();
        resetOutputFileName();
    }

    @Override
    public void splitFileByBytes(String fileName, String prefix, String bytesPerFile) throws Exception {
        System.out.println("splitFileByBytes");
        System.out.println(fileName);
        System.out.println(prefix);
        System.out.println(bytesPerFile);

        File file = new File(fileName);
        String directoryPath = file.getParent();

        if (directoryPath == null) {
            directoryPath = "";
        }


        int splitSize = extractSplitSize(bytesPerFile);
        byte[] lines = Files.readAllBytes(file.toPath());

        if (lines.length == 0) {
            return;
        }

        FileWriter fileWriter = new FileWriter(Paths.get(directoryPath, prefix + getOutputFileName()).toString());
        int counter = 0;
        for (Byte single_byte: lines) {
            if (counter == splitSize) {
                counter = 0;
                fileWriter.close();
                fileWriter = new FileWriter(Paths.get(directoryPath, prefix + getOutputFileName()).toString());
            }
            counter += 1;
            fileWriter.write(single_byte);
        }
        fileWriter.close();
        resetOutputFileName();
    }

    private int extractSplitSize(String bytesPerFile) {
        int splitSize = bytesPerFile.equals("") ? 0 : parseInt(bytesPerFile);

        if (suffix.equals("")) {
            return splitSize;
        }

        switch (parser.getSplitSuffix()) {
            case "b":
                splitSize *= 512;
                break;
            case "k":
                splitSize *= 1024;
                break;
            case "m":
                splitSize *= 1048576;
                break;

        }
        return splitSize;
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

    // Helper functions
    private String getOutputFileName() {
        asciiSecondLetter += 1;
        if (asciiSecondLetter == 124) {
            asciiFirstLetter += 1;
            asciiSecondLetter = 97;
        }

        if (asciiFirstLetter == 124) {
            parser.updatePrefix();
            asciiFirstLetter = 97;
        }

        return (char) asciiFirstLetter + Character.toString((char) asciiSecondLetter);
    }

    private void resetOutputFileName() {
        asciiFirstLetter = 97;
        asciiSecondLetter = 96;
    }
}
