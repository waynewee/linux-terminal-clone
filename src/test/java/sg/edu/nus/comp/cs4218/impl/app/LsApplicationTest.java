package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.LsException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

class LsApplicationTest {

    private static ByteArrayOutputStream outputStream;
    private static Application lsApplication;

    public static String testRecursive =
            "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive:\n" +
                    "answer.txt\n" +
                    "vig1\n" +
                    "vig2\n" +
                    "vig3\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive\\vig1:\n" +
                    "vig1doc1.txt\n" +
                    "vig1doc2.txt\n" +
                    "vig1doc3.txt\n" +
                    "vig1vig1\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive\\vig1\\vig1vig1:\n" +
                    "vig1vig1doc1.txt\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive\\vig2:\n" +
                    "vig2doc1.txt\n" +
                    "vig2doc2.txt\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive\\vig3:\n" +
                    "vig3doc1.txt\n";

    public static String testRecursiveDirectories =
            "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_directories:\n" +
                    "vig1\n" +
                    "vig2\n" +
                    "vig3\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_directories\\vig3:\n" +
                    "vig3vig1\n";


    public static String testRecursiveSort =
            "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort:\n" +
                    "vig1\n" +
                    "vig2\n" +
                    "vig3\n" +
                    "answer.txt\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort\\vig1:\n" +
                    "vig1vig1\n" +
                    "test3.doc\n" +
                    "vig1doc1.txt\n" +
                    "vig1doc3.txt\n" +
                    "test.xls\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort\\vig1\\vig1vig1:\n" +
                    "vig1vig1doc1.txt\n" +
                    "vig1vig2doc3.txt\n" +
                    "vig2vig1doc2.txt\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort\\vig2:\n" +
                    "newppt.pptx\n" +
                    "newxls.xls\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort\\vig3:\n" +
                    "vig3vig1\n" +
                    "vig3doc1.txt.py.txt\n" +
                    "\n" +
                    "src\\test\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort\\vig3\\vig3vig1:\n" +
                    "vig1vig1doc1.txt\n" +
                    "vig3vig5vig6.txt\n";

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
    public void run_SingleTokenLsCommand_ListsCorrectNumberOfFiles() throws Exception {
        // Prepare correct output
        int correctOutput;
        correctOutput = Objects.requireNonNull(new File(Environment.currentDirectory).listFiles()).length;

        lsApplication.run(new String[0], System.in, outputStream);
        assertEquals(correctOutput, outputStream.toString().split(StringUtils.STRING_NEWLINE).length);
    }

    // ls -d
    @Test
    public void run_LsCommandWithDirectoriesOption_ListsCorrectNumberOfDirectories() throws Exception {
        // Prepare correct output
        File[] files = new File(Environment.currentDirectory).listFiles(File::isDirectory);
        assert files != null;

        // Prepare args
        String[] args = new String[1];
        args[0] = "-d";

        lsApplication.run(args, System.in, outputStream);
        assertEquals(files.length, outputStream.toString().split(StringUtils.STRING_NEWLINE).length);

    }

    // ls -X
    @Test
    public void run_LsCommandWithSortOption_ListsFilesInOrder() throws Exception {
        // Prepare correct output
        File[] files = new File(Environment.currentDirectory).listFiles();
        ArrayList<File> filesWithoutExtensions = new ArrayList<>();
        ArrayList<File> filesWithExtensions = new ArrayList<>();

        for (File file: files) {
            String filename = file.getName();
            int indexOfLastDot = filename.lastIndexOf('.');
            if (indexOfLastDot != -1) {
                filesWithExtensions.add(file);
            } else {
                filesWithoutExtensions.add(file);
            }
        }

        Collections.sort(filesWithoutExtensions);
        filesWithExtensions.sort(new ExtensionComparator());
        String expectedOutput = getFileNames(filesWithoutExtensions) + getFileNames(filesWithExtensions);
        // Prepare args
        String[] args = new String[1];
        args[0] = "-X";

        lsApplication.run(args, System.in, outputStream);
        assertEquals(expectedOutput.trim(), outputStream.toString().trim());

    }

    // ls -R [specific folder]
    @Test
    public void run_LsCommandWithRecursiveOption_ListsOutputCorrectly() throws Exception {
        // Prepare correct output
        String correctOutput = testRecursive.replace("\n", StringUtils.STRING_NEWLINE);

        // Prepare args
        Path testsResourcesDir = Paths.get("src", "test", "resources", "impl", "app", "LsApplicationResources", "test_recursive");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString()).toString();
        String[] args = new String[2];
        args[0] = "-R";
        args[1] = path;

        lsApplication.run(args, System.in, outputStream);
        String output = outputStream.toString();
        assertEquals(correctOutput, output);
    }

    // ls -R -d [specific folder]
    @Test
    public void run_lsCommandWithRecursiveAndDirectoriesOption_ListsOutputCorrectly() throws Exception {
        // Prepare correct output
        String correctOutput = testRecursiveDirectories.replace("\n", StringUtils.STRING_NEWLINE);

        // Prepare args
        Path testsResourcesDir = Paths.get("src", "test", "resources", "impl", "app", "LsApplicationResources", "test_recursive_directories");
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

        // Prepare args
        Path testsResourcesDir = Paths.get("src", "test", "resources", "impl", "app", "LsApplicationResources", "test_recursive_sort");
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

        // Prepare args
        Path testsResourcesDir = Paths.get("src", "test", "resources", "impl", "app", "LsApplicationResources", "test_folders_sort");
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

        // Prepare args
        Path testsResourcesDir = Paths.get("src", "test", "resources", "impl", "app", "LsApplicationResources", "test_folders_sort_recursive");
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
    static class ExtensionComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            String ext1 = f1.toString().substring(f1.toString().lastIndexOf('.') + 1);
            String ext2 = f2.toString().substring(f2.toString().lastIndexOf('.') + 1);
            return ext1.compareTo(ext2);
        }
    }

    private String getFileNames(ArrayList<File> filesWithoutExtensions) {
        StringBuilder output = new StringBuilder();
        for (File file: filesWithoutExtensions) {
            output.append(file.getName()).append(StringUtils.STRING_NEWLINE);
        }
        return output.toString();
    }
}