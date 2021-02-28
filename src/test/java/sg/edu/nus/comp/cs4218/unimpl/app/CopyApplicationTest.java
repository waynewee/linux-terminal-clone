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
        Path source_folder = Paths.get("tests", "resources", "impl", "app", "CopyApplicationResources", "invalid_file", "source_folder");
        source_folder = Paths.get(Environment.currentDirectory, source_folder.toString());

        Path destination_folder = Paths.get("tests", "resources", "impl", "app", "CopyApplicationResources", "invalid_file", "destination_folder");
        destination_folder = Paths.get(Environment.currentDirectory, destination_folder.toString());

        Path sourceTestFile = Paths.get(source_folder.toString(), "test1.txt");
        Path destinationTestFile = Paths.get(destination_folder.toString(), "test1.txt");


        String[] args = new String[2];
        args[0] = sourceTestFile.toString();
        args[1] = destinationTestFile.toString();

        // Assert right exception thrown
        CopyException copyException = assertThrows(CopyException.class, () -> copyException.run(args, System.in, outputStream));

    }

    @Test
    public void run_InvalidFolder_ThrowsException() {
        // Prepare args
        Path source_folder = Paths.get("tests", "resources", "impl", "app", "CopyApplicationResources", "invalid_folder", "source_folder");
        source_folder = Paths.get(Environment.currentDirectory, source_folder.toString());

        Path destination_folder = Paths.get("tests", "resources", "impl", "app", "CopyApplicationResources", "invalid_folder", "destination_folder");
        destination_folder = Paths.get(Environment.currentDirectory, destination_folder.toString());

        String[] args = new String[2];
        args[0] = source_folder.toString();
        args[1] = destination_folder.toString();

        // Assert right exception thrown
        CopyException copyException = assertThrows(CopyException.class, () -> copyException.run(args, System.in, outputStream));
    }

    @Test
    public void run_ValidFile_CopiesToDestination() {
        // Prepare args
        Path source_folder = Paths.get("tests", "resources", "impl", "app", "CopyApplicationResources", "valid_file", "source_folder");
        source_folder = Paths.get(Environment.currentDirectory, source_folder.toString());

        Path destination_folder = Paths.get("tests", "resources", "impl", "app", "CopyApplicationResources", "valid_file", "destination_folder");
        destination_folder = Paths.get(Environment.currentDirectory, destination_folder.toString());

        Path sourceTestFile = Paths.get(source_folder.toString(), "test1.txt");
        Path destinationTestFile = Paths.get(destination_folder.toString(), "test1.txt");

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
        Path source_folder = Paths.get("tests", "resources", "impl", "app", "CopyApplicationResources", "valid_directory", "source_folder");
        source_folder = Paths.get(Environment.currentDirectory, source_folder.toString());

        Path destination_folder = Paths.get("tests", "resources", "impl", "app", "CopyApplicationResources", "valid_directory", "destination_folder");
        destination_folder = Paths.get(Environment.currentDirectory, destination_folder.toString());

        String[] args = new String[2];
        args[0] = source_folder.toString();
        args[1] = destination_folder.toString();

        copyException.run(args, System.in, outputStream);

        assert(!Files.exists(source_folder));
        assert(Files.exists(destination_folder));
    }

    @Test
    public void run_GivenRecursiveFlag_CopiesFoldersToDestinationRecursively() {
        // Prepare args
        Path source_folder = Paths.get("tests", "resources", "impl", "app", "CopyApplicationResources", "recursive_valid_folder", "source_folder");
        source_folder = Paths.get(Environment.currentDirectory, source_folder.toString());
        int fileCountInSource = getFileCount(source_folder);

        Path destination_folder = Paths.get("tests", "resources", "impl", "app", "CopyApplicationResources", "recursive_valid_folder", "destination_folder");
        destination_folder = Paths.get(Environment.currentDirectory, destination_folder.toString());
        int fileCountInDestination = getFileCount(source_folder);

        String[] args = new String[3];
        args[0] = "-R";
        args[1] = source_folder.toString();
        args[2] = destination_folder.toString();

        copyException.run(args, System.in, outputStream);

        assertEquals(fileCountInSource, fileCountInDestination);
    }

    @Test
    public void run_GivenSmallLetterRecursiveFlag_CopiesFoldersToDestinationRecursively() {
        // Prepare args
        Path source_folder = Paths.get("tests", "resources", "impl", "app", "CopyApplicationResources", "recursive_valid_folder", "source_folder");
        source_folder = Paths.get(Environment.currentDirectory, source_folder.toString());
        int fileCountInSource = getFileCount(source_folder);

        Path destination_folder = Paths.get("tests", "resources", "impl", "app", "CopyApplicationResources", "recursive_valid_folder", "destination_folder");
        destination_folder = Paths.get(Environment.currentDirectory, destination_folder.toString());
        int fileCountInDestination = getFileCount(source_folder);

        String[] args = new String[3];
        args[0] = "-r";
        args[1] = source_folder.toString();
        args[2] = destination_folder.toString();

        copyException.run(args, System.in, outputStream);

        assertEquals(fileCountInSource, fileCountInDestination);
    }

    // Helper Functions
    public static int getFileCount(Path file) {
        File[] files = new File(file.toString()).listFiles();
        int count = 0;
        for (File f : files)
            if (f.isDirectory())
                count += getFileCount(f.toPath());
            else
                count += 1;

        return count;
    }
}
