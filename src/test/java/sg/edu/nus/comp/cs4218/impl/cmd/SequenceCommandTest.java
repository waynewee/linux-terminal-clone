package sg.edu.nus.comp.cs4218.impl.cmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

public class SequenceCommandTest {

    @Test
    void evaluate_TwoCommands_DisplaysBothResults() throws AbstractApplicationException, ShellException {
        String expectedOutput = "destinationFolder\n" + "sourceFolder\n";
        expectedOutput = expectedOutput.replace("\n", STRING_NEWLINE);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        List<Command> commands = new ArrayList<>();

        List<String> argsList1 = new LinkedList<>();
        argsList1.add("cd");
        argsList1.add("src/test/resources/impl/app/CpApplicationResources/valid_file");
        commands.add(new CallCommand(argsList1, new ApplicationRunner(), new ArgumentResolver()));

        List<String> argsList2 = new LinkedList<>();
        argsList2.add("ls");
        commands.add(new CallCommand(argsList2, new ApplicationRunner(), new ArgumentResolver()));
        new SequenceCommand(commands).evaluate(System.in, output);
        assertEquals(expectedOutput, output.toString());
    }

    @Test
    void evaluate_TwoCommandsFirstException_DisplaysExceptionAndSecondResult() throws AbstractApplicationException, ShellException {
        String expectedOutput =
                "cd: No such file or directory\n" +
                "src\\test\\resources\\impl\\app\\CpApplicationResources\\valid_file:\n" +
                "destinationFolder\n" +
                "sourceFolder\n";

        expectedOutput = expectedOutput.replace("\n", STRING_NEWLINE);
        expectedOutput = expectedOutput.replace("\\\\", StringUtils.fileSeparator());

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        List<Command> commands = new ArrayList<>();

        List<String> argsList1 = new LinkedList<>();
        argsList1.add("cd");
        argsList1.add("randomNonExistentFolder");
        commands.add(new CallCommand(argsList1, new ApplicationRunner(), new ArgumentResolver()));

        List<String> argsList2 = new LinkedList<>();
        argsList2.add("ls");
        argsList2.add("src/test/resources/impl/app/CpApplicationResources/valid_file");
        commands.add(new CallCommand(argsList2, new ApplicationRunner(), new ArgumentResolver()));
        new SequenceCommand(commands).evaluate(System.in, output);
        assertEquals(expectedOutput, output.toString());
    }

        @Test
    void evaluate_TwoCommands_DisplaysFirstAndSecondException() throws AbstractApplicationException, ShellException {
        String expectedOutput =
                "shell: unimplementedCommand: Invalid sg.edu.nus.comp.cs4218.app\n" +
                "shell: unimplementedCommand: Invalid sg.edu.nus.comp.cs4218.app\n";

        expectedOutput = expectedOutput.replace("\n", STRING_NEWLINE);
        expectedOutput = expectedOutput.replace("\\\\", StringUtils.fileSeparator());

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        List<Command> commands = new ArrayList<>();

        List<String> argsList1 = new LinkedList<>();
        argsList1.add("unimplementedCommand");
        commands.add(new CallCommand(argsList1, new ApplicationRunner(), new ArgumentResolver()));

        List<String> argsList2 = new LinkedList<>();
        argsList2.add("unimplementedCommand");
        commands.add(new CallCommand(argsList2, new ApplicationRunner(), new ArgumentResolver()));
        new SequenceCommand(commands).evaluate(System.in, output);
        assertEquals(expectedOutput, output.toString());
    }
}
