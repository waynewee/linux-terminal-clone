package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.SplitInterface;
import sg.edu.nus.comp.cs4218.exception.SplitException;
import sg.edu.nus.comp.cs4218.impl.parser.SplitArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
            if (isSplitByLines) {
                splitFileByLines(parser.fileName(), prefix, parser.getSplitSize());
            } else if (isSplitByBytes) {
                splitFileByBytes(parser.fileName(), prefix, String.valueOf(parser.getSplitSize()));
            }
        } else {
            if (isSplitByLines) {
                splitStdinByLines(stdin, prefix, parser.getSplitSize());
            } else if (isSplitByBytes) {
                splitStdinByBytes(stdin, prefix, String.valueOf(parser.getSplitSize()));
            }

        }
    }

    @Override
    public void splitFileByLines(String fileName, String prefix, int linesPerFile) throws Exception {
        File file = new File(fileName);
        String directoryPath = file.getParent();

        if (directoryPath == null) {
            directoryPath = "";
        }

        List<String> lines = Files.readAllLines(file.toPath());

        if (lines.isEmpty()) {
            return;
        }

        FileWriter fileWriter = new FileWriter(Paths.get(directoryPath, getOutputFileName()).toString());
        int counter = 0;
        for (String line: lines) {
            if (counter == linesPerFile) {
                counter = 0;
                fileWriter.close();
                fileWriter = new FileWriter(Paths.get(directoryPath, getOutputFileName()).toString());
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

        FileWriter fileWriter = new FileWriter(Paths.get(directoryPath, getOutputFileName()).toString());
        int counter = 0;
        for (Byte single_byte: lines) {
            if (counter == splitSize) {
                counter = 0;
                fileWriter.close();
                fileWriter = new FileWriter(Paths.get(directoryPath, getOutputFileName()).toString());
            }
            counter += 1;
            fileWriter.write(single_byte);
        }
        fileWriter.close();
        resetOutputFileName();
    }

    @Override
    public void splitStdinByLines(InputStream stdin, String prefix, int linesPerFile) throws Exception {
        Scanner sc = new Scanner(System.in);
        String directoryPath = Paths.get(Environment.currentDirectory).toString();
        FileWriter fileWriter = new FileWriter(Paths.get(directoryPath, getOutputFileName()).toString());

        int counter = 0;

        while (true) {
            if (counter == linesPerFile) {
                fileWriter.close();
                fileWriter = new FileWriter(Paths.get(directoryPath, getOutputFileName()).toString());
                counter = 0;
            } else {
                counter += 1;
                String temp;
                try {
                    temp = sc.nextLine();
                } catch (Exception e) {
                    break;
                }
                fileWriter.write(temp);
                fileWriter.write(StringUtils.STRING_NEWLINE);
            }
        }
        resetOutputFileName();
    }

    @Override
    public void splitStdinByBytes(InputStream stdin, String prefix, String bytesPerFile) throws Exception {
        FileWriter fileWriter;
        Scanner sc = new Scanner(System.in);
        String directoryPath = Paths.get(Environment.currentDirectory).toString();

        int splitSize = extractSplitSize(bytesPerFile);

        String inputString = "";
        while (true) {
            try {
                inputString += sc.nextLine();
            } catch (Exception e) {
                break;
            }

            byte[] byteArray = inputString.getBytes();
            while (byteArray.length >= splitSize) {
                byte[] temp = Arrays.copyOfRange(byteArray, 0, splitSize);
                byteArray = Arrays.copyOfRange(byteArray, splitSize, byteArray.length);

                fileWriter = new FileWriter(Paths.get(directoryPath, getOutputFileName()).toString());
                fileWriter.write(new String(temp));
                fileWriter.close();
            }
            inputString = new String(byteArray);
        }
        resetOutputFileName();
    }

    // Helper functions
    private String getOutputFileName() {
        asciiSecondLetter += 1;
        if (asciiSecondLetter == 123) {
            asciiFirstLetter += 1;
            asciiSecondLetter = 97;
        }

        if (asciiFirstLetter == 123) {
            prefix = "z";
            asciiFirstLetter = 97;
        }

        return prefix + (char) asciiFirstLetter + (char) asciiSecondLetter;
    }

    private void resetOutputFileName() {
        asciiFirstLetter = 97;
        asciiSecondLetter = 96;
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
}
