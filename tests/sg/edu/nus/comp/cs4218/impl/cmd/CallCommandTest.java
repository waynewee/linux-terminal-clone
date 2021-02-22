package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

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
    public void getArgsList_ReturnsNonNullInstance() {
        // Initialise callCommand
        CallCommand callCommand = new CallCommand(tokens, new ApplicationRunner(), new ArgumentResolver());

        assert (callCommand.getArgsList() != null);
    }

}