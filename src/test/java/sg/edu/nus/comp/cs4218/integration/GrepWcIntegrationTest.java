package sg.edu.nus.comp.cs4218.integration;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

/**
 * A very common use case for UNIX shells:
 * 1. grep for a certain pattern
 * 2. count the number of lines of that pattern using wc
 */
public class GrepWcIntegrationTest {
    // categories and partitions:
    // - length of grepped file
    //  -> no-line
    //  -> one-line
    //  -> multiple-lines
    //  -> non-existent-file [error]
    // - number of matched lines
    //  -> no-line-matched
    //  -> one-line-matched
    //  -> multiple-lines-matched
    // - input direction operator used
    //  -> output-redirection
    //  -> pipe
    //  -> an-invalid-one [error]

    // output from microsoft pict (for generating pairwise test cases):
    //    length of grepped file | number of matched lines | input direction operator used
    //    non-existent-file       no-line-matched pipe
    //    one-line        one-line-matched        pipe
    //    no-line multiple-lines-matched  output-redirection
    //    non-existent-file       multiple-lines-matched  an-invalid-one
    //    multiple-lines  no-line-matched output-redirection
    //    non-existent-file       one-line-matched        output-redirection
    //    one-line        no-line-matched an-invalid-one
    //    no-line multiple-lines-matched  pipe
    //    no-line one-line-matched        an-invalid-one
    //    one-line        multiple-lines-matched  output-redirection
    //    multiple-lines  one-line-matched        an-invalid-one
    //    multiple-lines  multiple-lines-matched  pipe
    //    no-line no-line-matched an-invalid-one

    // test cases after applying error constraint:
    //    length of grepped file | number of matched lines | input direction operator used
    //    one-line        one-line-matched        pipe
    //    no-line multiple-lines-matched  output-redirection (impossible)
    //    multiple-lines  no-line-matched output-redirection
    //    non-existent-file x x
    //    no-line multiple-lines-matched  pipe (impossible)
    //    one-line        multiple-lines-matched  output-redirection (impossible)
    //    multiple-lines  multiple-lines-matched  pipe
    //    x x an-invalid-one

    // test cases after removing impossible scenarios:
    //    length of grepped file | number of matched lines | input direction operator used
    //    one-line        one-line-matched        pipe
    //    multiple-lines  no-line-matched output-redirection
    //    non-existent-file x x
    //    multiple-lines  multiple-lines-matched  pipe
    //    x x an-invalid-one

    private static Shell testShell;
    private static OutputStream mockOutputStream;
    private static final String ONE_LINER_PATH = String.format(
            "src%1$stest%1$sresources%1$simpl%1$sapp%1$sIntegrationResources%1$sGrepWc%1$soneLiner.txt",
            File.separator
            );
    private static final String MULTIPLE_LINER_PATH = String.format(
            "src%1$stest%1$sresources%1$simpl%1$sapp%1$sIntegrationResources%1$sGrepWc%1$smultipleLiner.txt",
            File.separator
    );

    @BeforeEach
    void setupShell() {
        testShell = new ShellImpl();
        mockOutputStream = new ByteArrayOutputStream();
    }

    @Test
    /*
     * length of grepped file | number of matched lines | input direction operator used
     * one-line        one-line-matched        pipe
     * happy path 1: grep a file that only contains 1 line, and that 1 line matches that particular pattern
     */
    public void parseAndEvaluate_grepHappyOneLinerAndPipeToWc_showsOneLine() throws Exception {
        String command = String.format("grep -i \"bob the builder\" %s | wc -l \n", ONE_LINER_PATH);
        testShell.parseAndEvaluate(command, mockOutputStream);
        assertEquals(" 1 stdin" + System.lineSeparator(), mockOutputStream.toString());
    }

    @Test
    /**
     * length of grepped file | number of matched lines | input direction operator used
     *   multiple-lines  no-line-matched output-redirection
     */
    public void parseAndEvaluate_grepMultipleLinesFileButNoMatchesAndOutputRedirection_showsEmptyFile() throws AbstractApplicationException, ShellException {
        String commandOne = String.format("grep -i \"unmatchable\" %s > matches.txt\n", MULTIPLE_LINER_PATH);
        testShell.parseAndEvaluate(commandOne, mockOutputStream);
        String commandTwo = "wc -l matches.txt\n";
        testShell.parseAndEvaluate(commandTwo, mockOutputStream);
        assertEquals(" 1 matches.txt" + System.lineSeparator(), mockOutputStream.toString());
    }

    @Test
    /**
     * multiple-lines  multiple-lines-matched  pipe
     * happy path 2: grep a file that contains 2 lines, and that both lines matches that particular pattern
     **/
    public void parseAndEvaluate_grepHappyTwoLinerAndPipeToWc_showsTwoLines() throws Exception {
        String command = String.format("grep -i \"bob the builder\" %s | wc -l \n", MULTIPLE_LINER_PATH);
        testShell.parseAndEvaluate(command, mockOutputStream);
        assertEquals(" 2 stdin" + System.lineSeparator(), mockOutputStream.toString());
    }

    @Test
    /*
     */
    public void parseAndEvaluate_grepNonExistentFile_throwsError() throws Exception {
        String command = "grep -i \"bob the builder\" nonExistentFile.txt | wc -l \n";
        assertThrows(Exception.class, () -> testShell.parseAndEvaluate(command, mockOutputStream));
    }

    @Test
    /*
    //    x x an-invalid-one
     */
    public void parseAndEvaluate_grepOneLinerButInvalidRedirectionOperator_throwsError(@TempDir Path dir) throws Exception {
        String command = String.format("grep -i \"bob the builder\" %s { wc -l \n", MULTIPLE_LINER_PATH);
        assertThrows(Exception.class, () -> testShell.parseAndEvaluate(command, mockOutputStream));
    }
}
