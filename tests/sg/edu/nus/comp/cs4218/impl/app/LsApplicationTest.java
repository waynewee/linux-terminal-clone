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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class LsApplicationTest {

    private static ByteArrayOutputStream outputStream;
    private static Application lsApplication;

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
        assertEquals(correctOutput, outputStream.toString().split("[\n|\r]").length);

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
        assertEquals(files.length, outputStream.toString().split("[\n|\r]").length);

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

    // ls -a
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

    // ls -b -h -e -g -f
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
            output.append(file.getName()).append("\n");
        }
        return output.toString();
    }
}