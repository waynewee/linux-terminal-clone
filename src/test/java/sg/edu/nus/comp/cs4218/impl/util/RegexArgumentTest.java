package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegexArgumentTest {

    /*
     * It is assumed that asterisks will not appear in the middle of the path.
     * Contents of Subdirectories are not tested because the test itself already uses subdirectories.
     */

    private static final String TEST_RESOURCE_PATH = "src/test/resources/impl/app/GlobResources/";
    private static final String SUBDIRECTORY1 = TEST_RESOURCE_PATH + "subdirectory1";
    private static final String SUBDIRECTORY2 = TEST_RESOURCE_PATH + "subdirectory2";
    private static final String FILE1 = TEST_RESOURCE_PATH + "test1.txt";
    private static final String FILE2 = TEST_RESOURCE_PATH + "test2.txt";
    private static final String FILE3 = TEST_RESOURCE_PATH + "alternate1.txt";
    private static final String FILE4 = TEST_RESOURCE_PATH + "alternate2.txt";

    RegexArgument testArg;

    @BeforeEach
    void setup() {
        testArg = new RegexArgument(TEST_RESOURCE_PATH);
    }

    @Test
    void globFiles_GetAllInCurrentDirectory_GetAllFilesAndSubdirectoriesInDirectory() {
        List<String> expected = Arrays.asList(FILE3, FILE4, SUBDIRECTORY1, SUBDIRECTORY2, FILE1, FILE2);
        testArg.appendAsterisk();
        List<String> results = testArg.globFiles();
        assertEquals(expected, results);
    }

    @Test
    void globFiles_WildcardAfter_ReturnsAllFilesAndSubdirectoriesStartingWithExpression() {
        List<String> expected = Arrays.asList(FILE1, FILE2);
        testArg.merge("te");
        testArg.appendAsterisk();
        List<String> results = testArg.globFiles();
        assertEquals(expected, results);
    }

    @Test
    void globFiles_WildcardBefore_ReturnsAllFilesAndSubdirectoriesEndingWithExpression() {
        List<String> expected = Arrays.asList(FILE3, FILE1);
        testArg.appendAsterisk();
        testArg.merge("1.txt");
        List<String> results = testArg.globFiles();
        assertEquals(expected, results);
    }

    @Test
    void globFiles_WildcardInBetween_ReturnsAllFilesAndSubdirectoriesStartingAndEndingWithExpression() {
        List<String> expected = Arrays.asList(FILE3, FILE4);
        testArg.append('a');
        testArg.appendAsterisk();
        testArg.merge("txt");
        List<String> results = testArg.globFiles();
        assertEquals(expected, results);
    }

    @Test
    void globFiles_ExpressionBetweenWildcards_ReturnsAllFilesAndSubdirectoriesContainingExpression() {
        List<String> expected = Arrays.asList(SUBDIRECTORY1, SUBDIRECTORY2);
        testArg.appendAsterisk();
        testArg.merge("directory");
        testArg.appendAsterisk();
        List<String> results = testArg.globFiles();
        assertEquals(expected, results);
    }
    
    @Test
    void globFiles_NoFilesFound_ReturnsPath() {
        List<String> expected = new LinkedList<>();
        expected.add(TEST_RESOURCE_PATH + "z");
        testArg.append('z');
        List<String> results = testArg.globFiles();
        assertEquals(expected, results);
    }
}