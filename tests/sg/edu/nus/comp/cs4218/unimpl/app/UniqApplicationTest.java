package sg.edu.nus.comp.cs4218.unimpl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;


class UniqApplicationTest {

    private static UniqApplication uniqApplication;

    private static final String PATH = "tests/resources/impl/app/UniqApplicationResources/";

    private static final String FILE_NO_DUP = "no-duplicate.txt";
    private static final String FILE_NO_ADJ = "duplicate-no-adjacent.txt";
    private static final String FILE_DUP_ADJ = "duplicate-adjacent.txt";
    private static final String FILE_OUTPUT = "output.txt";

    private static final String PATH_NO_DUP = PATH + FILE_NO_DUP;
    private static final String PATH_NO_ADJ = PATH + FILE_NO_ADJ;
    private static final String PATH_DUP_ADJ = PATH + FILE_DUP_ADJ;
    private static final String PATH_OUTPUT = PATH + FILE_OUTPUT;

    private static final String STDIN_NO_DUP = "This test file does not contain any duplicate text" +
    "This file test doesn't contain duplicate lines." +
    "This test files doesn't have duplicate lines.";
    private static final String STDIN_NO_ADJ = "This file contains duplicate lines" +
            STRING_NEWLINE +
            "But none of them are adjacent" +
            STRING_NEWLINE +
            "This file contains duplicate lines" +
            STRING_NEWLINE +
            "But none of them are adjacent" +
            STRING_NEWLINE +
            "This file contains duplicate lines" +
            STRING_NEWLINE +
            "But none of them are adjacent";
    private static final String STDIN_DUP_ADJ = "This file contains multiple duplicate lines" +
            STRING_NEWLINE +
            "This file contains multiple duplicate lines" +
            STRING_NEWLINE +
            "This file does contain duplicates" +
            STRING_NEWLINE +
            "And they are adjacent" +
            STRING_NEWLINE +
            "And they are adjacent" +
            STRING_NEWLINE +
            "And they are adjacent" +
            STRING_NEWLINE +
            "This file does contain duplicates" +
            STRING_NEWLINE +
            "This file does contain duplicates" +
            STRING_NEWLINE +
            "And they are adjacent" +
            STRING_NEWLINE +
            "And they are adjacent";

    @BeforeAll
    static void setupShell() {
        uniqApplication = new UniqApplication();
    }

    @Test
    public void uniq_PrefixLinesTrueGroupDuplicateTrueAllDuplicateTrueInputFileSpecifiedOutputFileSpecified_ReturnsAllDuplicatesPrefixed() {
        uniqApplication.uniq(true, true, true, PATH_DUP_ADJ, PATH_OUTPUT);
        String result = readOutputFile();
        assertEquals("2 This file contains multiple duplicate lines" +
                STRING_NEWLINE +
                "2 This file contains multiple duplicate lines" +
                STRING_NEWLINE +
                "3 And they are adjacent" +
                STRING_NEWLINE +
                "3 And they are adjacent" +
                STRING_NEWLINE +
                "3 And they are adjacent" +
                STRING_NEWLINE +
                "2 This file does contain duplicates" +
                STRING_NEWLINE +
                "2 This file does contain duplicates" +
                STRING_NEWLINE +
                "2 And they are adjacent" +
                STRING_NEWLINE +
                "2 And they are adjacent", result);
    }

    @Test
    public void uniq_PrefixLinesTrueGroupDuplicateFalseAllDuplicateFalseInputFileNotSpecifiedOutputFileNotSpecified_Returns() {
        String result = uniqApplication.uniq(true, false, false, STDIN_DUP_ADJ);
        assertEquals("2 This file contains multiple duplicate lines" +
                STRING_NEWLINE +
                "3 And they are adjacent" +
                STRING_NEWLINE +
                "2 This file does contain duplicates" +
                STRING_NEWLINE +
                "2 And they are adjacent", result);
    }

    @Test
    public void uniq_PrefixLinesFalseGroupDuplicateFalseAllDuplicateFalseInputFileNotSpecifiedOutputFileSpecified_Returns() {
        uniqApplication.uniq(false, false, false, STDIN_DUP_ADJ, PATH_OUTPUT);
        String result = readOutputFile();
        assertEquals("This file contains multiple duplicate lines" +
                STRING_NEWLINE +
                "And they are adjacent" +
                STRING_NEWLINE +
                "This file does contain duplicates" +
                STRING_NEWLINE +
                "And they are adjacent", result);
    }

    @Test
    public void uniq_PrefixLinesFalseGroupDuplicateTrueAllDuplicateFalseInputFileSpecifiedOutputFileNotSpecified_Returns() {
        String result = uniqApplication.uniq(false, true, false, PATH_DUP_ADJ);
        assertEquals("This file contains multiple duplicate lines" +
                STRING_NEWLINE +
                "And they are adjacent" +
                STRING_NEWLINE +
                "This file does contain duplicates", result);
    }

    @Test
    public void uniq_NoDuplicateInputPrefixLines_Returns() {
        String result = uniqApplication.uniq(true, false, false, STDIN_NO_DUP);
        assertEquals("", result);
    }

    @Test
    public void uniq_NoDuplicateInputGroupDuplicate_Returns() {
        String result = uniqApplication.uniq(false, true, false, STDIN_NO_DUP);
        assertEquals("", result);
    }

    @Test
    public void uniq_NoDuplicateInputAllDuplicate_Returns() {
        String result = uniqApplication.uniq(false, false, true, STDIN_NO_DUP);
        assertEquals("", result);
    }

    @Test
    public void uniq_NoAdjacentInputPrefixLines_Returns() {
        String result = uniqApplication.uniq(true, false, false, STDIN_NO_ADJ);
        assertEquals("", result);
    }

    @Test
    public void uniq_NoAdjacentInputGroupDuplicate_Returns() {
        String result = uniqApplication.uniq(false, true, false, STDIN_NO_ADJ);
        assertEquals("", result);
    }

    @Test
    public void uniq_NoAdjacentInputAllDuplicate_Returns() {
        String result = uniqApplication.uniq(false, false, true, STDIN_NO_ADJ);
        assertEquals("", result);
    }

    private String readOutputFile() {
        StringBuilder result = new StringBuilder();
        try {
            File file = new File(PATH_OUTPUT);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                result.append(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            return "Error";
        }
        return result.toString();
    }

}