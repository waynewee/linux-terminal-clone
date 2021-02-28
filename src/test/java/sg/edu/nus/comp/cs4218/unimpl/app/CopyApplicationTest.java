package sg.edu.nus.comp.cs4218.unimpl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CopyApplicationTest {
    private static ByteArrayOutputStream outputStream;
    private static Application copyApplication;

    @BeforeAll
    static void prepareApplication() {
        copyApplication = new CopyApplication();
    }

    @BeforeEach
    void prepareOutputStream() {
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    public void run_InvalidFile_ThrowsException() {
        // Prepare args
        Path sourceFolder = getFolderPath(Environment.currentDirectory, "invalid_file", "sourceFolder");
        Path destinationFolder = getFolderPath(Environment.currentDirectory, "invalid_file", "destinationFolder");

        Path sourceTestFile = Paths.get(sourceFolder.toString(), "test1.txt");
        Path destinationTestFile = Paths.get(destinationFolder.toString(), "test1.txt");


        String[] args = new String[2];
        args[0] = sourceTestFile.toString();
        args[1] = destinationTestFile.toString();

        // Assert right exception thrown
        CopyException copyException = assertThrows(CopyException.class, () -> copyException.run(args, System.in, outputStream));

    }

    private Path getFolderPath(String environmentDirectory, String invalidFile, String folderLocation) {
        return Paths.get(environmentDirectory, "tests", "resources", "impl", "app", "CopyApplicationResources", invalidFile, folderLocation);
    }

    @Test
    public void run_InvalidFolder_ThrowsException() {
        // Prepare args
        Path sourceFolder = getFolderPath(Environment.currentDirectory, "invalid_folder", "sourceFolder");
        Path destinationFolder = getFolderPath(Environment.currentDirectory, "invalid_folder", "destinationFolder");

        String[] args = new String[2];
        args[0] = sourceFolder.toString();
        args[1] = destinationFolder.toString();

        // Assert right exception thrown
        CopyException copyException = assertThrows(CopyException.class, () -> copyException.run(args, System.in, outputStream));
    }

    @Test
    public void run_ValidFile_CopiesToDestination() {
        // Prepare args
        Path sourceFolder = getFolderPath(Environment.currentDirectory, "valid_file", "sourceFolder");
        Path destinationFolder = getFolderPath(Environment.currentDirectory, "valid_file", "destinationFolder");

        Path sourceTestFile = Paths.get(sourceFolder.toString(), "test1.txt");
        Path destinationTestFile = Paths.get(destinationFolder.toString(), "test1.txt");

        String[] args = new String[2];
        args[0] = sourceTestFile.toString();
        args[1] = destinationTestFile.toString();

        copyException.run(args, System.in, outputStream);

        assert(!Files.exists(sourceTestFile));
        assert(Files.exists(destinationTestFile));
    }

    @Test
    public void run_ValidDirectory_CopiesToDestination() {
        // Prepare args
        Path sourceFolder = getFolderPath(Environment.currentDirectory, "valid_directory", "sourceFolder");
        Path destinationFolder = getFolderPath(Environment.currentDirectory, "valid_directory", "destinationFolder");

        String[] args = new String[2];
        args[0] = sourceFolder.toString();
        args[1] = destinationFolder.toString();

        copyException.run(args, System.in, outputStream);

        assert(!Files.exists(sourceFolder));
        assert(Files.exists(destinationFolder));
    }

    @Test
    public void run_GivenRecursiveFlag_CopiesFoldersToDestinationRecursively() {
        // Prepare args
        Path sourceFolder = getFolderPath(Environment.currentDirectory, "recursive_valid_folder", "sourceFolder");
        Path destinationFolder = getFolderPath(Environment.currentDirectory, "recursive_valid_folder", "destinationFolder");

        int fileCountInSource = getFileCount(sourceFolder);
        int fileCountInDestination = getFileCount(sourceFolder);

        String[] args = new String[3];
        args[0] = "-R";
        args[1] = sourceFolder.toString();
        args[2] = destinationFolder.toString();

        copyException.run(args, System.in, outputStream);

        assertEquals(fileCountInSource, fileCountInDestination);
    }

    @Test
    public void run_GivenSmallLetterRecursiveFlag_CopiesFoldersToDestinationRecursively() {
        // Prepare args
        Path sourceFolder = getFolderPath(Environment.currentDirectory, "recursive_valid_folder", "sourceFolder");
        Path destinationFolder = getFolderPath(Environment.currentDirectory, "recursive_valid_folder", "destinationFolder");

        int fileCountInSource = getFileCount(sourceFolder);
        int fileCountInDestination = getFileCount(sourceFolder);

        String[] args = new String[3];
        args[0] = "-r";
        args[1] = sourceFolder.toString();
        args[2] = destinationFolder.toString();

        copyException.run(args, System.in, outputStream);

        assertEquals(fileCountInSource, fileCountInDestination);
    }

    // Helper Functions
    public static int getFileCount(Path file) {
        File[] files = new File(file.toString()).listFiles();
        int count = 0;
        for (File f : files) {
            if (f.isDirectory()) {
                count += getFileCount(f.toPath());
            } else {
                count += 1;
            }
        }
        return count;
    }
}
