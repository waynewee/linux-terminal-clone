package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// TODO: assert for output stream as well
class TeeApplicationTest {
    private static TeeApplication teeApplication;
    private static String emptyFilePath = "empty.txt";
    private static String existingFilePath = "existing.txt";
    private static String anotherExistingFilePath = "anotherExisting.txt";
    private static String nonExistentFilePath = "nonExistent.txt";
    private static File emptyFile;
    private static File existingFile;
    private static File anotherExistingFile;
    private static File nonExistentFile;

    static InputStream streamFromString(String initialString) {
        InputStream inputStream = new ByteArrayInputStream(initialString.getBytes());
        return inputStream;
    };

    @BeforeAll
    static void setup() {
        teeApplication = new TeeApplication();
    }

    @BeforeEach
    void setupBeforeTest() throws IOException {
        emptyFile = new File(emptyFilePath);
        emptyFile.createNewFile();

        existingFile = new File(existingFilePath);
        existingFile.createNewFile();
        FileWriter fileWriter = new FileWriter(existingFile);
        fileWriter.write("hello");
        fileWriter.close();

        anotherExistingFile = new File(anotherExistingFilePath);
        anotherExistingFile.createNewFile();
        FileWriter anotherFileWriter = new FileWriter(anotherExistingFile);
        anotherFileWriter.write("hello again");
        anotherFileWriter.close();

        nonExistentFile = new File(nonExistentFilePath);
    }

    @AfterEach
    void tearDownAfterTest() throws IOException {
        emptyFile.delete();
        existingFile.delete();
        anotherExistingFile.delete();
        nonExistentFile.delete();
    }

    @Test
    void teeFromStdin_appendEmptyFile_stringGetsWritten() throws Exception {
        InputStream inputStream = streamFromString("hello");
        String[] fileNames = {emptyFilePath};
        teeApplication.teeFromStdin(true, inputStream, fileNames);
        assertEquals("hello" + System.lineSeparator(), Files.readString(Path.of(emptyFilePath)));
    }

    @Test
    void teeFromStdin_writeEmptyFile_stringGetsWritten() throws Exception {
        InputStream inputStream = streamFromString("hello");
        String[] fileNames = {emptyFilePath};
        teeApplication.teeFromStdin(false, inputStream, fileNames);
        assertEquals("hello" + System.lineSeparator(), Files.readString(Path.of(emptyFilePath)));
    }

    @Test
    void teeFromStdin_appendExistingFile_allStringsExist() throws Exception {
        InputStream inputStream = streamFromString("helloAgain");
        String[] fileNames = {existingFilePath};
        teeApplication.teeFromStdin(true, inputStream, fileNames);
        assertEquals("hello" + "helloAgain" + System.lineSeparator(), Files.readString(Path.of(existingFilePath)));
    }

    @Test
    void teeFromStdin_writeExistingFile_onlyNewStringExist() throws Exception {
        InputStream inputStream = streamFromString("helloAgain");
        String[] fileNames = {existingFilePath};
        teeApplication.teeFromStdin(false, inputStream, fileNames);
        assertEquals("helloAgain" + System.lineSeparator(), Files.readString(Path.of(existingFilePath)));
    }

    @Test
    void teeFromStdin_writeNonExistentFile_stringGetsWritten() throws Exception {
        InputStream inputStream = streamFromString("hello");
        String[] fileNames = {nonExistentFilePath};
        teeApplication.teeFromStdin(false, inputStream, fileNames);
        assertEquals("hello" + System.lineSeparator(), Files.readString(Path.of(nonExistentFilePath)));
    }

    @Test
    void teeFromStdin_writeMultipleFiles_stringGetsWritten() throws Exception {
        InputStream inputStream = streamFromString("hello");
        String[] fileNames = {existingFilePath, anotherExistingFilePath};
        teeApplication.teeFromStdin(false, inputStream, fileNames);
        assertEquals("hello" + System.lineSeparator(), Files.readString(Path.of(existingFilePath)));
        assertEquals("hello" + System.lineSeparator(), Files.readString(Path.of(anotherExistingFilePath)));
    }

    @Test
    void teeFromStdin_appendMultipleFiles_stringGetsAppended() throws Exception {
        InputStream inputStream = streamFromString("hello");
        String[] fileNames = {existingFilePath, anotherExistingFilePath};
        teeApplication.teeFromStdin(true, inputStream, fileNames);
        assertEquals("hello" + "hello" + System.lineSeparator(), Files.readString(Path.of(existingFilePath)));
        assertEquals("hello again" + "hello" + System.lineSeparator(), Files.readString(Path.of(anotherExistingFilePath)));
    }

    // TODO: add tests for pipe, input redirection, output redirection (integation tests)
}