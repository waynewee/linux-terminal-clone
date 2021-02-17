package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    private static File emptyFile;
    private static File existingFile;

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
    }

    @AfterEach
    void tearDownAfterTest() throws IOException {
        emptyFile.delete();
        existingFile.delete();
    }

    @Test
    void teeFromStdin_appendEmptyFile_stringGetsWritten() throws Exception {
        InputStream inputStream = streamFromString("hello");
        String[] fileNames = {emptyFilePath};
        teeApplication.teeFromStdin(true, inputStream, fileNames);
        assertEquals("hello", Files.readString(Path.of(emptyFilePath)));
    }

    @Test
    void teeFromStdin_appendExistingFile_allStringsExist() throws Exception {
        InputStream inputStream = streamFromString("helloAgain");
        String[] fileNames = {existingFilePath};
        teeApplication.teeFromStdin(true, inputStream, fileNames);
        assertEquals("hello" + "helloAgain", Files.readString(Path.of(existingFilePath)));
    }

    @Test
    void teeFromStdin_writeExistingFile_onlyNewStringExist() throws Exception {
        InputStream inputStream = streamFromString("helloAgain");
        String[] fileNames = {existingFilePath};
        teeApplication.teeFromStdin(false, inputStream, fileNames);
        assertEquals("helloAgain", Files.readString(Path.of(existingFilePath)));
    }
}