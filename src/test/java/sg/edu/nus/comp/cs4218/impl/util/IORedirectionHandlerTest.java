package sg.edu.nus.comp.cs4218.impl.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private static String nonExistentFile = "someNonExistentFile.txt";
    private static String trueFile = "trueFile.txt";
    private static String anotherTrueFile = "anotherTrueFile.txt";

    private static String[] TWO_INPUT_REDIRECTION_FILES = {"paste", "<", trueFile, anotherTrueFile};
    private static String[] MULTIPLE_IOREDIRECTION_TOKENS = {"paste", "<", "<", nonExistentFile};
    private static String[] OUTPUT_REDIRECTION_ONE_EXISTING_FILE = {"echo", "helloWorld", ">", trueFile};
    private static String[] INPUT_REDIRECTION_ONE_EXISTING_FILE = {"paste", "<", trueFile};
    private static String[] INPUT_REDIRECTION_ONE_NONEXISTENT_FILE = {"paste", "<", nonExistentFile};
    private static String[] OUTPUT_REDIRECTION_ONE_NONEXISTENT_FILE = {"echo", "helloWorld", ">", nonExistentFile};
    private static String[] NO_FILES_PROVIDED = {"paste", "<"};

    @BeforeAll
    static void setUp() throws IOException {
        argumentResolver = new ArgumentResolver();
        testStream = new ByteArrayOutputStream();
    }

    @BeforeEach
    void setupBeforeEachTest() throws IOException {
        Files.deleteIfExists(Path.of(nonExistentFile));
        Files.deleteIfExists(Path.of(trueFile));
        Files.deleteIfExists(Path.of(anotherTrueFile));

        Files.createFile(Path.of(trueFile));
        FileWriter trueFileWriter = new FileWriter(trueFile);
        trueFileWriter.write("Mark of the true file");
        trueFileWriter.close();

        Files.createFile(Path.of(anotherTrueFile));
    }

    @AfterEach
    void tearDownAfterEachTest() throws IOException {
        Files.deleteIfExists(Path.of(nonExistentFile));
        Files.deleteIfExists(Path.of(trueFile));
        Files.deleteIfExists(Path.of(anotherTrueFile));
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
    void getNoRedirArgsList_outputRedirectionOneExistingFile_noRedirArgsListHasNoOutputRedirectionArg() throws AbstractApplicationException, ShellException {
        List<String> argsList = new ArrayList<String>(Arrays.asList(OUTPUT_REDIRECTION_ONE_EXISTING_FILE));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertDoesNotThrow(redirHandler::extractRedirOptions);
        List<String> noRedirArgsList = redirHandler.getNoRedirArgsList();
        assertEquals(argsList.size() - 1, noRedirArgsList.size());
    }

    @Test
    void extractRedirOptions_inputRedirectionOneNonExistentFile_throwsShellException() {
        List<String> argsList = new ArrayList<String>(Arrays.asList(INPUT_REDIRECTION_ONE_NONEXISTENT_FILE));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions());
    }

    @Test
    void extractRedirOptions_outputRedirectionOneNonExistentFile_throwsNoErrors() throws IOException {
        List<String> argsList = new ArrayList<String>(Arrays.asList(OUTPUT_REDIRECTION_ONE_NONEXISTENT_FILE));

        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertDoesNotThrow(() -> redirHandler.extractRedirOptions());
        assertEquals(true, new File(nonExistentFile).exists());
    }

    @Test
    void extractRedirOptions_multipleIoRedirectionTokens_throwsShellException() {
        List<String> argsList = new ArrayList<String>(Arrays.asList(MULTIPLE_IOREDIRECTION_TOKENS));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions());
    }
}