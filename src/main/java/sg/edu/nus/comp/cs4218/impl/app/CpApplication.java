package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CpInterface;
import sg.edu.nus.comp.cs4218.exception.CpException;
import sg.edu.nus.comp.cs4218.impl.parser.CpArgsParser;

import java.io.InputStream;
import java.io.OutputStream;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;

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
    }

    @Override
    public String cpSrcFileToDestFile(Boolean isRecursive, String srcFile, String destFile) throws Exception {
        return null;
    }

    @Override
    public String cpFilesToFolder(Boolean isRecursive, String destFolder, String... fileName) throws Exception {
        return null;
    }
}
