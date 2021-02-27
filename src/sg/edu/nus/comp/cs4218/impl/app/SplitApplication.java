package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.SplitInterface;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.SplitException;
import sg.edu.nus.comp.cs4218.impl.parser.SplitArgsParser;

import java.io.InputStream;
import java.io.OutputStream;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class SplitApplication implements SplitInterface {

    public int splitSize;

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

        boolean isSplitByBytes = parser.isSplitByBytes();
        boolean isSplitByLines = parser.isSplitByLines();
        boolean prefixPresent = parser.prefixPresent();

        splitSize = parser.getSplitSize();

        if (parser.fileInput()) {
            System.out.println("file input");
        } else {
            System.out.println("standard input");
        }
    }

    @Override
    public void splitFileByLines(String fileName, String prefix, int linesPerFile) throws Exception {

    }

    @Override
    public void splitFileByBytes(String fileName, String prefix, String bytesPerFile) throws Exception {

    }

    @Override
    public void splitStdinByLines(InputStream stdin, String prefix, int linesPerFile) throws Exception {

    }

    @Override
    public void splitStdinByBytes(InputStream stdin, String prefix, String bytesPerFile) throws Exception {

    }
}
