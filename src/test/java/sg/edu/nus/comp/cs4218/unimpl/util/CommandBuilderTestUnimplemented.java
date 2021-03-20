package sg.edu.nus.comp.cs4218.unimpl.util;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.SequenceCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NOT_SUPPORTED;

class CommandBuilderTestUnimplemented {
    @Test
    public void parseCommand_oneSemiColon_makesTwoCommands() throws ShellException {
        Command command = CommandBuilder.parseCommand("echo hi; echo bye", new ApplicationRunner());
        SequenceCommand sequenceCommand = (SequenceCommand) command;
        assertEquals(2, sequenceCommand.getCommands().size());
    }

    @Test
    public void parseCommand_oneTerminatingSemiColon_makesOneCommand() throws ShellException {
        Command command = CommandBuilder.parseCommand("echo hi;", new ApplicationRunner());
        SequenceCommand sequenceCommand = (SequenceCommand) command;
        assertEquals(1, sequenceCommand.getCommands().size());
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