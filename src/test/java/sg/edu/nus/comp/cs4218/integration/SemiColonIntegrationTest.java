package sg.edu.nus.comp.cs4218.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.EnvironmentUtil;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class SemiColonIntegrationTest {
    private static Shell testShell;
    private static final String ROOT = EnvironmentUtil.currentDirectory;
    private static ByteArrayOutputStream outputStream;

    @AfterAll
    static void resetAll() {
        EnvironmentUtil.currentDirectory = ROOT;
    }

    @Test
    void evaluate_TwoCommands_DisplaysBothResults() throws AbstractApplicationException, ShellException {
        testShell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();

        String command1 = String.format(
                "cd src%1$stest%1$sresources%1$simpl%1$sapp%1$sCpApplicationResources%1$svalid_file; ls",
                File.separator);

        testShell.parseAndEvaluate(command1, outputStream);

        String expectedOutput = "destinationFolder\n" + "sourceFolder\n";
        expectedOutput = expectedOutput.replace("\n", STRING_NEWLINE);
        expectedOutput = expectedOutput.replace("\\\\", StringUtils.fileSeparator());

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void evaluate_TwoCommandsFirstException_DisplaysExceptionAndSecondResult() throws AbstractApplicationException, ShellException {
        testShell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();

        String command = String.format("cd randomNonExistentFolder; " +
                "ls src%1$stest%1$sresources%1$simpl%1$sapp%1$sCpApplicationResources%1$svalid_file", File.separator);

        testShell.parseAndEvaluate(command, outputStream);

        String expectedOutput =
                String.format("cd: No such file or directory%2$s" +
                        "src%1$stest%1$sresources%1$simpl%1$sapp%1$sCpApplicationResources%1$svalid_file:%2$s" +
                        "destinationFolder%2$s" + "sourceFolder%2$s", File.separator, System.lineSeparator());

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void evaluate_TwoCommands_DisplaysFirstAndSecondException() throws AbstractApplicationException, ShellException {
        testShell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();

        String command1 = "unimplementedCommand; unimplementedCommand";
        testShell.parseAndEvaluate(command1, outputStream);

        String expectedOutput =
                String.format("shell: unimplementedCommand: Invalid sg.edu.nus.comp.cs4218.app%1$s" +
                        "shell: unimplementedCommand: Invalid sg.edu.nus.comp.cs4218.app%1$s", System.lineSeparator());

        assertEquals(expectedOutput, outputStream.toString());
    }
}
