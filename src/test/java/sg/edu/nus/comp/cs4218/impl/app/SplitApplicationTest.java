package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Application;
import sg.edu.nus.comp.cs4218.EnvironmentUtil;
import sg.edu.nus.comp.cs4218.exception.SplitException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

class SplitApplicationTest {
    private static ByteArrayOutputStream outputStream;
    private static Application splitApplication;
    private static String baseInputString1 = "heythere";
    private static String baseInputString2 = "heythere" + StringUtils.STRING_NEWLINE;

    @BeforeAll
    static void prepareApplication() {
        splitApplication = new SplitApplication();
    }

    @BeforeEach
    void prepareOutputStream() {
        outputStream = new ByteArrayOutputStream();
    }

    @AfterAll
    static void cleanUp() {
        removeOutputFilesInCurrentDirectory();
    }

    // Null args
    @Test
    public void run_GivenNullForArgsParameter_ThrowsSplitException() {
        // Assert right exception thrown
        SplitException splitException = assertThrows(SplitException.class, () -> splitApplication.run(null, System.in, outputStream));
        // Assert right message in exception
        assertEquals(new SplitException(ERR_NULL_ARGS).getMessage(), splitException.getMessage());
    }

    // Null outputstream
    @Test
    public void run_GivenNullForOutputStreamParameter_ThrowsSplitException() {
        // Assert right exception thrown
        SplitException splitException = assertThrows(SplitException.class, () -> splitApplication.run(new String[0], System.in, null));
        // Assert right message in exception
        assertEquals(new SplitException(ERR_NO_OSTREAM).getMessage(), splitException.getMessage());
    }

    @Test
    public void run_onWrongFlags_ThrowsException() {
        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("wrong_arguments");
        Path testFile = Paths.get("wrong_arguments.txt");
        String path = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[2];
        args[0] = "-d";
        args[1] = path;

        // Assert right exception thrown
        SplitException splitException = assertThrows(SplitException.class, () -> splitApplication.run(args, System.in, outputStream));
        // Assert right message in exception
        assertEquals(new SplitException("illegal option -- d").getMessage(), splitException.getMessage());
    }


    @Test
    public void run_onOnlyLineFlag_ThrowsException() {
        // Prepare args
        String[] args = new String[1];
        args[0] = "-l";

        // Assert right exception thrown
        SplitException splitException = assertThrows(SplitException.class, () -> splitApplication.run(args, System.in, outputStream));
        // Assert right message in exception
        assertEquals(new SplitException(ERR_MISSING_ARG).getMessage(), splitException.getMessage());
    }

    @Test
    public void run_onOnlyByteFlag_ThrowsException() {
        // Prepare args
        String[] args = new String[1];
        args[0] = "-b";

        // Assert right exception thrown
        SplitException splitException = assertThrows(SplitException.class, () -> splitApplication.run(args, System.in, outputStream));
        // Assert right message in exception
        assertEquals(new SplitException(ERR_MISSING_ARG).getMessage(), splitException.getMessage());
    }

    @Test
    public void run_onLineArgumentInvalidFile_ThrowsException() {
        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("random_invalid_file");
        Path testFile = Paths.get("random_invalid_file");
        String path = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[3];
        args[0] = "-l";
        args[1] = "50";
        args[2] = path;

        // Assert right exception thrown
        assertThrows(SplitException.class, () -> splitApplication.run(args, System.in, outputStream));
    }

    @Test
    public void run_onNoArgumentsExceptFileName_splitsFileByLines() throws Exception {
        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("filename");
        Path testFile = Paths.get("filename.txt");
        String path = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[1];
        args[0] = path;

        splitApplication.run(args, System.in, outputStream);
        String outputFilePath = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), "xaa").toString();

        List<String> outputFileContents = Files.readAllLines(new File(outputFilePath).toPath());
        List<String> originalFileContents = Files.readAllLines(new File(path).toPath());

        assertEquals(outputFileContents, originalFileContents);

        removeOutputFiles(testsResourcesDir);
    }

    @Test
    public void run_onLineArgument_splitsFileByDefaultOneThousandLines() throws Exception {
        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("one_thousand_lines");
        Path testFile = Paths.get("one_thousand_lines.txt");
        String path = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[1];
        args[0] = path;

        splitApplication.run(args, System.in, outputStream);
        String outputFilePath1 = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), Paths.get("xaa").toString()).toString();
        String outputFilePath2 = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), Paths.get("xab").toString()).toString();

        File outputFile1 = new File(outputFilePath1);
        File outputFile2 = new File(outputFilePath2);

        List<String> outputFile1Lines = Files.readAllLines(outputFile1.toPath());
        List<String> outputFile2Lines = Files.readAllLines(outputFile2.toPath());
        assertEquals(1000, outputFile1Lines.size());
        assertEquals(3, outputFile2Lines.size());

        removeOutputFiles(testsResourcesDir);
    }

    @Test
    public void run_onLineArgumentWithValueArgument_splitsFileBySpecifiedValue() throws Exception {
        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("line_50_filename");
        Path testFile = Paths.get("line_50_filename.txt");
        String path = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[3];
        args[0] = "-l";
        args[1] = "50";
        args[2] = path;

        splitApplication.run(args, System.in, outputStream);
        String outputFilePath1 = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), Paths.get("xaa").toString()).toString();
        String outputFilePath2 = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), Paths.get("xab").toString()).toString();

        File outputFile1 = new File(outputFilePath1);
        File outputFile2 = new File(outputFilePath2);

        List<String> outputFile1Lines = Files.readAllLines(outputFile1.toPath());
        List<String> outputFile2Lines = Files.readAllLines(outputFile2.toPath());

        assertEquals(50, outputFile1Lines.size());
        assertEquals(50, outputFile2Lines.size());

        removeOutputFiles(testsResourcesDir);
    }

    @Test
    public void run_onBytesArgumentWithValueArgument_splitsFileBySpecifiedValue() throws Exception {
        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("bytes_50_filename");
        Path testFile = Paths.get("bytes_50_filename.txt");
        String path = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[3];
        args[0] = "-b";
        args[1] = "50";
        args[2] = path;

        splitApplication.run(args, System.in, outputStream);
        String outputFilePath1 = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), Paths.get("xaa").toString()).toString();

        File outputFile1 = new File(outputFilePath1);

        assertEquals(50, Files.readAllBytes(outputFile1.toPath()).length);

        removeOutputFiles(testsResourcesDir);
    }

    @Test
    public void run_onTwoArguments_ThrowsException() {
        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("line_bytes_filename");
        Path testFile = Paths.get("line_bytes_filename.txt");
        String path = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[3];
        args[0] = "-l";
        args[1] = "-d";
        args[1] = path;

        // Assert right exception thrown
        SplitException splitException = assertThrows(SplitException.class, () -> splitApplication.run(args, System.in, outputStream));
    }

    @Test
    public void run_onLineArgument_createsFileNamesWhichStartsWithZ() throws Exception {
        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("line_1_filename");
        Path testFile = Paths.get("line_1_filename.txt");
        String path = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[3];
        args[0] = "-l";
        args[1] = "1";
        args[2] = path;

        splitApplication.run(args, System.in, outputStream);
        Path outputFilePath = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), "zaa");
        assert(Files.exists(outputFilePath));

        removeOutputFiles(testsResourcesDir);
    }

    @Test
    public void run_onBytesArgument_createsFileNamesWhichStartsWithZ() throws Exception {
        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("bytes_5_filename");
        Path testFile = Paths.get("bytes_5_filename.txt");
        String path = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[3];
        args[0] = "-b";
        args[1] = "5";
        args[2] = path;

        splitApplication.run(args, System.in, outputStream);
        Path outputFilePath = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), "zaa");
        assert(Files.exists(outputFilePath));

        removeOutputFiles(testsResourcesDir);
    }

    @Test
    public void run_onLineArgumentWithPrefix_createsFileNamesStartingWithThatPrefix() throws Exception {
        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("line_5_filename_prefix");
        Path testFile = Paths.get("line_5_filename_prefix.txt");
        String path = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[4];
        args[0] = "-l";
        args[1] = "5";
        args[2] = path;
        args[3] = "vig";

        splitApplication.run(args, System.in, outputStream);
        Path outputFilePath = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), "vigaa");
        assert(Files.exists(outputFilePath));

        removeOutputFiles(testsResourcesDir);
    }

    @Test
    public void run_onByteArgumentWithPrefix_createsFileNamesStartingWithThatPrefix() throws Exception {
        // Prepare args
        Path testsResourcesDir = getTestsResourcesDir("bytes_5_filename_prefix");
        Path testFile = Paths.get("bytes_5_filename_prefix.txt");
        String path = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), testFile.toString()).toString();

        String[] args = new String[4];
        args[0] = "-b";
        args[1] = "5";
        args[2] = path;
        args[3] = "vig";

        splitApplication.run(args, System.in, outputStream);
        Path outputFilePath = Paths.get(EnvironmentUtil.currentDirectory, testsResourcesDir.toString(), "vigaa");
        assert(Files.exists(outputFilePath));

        removeOutputFiles(testsResourcesDir);
    }

    @Test
    public void run_onByteFlagWithStandardInput_SplitsInputIntoFiles() throws Exception {
        // Prepare Args
        String path = Paths.get(EnvironmentUtil.currentDirectory).toString();
        String[] args = new String[2];
        args[0] = "-b";
        args[1] = "5";

        // Prepare input stream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(("testshouldgiveme" + StringUtils.STRING_NEWLINE).getBytes());
        System.setIn(inputStream);

        splitApplication.run(args, inputStream, outputStream);
        inputStream.close();

        Path outputFilePath1 = Paths.get(EnvironmentUtil.currentDirectory, "xaa");
        Path outputFilePath2 = Paths.get(EnvironmentUtil.currentDirectory, "xab");
        Path outputFilePath3 = Paths.get(EnvironmentUtil.currentDirectory, "xac");

        assert(Files.exists(outputFilePath1));
        assert(Files.exists(outputFilePath2));
        assert(Files.exists(outputFilePath3));

        // Restore System.in
        System.setIn(System.in);

        // Remove output files in current directory
        removeOutputFilesInCurrentDirectory();
    }

    @Test
    public void run_onLineFlagWithStandardInput_SplitsInputIntoFiles() throws Exception {
        // Prepare Args
        String[] args = new String[2];
        args[0] = "-l";
        args[1] = "1";

        // Prepare input stream
        String inputString = baseInputString2.repeat(2);
        ByteArrayInputStream inputStream = new ByteArrayInputStream((inputString + StringUtils.STRING_NEWLINE).getBytes());
        System.setIn(inputStream);

        splitApplication.run(args, inputStream, outputStream);
        inputStream.close();

        Path outputFilePath1 = Paths.get(EnvironmentUtil.currentDirectory, "xaa");
        Path outputFilePath2 = Paths.get(EnvironmentUtil.currentDirectory, "xab");

        assert(Files.exists(outputFilePath1));
        assert(Files.exists(outputFilePath2));

        // Restore System.in
        System.setIn(System.in);

        // Remove output files in current directory
        removeOutputFilesInCurrentDirectory();
    }

    @Test
    public void run_onByteFlagWithBSuffixStandardInput_SplitsInputIntoFiles() throws Exception {
        // Prepare Args
        String[] args = new String[2];
        args[0] = "-b";
        args[1] = "1b";

        // Prepare input stream
        String inputString = baseInputString1.repeat(70);

        ByteArrayInputStream inputStream = new ByteArrayInputStream((inputString + StringUtils.STRING_NEWLINE).getBytes());
        System.setIn(inputStream);

        splitApplication.run(args, inputStream, outputStream);
        inputStream.close();

        Path outputFilePath1 = Paths.get(EnvironmentUtil.currentDirectory, "xaa");
//
        assert(Files.exists(outputFilePath1));

        // Restore System.in
        System.setIn(System.in);

        // Remove output files in current directory
        removeOutputFilesInCurrentDirectory();
    }

    @Test
    public void run_onByteFlagWithKSuffixStandardInput_SplitsInputIntoFiles() throws Exception {
        // Prepare Args
        String path = Paths.get(EnvironmentUtil.currentDirectory).toString();
        String[] args = new String[2];
        args[0] = "-b";
        args[1] = "1k";

        // Prepare input stream
        String inputString = baseInputString1.repeat(150);

        ByteArrayInputStream inputStream = new ByteArrayInputStream((inputString + StringUtils.STRING_NEWLINE).getBytes());
        System.setIn(inputStream);

        splitApplication.run(args, inputStream, outputStream);
        inputStream.close();

        Path outputFilePath1 = Paths.get(EnvironmentUtil.currentDirectory, "xaa");

        assert(Files.exists(outputFilePath1));
        // Restore System.in
        System.setIn(System.in);

        // Remove output files in current directory
        removeOutputFilesInCurrentDirectory();
    }

    @Test
    public void run_onByteFlagWithMSuffixStandardInput_SplitsInputIntoFiles() throws Exception {
        // Prepare Args
        String path = Paths.get(EnvironmentUtil.currentDirectory).toString();
        String[] args = new String[2];
        args[0] = "-b";
        args[1] = "1m";

        // Prepare input stream
        String inputString = baseInputString1.repeat(400000);

        ByteArrayInputStream inputStream = new ByteArrayInputStream((inputString + StringUtils.STRING_NEWLINE).getBytes());
        System.setIn(inputStream);

        splitApplication.run(args, inputStream, outputStream);
        inputStream.close();

        Path outputFilePath1 = Paths.get(EnvironmentUtil.currentDirectory, "xaa");
        Path outputFilePath2 = Paths.get(EnvironmentUtil.currentDirectory, "xab");

        assert(Files.exists(outputFilePath1));
        assert(Files.exists(outputFilePath2));
        // Restore System.in
        System.setIn(System.in);

        // Remove output files in current directory
        removeOutputFilesInCurrentDirectory();
    }

    // Helper methods
    private void removeOutputFiles(Path testsResourcesDir) {
        File dir = testsResourcesDir.toFile();
        for (File file: dir.listFiles()) {
            if (!file.getName().endsWith("txt")) {
                file.delete();
            }
        }
    }
    private static void removeOutputFilesInCurrentDirectory() {
        File dir = Paths.get(EnvironmentUtil.currentDirectory).toFile();
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("xa")) {
                file.delete();
            }
        }
    }

    private Path getTestsResourcesDir(String argument) {
        return Paths.get("src", "test", "resources", "impl", "app", "SplitApplicationResources", argument);
    }
}
