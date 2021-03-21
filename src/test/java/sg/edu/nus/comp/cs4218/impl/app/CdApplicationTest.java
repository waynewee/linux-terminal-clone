package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import sg.edu.nus.comp.cs4218.EnvironmentUtil;
import sg.edu.nus.comp.cs4218.exception.CdException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class CdApplicationTest {
    static CdApplication cdApplication;
    static String originalDir;
    static InputStream mockInputStream = Mockito.mock(InputStream.class);
    static OutputStream mockOutputStream = Mockito.mock(OutputStream.class);

    @BeforeAll
    static void setUp() throws IOException {
        cdApplication = new CdApplication();
        originalDir = EnvironmentUtil.currentDirectory;

        Path testDirectoryPath = Paths.get("./.directoryForTesting");
        Files.deleteIfExists(testDirectoryPath);
        Files.createDirectory(testDirectoryPath);
    }

    @AfterAll
    static void tearDown() throws CdException {
        cdApplication.changeToDirectory(originalDir);
    }

    @BeforeEach
    void setUpBeforeEachTest() throws CdException {
        cdApplication.changeToDirectory(originalDir);
    }


    @Test
    void changeToDirectory_relativePathExisting_changesDirectory() {
        assertDoesNotThrow(() -> cdApplication.changeToDirectory("." + File.separator + ".directoryForTesting"));
        assertEquals(EnvironmentUtil.currentDirectory, originalDir + File.separator + ".directoryForTesting");
    }

    @Test
    void changeToDirectory_absolutePathExisting_changesDirectory() {
        String absolutePath = originalDir + File.separator + ".directoryForTesting";
        assertDoesNotThrow(() -> cdApplication.changeToDirectory(absolutePath));
        assertEquals(EnvironmentUtil.currentDirectory, absolutePath);
    }

    @Test
    void changeToDirectory_relativePathNonExistant_throwsCdException() {
        assertThrows(CdException.class, () -> cdApplication.changeToDirectory("." + File.separator + ".nonExistantDirectory"));
    }

    @Test
    void changeToDirectory_nullPath_throwsCdException() {
        assertThrows(CdException.class, () -> cdApplication.changeToDirectory(null));
    }

    @Test
    void run_nullArgs_throwsCdException() {
        assertThrows(CdException.class, () -> cdApplication.run(null, mockInputStream, mockOutputStream));
    }

    @Test
    void changeToDirectory_emptyPath_throwsCdException() {
        assertThrows(CdException.class, () -> cdApplication.changeToDirectory(""));
    }

    @Test
    void changeToDirectory_isFile_throwsCdException() {
        assertThrows(CdException.class, () -> cdApplication.changeToDirectory(".gitignore"));
    }
}