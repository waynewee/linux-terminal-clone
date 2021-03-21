package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.exception.MvException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class MvApplicationTest {

    private static final String NO_OVERWRITE_FLAG = "-n";
    private static final String FILE_NAME = "test";
    private static final String FILE_NAME2 = "tests";
    private static final String SUBDIRECTORY_NAME = "subdirectory";

    @Test
    void run_FirstFormOverwrite_Successful(@TempDir Path root) throws Exception {
        Path sourcePath = root.resolve(FILE_NAME);
        Path destPath = root.resolve(FILE_NAME2);
        Files.createFile(sourcePath);

        MvApplication app = new MvApplication();
        String[] args = {sourcePath.toString(), destPath.toString()};
        app.run(args, System.in, System.out);
        assertTrue(Files.exists(destPath));
    }

    @Test
    void run_FirstFormNoOverwrite_Successful(@TempDir Path root) throws Exception {
        Path sourcePath = root.resolve(FILE_NAME);
        Path destPath = root.resolve(FILE_NAME2);
        Files.createFile(sourcePath);

        MvApplication app = new MvApplication();
        String[] args = {NO_OVERWRITE_FLAG, sourcePath.toString(), destPath.toString()};
        app.run(args, System.in, System.out);
        assertTrue(Files.exists(destPath));
    }

    @Test
    void run_SecondFormSingleFileOverwrite_Successful(@TempDir Path root) throws Exception {
        Path sourcePath = root.resolve(FILE_NAME);
        Path destPath = root.resolve(SUBDIRECTORY_NAME);
        Path destFilePath = destPath.resolve(FILE_NAME);
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
        Path sourcePath1 = root.resolve(FILE_NAME + "1");
        Path sourcePath2 = root.resolve(FILE_NAME + "2");
        Path sourcePath3 = root.resolve(FILE_NAME + "3");
        Path destPath = root.resolve(SUBDIRECTORY_NAME);
        Path destFilePath1 = destPath.resolve(FILE_NAME + "1");
        Path destFilePath2 = destPath.resolve(FILE_NAME + "2");
        Path destFilePath3 = destPath.resolve(FILE_NAME + "3");
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
        Path sourcePath = root.resolve(FILE_NAME);
        Path destPath = root.resolve(FILE_NAME2);
        Files.createFile(sourcePath);
        Files.createFile(destPath);

        // Destination file already exists
        MvApplication app = new MvApplication();
        String[] args = {NO_OVERWRITE_FLAG, sourcePath.toString(), destPath.toString()};
        assertThrows(MvException.class, () -> {
            app.run(args, System.in, System.out);
        }, ERR_TARGET_EXISTS);
    }

    @Test
    void run_SecondFormSingleFileNoOverwriteDestExists_Successful(@TempDir Path root) throws Exception {
        Path sourcePath = root.resolve(FILE_NAME);
        Path destPath = root.resolve(SUBDIRECTORY_NAME);
        Path destFilePath = destPath.resolve(FILE_NAME);
        Files.createFile(sourcePath);
        Files.createDirectory(destPath);
        Files.createFile(destFilePath);

        // Not checking if files already exists in subdirectory because it is newly created.
        MvApplication app = new MvApplication();
        String[] args = {NO_OVERWRITE_FLAG, sourcePath.toString(), destPath.toString()};
        assertThrows(MvException.class, () -> {
            app.run(args, System.in, System.out);
        }, ERR_TARGET_EXISTS);
    }

    @Test
    void run_EmptyArgument_ThrowException() {
        MvApplication app = new MvApplication();
        String[] args = {};
        assertThrows(MvException.class, () -> {
            app.run(args, System.in, System.out);
        }, ERR_TARGET_EXISTS);
    }

    @Test
    void run_FlagOnly_ThrowException() {
        MvApplication app = new MvApplication();
        String[] args = {NO_OVERWRITE_FLAG};
        assertThrows(MvException.class, () -> {
            app.run(args, System.in, System.out);
        }, ERR_TARGET_EXISTS);
    }

    @Test
    void mvSrcFileToDestFile_SourceNotFile_ThrowException(@TempDir Path root) throws Exception {
        Path srcPath = root.resolve(SUBDIRECTORY_NAME);
        Path dstPath = root.resolve(SUBDIRECTORY_NAME + "1");
        Files.createDirectory(srcPath);

        MvApplication app = new MvApplication();
        assertThrows(MvException.class, () -> {
            app.mvSrcFileToDestFile(false, srcPath.toString(), dstPath.toString());
        }, ERR_SOURCE_NOT_FILE);
    }

    @Test
    void mvFilesToFolder_SourceDontExists_ThrowException(@TempDir Path root) throws Exception {
        Path srcPath = root.resolve(FILE_NAME);
        Path dstPath = root.resolve(SUBDIRECTORY_NAME);

        MvApplication app = new MvApplication();
        assertThrows(MvException.class, () -> {
            app.mvFilesToFolder(false, srcPath.toString(), dstPath.toString());
        }, ERR_SOURCE_NOT_FOUND);
    }
    @Test
    void mvFilesToFolder_SourceIsDirectory_ThrowException(@TempDir Path root) throws Exception {
        Path srcPath = root.resolve(SUBDIRECTORY_NAME);
        Path dstPath = root.resolve(SUBDIRECTORY_NAME + "1");
        Files.createDirectory(srcPath);

        MvApplication app = new MvApplication();
        assertThrows(MvException.class, () -> {
            app.mvFilesToFolder(false, dstPath.toString(), srcPath.toString());
        }, ERR_SOURCE_NOT_FILE);
    }

}