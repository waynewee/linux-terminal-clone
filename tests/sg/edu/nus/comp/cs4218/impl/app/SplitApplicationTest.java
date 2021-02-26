package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class SplitApplicationTest {
    private static ByteArrayOutputStream outputStream;
    private static Application splitApplication;

    @BeforeAll
    static void prepareApplication() {
        splitApplication = new SplitApplication();
    }

    @BeforeEach
    void prepareOutputStream() {
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    public void run_onWrongFlags_ThrowsException() {
        // Prepare args
        Path testsResourcesDir = Paths.get("tests", "resources", "impl", "app", "SplitApplicationResources", "wrong_arguments");
        Path testFile = Paths.get("wrong_arguments.txt");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[2];
        args[0] = "-d";
        args[1] = path;

        // Assert right exception thrown
        ShellException shellException = assertThrows(ShellException.class, () -> splitApplication.run(args, System.in, outputStream));
        // Assert right message in exception
        assertEquals(new ShellException(ERR_INVALID_FLAG).getMessage(), shellException.getMessage());
    }

    @Test
    public void run_onNoArguments_WaitsForInput() {

    }

    @Test
    public void run_onLineArgumentInvalidFile_ThrowsException() {
        // Prepare args
        Path testsResourcesDir = Paths.get("tests", "resources", "impl", "app", "SplitApplicationResources", "wrong_arguments");
        Path testFile = Paths.get("random_invalid_file");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[2];
        args[0] = "-l";
        args[1] = path;

        // Assert right exception thrown
        ShellException shellException = assertThrows(ShellException.class, () -> splitApplication.run(args, System.in, outputStream));
        // Assert right message in exception
        assertEquals(new ShellException(ERR_FILE_NOT_FOUND).getMessage(), shellException.getMessage());

    }

    @Test
    public void run_onNoArgumentsExceptFileName_splitsFileByLines() throws Exception {
        // Prepare args
        Path testsResourcesDir = Paths.get("tests", "resources", "impl", "app", "SplitApplicationResources", "filename");
        Path testFile = Paths.get("filename.txt");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[1];
        args[0] = path;

        splitApplication.run(args, System.in, outputStream);
        String outputFilePath = Paths.get(Environment.currentDirectory, Paths.get("xaa").toString()).toString();

        File outputFile = new File(outputFilePath);
        File originalFile = new File(path);

        assertEquals(outputFile.toString(), originalFile.toString());
    }

    @Test
    public void run_onLineArgument_splitsFileByDefaultOneThousandLines() throws Exception {
        // Prepare args
        Path testsResourcesDir = Paths.get("tests", "resources", "impl", "app", "SplitApplicationResources", "no_arguments_except_filename");
        Path testFile = Paths.get("no_arguments_except_filename.txt");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[2];
        args[0] = "-l";
        args[1] = path;

        splitApplication.run(args, System.in, outputStream);
        String outputFilePath1 = Paths.get(Environment.currentDirectory, Paths.get("xaa").toString()).toString();
        String outputFilePath2 = Paths.get(Environment.currentDirectory, Paths.get("xab").toString()).toString();

        File outputFile1 = new File(outputFilePath1);
        File outputFile2 = new File(outputFilePath2);

        String[] outputFile1Lines = outputFile1.toString().split(StringUtils.STRING_NEWLINE);
        assertEquals(1000, outputFile1Lines.length);
        String[] outputFile2Lines = outputFile2.toString().split(StringUtils.STRING_NEWLINE);
        assertEquals(10, outputFile2Lines.length);
    }

    @Test
    public void run_onLineArgumentWithValueArgument_splitsFileBySpecifiedValue() throws Exception {
        // Prepare args
        Path testsResourcesDir = Paths.get("tests", "resources", "impl", "app", "SplitApplicationResources", "line_50_filename");
        Path testFile = Paths.get("line_50_filename.txt");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[3];
        args[0] = "-l";
        args[1] = "50";
        args[2] = path;

        splitApplication.run(args, System.in, outputStream);
        String outputFilePath1 = Paths.get(Environment.currentDirectory, Paths.get("xaa").toString()).toString();
        String outputFilePath2 = Paths.get(Environment.currentDirectory, Paths.get("xab").toString()).toString();

        File outputFile1 = new File(outputFilePath1);
        File outputFile2 = new File(outputFilePath2);

        String[] outputFile1Lines = outputFile1.toString().split(StringUtils.STRING_NEWLINE);
        assertEquals(50, outputFile1Lines.length);
        String[] outputFile2Lines = outputFile2.toString().split(StringUtils.STRING_NEWLINE);
        assertEquals(50, outputFile2Lines.length);
    }

    @Test
    public void run_onBytesArgumentWithValueArgument_splitsFileBySpecifiedValue() throws Exception {
        // Prepare args
        Path testsResourcesDir = Paths.get("tests", "resources", "impl", "app", "SplitApplicationResources", "bytes_50_filename");
        Path testFile = Paths.get("bytes_50_filename.txt");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[3];
        args[0] = "-b";
        args[1] = "50";
        args[2] = path;

        splitApplication.run(args, System.in, outputStream);
        String outputFilePath1 = Paths.get(Environment.currentDirectory, Paths.get("xaa").toString()).toString();

        File outputFile1 = new File(outputFilePath1);

        assertEquals(50, Files.readAllBytes(outputFile1.toPath()).length);
    }

    @Test
    public void run_onTwoArguments_ThrowsException() {
        // Prepare args
        Path testsResourcesDir = Paths.get("tests", "resources", "impl", "app", "SplitApplicationResources", "line_bytes_filename");
        Path testFile = Paths.get("line_bytes_filename.txt");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[3];
        args[0] = "-l";
        args[1] = "-d";
        args[1] = path;

        // Assert right exception thrown
        ShellException shellException = assertThrows(ShellException.class, () -> splitApplication.run(args, System.in, outputStream));
        // Assert right message in exception
        assertEquals(new ShellException("split: invalid number of lines: ‘-b’").getMessage(), shellException.getMessage());
    }

    @Test
    public void run_onLineArgument_createsFileNamesWhichStartsWithZ() throws Exception {
        // Prepare args
        Path testsResourcesDir = Paths.get("tests", "resources", "impl", "app", "SplitApplicationResources", "line_1_filename");
        Path testFile = Paths.get("line_1_filename.txt");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[1];
        args[0] = "-l";
        args[1] = "1";
        args[2] = path;

        splitApplication.run(args, System.in, outputStream);
        assert(Paths.get(Environment.currentDirectory, Paths.get("zaa").toString()).toString() != null);
    }

    @Test
    public void run_onBytesArgument_createsFileNamesWhichStartsWithZ() throws Exception {
        // Prepare args
        Path testsResourcesDir = Paths.get("tests", "resources", "impl", "app", "SplitApplicationResources", "bytes_5_filename");
        Path testFile = Paths.get("bytes_5_filename.txt");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[1];
        args[0] = "-b";
        args[1] = "5";
        args[2] = path;

        splitApplication.run(args, System.in, outputStream);
        assert(Paths.get(Environment.currentDirectory, Paths.get("zaa").toString()).toString() != null);
    }

    @Test
    public void run_onLineArgumentWithPrefix_createsFileNamesStartingWithThatPrefix() throws Exception {
        // Prepare args
        Path testsResourcesDir = Paths.get("tests", "resources", "impl", "app", "SplitApplicationResources", "line_5_filename_prefix");
        Path testFile = Paths.get("line_5_filename_prefix.txt");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[1];
        args[0] = "-l";
        args[1] = "5";
        args[2] = path;
        args[3] = "vig";

        splitApplication.run(args, System.in, outputStream);
        assert(Paths.get(Environment.currentDirectory, Paths.get("vigaa").toString()).toString() != null);
    }

    @Test
    public void run_onByteArgumentWithPrefix_createsFileNamesStartingWithThatPrefix() throws Exception {
        // Prepare args
        Path testsResourcesDir = Paths.get("tests", "resources", "impl", "app", "SplitApplicationResources", "bytes_5_filename_prefix");
        Path testFile = Paths.get("bytes_5_filename_prefix.txt");
        String path = Paths.get(Environment.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[1];
        args[0] = "-b";
        args[1] = "5";
        args[2] = path;
        args[3] = "vig";

        splitApplication.run(args, System.in, outputStream);
        assert(Paths.get(Environment.currentDirectory, Paths.get("vigaa").toString()).toString() != null);
    }
}
