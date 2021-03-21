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
    private static final String nonExistentFilePath = "IORedirectionHandler_someNonExistentFile.txt";
    private static final String anotherNonExistentFilePath = "IORedirectionHandler_anotherNonExistentFile.txt";
    private static final String trueFilePath = "IORedirectionHandler_trueFile.txt";
    private static final String anotherTrueFilePath = "IORedirectionHandler_anotherTrueFile.txt";

    private static File trueFile;
    private static File anotherTrueFile;

    private static String[] twoInputRedirectionFiles = {"paste", "<", trueFilePath, anotherTrueFilePath};
    private static String[] multipleIORedirectionTokens = {"paste", "<", "<", nonExistentFilePath};
    private static String[] outputRedirectionOneExistingFile = {"echo", "helloWorld", ">", trueFilePath};
    private static String[] inputRedirectionOneExistingFile = {"paste", "<", trueFilePath};
    private static String[] inputRedirectionOneNonexistentFile = {"paste", "<", nonExistentFilePath};
    private static String[] outputRedirectionOneNonexistentFile = {"echo", "helloWorld", ">", anotherNonExistentFilePath};
    private static String[] noFilesProvided = {"paste", "<"};

    @BeforeAll
    static void setUp() {
        argumentResolver = new ArgumentResolver();
        testStream = new ByteArrayOutputStream();

        File nonExistentFile = new File(nonExistentFilePath);
        nonExistentFile.delete();
        nonExistentFile.deleteOnExit();

        File anotherNonExistentFile = new File(anotherNonExistentFilePath);
        anotherNonExistentFile.delete();
        anotherNonExistentFile.deleteOnExit();
    }

    @BeforeEach
    void setupBeforeEachTest() throws IOException {
        trueFile = new File(trueFilePath);
        trueFile.createNewFile();
        trueFile.deleteOnExit();
        FileWriter trueFileWriter = new FileWriter(trueFile, false);
        trueFileWriter.write("Mark of the true file");
        trueFileWriter.close();

        anotherTrueFile = new File(anotherTrueFilePath);
        anotherTrueFile.createNewFile();
        anotherTrueFile.deleteOnExit();
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
        anotherNonExistentFile.deleteOnExit();
    }

    @Test
    void extractRedirOptions_inputRedirectionTwoFiles_throwsShellException() {
        List<String> argsList = new ArrayList<String>(Arrays.asList(twoInputRedirectionFiles));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions());
    }

    @Test
    void extractRedirOptions_noFilesProvided_throwsNoSuchElementException() {
        List<String> argsList = new ArrayList<String>(Arrays.asList(noFilesProvided));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertThrows(NoSuchElementException.class, () -> redirHandler.extractRedirOptions());
    }

    @Test
    void extractRedirOptions_inputRedirectionOneExistingFile_readsFromFile() throws IOException {
        List<String> argsList = new ArrayList<String>(Arrays.asList(inputRedirectionOneExistingFile));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertDoesNotThrow(redirHandler::extractRedirOptions);

        assertArrayEquals("Mark of the true file".getBytes(StandardCharsets.UTF_8),
                redirHandler.getInputStream().readAllBytes());
    }

    @Test
    void getNoRedirArgsList_outputRedirectionOneExistingFile_noRedirArgsListHasNoOutputRedirectionArgs() throws AbstractApplicationException, ShellException {
        List<String> argsList = new ArrayList<String>(Arrays.asList(outputRedirectionOneExistingFile));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertDoesNotThrow(redirHandler::extractRedirOptions);
        List<String> noRedirArgsList = redirHandler.getNoRedirArgsList();
        assertEquals(argsList.size() - 2, noRedirArgsList.size());
    }

    @Test
    void getNoRedirArgsList_inputRedirectionOneExistingFile_noRedirArgsListHasNoInputRedirectionArgs() throws AbstractApplicationException, ShellException {
        List<String> argsList = new ArrayList<String>(Arrays.asList(inputRedirectionOneExistingFile));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertDoesNotThrow(redirHandler::extractRedirOptions);
        List<String> noRedirArgsList = redirHandler.getNoRedirArgsList();
        assertEquals(argsList.size() - 2, noRedirArgsList.size());
    }


    @Test
    void extractRedirOptions_inputRedirectionOneNonExistentFile_throwsShellException() {
        List<String> argsList = new ArrayList<String>(Arrays.asList(inputRedirectionOneNonexistentFile));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertThrows(ShellException.class, redirHandler::extractRedirOptions);
    }

    @Test
    void extractRedirOptions_outputRedirectionOneNonExistentFile_throwsNoErrors() throws IOException {
        List<String> argsList = new ArrayList<String>(Arrays.asList(outputRedirectionOneNonexistentFile));

        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertDoesNotThrow(redirHandler::extractRedirOptions);
        assertTrue(new File(anotherNonExistentFilePath).exists());
    }

    @Test
    void extractRedirOptions_multipleIoRedirectionTokens_throwsShellException() {
        List<String> argsList = new ArrayList<String>(Arrays.asList(multipleIORedirectionTokens));
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertThrows(ShellException.class, redirHandler::extractRedirOptions);
    }
}