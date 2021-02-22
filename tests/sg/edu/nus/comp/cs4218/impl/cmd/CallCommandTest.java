package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_APP;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_SYNTAX;

class CallCommandTest {

    private static ByteArrayOutputStream outputStream;
    private static List<String> tokens;

    @BeforeEach
    // Prepare output stream
    public void prepareOutputStream() {
        outputStream = new ByteArrayOutputStream();
    }
    
    @BeforeEach
    // Prepare list of tokens
    public void prepareTokensList() {
        tokens = new LinkedList<>();
    }
    
    @Test
    public void evaluate_SimpleCommand_ReturnsOutput() throws AbstractApplicationException, ShellException {
        // Add 'ls' command as token
        tokens.add("ls");

        // Initialise callCommand
        CallCommand callCommand = new CallCommand(tokens, new ApplicationRunner(), new ArgumentResolver());

        // Prepare correct output
        boolean correctOutput;
        correctOutput = Objects.requireNonNull(new File(Environment.currentDirectory).listFiles()).length == 0;

        callCommand.evaluate(System.in, outputStream);
        assertEquals(correctOutput, outputStream.toString().isEmpty());
    }

    @Test
    public void evaluate_EmptyCommand_ThrowsShellException() {
        // Initialise callCommand
        CallCommand callCommand = new CallCommand(tokens, new ApplicationRunner(), new ArgumentResolver());
        // Assert right exception thrown
        ShellException shellException = assertThrows(ShellException.class, () -> callCommand.evaluate(System.in, outputStream));
        // Assert right message in exception
        assertEquals(new ShellException(ERR_SYNTAX).getMessage(), shellException.getMessage());
    }

    @Test
    public void evaluate_UnknownCommand_ThrowsShellException() {
        // Add unimplemented command as token
        tokens.add("move");
        tokens.add("file.txt");
        tokens.add("new_file.txt");

        // Initialise callCommand
        CallCommand callCommand = new CallCommand(tokens, new ApplicationRunner(), new ArgumentResolver());
        // Assert right exception thrown
        ShellException shellException = assertThrows(ShellException.class, () -> callCommand.evaluate(System.in, outputStream));
        // Assert right message in exception
        assertEquals(new ShellException(tokens.get(0) + ": " + ERR_INVALID_APP).getMessage(), shellException.getMessage());
    }

    @Test 
    public void getArgsList_ReturnsListInstance() {
        // Add unimplemented command as token
        tokens.add("command1");
        tokens.add("command2");
        tokens.add("command3");

        // Initialise callCommand
        CallCommand callCommand = new CallCommand(tokens, new ApplicationRunner(), new ArgumentResolver());
        // Assert return value is not null
        assert(callCommand.getArgsList() != null);
        // Assert object is instance of List
        assertTrue(callCommand.getArgsList() instanceof List);
    }

}