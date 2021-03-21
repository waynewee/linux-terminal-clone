package sg.edu.nus.comp.cs4218.impl.app;
import sg.edu.nus.comp.cs4218.app.CpInterface;
import sg.edu.nus.comp.cs4218.exception.CpException;
import sg.edu.nus.comp.cs4218.impl.parser.CpArgsParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class CpApplication implements CpInterface {
    private CpArgsParser parser;

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws Exception {
        if (args == null) {
            throw new CpException(ERR_NULL_ARGS);
        }

        if (stdout == null) {
            throw new CpException(ERR_NO_OSTREAM);
        }

        parser = new CpArgsParser();
        try {
            parser.parse(args);
            parser.processArguments();
        } catch (Exception e) {
            throw new CpException(e.getMessage());
        }

        if (parser.destinationIsFolder) {
            cpFilesToFolder(parser.isRecursive(), parser.destination, parser.sourceFiles);
        } else {
            cpSrcFileToDestFile(parser.isRecursive(), parser.sourceFiles[0], parser.destination);
        }
    }

    @Override
    public String cpSrcFileToDestFile(Boolean isRecursive, String srcFile, String destFile) throws Exception {
        Files.copy(Paths.get(srcFile), Paths.get(destFile), StandardCopyOption.REPLACE_EXISTING);

        return destFile;
    }

    @Override
    public String cpFilesToFolder(Boolean isRecursive, String destFolder, String... fileName) throws Exception {
        if (fileName.length == 1) {
            String rawFileName = new File(fileName[0]).getName();
            if (Files.isDirectory(Paths.get(fileName[0]))) {
                if (isRecursive) {
                    Path src = Paths.get(fileName[0]);
                    Path dest = Paths.get(destFolder, rawFileName);
                    Files.walk(src)
                            .forEach(source -> {
                                try {
                                    Files.copy(source, dest.resolve(src.relativize(source)),
                                            StandardCopyOption.REPLACE_EXISTING);
                                } catch (IOException ignored) {}
                            });
                } else {
                    Files.copy(Paths.get(fileName[0]), Paths.get(destFolder, rawFileName));
                }
            } else {
                Files.copy(Paths.get(fileName[0]), Paths.get(destFolder, rawFileName));
            }
        } else {
            for (String file: fileName) {
                String rawFileName = new File(file).getName();
                Files.copy(Paths.get(file), Paths.get(destFolder, rawFileName));
            }
        }

        return destFolder;
    }
}
