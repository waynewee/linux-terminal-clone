package sg.edu.nus.comp.cs4218.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.exception.MvException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ShellImplTest {

    private static Shell testShell;

    @BeforeAll
    static void setupShell() {
        testShell = new ShellImpl();
    }

    // Reference for how to time out a test in JUnit5:
    // https://stackoverflow.com/questions/57116801/how-to-fail-a-test-after-a-timeout-is-exceeded-in-junit-5
    @Test
    public void testSingleInvalidCommand() throws AbstractApplicationException, ShellException, FileNotFoundException {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            ByteArrayOutputStream testStream = new ByteArrayOutputStream();
            // Expect Exception
            testShell.parseAndEvaluate("invalidCommand", testStream);
        });
    }

    @Test
    public void parseAndEvaluate_MvExistingSingleFile_Successful(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("test.txt");
        Path movedFile = dir.resolve("test1.txt");
        Files.createFile(file);
        String command = "mv " + file.toString() + " " + movedFile.toString() + "\n";
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            // Expect Exception
            testShell.parseAndEvaluate(command, System.out);
        });
        assertTrue(Files.exists(movedFile));
    }

    @Test
    public void parseAndEvaluate_MvExistingSingleFileToFolder_Successful(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("test.txt");
        Path subDir = dir.resolve("subdir");
        Path movedFile = subDir.resolve("test1.txt");
        Files.createFile(file);
        Files.createDirectory(subDir);

        String command = "mv " + file.toString() + " " + movedFile.toString() + "\n";
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            // Expect Exception
            testShell.parseAndEvaluate(command, System.out);
        });
        assertTrue(Files.exists(movedFile));
    }

    @Test
    public void parseAndEvaluate_MvExistingSingleFileToItself_Successful(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("test.txt");
        Files.createFile(file);
        String command = "mv " + file.toString() + " " + file.toString() + "\n";
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            // Expect Exception
            testShell.parseAndEvaluate(command, System.out);
        });
        assertTrue(Files.exists(file));
    }

    @Test
    public void parseAndEvaluate_MvExistingSingleFileToExistingFile_Successful(@TempDir Path dir) throws Exception {
        Path file1 = dir.resolve("test.txt");
        Path file2 = dir.resolve("test1.txt");
        Files.createFile(file1);
        Files.createFile(file2);
        String command = "mv " + file1.toString() + " " + file2.toString() + "\n";
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            // Expect Exception
            testShell.parseAndEvaluate(command, System.out);
        });
        assertFalse(Files.exists(file1));
        assertTrue(Files.exists(file2));
    }

    @Test
    public void parseAndEvaluate_MvExistingMultipleFile_Successful(@TempDir Path dir) throws Exception {
        Path file1 = dir.resolve("test1.txt");
        Path file2 = dir.resolve("test2.txt");
        Path subDir = dir.resolve("subdir");
        Path movedFile1 = subDir.resolve("test1.txt");
        Path movedFile2 = subDir.resolve("test2.txt");
        Files.createFile(file1);
        Files.createFile(file2);
        Files.createDirectory(subDir);

        String command = "mv " + file1.toString() + " " + file2.toString() + " " + subDir.toString() + "\n";
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            // Expect Exception
            testShell.parseAndEvaluate(command, System.out);
        });
        assertTrue(Files.exists(movedFile1));
        assertTrue(Files.exists(movedFile2));
    }

    @Test
    public void parseAndEvaluate_MvNonExistingFile_ThrowsException(){
        // This test uses relative path instead
        String command = "mv asfkjjafasf jkdkdhksdn\n";
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            // Expect Exception
            assertThrows(MvException.class, () -> {
                testShell.parseAndEvaluate(command, System.out);
            });
        });
    }

    @Test
    public void parseAndEvaluate_MvNoOverwriteOnExistingFile_ThrowsException(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("test.txt");
        Path file2 = dir.resolve("test1.txt");
        Files.createFile(file);
        Files.createFile(file2);
        String command = "mv -n " + file.toString() + " " + file2.toString() + "\n";
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            // Expect Exception
            assertThrows(MvException.class, () -> {
                testShell.parseAndEvaluate(command, System.out);
            });
        });
    }

    @Test
    public void parseAndEvaluate_MvNoOverwriteOnExistingFileInFolder_ThrowsException(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("test.txt");
        Path subDir = dir.resolve("subdir");
        Path file2 = subDir.resolve("test.txt");
        Files.createFile(file);
        Files.createDirectory(subDir);
        Files.createFile(file2);

        String command = "mv -n " + file.toString() + " " + file2.toString() + "\n";
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            // Expect Exception
            assertThrows(MvException.class, () -> {
                testShell.parseAndEvaluate(command, System.out);
            });
        });
    }
}