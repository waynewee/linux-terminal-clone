package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.LsException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class LsApplicationTest {

    private static ByteArrayOutputStream outputStream;
    private static Application lsApplication;

    @BeforeAll
    static void prepareApplication() {
        lsApplication = new LsApplication();
    }

    @BeforeEach
    void prepareOutputStream() {
        outputStream = new ByteArrayOutputStream();
    }

    // ls
    @Test
    public void run_SingleTokenLsCommand_ListsCorrectNumberOfFiles() throws AbstractApplicationException {
        // Prepare correct output
        int correctOutput;
        correctOutput = Objects.requireNonNull(new File(Environment.currentDirectory).listFiles()).length;

        lsApplication.run(new String[0], System.in, outputStream);
        assertEquals(correctOutput, outputStream.toString().split("[\n|\r]").length);

    }

    // ls -d
    @Test
    public void run_LsCommandWithDirectoriesOption_ListsCorrectNumberOfDirectories() throws AbstractApplicationException {
        // Prepare correct output
        File[] files = new File(Environment.currentDirectory).listFiles(File::isDirectory);

        // Prepare args
        String[] args = new String[1];
        args[0] = "-d";

        lsApplication.run(new String[0], System.in, outputStream);
        assertEquals(files.length, outputStream.toString().split("[\n|\r]").length);

    }

    // ls -a
    @Test
    public void run_GivenUnknownArgsWithHyphen_ThrowsLsException() {
        String[] args = new String[1];
        args[0] = "-a";
        // Assert right exception thrown
        LsException lsException = assertThrows(LsException.class, () -> lsApplication.run(args, System.in, outputStream));
        // Assert right message in exception
        InvalidArgsException invalidArgsException = new InvalidArgsException(ILLEGAL_FLAG_MSG + "a");
        assertEquals("ls: " + invalidArgsException.getMessage(), lsException.getMessage());
    }

    // ls -b -h -e -g -f
    @Test
    public void run_GivenUnknownMultipleArgsWithHyphen_ThrowsLsException() {
        String[] args = new String[5];
        args[0] = "-b";
        args[1] = "-h";
        args[2] = "-e";
        args[3] = "-g";
        args[4] = "-f";

        // Assert right exception thrown
        LsException lsException = assertThrows(LsException.class, () -> lsApplication.run(args, System.in, outputStream));
        // Assert right message in exception
        InvalidArgsException invalidArgsException = new InvalidArgsException(ILLEGAL_FLAG_MSG + "b");
        assertEquals("ls: " + invalidArgsException.getMessage(), lsException.getMessage());
    }

    @Test
    public void run_GivenNullForArgsParameter_ThrowsLsException() {
        // Assert right exception thrown
        LsException lsException = assertThrows(LsException.class, () -> lsApplication.run(null, System.in, outputStream));
        // Assert right message in exception
        assertEquals(new LsException(ERR_NULL_ARGS).getMessage(), lsException.getMessage());
    }

    @Test
    public void run_GivenNullForOutputStreamParameter_ThrowsLsException() {
        // Assert right exception thrown
        LsException lsException = assertThrows(LsException.class, () -> lsApplication.run(new String[0], System.in, null));
        // Assert right message in exception
        assertEquals(new LsException(ERR_NO_OSTREAM).getMessage(), lsException.getMessage());
    }

}