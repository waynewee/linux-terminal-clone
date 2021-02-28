package sg.edu.nus.comp.cs4218.impl.util;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.List;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CommandBuilderTest {

    @Test
    public void parseCommand_unquotedArg_isActuallyTwoArgs() throws ShellException {
        Command command = CommandBuilder.parseCommand("echo hello world", new ApplicationRunner());
        CallCommand callCommand = (CallCommand) command;
        List<String> argsList = callCommand.getArgsList();
        assertEquals(argsList.size(), 3);
    }

    @Test
    public void parseCommand_singleQuotedArg_isOneArg() throws ShellException {
        Command command = CommandBuilder.parseCommand("echo 'hello world'", new ApplicationRunner());
        CallCommand callCommand = (CallCommand) command;
        List<String> argsList = callCommand.getArgsList();
        assertEquals(argsList.size(), 2);
    }

    @Test
    public void parseCommand_doubleQuotedArg_isOneArg() throws ShellException {
        Command command = CommandBuilder.parseCommand("echo \"hello world\"", new ApplicationRunner());
        CallCommand callCommand = (CallCommand) command;
        List<String> argsList = callCommand.getArgsList();
        assertEquals(argsList.size(), 2);
    }

    @Test
    public void parseCommand_backQuotedArg_isOneArg() throws ShellException {
        Command command = CommandBuilder.parseCommand("echo `echo hello`", new ApplicationRunner());
        CallCommand callCommand = (CallCommand) command;
        List<String> argsList = callCommand.getArgsList();
        assertEquals(argsList.size(), 2);
    }

    @Test
    public void parseCommand_inputRedirection_isAnArgument() throws ShellException {
        Command command = CommandBuilder.parseCommand("paste < siaoEh.txt", new ApplicationRunner());
        CallCommand callCommand = (CallCommand) command;
        List<String> argsList = callCommand.getArgsList();
        assertEquals(argsList.get(1), "<");
    }

    @Test
    public void parseCommand_outputRedirection_isAnArgument() throws ShellException {
        Command command = CommandBuilder.parseCommand("echo chickenBurger > fishmonger.txt", new ApplicationRunner());
        CallCommand callCommand = (CallCommand) command;
        List<String> argsList = callCommand.getArgsList();
        assertEquals(argsList.get(2), ">");
    }
}