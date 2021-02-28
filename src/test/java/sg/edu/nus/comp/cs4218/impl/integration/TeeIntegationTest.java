package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TeeIntegationTest {
    private static Shell testShell;
    private static OutputStream mockOutputStream;
    private static final String tempFilePath = "temp.txt";
    private static final String existingFilePath = "existing.txt";
    private static final String anotherExistingFilePath = "anotherExisting.txt";
    private static final String nonExistentFilePath = "nonExistent.txt";
    private static File tempFile;
    private static File existingFile;
    private static File anotherExistingFile;
    private static File nonExistentFile;

    private static final String expectedString = "hello";

    @BeforeAll
    static void setupShell() {
        testShell = new ShellImpl();
    }

    @BeforeEach
    void setupEach() throws IOException {
        tempFile = new File(tempFilePath);
        existingFile = new File(existingFilePath);
        anotherExistingFile = new File(anotherExistingFilePath);
        nonExistentFile = new File(nonExistentFilePath);

        FileWriter existingFileWriter = new FileWriter(existingFile, false);
        existingFileWriter.write(expectedString);
        existingFileWriter.close();

        FileWriter anotherFileWriter = new FileWriter(anotherExistingFile, false);
        anotherFileWriter.write(expectedString);
        anotherFileWriter.close();

        nonExistentFile.delete();

        mockOutputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void teardownEach() {
        if (tempFile != null) {
            tempFile.delete();
        }
        if (existingFile != null) {
            existingFile.delete();
        }
        if (anotherExistingFile != null) {
            anotherExistingFile.delete();
        }
        if (nonExistentFile != null) {
            nonExistentFile.delete();
        }
    }


    // Tee + Pipe + Cat Integration tests
    @Test
    void tee_pipeFromCat_fileGetsWritten() throws AbstractApplicationException, ShellException, IOException {
        testShell.parseAndEvaluate(String.format("cat %s | tee %s", existingFilePath, tempFilePath), mockOutputStream);
        assertEquals(expectedString + System.lineSeparator(), Files.readString(Path.of(tempFilePath)));
    }

    @Test
    void tee_writeInputRedirectionFromSourceFile_targetFileWritten()
            throws AbstractApplicationException, ShellException, IOException {
        testShell.parseAndEvaluate(String.format("tee %s < %s",
                tempFilePath, existingFilePath), mockOutputStream);
        assertEquals(expectedString + System.lineSeparator(), Files.readString(Path.of(tempFilePath)));
    }

    @Test
    void tee_appendInputRedirectionFromSourceFile_targetFileAppended()
            throws AbstractApplicationException, ShellException, IOException {
        testShell.parseAndEvaluate(String.format("tee -a %s < %s",
                anotherExistingFilePath, existingFilePath), mockOutputStream);
        assertEquals(expectedString + expectedString + System.lineSeparator(),
                Files.readString(Path.of(anotherExistingFilePath)));
    }

    @Test
    void tee_appendInputRedirectionFromNonExistentSourceFile_throwsException() {
        assertThrows(ShellException.class, () -> testShell.parseAndEvaluate(String.format("tee -a %s < %s",
                existingFilePath, nonExistentFilePath), mockOutputStream));
    }
}
