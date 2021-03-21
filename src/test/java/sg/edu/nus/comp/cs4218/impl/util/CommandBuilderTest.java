package sg.edu.nus.comp.cs4218.impl.util;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.List;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.SequenceCommand;
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

    @Test
    public void parseCommand_backquotedSubCommandWithQuotes_isOneArgument() throws ShellException {
        Command command = CommandBuilder.parseCommand("echo `echo \"'this is not special'\"`", new ApplicationRunner());
        CallCommand callCommand = (CallCommand) command;
        List<String> argsList = callCommand.getArgsList();
        assertEquals("`echo \"'this is not special'\"`", argsList.get(1));
    }

    @Test
    /**
     * In typical UNIX systems, a new line will be prompted but our shell does not support multiline commands
     */
    public void parseCommand_incompleteBackquote_throwsShellException() throws ShellException {
        assertThrows(ShellException.class, () ->
                CommandBuilder.parseCommand("`echo hi", new ApplicationRunner()));
    }

    @Test
    public void parseCommand_backquotedSubCommand_isOneArgument() throws ShellException {
        Command command = CommandBuilder.parseCommand("echo `echo hi`", new ApplicationRunner());
        CallCommand callCommand = (CallCommand) command;
        List<String> argsList = callCommand.getArgsList();
        assertEquals("`echo hi`", argsList.get(1));
    }

    @Test
    public void parseCommand_oneSemiColon_makesTwoCommands() throws ShellException {
        Command command = CommandBuilder.parseCommand("echo hi; echo bye", new ApplicationRunner());
        SequenceCommand sequenceCommand = (SequenceCommand) command;
        assertEquals(2, sequenceCommand.getCommands().size());
    }

    @Test
    public void parseCommand_semiColonInSubcommand_makesOneCommand() throws ShellException {
        Command command = CommandBuilder.parseCommand("echo `echo hi; echo bye`", new ApplicationRunner());
        CallCommand callCommand = (CallCommand) command;
        assertEquals(2, callCommand.getArgsList().size());
    }

    @Test
    public void parseCommand_twoSemiColon_makesThreeCommands() throws ShellException {
        Command command = CommandBuilder.parseCommand("echo hi; echo bye; echo cat", new ApplicationRunner());
        SequenceCommand sequenceCommand = (SequenceCommand) command;
        assertEquals(3, sequenceCommand.getCommands().size());
    }
}