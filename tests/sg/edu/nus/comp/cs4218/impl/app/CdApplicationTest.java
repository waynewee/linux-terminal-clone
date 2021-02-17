package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CdApplicationTest {
    static CdApplication cdApplication;
    static String originalDir;

    @BeforeAll
    static void setUp() throws IOException {
        cdApplication = new CdApplication();
        originalDir = Environment.currentDirectory;

        Path testDirectoryPath = Paths.get("./.directoryForTesting");
        Files.deleteIfExists(testDirectoryPath);
        Files.createDirectory(testDirectoryPath);
    }

    @BeforeEach
    void setUpBeforeEachTest() throws CdException {
        cdApplication.changeToDirectory(originalDir);
    }


    @Test
    void changeToDirectory_relativePathExisting_changesDirectory() {
        assertDoesNotThrow(() -> cdApplication.changeToDirectory("." + File.separator + ".directoryForTesting"));
        assertEquals(Environment.currentDirectory, originalDir + File.separator + ".directoryForTesting");
    }

    @Test
    void changeToDirectory_absolutePathExisting_changesDirectory() {
        String absolutePath = originalDir + File.separator + ".directoryForTesting";
        assertDoesNotThrow(() -> cdApplication.changeToDirectory(absolutePath));
        assertEquals(Environment.currentDirectory, absolutePath);
    }

    @Test
    void changeToDirectory_relativePathNonExistant_changesDirectory() {
        assertThrows(CdException.class, () -> cdApplication.changeToDirectory("." + File.separator + ".nonExistantDirectory"));
    }
}