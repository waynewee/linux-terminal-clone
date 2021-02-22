package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CallCommandTest {

    @Test
    public void evaluate_SimpleCommand_ReturnsOutput() throws AbstractApplicationException, ShellException {
        // Prepare empty list of tokens
        List<String> tokens = new LinkedList<>();
        tokens.add("ls");
        // Initialise callCommand
        CallCommand callCommand = new CallCommand(tokens, new ApplicationRunner(), new ArgumentResolver());
        // Prepare output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


        callCommand.evaluate(System.in, outputStream);
        System.out.println(outputStream.toString());
        assertFalse(outputStream.toString().isEmpty());
    }


}