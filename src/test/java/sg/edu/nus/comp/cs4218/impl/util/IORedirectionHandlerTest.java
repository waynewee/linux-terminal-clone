package sg.edu.nus.comp.cs4218.impl.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.*;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

class IORedirectionHandlerTest {

    private static ArgumentResolver argumentResolver;
    private static ByteArrayOutputStream testStream;
    private static String nonExistentFilePath = "someNonExistentFile.txt";
    private static String anotherNonExistentFilePath = "anotherNonExistentFile.txt";
    private static String trueFilePath = "trueFile.txt";
    private static String anotherTrueFilePath = "anotherTrueFile.txt";

    private static File trueFile;
    private static File anotherTrueFile;

    private static String[] TWO_INPUT_REDIRECTION_FILES = {"paste", "<", trueFilePath, anotherTrueFilePath};
    private static String[] MULTIPLE_IOREDIRECTION_TOKENS = {"paste", "<", "<", nonExistentFilePath};
    private static String[] OUTPUT_REDIRECTION_ONE_EXISTING_FILE = {"echo", "helloWorld", ">", trueFilePath};
    private static String[] INPUT_REDIRECTION_ONE_EXISTING_FILE = {"paste", "<", trueFilePath};
    private static String[] INPUT_REDIRECTION_ONE_NONEXISTENT_FILE = {"paste", "<", nonExistentFilePath};
    private static String[] OUTPUT_REDIRECTION_ONE_NONEXISTENT_FILE = {"echo", "helloWorld", ">", anotherNonExistentFilePath};
    private static String[] NO_FILES_PROVIDED = {"paste", "<"};

    @BeforeAll
    static void setUp() throws IOException {
        argumentResolver = new ArgumentResolver();
        testStream = new ByteArrayOutputStream();
    }

    @BeforeEach
    void setupBeforeEachTest() throws IOException {
        trueFile = new File(trueFilePath);
        trueFile.createNewFile();
        FileWriter trueFileWriter = new FileWriter(trueFile, false);
        trueFileWriter.write("Mark of the true file");
        trueFileWriter.close();

        anotherTrueFile = new File(anotherTrueFilePath);
        anotherTrueFile.createNewFile();
        FileWriter anotherTrueFileWriter = new FileWriter(anotherTrueFile, false);
        anotherTrueFileWriter.close();
    }

    @AfterEach
    void tearDownAfterEachTest() {
        trueFile.delete();
        anotherTrueFile.delete();
    }

    @AfterAll
    static void tearDownAfterAll() {
        File anotherNonExistentFile = new File(anotherNonExistentFilePath);
        anotherNonExistentFile.delete();
    }

    @Test
    void extractRedirOptions_inputRedirectionTwoFiles_throwsShellException() {
        List<String> argsList = new ArrayList<String>(Arrays.asList(TWO_INPUT_REDIRECTION_FILES));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions());
    }

    @Test
    void extractRedirOptions_noFilesProvided_throwsNoSuchElementException() {
        List<String> argsList = new ArrayList<String>(Arrays.asList(NO_FILES_PROVIDED));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertThrows(NoSuchElementException.class, () -> redirHandler.extractRedirOptions());
    }

    @Test
    void extractRedirOptions_inputRedirectionOneExistingFile_readsFromFile() throws IOException {
        List<String> argsList = new ArrayList<String>(Arrays.asList(INPUT_REDIRECTION_ONE_EXISTING_FILE));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertDoesNotThrow(redirHandler::extractRedirOptions);

        assertArrayEquals("Mark of the true file".getBytes(StandardCharsets.UTF_8),
                redirHandler.getInputStream().readAllBytes());
    }

    @Test
    void getNoRedirArgsList_outputRedirectionOneExistingFile_noRedirArgsListHasNoOutputRedirectionArgs() throws AbstractApplicationException, ShellException {
        List<String> argsList = new ArrayList<String>(Arrays.asList(OUTPUT_REDIRECTION_ONE_EXISTING_FILE));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertDoesNotThrow(redirHandler::extractRedirOptions);
        List<String> noRedirArgsList = redirHandler.getNoRedirArgsList();
        assertEquals(argsList.size() - 2, noRedirArgsList.size());
    }

    @Test
    void getNoRedirArgsList_inputRedirectionOneExistingFile_noRedirArgsListHasNoInputRedirectionArgs() throws AbstractApplicationException, ShellException {
        List<String> argsList = new ArrayList<String>(Arrays.asList(INPUT_REDIRECTION_ONE_EXISTING_FILE));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertDoesNotThrow(redirHandler::extractRedirOptions);
        List<String> noRedirArgsList = redirHandler.getNoRedirArgsList();
        assertEquals(argsList.size() - 2, noRedirArgsList.size());
    }


    @Test
    void extractRedirOptions_inputRedirectionOneNonExistentFile_throwsShellException() {
        List<String> argsList = new ArrayList<String>(Arrays.asList(INPUT_REDIRECTION_ONE_NONEXISTENT_FILE));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertThrows(ShellException.class, redirHandler::extractRedirOptions);
    }

    @Test
    void extractRedirOptions_outputRedirectionOneNonExistentFile_throwsNoErrors() throws IOException {
        List<String> argsList = new ArrayList<String>(Arrays.asList(OUTPUT_REDIRECTION_ONE_NONEXISTENT_FILE));

        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertDoesNotThrow(redirHandler::extractRedirOptions);
        assertTrue(new File(anotherNonExistentFilePath).exists());
    }

    @Test
    void extractRedirOptions_multipleIoRedirectionTokens_throwsShellException() {
        List<String> argsList = new ArrayList<String>(Arrays.asList(MULTIPLE_IOREDIRECTION_TOKENS));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertThrows(ShellException.class, redirHandler::extractRedirOptions);
    }
}