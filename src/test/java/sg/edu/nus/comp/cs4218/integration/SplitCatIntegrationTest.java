package sg.edu.nus.comp.cs4218.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static org.junit.jupiter.api.Assertions.*;

public class SplitCatIntegrationTest {

    private static Shell testShell;
    ByteArrayOutputStream outputStream;

    @AfterEach
    void removeOutputFiles() {
        Path path = Paths.get(Environment.currentDirectory, "src", "test", "resources", "impl", "app", "IntegrationResources", "SplitCatIntegration");
        File[] files = path.toFile().listFiles();
        for (File file: files) {
            if (!file.toString().endsWith(".txt")) {
                file.delete();
            }
        }
    }

    @Test
    public void parseAndEvaluate_SplitValidFileAndCatValidFile_OutputsCorrectResult() throws AbstractApplicationException, ShellException {
        testShell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();

        String command1 = "split -l 3 src/test/resources/impl/app/IntegrationResources/SplitCatIntegration/test1.txt";
        String command2 = "cat src/test/resources/impl/app/IntegrationResources/SplitCatIntegration/xaa";

        testShell.parseAndEvaluate(command1, outputStream);
        testShell.parseAndEvaluate(command2, outputStream);

        assertEquals("hey" + STRING_NEWLINE + "there" + STRING_NEWLINE + "dude" + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void parseAndEvaluate_SplitValidFileAndCatInValidFile_ThrowsException() throws AbstractApplicationException, ShellException {
        testShell = new ShellImpl();
        outputStream = new ByteArrayOutputStream();

        String command1 = "split -l 5 src/test/resources/impl/app/IntegrationResources/SplitCatIntegration/test1.txt";
        String command2 = "cat src/test/resources/impl/app/IntegrationResources/SplitCatIntegration/xad";

        testShell.parseAndEvaluate(command1, outputStream);
        testShell.parseAndEvaluate(command2, outputStream);

        assertEquals("cat: No such file or directory", outputStream.toString().trim());
    }

}
