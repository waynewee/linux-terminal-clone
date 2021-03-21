package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.LsException;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

class LsApplicationTest {

    private static ByteArrayOutputStream outputStream;
    private static Application lsApplication;

    public static String testOrderSort =
            "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_order_files_sort:\n" +
            "test3_doc_ext.docx\n" +
            "test1_ppt_ext.ppt\n" +
            "test2_pub_ext.pub\n" +
            "test4_txt_ext.txt\n";

    public static String testRecursive =
            "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive:\n" +
                    "answer.txt\n" +
                    "space1\n" +
                    "space2\n" +
                    "space3\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive\\space1:\n" +
                    "space1doc1.txt\n" +
                    "space1doc2.txt\n" +
                    "space1doc3.txt\n" +
                    "space1space1\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive\\space1\\space1space1:\n" +
                    "space1space1doc1.txt\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive\\space2:\n" +
                    "space2doc1.txt\n" +
                    "space2doc2.txt\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive\\space3:\n" +
                    "space3doc1.txt\n";

    public static String testRecursiveDirectories =
            "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_directories:\n" +
                    "space1\n" +
                    "space2\n" +
                    "space3\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_directories\\space3:\n" +
                    "space3space1\n";


    public static String testRecursiveSort =
            "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort:\n" +
                    "space1\n" +
                    "space2\n" +
                    "space3\n" +
                    "answer.txt\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort\\space1:\n" +
                    "space1space1\n" +
                    "test3.doc\n" +
                    "space1doc1.txt\n" +
                    "space1doc3.txt\n" +
                    "test.xls\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort\\space1\\space1space1:\n" +
                    "space1space1doc1.txt\n" +
                    "space1space2doc3.txt\n" +
                    "space2space1doc2.txt\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort\\space2:\n" +
                    "newppt.pptx\n" +
                    "newxls.xls\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort\\space3:\n" +
                    "space3space1\n" +
                    "space3doc1.txt.py.txt\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort\\space3\\space3space1:\n" +
                    "space1space1doc1.txt\n" +
                    "space3space5space6.txt\n";

    public static String testFoldersOnlySort =
            "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_folders_sort:\n" +
                    "fafafaf\n" +
                    "folder1\n" +
                    "folderfsjofjs\n" +
                    "new folder\n";

    public static String testFolderOnlySortRecursive =
            "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_folders_sort_recursive:\n" +
                    "folder1\n" +
                    "folder2\n" +
                    "folder3\n" +
                    "guys\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_folders_sort_recursive\\guys:\n" +
                    "folder23\n";

    @BeforeAll
    static void prepareApplication() {
        lsApplication = new LsApplication();
    }

    @BeforeEach
    void prepareOutputStream() {
        outputStream = new ByteArrayOutputStream();
    }

    // ls
    @Test
    public void run_LsCommandWithoutFlags_ListsCorrectNumberOfFiles() throws Exception {
        // Prepare correct output
        int correctOutput = 7;

        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("test_number_of_files");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString()).toString();

        String[] args = new String[1];
        args[0] = path;

        lsApplication.run(args, System.in, outputStream);

        // Remove header line printed in output stream
        int outputNumberOfFiles = outputStream.toString().trim().split(StringUtils.STRING_NEWLINE).length - 1;
        assertEquals(correctOutput, outputNumberOfFiles);
    }

    // ls -d
    @Test
    public void run_LsCommandWithDirectoriesOption_ListsCorrectNumberOfDirectories() throws Exception {
        // Prepare correct output
        int correctOutput = 3;

        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("test_number_of_folders");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString()).toString();

        String[] args = new String[2];
        args[0] = "-d";
        args[1] = path;

        lsApplication.run(args, System.in, outputStream);

        // Remove header line printed in output stream
        int outputNumberOfFiles = outputStream.toString().trim().split(StringUtils.STRING_NEWLINE).length - 1;
        assertEquals(correctOutput, outputNumberOfFiles);
    }

    // ls -X
    @Test
    public void run_LsCommandWithSortOption_ListsFilesInOrder() throws Exception {
        // Prepare correct output
        String correctOutput = testOrderSort.replace("\n", StringUtils.STRING_NEWLINE);
        correctOutput = correctOutput.replace("\\\\", StringUtils.fileSeparator());

        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("test_order_files_sort");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString()).toString();

        // Prepare args
        String[] args = new String[2];
        args[0] = "-X";
        args[1] = path;

        lsApplication.run(args, System.in, outputStream);
        assertEquals(correctOutput, outputStream.toString());
    }

    // ls -R [specific folder]
    @Test
    public void run_LsCommandWithRecursiveOption_ListsOutputCorrectly() throws Exception {
        // Prepare correct output
        String correctOutput = testRecursive.replace("\n", StringUtils.STRING_NEWLINE);
        correctOutput = correctOutput.replace("\\\\", StringUtils.fileSeparator());

        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("test_recursive");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString()).toString();
        String[] args = new String[2];
        args[0] = "-R";
        args[1] = path;

        lsApplication.run(args, System.in, outputStream);
        assertEquals(correctOutput, outputStream.toString());
    }

    // ls -R -d [specific folder]
    @Test
    public void run_lsCommandWithRecursiveAndDirectoriesOption_ListsOutputCorrectly() throws Exception {
        // Prepare correct output
        String correctOutput = testRecursiveDirectories.replace("\n", StringUtils.STRING_NEWLINE);
        correctOutput = correctOutput.replace("\\\\", StringUtils.fileSeparator());

        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("test_recursive_directories");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString()).toString();
        String[] args = new String[3];
        args[0] = "-R";
        args[1] = "-d";
        args[2] = path;

        lsApplication.run(args, System.in, outputStream);
        assertEquals(correctOutput, outputStream.toString());
    }

    // ls -R -X [specific folder]
    @Test
    public void run_lsCommandWithRecursiveAndSortOption_ListsOutputCorrectly() throws Exception {
        // Prepare correct output
        String correctOutput = testRecursiveSort.replace("\n", StringUtils.STRING_NEWLINE);
        correctOutput = correctOutput.replace("\\\\", StringUtils.fileSeparator());

        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("test_recursive_sort");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString()).toString();
        String[] args = new String[3];
        args[0] = "-R";
        args[1] = "-X";
        args[2] = path;

        lsApplication.run(args, System.in, outputStream);
        assertEquals(correctOutput, outputStream.toString());
    }

    // ls -d -X [specific folder]
    @Test
    public void run_lsCommandWithFoldersOnlyAndSortOption_ListsOutputCorrectly() throws Exception {
        // Prepare correct output
        String correctOutput = testFoldersOnlySort.replace("\n", StringUtils.STRING_NEWLINE);
        correctOutput = correctOutput.replace("\\\\", StringUtils.fileSeparator());

        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("test_folders_sort");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString()).toString();
        String[] args = new String[3];
        args[0] = "-d";
        args[1] = "-X";
        args[2] = path;

        lsApplication.run(args, System.in, outputStream);
        assertEquals(correctOutput, outputStream.toString());
    }

    // ls -d -X -R [specific folder]
    @Test
    public void run_lsCommandWithFoldersOnlyAndSortOptionAndRecursive_ListsOutputCorrectly() throws Exception {
        // Prepare correct output
        String correctOutput = testFolderOnlySortRecursive.replace("\n", StringUtils.STRING_NEWLINE);
        correctOutput = correctOutput.replace("\\\\", StringUtils.fileSeparator());

        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("test_folders_sort_recursive");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString()).toString();
        String[] args = new String[4];
        args[0] = "-d";
        args[1] = "-X";
        args[2] = "-R";
        args[3] = path;

        lsApplication.run(args, System.in, outputStream);
        assertEquals(correctOutput, outputStream.toString());
    }

    // Unknown arg: ls -a
    @Test
    public void run_GivenUnknownArgsWithHyphen_ThrowsLsException() {
        String[] args = new String[1];
        args[0] = "-a";
        // Assert right exception thrown
        LsException lsException = assertThrows(LsException.class, () -> lsApplication.run(args, System.in, outputStream));
        // Assert right message in exception
        InvalidArgsException invalidArgsException = new InvalidArgsException(ILLEGAL_FLAG_MSG + "a");
        assertEquals("ls: " + invalidArgsException.getMessage(), lsException.getMessage());
    }

    // Unknown args: ls -b -h -e -g -f
    @Test
    public void run_GivenUnknownMultipleArgsWithHyphen_ThrowsLsException() {
        String[] args = new String[5];
        args[0] = "-b";
        args[1] = "-h";
        args[2] = "-e";
        args[3] = "-g";
        args[4] = "-f";

        // Assert right exception thrown
        LsException lsException = assertThrows(LsException.class, () -> lsApplication.run(args, System.in, outputStream));
        // Assert right message in exception
        InvalidArgsException invalidArgsException = new InvalidArgsException(ILLEGAL_FLAG_MSG + "b");
        assertEquals("ls: " + invalidArgsException.getMessage(), lsException.getMessage());
    }

    // Null args
    @Test
    public void run_GivenNullForArgsParameter_ThrowsLsException() {
        // Assert right exception thrown
        LsException lsException = assertThrows(LsException.class, () -> lsApplication.run(null, System.in, outputStream));
        // Assert right message in exception
        assertEquals(new LsException(ERR_NULL_ARGS).getMessage(), lsException.getMessage());
    }

    // Null outputstream
    @Test
    public void run_GivenNullForOutputStreamParameter_ThrowsLsException() {
        // Assert right exception thrown
        LsException lsException = assertThrows(LsException.class, () -> lsApplication.run(new String[0], System.in, null));
        // Assert right message in exception
        assertEquals(new LsException(ERR_NO_OSTREAM).getMessage(), lsException.getMessage());
    }

    // Utils
    private Path getTestsResourcesDir(String fileName) {
        return Paths.get("src", "test", "resources", "impl", "app", "LsApplicationResources", fileName);
    }
}