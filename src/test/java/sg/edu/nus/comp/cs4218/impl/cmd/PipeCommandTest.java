package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PipeCommandTest {
    private static ByteArrayOutputStream outputStream;
    private static PipeCommand pipeCommand;
    private static List<CallCommand> callCommands;

    @BeforeEach
    // Prepare output stream
    public void prepareOutputStream() {
        outputStream = new ByteArrayOutputStream();
    }

    @BeforeEach
    // Prepare list of tokens
    public void prepareCallCommandList() {
        callCommands = new LinkedList<>();
    }

    @Test
    void evaluate_KnownCommandPipeKnownCommand_ProducesCorrectOutput() throws AbstractApplicationException, ShellException {
        // Expected output
        String expectedOutput =
                "src" + File.separator + "test" + File.separator + "resources" + File.separator + "impl"
                        + File.separator + "app" + File.separator + "PipeCommandResources:" + StringUtils.STRING_NEWLINE
                        + "test1.txt" + StringUtils.STRING_NEWLINE + "testtest2.txt" + StringUtils.STRING_NEWLINE;

        // Add 'ls' command as token
        List<String> tokens1 = new LinkedList<>();
        Path testsResourcesDir = Paths.get("src", "test", "resources", "impl", "app", "PipeCommandResources");
        tokens1.add("ls");
        tokens1.add(Paths.get(Environment.currentDirectory, testsResourcesDir.toString()).toString());

        // Initialise callCommand
        CallCommand callCommand1 = new CallCommand(tokens1, new ApplicationRunner(), new ArgumentResolver());

        // Add 'ls' command as token
        List<String> tokens2 = new LinkedList<>();
        tokens2.add("grep");
        tokens2.add("test");
        // Initialise callCommand
        CallCommand callCommand2 = new CallCommand(tokens2, new ApplicationRunner(), new ArgumentResolver());

        callCommands.add(callCommand1);
        callCommands.add(callCommand2);

        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void evaluate_KnownCommandPipeKnownCommandPipeKnownCommand_ProducesCorrectOutput() throws AbstractApplicationException, ShellException {
        // Expected output
        String expectedOutput =
                "this is hopefully a nice 2 test\n";
        expectedOutput = expectedOutput.replace("\n", StringUtils.STRING_NEWLINE);

        // Add 'cat' command as token
        List<String> tokens1 = new LinkedList<>();
        Path testsResourcesDir = Paths.get("src", "test", "resources", "impl", "app", "PipeCommandResources");
        tokens1.add("cat");
        tokens1.add(Paths.get(Environment.currentDirectory, testsResourcesDir.toString(), "git1.txt").toString());
        // Initialise 'grep' command as token
        CallCommand callCommand1 = new CallCommand(tokens1, new ApplicationRunner(), new ArgumentResolver());

        // Add 'grep' command as token
        List<String> tokens2 = new LinkedList<>();
        tokens2.add("grep");
        tokens2.add("nice");
        // Initialise callCommand2
        CallCommand callCommand2 = new CallCommand(tokens2, new ApplicationRunner(), new ArgumentResolver());

        // Add 'grep' command as token
        List<String> tokens3 = new LinkedList<>();
        tokens3.add("grep");
        tokens3.add("2");
        // Initialise callCommand2
        CallCommand callCommand3 = new CallCommand(tokens3, new ApplicationRunner(), new ArgumentResolver());

        callCommands.add(callCommand1);
        callCommands.add(callCommand2);
        callCommands.add(callCommand3);

        PipeCommand pipeCommand = new PipeCommand(callCommands);
        pipeCommand.evaluate(System.in, outputStream);
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void evaluate_UnknownCommandPipeKnownCommand_ThrowsExceptionBeforeProcessingSecondCommand() {
        // Add 'lsa' command as token
        List<String> tokens1 = new LinkedList<>();
        tokens1.add("lsa");
        // Initialise callCommand
        CallCommand callCommand1 = new CallCommand(tokens1, new ApplicationRunner(), new ArgumentResolver());

        // Add 'ls' command as token
        List<String> tokens2 = new LinkedList<>();
        tokens2.add("ls");
        // Initialise callCommand
        CallCommand callCommand2 = new CallCommand(tokens2, new ApplicationRunner(), new ArgumentResolver());

        callCommands.add(callCommand1);
        callCommands.add(callCommand2);

        PipeCommand pipeCommand = new PipeCommand(callCommands);

        // Assert right exception thrown
        assertThrows(ShellException.class, () -> pipeCommand.evaluate(System.in, outputStream));
    }

    @Test
    void evaluate_KnownCommandPipeUnknownCommand_ThrowsException() {
        // Add 'lsa' command as token
        List<String> tokens1 = new LinkedList<>();
        tokens1.add("ls");
        // Initialise callCommand
        CallCommand callCommand1 = new CallCommand(tokens1, new ApplicationRunner(), new ArgumentResolver());

        // Add 'ls' command as token
        List<String> tokens2 = new LinkedList<>();
        tokens2.add("lsa");
        // Initialise callCommand
        CallCommand callCommand2 = new CallCommand(tokens2, new ApplicationRunner(), new ArgumentResolver());

        callCommands.add(callCommand1);
        callCommands.add(callCommand2);

        PipeCommand pipeCommand = new PipeCommand(callCommands);

        // Assert right exception thrown
        assertThrows(ShellException.class, () -> pipeCommand.evaluate(System.in, outputStream));
    }

}