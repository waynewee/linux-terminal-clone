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
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

class IORedirectionHandlerTest {

    private static ArgumentResolver argumentResolver;
    private static ByteArrayOutputStream testStream;
    private static String nonExistentFile = "someNonExistentFile.txt";
    private static String trueFile = "trueFile.txt";
    private static String anotherTrueFile = "anotherTrueFile.txt";


    @BeforeAll
    static void setUp() throws IOException {
        argumentResolver = new ArgumentResolver();
        testStream = new ByteArrayOutputStream();
        Files.deleteIfExists(Path.of(nonExistentFile));
        Files.deleteIfExists(Path.of(trueFile));
        Files.deleteIfExists(Path.of(anotherTrueFile));

        Files.createFile(Path.of(trueFile));
        FileWriter trueFileWriter = new FileWriter(trueFile);
        trueFileWriter.write("Mark of the true file");
        trueFileWriter.close();

        Files.createFile(Path.of(anotherTrueFile));
    }

    @Test
    void extractRedirOptions_inputRedirectionTwoFiles_throwsShellException() {
        List<String> argsList = new ArrayList<String>();
        argsList.add("paste");
        argsList.add("<");
        argsList.add(trueFile);
        argsList.add(anotherTrueFile);
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions());
    }

    @Test
    void extractRedirOptions_inputRedirectionNoFile_throwsNoSuchElementException() {
        List<String> argsList = new ArrayList<String>();
        argsList.add("paste");
        argsList.add("<");
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertThrows(NoSuchElementException.class, () -> redirHandler.extractRedirOptions());
    }

    @Test
    void extractRedirOptions_inputRedirectionOneExistingFile_readsFromFile() throws IOException {
        List<String> argsList = new ArrayList<String>();
        argsList.add("paste");
        argsList.add("<");
        argsList.add(trueFile);
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertDoesNotThrow(() -> redirHandler.extractRedirOptions());

        List<String> noRedirArgsList = redirHandler.getNoRedirArgsList();
        assertEquals(noRedirArgsList.size(), 1);
        assertEquals(noRedirArgsList.get(0), "paste");
        assertArrayEquals("Mark of the true file".getBytes(StandardCharsets.UTF_8),
                redirHandler.getInputStream().readAllBytes());
    }

    @Test
    // TODO: confirm if this is comprehensive enough
    void extractRedirOptions_outputRedirectionOneExistingFile_noRedirectionArguments() {
        List<String> argsList = new ArrayList<String>();
        argsList.add("echo");
        argsList.add("helloWorld");
        argsList.add(">");
        argsList.add(trueFile);
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertDoesNotThrow(() -> redirHandler.extractRedirOptions());

        List<String> noRedirArgsList = redirHandler.getNoRedirArgsList();
        assertEquals(noRedirArgsList.size(), 2);
        assertEquals(noRedirArgsList.get(1), "helloWorld");
    }


    @Test
    void extractRedirOptions_inputRedirectionOneNonExistentFile_throwsShellException() {
        List<String> argsList = new ArrayList<String>();
        argsList.add("paste");
        argsList.add("<");
        argsList.add(nonExistentFile);
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertThrows(ShellException.class, () -> redirHandler.extractRedirOptions());
    }

    @Test
    // TODO: confirm if this is comprehensive enough
    void extractRedirOptions_outputRedirectionOneNonExistentFile_throwsNoErrors() throws IOException {
        List<String> argsList = new ArrayList<String>();
        argsList.add("echo");
        argsList.add("helloWorld");
        argsList.add(">");
        argsList.add(nonExistentFile);
        IORedirectionHandler redirHandler = new IORedirectionHandler(argsList, System.in, testStream, argumentResolver);
        assertDoesNotThrow(() -> redirHandler.extractRedirOptions());
        assertEquals(true, new File(nonExistentFile).exists());
    }
}