package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.EnvironmentUtil;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CpException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CpApplicationTest {
    private static ByteArrayOutputStream outputStream;
    private static CpApplication cpApplication;
    // Given tests from teaching stuff
    public static final String TEMP = "temp-cp";
    public static final Path TEMP_PATH = Paths.get(EnvironmentUtil.currentDirectory, TEMP);

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
        Path srcFolder = getFolderPath("invalid_file", "sourceFolder");
        Path srcTestFile = Paths.get(srcFolder.toString(), "test1.txt");


        String[] args = new String[1];
        args[0] = srcTestFile.toString();

        // Assert right exception thrown
        assertThrows(CpException.class, () -> cpApplication.run(args, System.in, outputStream));
    }

    @Test
    public void run_InvalidFile_ThrowsException() {
        // Prepare args
        Path srcFolder = getFolderPath("invalid_file", "sourceFolder");
        Path destFolder = getFolderPath("invalid_file", "destinationFolder");

        Path srcTestFile = Paths.get(srcFolder.toString(), "test1.txt");
        Path destTestFile = Paths.get(destFolder.toString(), "test1.txt");


        String[] args = new String[2];
        args[0] = srcTestFile.toString();
        args[1] = destTestFile.toString();

        // Assert right exception thrown
        assertThrows(CpException.class, () -> cpApplication.run(args, System.in, outputStream));
    }

    @Test
    public void run_InvalidFolder_ThrowsException() {
        // Prepare args
        Path srcFolder = getFolderPath("invalid_folder", "sourceFolder");
        Path destFolder = getFolderPath("invalid_folder", "destinationFolder");

        String[] args = new String[2];
        args[0] = srcFolder.toString();
        args[1] = destFolder.toString();

        // Assert right exception thrown
        assertThrows(CpException.class, () -> cpApplication.run(args, System.in, outputStream));
    }

    @Test
    public void run_ValidFile_CopiesToDestination() throws Exception {
        // Prepare args
        Path srcFolder = getFolderPath("valid_file", "sourceFolder");
        Path destFolder = getFolderPath("valid_file", "destinationFolder");

        Path srcTestFile = Paths.get(srcFolder.toString(), "test1.txt");
        Path destTestFile = Paths.get(destFolder.toString(), "test1.txt");

        String[] args = new String[2];
        args[0] = srcTestFile.toString();
        args[1] = destTestFile.toString();

        cpApplication.run(args, System.in, outputStream);

        assert(Files.exists(destTestFile));
        cleanUpCode(destTestFile);
    }

    @Test
    public void run_ValidMultipleFiles_CopiesToDestination() throws Exception {
        // Prepare args
        Path srcFolder = getFolderPath("valid_multiple_files", "sourceFolder");
        Path destFolder = getFolderPath("valid_multiple_files", "destinationFolder");

        Path srcTestFile1 = Paths.get(srcFolder.toString(), "test1.txt");
        Path srcTestFile2 = Paths.get(srcFolder.toString(), "test2.txt");
        Path srcTestFile3 = Paths.get(srcFolder.toString(), "test3.txt");
        Path destTestFile1 = Paths.get(destFolder.toString(), "test1.txt");
        Path destTestFile2 = Paths.get(destFolder.toString(), "test2.txt");
        Path destTestFile3 = Paths.get(destFolder.toString(), "test3.txt");

        String[] args = new String[4];
        args[0] = srcTestFile1.toString();
        args[1] = srcTestFile2.toString();
        args[2] = srcTestFile3.toString();
        args[3] = destFolder.toString();

        cpApplication.run(args, System.in, outputStream);

        assert(Files.exists(destTestFile1));
        assert(Files.exists(destTestFile2));
        assert(Files.exists(destTestFile3));
        cleanUpCode(destTestFile1);
        cleanUpCode(destTestFile2);
        cleanUpCode(destTestFile3);
    }

    @Test
    public void run_ValidDirectory_CopiesToDestination() throws Exception {
        // Prepare args
        Path srcFolder = getFolderPath("valid_directory", "sourceFolder");
        Path destFolder = getFolderPath("valid_directory", "destinationFolder" + File.separator + "sourceFolder");

        String[] args = new String[2];
        args[0] = srcFolder.toString();
        args[1] = destFolder.toString();

        cpApplication.run(args, System.in, outputStream);

        assert(Files.exists(destFolder));
        cleanUpCode(destFolder);
    }

    @Test
    public void run_GivenRecursiveFlag_CopiesFoldersToDestinationRecursively() throws Exception {
        // Prepare args
        Path srcFolder = getFolderPath("recursive_valid_folder", "sourceFolder");
        Path destFolder = getFolderPath("recursive_valid_folder", "destinationFolder" + File.separator + "sourceFolder");

        int fileCountInSrc = getFileCount(srcFolder);

        String[] args = new String[3];
        args[0] = "-R";
        args[1] = srcFolder.toString();
        args[2] = destFolder.toString();

        cpApplication.run(args, System.in, outputStream);

        int fileCountInDest = getFileCount(destFolder);

        assertEquals(fileCountInSrc, fileCountInDest);
        cleanUpCode(destFolder);
    }

    @Test
    public void run_GivenSmallLetterRecursiveFlag_CopiesFoldersToDestinationRecursively() throws Exception {
        // Prepare args
        Path srcFolder = getFolderPath("recursive_valid_folder", "sourceFolder");
        Path destFolder = getFolderPath("recursive_valid_folder", "destinationFolder" + File.separator + "sourceFolder");

        int fileCountInSrc = getFileCount(srcFolder);
        int fileCountInDest = getFileCount(srcFolder);

        String[] args = new String[3];
        args[0] = "-r";
        args[1] = srcFolder.toString();
        args[2] = destFolder.toString();

        cpApplication.run(args, System.in, outputStream);

        assertEquals(fileCountInSrc, fileCountInDest);
        cleanUpCode(destFolder);
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

    @BeforeEach
    void createTemp() throws IOException {
        Files.createDirectory(TEMP_PATH);
    }

    @AfterEach
    void deleteTemp() throws IOException {
        Files.walk(TEMP_PATH)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    private Path createFile(String name) throws IOException {
        return createFile(name, TEMP_PATH);
    }

    private Path createDirectory(String folder) throws IOException {
        return createDirectory(folder, TEMP_PATH);
    }

    private Path createFile(String name, Path inPath) throws IOException {
        Path path = inPath.resolve(name);
        Files.createFile(path);
        return path;
    }

    private Path createDirectory(String folder, Path inPath) throws IOException {
        Path path = inPath.resolve(folder);
        Files.createDirectory(path);
        return path;
    }

    private void writeToFile(Path path, String content) throws IOException {
        Files.write(path, content.getBytes());
    }

    private String[] toArgs(String flag, String... files) {
        List<String> args = new ArrayList<>();
        if (!flag.isEmpty()) {
            args.add("-" + flag);
        }
        for (String file : files) {
            args.add(Paths.get(TEMP, file).toString());
        }
        return args.toArray(new String[0]);
    }

    @Test
    void run_EmptyFileToNonemptyFile_OverwritesDestWithEmpty() throws Exception {
        String srcName = "src_file.txt";
        String destName = "dest_file.txt";
        createFile(srcName);
        Path destFile = createFile(destName);
        String destContent = "This file is not empty.";
        writeToFile(destFile, destContent);
        new CpApplication().run(toArgs("", srcName, destName), System.in, System.out);
        assertArrayEquals(("").getBytes(), Files.readAllBytes(destFile));
    }

    @Test
    void run_NonemptyFileToEmptyFile_CopiesContentToDest() throws Exception {
        String srcName = "src_file.txt";
        String destName = "dest_file.txt";
        Path srcFile = createFile(srcName);
        Path destFile = createFile(destName);
        String srcContent = "This file is not empty.";
        writeToFile(srcFile, srcContent);
        new CpApplication().run(toArgs("", srcName, destName), System.in, System.out);
        assertArrayEquals(srcContent.getBytes(), Files.readAllBytes(destFile));
    }

    @Test
    void run_NonemptyFileToNonemptyFile_OverwritesDest() throws Exception {
        String srcName = "src_file.txt";
        String destName = "dest_file.txt";
        Path srcFile = createFile(srcName);
        Path destFile = createFile(destName);
        String srcContent = "This is the source file.";
        String destContent = "This is the destination file.";
        writeToFile(srcFile, srcContent);
        writeToFile(destFile, destContent);
        new CpApplication().run(toArgs("", srcName, destName), System.in, System.out);
        assertArrayEquals(srcContent.getBytes(), Files.readAllBytes(destFile));
    }

    @Test
    void run_DirectoryToFile_Throws() throws IOException {
        String srcName = "src_dir";
        String destName = "dest_file.txt";
        createDirectory(srcName);
        Path destFile = createFile(destName);
        String destContent = "This is the destination file.";
        writeToFile(destFile, destContent);
        assertThrows(CpException.class, () -> new CpApplication().run(toArgs("", srcName, destName), System.in, System.out));
    }

    @Test
    void run_NonexistentFileToFile_Throws() throws IOException {
        String srcName = "src_file.txt";
        String destName = "dest_file.txt";
        Path destFile = createFile(destName);
        String destContent = "This is the destination file.";
        writeToFile(destFile, destContent);
        assertThrows(CpException.class, () -> new CpApplication().run(toArgs("", srcName, destName), System.in, System.out));
    }

    @Test
    void run_FileToNonexistentFile_CreatesNewDestFile() throws Exception {
        String srcName = "src_file.txt";
        String destName = "dest_file.txt";
        Path srcFile = createFile(srcName);
        String srcContent = "This is the source file.";
        writeToFile(srcFile, srcContent);
        new CpApplication().run(toArgs("", srcName, destName), System.in, System.out);
        Path destFile = TEMP_PATH.resolve(destName);
        assertTrue(Files.exists(destFile));
        assertArrayEquals(srcContent.getBytes(), Files.readAllBytes(destFile));
    }

    @Test
    void run_FileToEmptyDirectory_CopiesToDirectory() throws Exception {
        String srcName = "src_file.txt";
        String destName = "dest_dir";
        Path srcFile = createFile(srcName);
        String srcContent = "This is the source file.";
        writeToFile(srcFile, srcContent);
        Path destDir = createDirectory(destName);
        new CpApplication().run(toArgs("", srcName, destName), System.in, System.out);
        Path destFile = destDir.resolve(srcName);
        assertTrue(Files.exists(destFile));
        assertArrayEquals(srcContent.getBytes(), Files.readAllBytes(destFile));
    }

    @Test
    void run_FileToNonemptyDirectory_CopiesToDirectory() throws Exception {
        String srcName = "src_file.txt";
        Path srcFile = createFile(srcName);
        String srcContent = "This is the source file.";
        writeToFile(srcFile, srcContent);
        String destName = "dest_dir";
        Path destDir = createDirectory(destName);
        String destOrigName = "dest_orig_file.txt";
        Path destOrigFile = createFile(destOrigName, destDir);
        new CpApplication().run(toArgs("", srcName, destName), System.in, System.out);
        assertTrue(Files.exists(destOrigFile));
        Path destFile = destDir.resolve(srcName);
        assertTrue(Files.exists(destFile));
        assertArrayEquals(srcContent.getBytes(), Files.readAllBytes(destFile));
    }

    @Test
    void run_DirectoryToDirectoryWithFlag_CopiesSrcDirectoryToDestDirectory() throws Exception {
        String srcName = "src_dir";
        String destName = "dest_dir";
        createDirectory(srcName);
        Path destDir = createDirectory(destName);
        new CpApplication().run(toArgs("r", srcName, destName), System.in, System.out);
        Path destFile = destDir.resolve(srcName);
        assertTrue(Files.exists(destFile));
        assertTrue(Files.isDirectory(destFile));
    }

    @Test
    void run_NonexistentDirectoryToDirectoryWithFlag_Throws() throws IOException {
        String nonexistentSrcName = "nonexistent_dir";
        String destName = "dest_dir";
        createDirectory(destName);
        assertThrows(CpException.class, () -> new CpApplication().run(toArgs("r", nonexistentSrcName, destName), System.in, System.out));
    }

    @Test
    void run_FileToNonexistentDirectory_CreatesFileWithDestNameWithSrcContent() throws Exception {
        String srcName = "src_file.txt";
        Path srcFile = createFile(srcName);
        String srcContent = "This is the source file.";
        writeToFile(srcFile, srcContent);
        String destName = "dest_dir";
        new CpApplication().run(toArgs("", srcName, destName), System.in, System.out);
        Path destFile = TEMP_PATH.resolve(destName);
        assertTrue(Files.exists(destFile));
        assertArrayEquals(srcContent.getBytes(), Files.readAllBytes(destFile));
    }

    @Test
    void run_MissingSrcAndDestArguments_Throws() {
        assertThrows(CpException.class, () -> new CpApplication().run(toArgs(""), System.in, System.out));
    }

    @Test
    void run_FileToMissingDestArgument_Throws() throws IOException {
        String srcName = "src_file.txt";
        createFile(srcName);
        assertThrows(CpException.class, () -> new CpApplication().run(toArgs("", srcName), System.in, System.out));
    }

    @Test
    void run_DirectoryToMissingDestArgument_Throws() throws IOException {
        String srcName = "src_dir";
        createDirectory(srcName);
        assertThrows(CpException.class, () -> new CpApplication().run(toArgs("", srcName), System.in, System.out));
    }

    @Test
    void run_MultipleFilesToDirectory_CopiesToDirectory() throws Exception, AbstractApplicationException {
        String srcAName = "srcA_file.txt";
        String srcBName = "srcB_file.txt";
        String destName = "dest_dir";
        createFile(srcAName);
        createFile(srcBName);
        Path destDir = createDirectory(destName);
        new CpApplication().run(toArgs("", srcAName, srcBName, destName), System.in, System.out);
        Path destAFile = destDir.resolve(srcAName);
        Path destBFile = destDir.resolve(srcBName);
        assertTrue(Files.exists(destAFile));
        assertTrue(Files.exists(destBFile));
    }

    @Test
    void run_MultipleFilesToFile_Throws() throws IOException {
        String srcAName = "srcA_file.txt";
        String srcBName = "srcB_file.txt";
        String destName = "dest_file.txt";
        createFile(srcAName);
        createFile(srcBName);
        createFile(destName);
        assertThrows(CpException.class, () -> new CpApplication().run(toArgs("", srcAName, srcBName, destName), System.in, System.out));
    }

    @Test
    void run_MultipleDirectoriesToFile_Throws() throws IOException {
        String srcAName = "srcA_dir";
        String srcBName = "srcB_dir";
        String destName = "dest_file.txt";
        createDirectory(srcAName);
        createDirectory(srcBName);
        createFile(destName);
        assertThrows(CpException.class, () -> new CpApplication().run(toArgs("r", srcAName, srcBName, destName), System.in, System.out));
    }

    @Test
    void run_MultipleFilesAndDirectoriesToFile_Throws() throws IOException {
        String srcAName = "srcA_file.txt";
        String srcBName = "srcB_dir";
        String destName = "dest_file.txt";
        createFile(srcAName);
        createDirectory(srcBName);
        createFile(destName);
        assertThrows(CpException.class, () -> new CpApplication().run(toArgs("", srcAName, srcBName, destName), System.in, System.out));
    }
}
