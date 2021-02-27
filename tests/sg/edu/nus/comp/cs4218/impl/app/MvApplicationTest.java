package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.exception.MvException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_TARGET_EXISTS;

class MvApplicationTest {

    private static final String noOverwriteFlag = "-n";

    @Test
    void run_FirstFormOverwrite_Successful(@TempDir Path root) throws Exception {
        Path sourcePath = root.resolve("test");
        Path destPath = root.resolve("tests");
        Files.createFile(sourcePath);

        MvApplication app = new MvApplication();
        String[] args = {sourcePath.toString(), destPath.toString()};
        app.run(args, System.in, System.out);
        assertTrue(Files.exists(destPath));
    }

    @Test
    void run_SecondFormSingleFileOverwrite_Successful(@TempDir Path root) throws Exception {
        String testFileName = "test";
        Path sourcePath = root.resolve(testFileName);
        Path destPath = root.resolve("subfolder");
        Path destFilePath = destPath.resolve(testFileName);
        Files.createFile(sourcePath);
        Files.createDirectory(destPath);

        // Not checking if files already exists in subdirectory because it is newly created.
        MvApplication app = new MvApplication();
        String[] args = {sourcePath.toString(), destPath.toString()};
        app.run(args, System.in, System.out);
        assertTrue(Files.exists(destFilePath));
    }

    @Test
    void run_SecondFormMultipleFilesOverwrite_Successful(@TempDir Path root) throws Exception {
        String testFileName = "test";
        Path sourcePath1 = root.resolve(testFileName + "1");
        Path sourcePath2 = root.resolve(testFileName + "2");
        Path sourcePath3 = root.resolve(testFileName + "3");
        Path destPath = root.resolve("subfolder");
        Path destFilePath1 = destPath.resolve(testFileName + "1");
        Path destFilePath2 = destPath.resolve(testFileName + "2");
        Path destFilePath3 = destPath.resolve(testFileName + "3");
        Files.createFile(sourcePath1);
        Files.createFile(sourcePath2);
        Files.createFile(sourcePath3);
        Files.createDirectory(destPath);

        // Not checking if files already exists in subdirectory because it is newly created.
        MvApplication app = new MvApplication();
        String[] args = {sourcePath1.toString(), sourcePath2.toString(), sourcePath3.toString(), destPath.toString()};
        app.run(args, System.in, System.out);
        assertTrue(Files.exists(destFilePath1));
        assertTrue(Files.exists(destFilePath2));
        assertTrue(Files.exists(destFilePath3));
    }

    // Test move without overwrite into folder with existing filename
    @Test
    void run_FirstFormNoOverwriteDestExists_ThrowsException(@TempDir Path root) throws IOException {
        Path sourcePath = root.resolve("test");
        Path destPath = root.resolve("tests");
        Files.createFile(sourcePath);
        Files.createFile(destPath);

        // Destination file already exists
        MvApplication app = new MvApplication();
        String[] args = {noOverwriteFlag, sourcePath.toString(), destPath.toString()};
        assertThrows(MvException.class, () -> {
            app.run(args, System.in, System.out);
        }, ERR_TARGET_EXISTS);
    }

    @Test
    void run_SecondFormSingleFileNoOverwriteDestExists_Successful(@TempDir Path root) throws Exception {
        String testFileName = "test";
        Path sourcePath = root.resolve(testFileName);
        Path destPath = root.resolve("subfolder");
        Path destFilePath = destPath.resolve(testFileName);
        Files.createFile(sourcePath);
        Files.createDirectory(destPath);
        Files.createFile(destFilePath);

        // Not checking if files already exists in subdirectory because it is newly created.
        MvApplication app = new MvApplication();
        String[] args = {noOverwriteFlag, sourcePath.toString(), destPath.toString()};
        assertThrows(MvException.class, () -> {
            app.run(args, System.in, System.out);
        }, ERR_TARGET_EXISTS);
    }
}