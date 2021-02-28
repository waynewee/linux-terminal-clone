package sg.edu.nus.comp.cs4218.unimpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class RmApplicationTest {

    @Test
    public void remove_RemoveExistingFile_Success(@TempDir Path tmp) throws Exception {
        Path fileToDelete = tmp.resolve("test");
        Files.createFile(fileToDelete);

        RmApplication app = new RmApplication();
        app.remove(false, false, "test");
        assertFalse(Files.exists(fileToDelete));
    }

    @Test
    public void remove_RemoveMultipleExistingFile_Success(@TempDir Path tmp) throws Exception {
        Path fileToDelete1 = tmp.resolve("test1");
        Path fileToDelete2 = tmp.resolve("test2");
        Files.createFile(fileToDelete1);
        Files.createFile(fileToDelete2);

        RmApplication app = new RmApplication();
        app.remove(false, false, "test1", "test2");
        assertFalse(Files.exists(fileToDelete1));
        assertFalse(Files.exists(fileToDelete2));
    }

    @Test
    public void remove_RemoveSingleExistingDirectory_Success(@TempDir Path tmp) throws Exception {
        Path dirToDelete = tmp.resolve("dir");
        Path extraFile = dirToDelete.resolve("test");
        Files.createDirectory(dirToDelete);

        RmApplication app = new RmApplication();
        app.remove(false, true, "dir");
        assertFalse(Files.exists(dirToDelete));
    }

    @Test
    public void remove_RemoveSingleExistingEmptyDirectory_Success(@TempDir Path tmp) throws Exception {
        Path dirToDelete = tmp.resolve("dir");
        Files.createDirectory(dirToDelete);

        RmApplication app = new RmApplication();
        app.remove(true, false, "dir");
        assertFalse(Files.exists(dirToDelete));
    }

    @Test
    public void remove_RemoveOnlyEmptySubdirectories_Success(@TempDir Path tmp) throws Exception {
        // Two layers
        Path dir = tmp.resolve("dir");
        Path subDir1 = dir.resolve("subdir1");
        Path subDir2 = dir.resolve("subdir2");
        Path extraFiles = subDir2.resolve("test");
        Files.createDirectory(dir);
        Files.createDirectory(subDir1);
        Files.createDirectory(subDir2);
        Files.createFile(extraFiles);

        RmApplication app = new RmApplication();
        app.remove(true, true, "dir");
        // subDir1 will be removed; subDir2 will remain
        assertFalse(Files.exists(subDir1));
        assertTrue(Files.exists(subDir1));
    }

    // Negative test case
    @Test
    public void remove_RemoveNonExistingFile_ThrowsException(@TempDir Path tmp) throws Exception {
        RmApplication app = new RmApplication();
        assertThrows(Exception.class, () -> {
            app.remove(false, false, "notExists");
        });
    }

    @Test
    public void remove_RemoveDirectoryWithoutFlag_ThrowsException(@TempDir Path tmp) throws Exception {
        Path dir = tmp.resolve("dir");
        Files.createDirectory(dir);

        RmApplication app = new RmApplication();
        assertThrows(Exception.class, () -> {
            app.remove(false, false, "dir");
        });
    }

    @Test
    public void remove_RemoveFileWithRecursiveFlag_ThrowsException(@TempDir Path tmp) throws Exception {
        Path file = tmp.resolve("test");
        Files.createFile(file);

        RmApplication app = new RmApplication();
        assertThrows(Exception.class, () -> {
            app.remove(false, true, "test");
        });
    }

    @Test
    public void remove_RemoveFileWithEmptyDirectoryFlag_ThrowsException(@TempDir Path tmp) throws Exception {
        Path file = tmp.resolve("test");
        Files.createFile(file);

        RmApplication app = new RmApplication();
        assertThrows(Exception.class, () -> {
            app.remove(true, false, "test");
        });
    }

    @Test
    public void remove_RemoveNonEmptyDirectoryWithEmptyDirectoryFlag_ThrowsException(@TempDir Path tmp) throws Exception {
        Path dir = tmp.resolve("dir");
        Path extraFile = dir.resolve("test");
        Files.createDirectory(dir);
        Files.createFile(extraFile);

        RmApplication app = new RmApplication();
        assertThrows(Exception.class, () -> {
            app.remove(true, false, "dir");
        });
    }

    @Test
    public void remove_RecursiveRemoveDirectoryAndFile_ThrowsException(@TempDir Path tmp) throws Exception {
        Path dir = tmp.resolve("dir");
        Path file = tmp.resolve("test");
        Files.createDirectory(dir);
        Files.createFile(file);

        RmApplication app = new RmApplication();
        assertThrows(Exception.class, () -> {
            app.remove(false, true, "dir", "file");
        });
    }

    @Test
    public void remove_RecursiveRemoveDirectoryAndFile_ThrowsException(@TempDir Path tmp) throws Exception {
        Path dir = tmp.resolve("dir");
        Path file = tmp.resolve("test");
        Files.createDirectory(dir);
        Files.createFile(file);

        RmApplication app = new RmApplication();
        assertThrows(Exception.class, () -> {
            app.remove(true, false, "dir", "file");
        });
    }
}
