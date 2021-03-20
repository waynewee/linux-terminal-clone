package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CpException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CpApplicationTest {
    private static ByteArrayOutputStream outputStream;
    private static CpApplication cpApplication;

    @BeforeAll
    static void prepareApplication() {
        cpApplication = new CpApplication();
    }

    @BeforeEach
    void prepareOutputStream() {
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    public void run_InvalidNumberOfFiles_ThrowsException() {
        // Prepare args
        Path sourceFolder = getFolderPath("invalid_file", "sourceFolder");
        Path sourceTestFile = Paths.get(sourceFolder.toString(), "test1.txt");


        String[] args = new String[1];
        args[0] = sourceTestFile.toString();

        // Assert right exception thrown
        assertThrows(CpException.class, () -> cpApplication.run(args, System.in, outputStream));
    }

    @Test
    public void run_InvalidFile_ThrowsException() {
        // Prepare args
        Path sourceFolder = getFolderPath("invalid_file", "sourceFolder");
        Path destinationFolder = getFolderPath("invalid_file", "destinationFolder");

        Path sourceTestFile = Paths.get(sourceFolder.toString(), "test1.txt");
        Path destinationTestFile = Paths.get(destinationFolder.toString(), "test1.txt");


        String[] args = new String[2];
        args[0] = sourceTestFile.toString();
        args[1] = destinationTestFile.toString();

        // Assert right exception thrown
        assertThrows(CpException.class, () -> cpApplication.run(args, System.in, outputStream));
    }

    @Test
    public void run_InvalidFolder_ThrowsException() {
        // Prepare args
        Path sourceFolder = getFolderPath("invalid_folder", "sourceFolder");
        Path destinationFolder = getFolderPath("invalid_folder", "destinationFolder");

        String[] args = new String[2];
        args[0] = sourceFolder.toString();
        args[1] = destinationFolder.toString();

        // Assert right exception thrown
        assertThrows(CpException.class, () -> cpApplication.run(args, System.in, outputStream));
    }

    @Test
    public void run_ValidFile_CopiesToDestination() throws Exception {
        // Prepare args
        Path sourceFolder = getFolderPath("valid_file", "sourceFolder");
        Path destinationFolder = getFolderPath("valid_file", "destinationFolder");

        Path sourceTestFile = Paths.get(sourceFolder.toString(), "test1.txt");
        Path destinationTestFile = Paths.get(destinationFolder.toString(), "test1.txt");

        String[] args = new String[2];
        args[0] = sourceTestFile.toString();
        args[1] = destinationTestFile.toString();

        cpApplication.run(args, System.in, outputStream);

        assert(Files.exists(destinationTestFile));
        cleanUpCode(destinationTestFile);
    }

    @Test
    public void run_ValidDirectory_CopiesToDestination() throws Exception {
        // Prepare args
        Path sourceFolder = getFolderPath("valid_directory", "sourceFolder");
        Path destinationFolder = getFolderPath("valid_directory", "destinationFolder" + File.separator + "sourceFolder");

        String[] args = new String[2];
        args[0] = sourceFolder.toString();
        args[1] = destinationFolder.toString();

        cpApplication.run(args, System.in, outputStream);

        assert(Files.exists(destinationFolder));
        cleanUpCode(destinationFolder);
    }

    @Test
    public void run_GivenRecursiveFlag_CopiesFoldersToDestinationRecursively() throws Exception {
        // Prepare args
        Path sourceFolder = getFolderPath("recursive_valid_folder", "sourceFolder");
        Path destinationFolder = getFolderPath("recursive_valid_folder", "destinationFolder" + File.separator + "sourceFolder");

        int fileCountInSource = getFileCount(sourceFolder);

        String[] args = new String[3];
        args[0] = "-R";
        args[1] = sourceFolder.toString();
        args[2] = destinationFolder.toString();

        cpApplication.run(args, System.in, outputStream);

        int fileCountInDestination = getFileCount(destinationFolder);

        assertEquals(fileCountInSource, fileCountInDestination);
        cleanUpCode(destinationFolder);
    }

    @Test
    public void run_GivenSmallLetterRecursiveFlag_CopiesFoldersToDestinationRecursively() throws Exception {
        // Prepare args
        Path sourceFolder = getFolderPath("recursive_valid_folder", "sourceFolder");
        Path destinationFolder = getFolderPath("recursive_valid_folder", "destinationFolder" + File.separator + "sourceFolder");

        int fileCountInSource = getFileCount(sourceFolder);
        int fileCountInDestination = getFileCount(sourceFolder);

        String[] args = new String[3];
        args[0] = "-r";
        args[1] = sourceFolder.toString();
        args[2] = destinationFolder.toString();

        cpApplication.run(args, System.in, outputStream);

        assertEquals(fileCountInSource, fileCountInDestination);
        cleanUpCode(destinationFolder);
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

    private Path getFolderPath(String invalidFile, String folderLocation) {
        return Paths.get("src", "test", "resources", "impl", "app", "CpApplicationResources", invalidFile, folderLocation);
    }

    private void cleanUpCode(Path destinationTestFile) {
        try {
            if (!Files.isDirectory(destinationTestFile)) {
                Files.delete(destinationTestFile);
            } else {
                deleteRecursively(destinationTestFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteRecursively(entry);
                }
            }
        }
        Files.delete(path);
    }
}
