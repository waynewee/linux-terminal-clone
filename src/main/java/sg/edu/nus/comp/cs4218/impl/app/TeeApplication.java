package sg.edu.nus.comp.cs4218.impl.app;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import sg.edu.nus.comp.cs4218.app.TeeInterface;
import sg.edu.nus.comp.cs4218.exception.TeeException;
import sg.edu.nus.comp.cs4218.impl.app.args.TeeArguments;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;

public class TeeApplication implements TeeInterface {
    private static OutputStream outputStream = System.out;

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws Exception {
        outputStream = stdout;

        TeeArguments teeArgs = new TeeArguments();
        teeArgs.parse(args);
        String output = teeFromStdin(teeArgs.isAppend(), stdin, teeArgs.getFiles());
    }

    @Override
    public String teeFromStdin(Boolean isAppend, InputStream stdin, String... fileName) throws Exception {
        // stream validations
        if (stdin == null) {
            throw new TeeException(ERR_NULL_STREAMS);
        }
        if (outputStream == null) {
            throw new TeeException(ERR_NULL_STREAMS);
        }

        List<FileWriter> fileWriters = new ArrayList<FileWriter>();
        for (String filePath: fileName) {
            File file = new File(filePath);
            file.createNewFile(); // doesn't matter if file does not exist

            FileWriter fileWriter = new FileWriter(file, isAppend);
            fileWriters.add(fileWriter);
        }
        Scanner scanner = new Scanner(stdin);
        List<String> outputLines = new ArrayList<String>();
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            line = line.trim() + System.lineSeparator();

            outputLines.add(line);
            editFiles(fileWriters, line, isAppend);

            outputStream.write(line.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
        flushFiles(fileWriters);

        return String.join("", outputLines);
    }

    private void editFiles(List<FileWriter> fileWriters, String content, boolean isAppend) throws IOException {
        for (FileWriter fileWriter : fileWriters) {
            editFile(fileWriter, content, isAppend);
        }
    }

    private void editFile(FileWriter fileWriter, String content, boolean isAppend) throws IOException {
        if (isAppend) {
            fileWriter.append(content);
        } else {
            fileWriter.write(content);
        }
    }

    private void flushFiles(List<FileWriter> fileWriters) throws IOException {
        for (FileWriter fileWriter : fileWriters) {
            fileWriter.flush();
            fileWriter.close();
        }
    }

}
