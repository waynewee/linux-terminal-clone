package sg.edu.nus.comp.cs4218.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

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
        String expected = "test_folders_sort\r\n" +
                "test_folders_sort_recursive\r\n" +
                "test_number_of_files\r\n" +
                "test_number_of_folders\r\n" +
                "test_order_files_sort\r\n" +
                "test_recursive\r\n" +
                "test_recursive_directories\r\n" +
                "test_recursive_sort\r\n";

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
        String expected = "test_folders_sort\r\n" +
                "test_folders_sort_recursive\r\n" +
                "test_number_of_files\r\n" +
                "test_number_of_folders\r\n" +
                "test_order_files_sort\r\n" +
                "test_recursive\r\n" +
                "test_recursive_directories\r\n" +
                "test_recursive_sort\r\n";

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

        final String command2 = "ls -R test_recursive\n";
        String expected = "test_recursive:\r\n" +
                "answer.txt\r\n" +
                "vig1\r\n" +
                "vig2\r\n" +
                "vig3\r\n" +
                "\r\n" +
                "test_recursive\\vig1:\r\n" +
                "vig1doc1.txt\r\n" +
                "vig1doc2.txt\r\n" +
                "vig1doc3.txt\r\n" +
                "vig1vig1\r\n" +
                "\r\n" +
                "test_recursive\\vig1\\vig1vig1:\r\n" +
                "vig1vig1doc1.txt\r\n" +
                "\r\n" +
                "test_recursive\\vig2:\r\n" +
                "vig2doc1.txt\r\n" +
                "vig2doc2.txt\r\n" +
                "\r\n" +
                "test_recursive\\vig3:\r\n" +
                "vig3doc1.txt\r\n";

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

        final String command2 = "ls -X test_folders_sort_recursive\n";
        String expected = "test_folders_sort_recursive:\r\n" +
                "folder1\r\n" +
                "folder2\r\n" +
                "folder3\r\n" +
                "guys\r\n";

        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            testShell.parseAndEvaluate(command2, outputStream);
        });
        assertEquals(expected, outputStream.toString());
    }
}
