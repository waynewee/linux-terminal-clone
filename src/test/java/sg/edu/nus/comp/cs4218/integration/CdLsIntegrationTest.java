package sg.edu.nus.comp.cs4218.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class CdLsIntegrationTest {

    private static final String PATH_TO_LS_RESOURCES = "src/test/resources/impl/app/LsApplicationResources/";
    private static final String ROOT = Environment.currentDirectory;
    private static ShellImpl testShell;
    private static ByteArrayOutputStream outputStream;

    @BeforeAll
    static void setupShell() {
        testShell = new ShellImpl();
    }

    @BeforeEach
    void reset() {
        Environment.currentDirectory = ROOT;
        outputStream = new ByteArrayOutputStream();
    }

    @AfterAll
    static void resetAll() {
        Environment.currentDirectory = ROOT;
    }

    @Test
    public void parseAndEvaluate_CdThenLs_ListOutputCorrectly() {
        final String command1 = "cd " + PATH_TO_LS_RESOURCES + " \n";
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            testShell.parseAndEvaluate(command1, outputStream);
        });

        final String command2 = "ls\n";
        String expected = "test_folders_sort\n" +
                "test_folders_sort_recursive\n" +
                "test_number_of_files\n" +
                "test_number_of_folders\n" +
                "test_order_files_sort\n" +
                "test_recursive\n" +
                "test_recursive_directories\n" +
                "test_recursive_sort\n";

        expected = expected.replace("\n", StringUtils.STRING_NEWLINE);
        expected = expected.replace("\\\\", StringUtils.fileSeparator());

        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            testShell.parseAndEvaluate(command2, outputStream);
        });
        assertEquals(expected, outputStream.toString());
    }

    @Test
    public void parseAndEvaluate_CdThenLsD_ListOutputCorrectly() {
        final String command1 = "cd " + PATH_TO_LS_RESOURCES + "\n";
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            testShell.parseAndEvaluate(command1, outputStream);
        });

        final String command2 = "ls -d\n";
        String expected = "test_folders_sort\n" +
                "test_folders_sort_recursive\n" +
                "test_number_of_files\n" +
                "test_number_of_folders\n" +
                "test_order_files_sort\n" +
                "test_recursive\n" +
                "test_recursive_directories\n" +
                "test_recursive_sort\n";

        expected = expected.replace("\n", StringUtils.STRING_NEWLINE);
        expected = expected.replace("\\\\", StringUtils.fileSeparator());

        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            testShell.parseAndEvaluate(command2, outputStream);
        });
        assertEquals(expected, outputStream.toString());
    }

    @Test
    public void parseAndEvaluate_CdThenLsR_ListOutputCorrectly() {
        final String command1 = "cd " + PATH_TO_LS_RESOURCES + " \n";
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            testShell.parseAndEvaluate(command1, outputStream);
        });

        final String command2 = "ls -R test_recursive";
        String expected = "test_recursive:\n" +
                "answer.txt\n" +
                "space1\n" +
                "space2\n" +
                "space3\n" +
                "\n" +
                "test_recursive\\space1:\n" +
                "space1doc1.txt\n" +
                "space1doc2.txt\n" +
                "space1doc3.txt\n" +
                "space1space1\n" +
                "\n" +
                "test_recursive\\space1\\space1space1:\n" +
                "space1space1doc1.txt\n" +
                "\n" +
                "test_recursive\\space2:\n" +
                "space2doc1.txt\n" +
                "space2doc2.txt\n" +
                "\n" +
                "test_recursive\\space3:\n" +
                "space3doc1.txt\n";

        expected = expected.replace("\n", StringUtils.STRING_NEWLINE);
        expected = expected.replace("\\\\", StringUtils.fileSeparator());

        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            testShell.parseAndEvaluate(command2, outputStream);
        });
        assertEquals(expected, outputStream.toString());
    }

    @Test
    public void parseAndEvaluate_CdThenLsX_ListOutputCorrectly() {
        final String command1 = "cd " + PATH_TO_LS_RESOURCES + " \n";
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            testShell.parseAndEvaluate(command1, outputStream);
        });

        final String command2 = "ls -X test_folders_sort_recursive";
        String expected = "test_folders_sort_recursive:\n" +
                "folder1\n" +
                "folder2\n" +
                "folder3\n" +
                "guys\n";

        expected = expected.replace("\n", StringUtils.STRING_NEWLINE);
        expected = expected.replace("\\\\", StringUtils.fileSeparator());

        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            testShell.parseAndEvaluate(command2, outputStream);
        });
        assertEquals(expected, outputStream.toString());
    }

    @Test
    public void parseAndEvaluate_cdToNonExistentPathThenLs_shouldNotThrowErrors() throws AbstractApplicationException, ShellException {
        final String cdToNonExistentPath = "cantTouchMe";
        assertThrows(Exception.class, () -> testShell.parseAndEvaluate(cdToNonExistentPath, outputStream));
        final String secondLsCommand = "ls -R";
        assertDoesNotThrow(() -> testShell.parseAndEvaluate(secondLsCommand, outputStream));
    }
}
