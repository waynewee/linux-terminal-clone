package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.GrepException;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GrepApplicationTest {

    private static GrepApplication grepApplication;

    private static final String PATH = "src/test/resources/impl/app/GrepApplicationResources/".replace('/', CHAR_FILE_SEP);

    private static final String FILE_MULTI_1 = "multi-line-1.txt";
    private static final String FILE_MULTI_2 = "multi-line-2.txt";
    private static final String FILE_SINGLE_1 = "single-line-1.txt";
    private static final String FILE_SINGLE_2 = "single-line-1.txt";
    private static final String FILE_NOT_EXIST = "not-exist.txt";
    private static final String FILE_EMPTY = "empty.txt";

    private static final String DIRECTORY = "directory";

    private static final String TEXT_MULTI_1_1 = "Hi, I like apples.";
    private static final String TEXT_MULTI_1_2 = "What fruits do you like?";
    private static final String TEXT_MULTI_1_3 = "I like apples!";
    private static final String TEXT_MULTI_1_4 = "Apples are my favourite.";
    private static final String TEXT_MULTI_1_5 = "Do you like pears?";
    private static final String TEXT_MULTI_1_6 = "No, only apples.";
    private static final String TEXT_MULTI_2_1 = "This is a single multi-line file.";
    private static final String TEXT_MULTI_2_2 = "It contains multiple lines.";
    private static final String TEXT_MULTI_2_3 = "Do you know. Gods of death love apples?";
    private static final String TEXT_SINGLE_1 = "Hello World!";
    private static final String TEXT_SINGLE_2 = "This is a test file.";

    private static final String PATH_MULTI_1 = PATH + FILE_MULTI_1;
    private static final String PATH_MULTI_2 = PATH + FILE_MULTI_2;
    private static final String PATH_SINGLE_1 = PATH + FILE_SINGLE_1;
    private static final String PATH_SINGLE_2 = PATH + FILE_SINGLE_2;
    private static final String PATH_NOT_EXIST = PATH + FILE_NOT_EXIST;
    private static final String PATH_EMPTY = PATH + FILE_EMPTY;
    private static final String PATH_DIRECTORY = PATH + DIRECTORY;

    private static final String STDIN_MULTI_1_1 = "Dogs are my favourite colour.";
    private static final String STDIN_MULTI_1_2 = "I love cats too.";
    private static final String STDIN_MULTI_1_3 = "But dogs are my favorite.";
    private static final String STDIN_MULTI_2_1 = "Woof! Woof!";
    private static final String STDIN_MULTI_2_2 = "Said the doggy.";
    private static final String STDIN_MULTI_3_1 = "My favourite fruits are:";
    private static final String STDIN_MULTI_3_2 = "APPLES, oranges, PEARS!";

    private static final String STDIN_MULTI_1 = STDIN_MULTI_1_1 + '\n' + STDIN_MULTI_1_2 + '\n' + STDIN_MULTI_1_3;
    private static final String STDIN_MULTI_2 = STDIN_MULTI_2_1 + '\n' + STDIN_MULTI_2_2;
    private static final String STDIN_MULTI_3 = STDIN_MULTI_3_1 + '\n' + STDIN_MULTI_3_2;
    private static final String STDIN_SINGLE_1 = "Hello World";
    private static final String STDIN_SINGLE_2 = "This is a test string.";

    private static final String PATTERN_DOG = "dog";
    private static final String PATTERN_APPLES = "apples";
    private static final String PATTERN_APPLES_UP = "APPLES";
    private static final String PATTERN_HELLO = "hello world";
    private static final String PATTERN_INVALID = "Test\\";
    private static final String PATTERN_VALID = "valid pattern";

    private static final String STDIN = "stdin";

    private static final String NUMBER_FORMAT = ": %d";
    private static final String STRING_FORMAT = "%s: ";

    @BeforeAll
    public static void setupShell() {
        grepApplication = new GrepApplication();
    }

    @Test
    public void grepFromFiles_PatternMissingCaseInsensitiveTrueLinesTruePrefixFileNameTrueFilesNotAllValid_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFiles(null, true, true, true, PATH_MULTI_1, PATH_NOT_EXIST);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFiles_PatternMissingCaseInsensitiveFalseLinesFalsePrefixFileNameTrueFilesAllValid_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFiles(null, false, false, true, PATH_MULTI_1, PATH_SINGLE_1);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFiles_PatternInvalidCaseInsensitiveFalseLinesTruePrefixFileNameTrueFilesNone_ReturnsEmpty() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_INVALID, false, true, true);
        assertEquals(STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFiles_PatternInvalidCaseInsensitiveTrueLinesFalsePrefixFileNameTrueFilesNoValid_ReturnsError() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_INVALID, true, false, true, PATH_NOT_EXIST);
        assertEquals(new GrepException(String.format(STRING_FORMAT, FILE_NOT_EXIST) + ERR_FILE_NOT_FOUND + STRING_NEWLINE).getMessage(), result);
    }

    @Test
    public void grepFromFiles_PatternInvalidCaseInsensitiveFalseLinesTruePrefixFileNameTrueFilesNotAllValid_ThrowsInvalidRegexException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFiles(PATTERN_INVALID, false, true, true, PATH_NOT_EXIST, PATH_SINGLE_1);
        });
        assertEquals(new GrepException(ERR_INVALID_REGEX).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFiles_PatternInvalidCaseInsensitiveTrueLinesFalsePrefixFileNameTrueFilesAllValid_ThrowsInvalidRegexException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFiles(PATTERN_INVALID, true, false, true, PATH_MULTI_1, PATH_SINGLE_1);
        });
        assertEquals(new GrepException(ERR_INVALID_REGEX).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFiles_PatternValidCaseInsensitiveTrueLinesTruePrefixFileNameTrueFilesNone_ReturnsEmpty() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_VALID, true, true, true);
        assertEquals(STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFiles_PatternValidCaseInsensitiveFalseLinesFalsePrefixFileNameTrueFilesNoValid_ReturnsError() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_INVALID, false, false, true, PATH_NOT_EXIST);
        assertEquals(new GrepException(String.format(STRING_FORMAT, FILE_NOT_EXIST) + ERR_FILE_NOT_FOUND + STRING_NEWLINE).getMessage(), result);
    }

    @Test
    public void grepFromFiles_PatternValidCaseInsensitiveTrueLinesTruePrefixFileNameTrueFilesNotAllValid_ReturnsErrorAndCount() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_APPLES, true, true, true, PATH_MULTI_1, PATH_NOT_EXIST);
        assertEquals(FILE_MULTI_1 +
                String.format(NUMBER_FORMAT, 4) +
                STRING_NEWLINE +
                new GrepException(String.format(STRING_FORMAT, FILE_NOT_EXIST) + ERR_FILE_NOT_FOUND).getMessage() +
                STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFiles_PatternValidCaseInsensitiveFalseLinesFalsePrefixFileNameTrueFilesAllValid_ReturnsLines() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_APPLES, false, false, true, PATH_MULTI_1, PATH_MULTI_2);
        assertEquals(String.format(STRING_FORMAT, FILE_MULTI_1) +
                TEXT_MULTI_1_1 +
                STRING_NEWLINE +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                TEXT_MULTI_1_3 +
                STRING_NEWLINE +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                TEXT_MULTI_1_6 +
                STRING_NEWLINE +
                String.format(STRING_FORMAT, FILE_MULTI_2) +
                TEXT_MULTI_2_3 +
                STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFiles_PatternMissingCaseInsensitiveFalseLinesTruePrefixFileNameTrueFilesNone_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFiles(null, false, true, true);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFiles_PatternMissingCaseInsensitiveTrueLinesFalsePrefixFileNameTrueFilesNoValid_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFiles(null, true, false, true, PATH_EMPTY);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFiles_PatternMissingCaseInsensitiveFalseLinesTruePrefixFileNameTrueFilesNotAllValid_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFiles(null, false, true, true, PATH_EMPTY, PATH_SINGLE_1);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFiles_PatternMissingCaseInsensitiveTrueLinesFalsePrefixFileNameTrueFilesAllValid_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFiles(null, true, false, true, PATH_SINGLE_1, PATH_MULTI_1);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromStdin_PatternMissingCaseInsensitiveTrueLinesTruePrefixFileNameTrueInputStreamEmpty_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromStdin(null, true, true, true, InputStream.nullInputStream());
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromStdin_PatternInvalidCaseInsensitiveTrueLinesFalsePrefixFileNameTrueInputStreamValid_ThrowsInvalidRegexException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromStdin(PATTERN_INVALID, true, false, true, new ByteArrayInputStream(STDIN_MULTI_1.getBytes(StandardCharsets.UTF_8)));
        });
        assertEquals(new GrepException(ERR_INVALID_REGEX).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromStdin_PatternInvalidCaseInsensitiveFalseLinesTruePrefixFileNameFalseInputStreamEmpty_ThrowsInvalidRegexException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromStdin(PATTERN_INVALID, false, true, false, InputStream.nullInputStream());
        });
        assertEquals(new GrepException(ERR_INVALID_REGEX).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromStdin_PatternInvalidCaseInsensitiveTrueLinesFalsePrefixFileNameTrueInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromStdin(PATTERN_INVALID, true, false, true, null);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromStdin_PatternValidCaseInsensitiveTrueLinesTruePrefixFileNameTrueInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromStdin(PATTERN_VALID, true, true, true, null);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromStdin_PatternValidCaseInsensitiveFalseLinesFalsePrefixFileNameFalseInputStreamValid_ReturnsLines() throws Exception {
        String result = grepApplication.grepFromStdin(PATTERN_DOG, false, false, false, new ByteArrayInputStream(STDIN_MULTI_2.getBytes(StandardCharsets.UTF_8)));
        assertEquals(STDIN_MULTI_2_2 + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromStdin_PatternValidCaseInsensitiveTrueLinesTruePrefixFileNameTrueInputStreamEmpty_ReturnsCount() throws Exception {
        String result = grepApplication.grepFromStdin(PATTERN_VALID, true, true, true, InputStream.nullInputStream());
        assertEquals("0" + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromStdin_PatternValidCaseInsensitiveFalseLinesFalsePrefixFileNameFalseInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromStdin(PATTERN_VALID, false, false, false, null);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromStdin_PatternMissingCaseInsensitiveFalseLinesTruePrefixFileNameFalseInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromStdin(null, false, true, false, null);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromStdin_PatternMissingCaseInsensitiveTrueLinesFalsePrefixFileNameTrueInputStreamValid_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromStdin(null, true, false, true, new ByteArrayInputStream(STDIN_MULTI_1.getBytes(StandardCharsets.UTF_8)));
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromStdin_PatternMissingCaseInsensitiveFalseLinesTruePrefixFileNameFalseInputStreamEmpty_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromStdin(null, false, true, false, InputStream.nullInputStream());
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromStdin_PatternMissingCaseInsensitiveTrueLinesFalsePrefixFileNameTrueInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromStdin(null, true, false, true, null);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternMissingCaseInsensitiveTrueLinesTruePrefixFileNameTrueFilesNoValidInputStreamEmpty_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(null, true, true, true, InputStream.nullInputStream(), PATH_EMPTY);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternInvalidCaseInsensitiveFalseLinesTruePrefixFileNameFalseFilesAllValidInputStreamValid_ThrowsInvalidRegexException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(PATTERN_INVALID, false, true, false, new ByteArrayInputStream(STDIN_MULTI_1.getBytes(StandardCharsets.UTF_8)), PATH_MULTI_2, PATH_MULTI_1);
        });
        assertEquals(new GrepException(ERR_INVALID_REGEX).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternInvalidCaseInsensitiveFalseLinesTruePrefixFileNameTrueFilesNoneInputStreamEmpty_ThrowsInvalidRegexException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(PATTERN_INVALID, false, true, true, InputStream.nullInputStream());
        });
        assertEquals(new GrepException(ERR_INVALID_REGEX).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternInvalidCaseInsensitiveTrueLinesTruePrefixFileNameFalseFilesNoValidInputStreamValid_ThrowsInvalidRegexException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(PATTERN_INVALID, true, true, false, new ByteArrayInputStream(STDIN_SINGLE_2.getBytes(StandardCharsets.UTF_8)), PATH_NOT_EXIST);
        });
        assertEquals(new GrepException(ERR_INVALID_REGEX).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternInvalidCaseInsensitiveTrueLinesFalsePrefixFileNameTrueFilesNotAllValidInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(PATTERN_INVALID, true, false, true, null, PATH_NOT_EXIST, PATH_MULTI_1);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternValidCaseInsensitiveTrueLinesTruePrefixFileNameFalseFilesNotAllValidInputStreamValid_ReturnsErrorAndLines() throws Exception {
        String result = grepApplication.grepFromFileAndStdin(PATTERN_APPLES, true, true, false, new ByteArrayInputStream(STDIN_MULTI_3.getBytes(StandardCharsets.UTF_8)), PATH_NOT_EXIST, PATH_MULTI_2);
        assertEquals(new GrepException(String.format(STRING_FORMAT, FILE_NOT_EXIST) + ERR_FILE_NOT_FOUND).getMessage() +
                STRING_NEWLINE +
                FILE_MULTI_2 +
                String.format(NUMBER_FORMAT, 1) +
                STRING_NEWLINE +
                STDIN +
                String.format(NUMBER_FORMAT, 1) +
                STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFileAndStdin_PatternValidCaseInsensitiveFalseLinesTruePrefixFileNameTrueFilesAllValidInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(PATTERN_VALID, false, true, true, null, PATH_MULTI_2, PATH_MULTI_1);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternValidCaseInsensitiveTrueLinesFalsePrefixFileNameFalseFilesAllValidInputStreamEmpty_ReturnsLines() throws Exception {
        String result = grepApplication.grepFromFileAndStdin(PATTERN_APPLES, true, false, false, InputStream.nullInputStream(), PATH_MULTI_1, PATH_MULTI_2, PATH_SINGLE_1);
        assertEquals(TEXT_MULTI_1_1 +
                STRING_NEWLINE +
                TEXT_MULTI_1_3 +
                STRING_NEWLINE +
                TEXT_MULTI_1_4 +
                STRING_NEWLINE +
                TEXT_MULTI_1_6 +
                STRING_NEWLINE +
                TEXT_MULTI_2_3 +
                STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFileAndStdin_PatternValidCaseInsensitiveTrueLinesTruePrefixFileNameTrueFilesNoneInputStreamValid_ReturnsLines() throws Exception {
        String result = grepApplication.grepFromFileAndStdin(PATTERN_APPLES, true, true, true, new ByteArrayInputStream(STDIN_MULTI_3.getBytes(StandardCharsets.UTF_8)));
        assertEquals(STDIN + String.format(NUMBER_FORMAT, 1) + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFileAndStdin_PatternValidCaseInsensitiveFalseLinesFalsePrefixFileNameTrueFilesNoValidInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(PATTERN_VALID, false, false, true, null, PATH_EMPTY);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternMissingCaseInsensitiveFalseLinesFalsePrefixFileNameTrueFilesNoValidInputStreamValid_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(null, false, false, true, new ByteArrayInputStream(STDIN_MULTI_3.getBytes(StandardCharsets.UTF_8)), PATH_EMPTY);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternMissingCaseInsensitiveTrueLinesTruePrefixFileNameTrueFilesNotAllValidInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(null, true, true, true, null, PATH_EMPTY, PATH_MULTI_2);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternMissingCaseInsensitiveFalseLinesTruePrefixFileNameTrueFilesAllValidInputStreamValid_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(null, false, true, true, new ByteArrayInputStream(STDIN_MULTI_2.getBytes(StandardCharsets.UTF_8)), PATH_SINGLE_1, PATH_MULTI_1);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternMissingCaseInsensitiveTrueLinesTruePrefixFileNameFalseFilesNoneInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(null, true, true, false, null);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternInvalidCaseInsensitiveTrueLinesFalsePrefixFileNameTrueFilesNoneInputStreamValid_ThrowsInvalidRegexException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(PATTERN_INVALID, true, false, true, new ByteArrayInputStream(STDIN_SINGLE_2.getBytes(StandardCharsets.UTF_8)));
        });
        assertEquals(new GrepException(ERR_INVALID_REGEX).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternInvalidCaseInsensitiveTrueLinesTruePrefixFileNameFalseFilesNoValidInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(PATTERN_INVALID, true, true, false, null, PATH_NOT_EXIST);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternInvalidCaseInsensitiveFalseLinesTruePrefixFileNameTrueFilesNotAllValidInputStreamEmpty_ThrowsInvalidRegexException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(PATTERN_INVALID, false, true, true, InputStream.nullInputStream(), PATH_NOT_EXIST, PATH_MULTI_1);
        });
        assertEquals(new GrepException(ERR_INVALID_REGEX).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternInvalidCaseInsensitiveTrueLinesFalsePrefixFileNameTrueFilesAllValidInputStreamValid_ThrowsInvalidRegexException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(PATTERN_INVALID, true, false, true, new ByteArrayInputStream(STDIN_MULTI_2.getBytes(StandardCharsets.UTF_8)), PATH_MULTI_1, PATH_MULTI_2);
        });
        assertEquals(new GrepException(ERR_INVALID_REGEX).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFileAndStdin_PatternInvalidCaseInsensitiveFalseLinesTruePrefixFileNameFalseFilesAllValidInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.grepFromFileAndStdin(PATTERN_INVALID, false, true, false, null, PATH_SINGLE_1, PATH_MULTI_1);
        });
        assertEquals(new GrepException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void grepFromFiles_FileEmptyLinesTrue_ReturnsCount() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_VALID, true, true, true, PATH_EMPTY);
        assertEquals("0" + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFiles_FileSingleLineLinesTrue_ReturnsCount() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_HELLO, true, true, true, PATH_SINGLE_1);
        assertEquals("1" + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFiles_FileMultiLineLinesTrue_ReturnsCount() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_APPLES, true, true, true, PATH_MULTI_2);
        assertEquals("1" + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromStdin_InputStreamEmptyLinesTrue_ReturnsCount() throws Exception {
        String result = grepApplication.grepFromStdin(PATTERN_VALID, true, true, true, InputStream.nullInputStream());
        assertEquals("0" + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromStdin_InputStreamSingleLineLinesTrue_ReturnsCount() throws Exception {
        String result = grepApplication.grepFromStdin(PATTERN_HELLO, true, true, true, new ByteArrayInputStream(STDIN_SINGLE_1.getBytes(StandardCharsets.UTF_8)));
        assertEquals("1" + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromStdin_InputStreamMultiLineLinesTrue_ReturnsCount() throws Exception {
        String result = grepApplication.grepFromStdin(PATTERN_DOG, true, true, true, new ByteArrayInputStream(STDIN_MULTI_1.getBytes(StandardCharsets.UTF_8)));
        assertEquals("2" + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFiles_FileMultiLinesTrue_ReturnsCount() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_APPLES, true, true, true, PATH_MULTI_1, PATH_MULTI_2);
        assertEquals(FILE_MULTI_1 + String.format(NUMBER_FORMAT, 4) +
                STRING_NEWLINE +
                FILE_MULTI_2 + String.format(NUMBER_FORMAT, 1) +
                STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFileAndStdin_FileMultiInputStreamMultiLineLinesTrue_ReturnsCount() throws Exception {
        String result = grepApplication.grepFromFileAndStdin(PATTERN_APPLES, true, true, true, new ByteArrayInputStream(STDIN_MULTI_3.getBytes(StandardCharsets.UTF_8)), PATH_MULTI_1);
        assertEquals(FILE_MULTI_1 + String.format(NUMBER_FORMAT, 4) +
                STRING_NEWLINE +
                STDIN + String.format(NUMBER_FORMAT, 1) +
                STRING_NEWLINE, result);
    }


    @Test
    public void grepFromFiles_FileEmptyLinesFalse_ReturnsEmpty() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_VALID, true, false, true, PATH_EMPTY);
        assertEquals("" + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFiles_FileSingleLineLinesFalse_ReturnsLines() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_HELLO, true, false, true, PATH_SINGLE_1);
        assertEquals(String.format(STRING_FORMAT, FILE_SINGLE_1) + TEXT_SINGLE_1 + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFiles_FileMultiLineLinesFalse_ReturnsLines() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_APPLES, true, false, true, PATH_MULTI_2);
        assertEquals(String.format(STRING_FORMAT, FILE_MULTI_2) + TEXT_MULTI_2_3 + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromStdin_InputStreamEmptyLinesFalse_ReturnsEmpty() throws Exception {
        String result = grepApplication.grepFromStdin(PATTERN_VALID, true, false, true, InputStream.nullInputStream());
        assertEquals("" + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromStdin_InputStreamSingleLineLinesFalse_ReturnsLines() throws Exception {
        String result = grepApplication.grepFromStdin(PATTERN_HELLO, true, false, true, new ByteArrayInputStream(STDIN_SINGLE_1.getBytes(StandardCharsets.UTF_8)));
        assertEquals(String.format(STRING_FORMAT, STDIN) + STDIN_SINGLE_1 + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromStdin_InputStreamMultiLineLinesFalse_ReturnsLines() throws Exception {
        String result = grepApplication.grepFromStdin(PATTERN_DOG, true, false, true, new ByteArrayInputStream(STDIN_MULTI_1.getBytes(StandardCharsets.UTF_8)));
        assertEquals(String.format(STRING_FORMAT, STDIN) +
                STDIN_MULTI_1_1 +
                STRING_NEWLINE +
                String.format(STRING_FORMAT, STDIN) +
                STDIN_MULTI_1_3 +
                STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFiles_FileMultiLinesFalse_ReturnsLines() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_APPLES, true, false, true, PATH_MULTI_1, PATH_MULTI_2);
        assertEquals(String.format(STRING_FORMAT, FILE_MULTI_1) +
                TEXT_MULTI_1_1 +
                STRING_NEWLINE +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                TEXT_MULTI_1_3 +
                STRING_NEWLINE +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                TEXT_MULTI_1_4 +
                STRING_NEWLINE +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                TEXT_MULTI_1_6 +
                STRING_NEWLINE +
                String.format(STRING_FORMAT, FILE_MULTI_2) +
                TEXT_MULTI_2_3 +
                STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFileAndStdin_FileMultiInputStreamMultiLineLinesFalse_ReturnsLines() throws Exception {
        String result = grepApplication.grepFromFileAndStdin(PATTERN_APPLES, true, false, true, new ByteArrayInputStream(STDIN_MULTI_3.getBytes(StandardCharsets.UTF_8)), PATH_MULTI_1);
        assertEquals(String.format(STRING_FORMAT, FILE_MULTI_1) +
                TEXT_MULTI_1_1 +
                STRING_NEWLINE +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                TEXT_MULTI_1_3 +
                STRING_NEWLINE +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                TEXT_MULTI_1_4 +
                STRING_NEWLINE +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                TEXT_MULTI_1_6 +
                STRING_NEWLINE +
                String.format(STRING_FORMAT, STDIN) +
                STDIN_MULTI_3_2 +
                STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFiles_PatternNotFoundLinesFalse_ReturnsZero() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_DOG, true, true, true, PATH_MULTI_1);
        assertEquals("0" + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromStdin_PatternNotFoundLinesFalse_ReturnsZero() throws Exception {
        String result = grepApplication.grepFromStdin(PATTERN_APPLES, true, true, true, new ByteArrayInputStream(STDIN_SINGLE_1.getBytes(StandardCharsets.UTF_8)));
        assertEquals("0" + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFileAndStdin_PatternNotFoundLinesFalse_ReturnsZero() throws Exception {
        String result = grepApplication.grepFromFileAndStdin(PATTERN_DOG, true, true, true, new ByteArrayInputStream(STDIN_SINGLE_1.getBytes(StandardCharsets.UTF_8)), PATH_MULTI_1);
        assertEquals(FILE_MULTI_1 + String.format(NUMBER_FORMAT, 0) +
                STRING_NEWLINE +
                STDIN + String.format(NUMBER_FORMAT, 0) + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFiles_CaseInsensitiveFalseLinesFalse_ReturnsLines() throws Exception {
        String result = grepApplication.grepFromFiles(PATTERN_APPLES_UP, false, false, true, PATH_MULTI_1);
        assertEquals("" + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromStdin_CaseInsensitiveFalseLinesFalse_ReturnsLines() throws Exception {
        String result = grepApplication.grepFromStdin(PATTERN_DOG, false, false, true, new ByteArrayInputStream(STDIN_MULTI_1.getBytes(StandardCharsets.UTF_8)));
        assertEquals(String.format(STRING_FORMAT, STDIN) + STDIN_MULTI_1_3 + STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFileAndStdin_CaseInsensitiveFalseLinesFalse_ReturnsLines() throws Exception {
        String result = grepApplication.grepFromFileAndStdin(PATTERN_APPLES, false, false, true, new ByteArrayInputStream(STDIN_MULTI_3.getBytes(StandardCharsets.UTF_8)), PATH_MULTI_1);
        assertEquals(String.format(STRING_FORMAT, FILE_MULTI_1) +
                TEXT_MULTI_1_1 +
                STRING_NEWLINE +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                TEXT_MULTI_1_3 +
                STRING_NEWLINE +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                TEXT_MULTI_1_6 +
                STRING_NEWLINE, result);
    }

    @Test
    public void grepFromFile_FileIsDir_ReturnsIsDirException() throws Exception {
        String filename = PATH_DIRECTORY;
        String result = grepApplication.grepFromFiles(PATTERN_APPLES, false, false, false, filename);
        assertEquals(new GrepException(DIRECTORY + ": " + ERR_IS_DIR).getMessage() + STRING_NEWLINE, result);
    }

    @Test
    public void run_PatternExistsInputStreamNullFilesEmpty_ThrowsNoInputException() {
        String[] args = {PATTERN_APPLES};
        OutputStream outputStream = new ByteArrayOutputStream();
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.run(args, null, outputStream);
        });
        assertEquals(new GrepException(ERR_NO_INPUT).getMessage(), exception.getMessage());
    }

    @Test
    public void run_PatternNoExists_ThrowsInvalidSyntax() {
        String[] args = {PATH_MULTI_1};
        OutputStream outputStream = new ByteArrayOutputStream();
        Exception exception = assertThrows(Exception.class, ()->{
            grepApplication.run(args, new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)), outputStream);
        });
        assertEquals(new GrepException(ERR_INVALID_REGEX).getMessage(), exception.getMessage());
    }

    @Test
    public void run_FilesEmpty_ReturnsGrepFromStdin() throws Exception {
        String[] args = {PATTERN_APPLES};
        OutputStream outputStream = new ByteArrayOutputStream();
        grepApplication.run(args, new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)), outputStream);
        assertEquals(STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void run_FilesNotEmptyInputStreamNull_ReturnsGrepFromFiles() throws Exception {
        String[] args = {PATTERN_APPLES, PATH_MULTI_1};
        OutputStream outputStream = new ByteArrayOutputStream();
        grepApplication.run(args, null, outputStream);
        assertEquals(TEXT_MULTI_1_1 +
                STRING_NEWLINE +
                TEXT_MULTI_1_3 +
                STRING_NEWLINE +
                TEXT_MULTI_1_6 +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void run_FilesNotEmptyInputStreamNotNull_ReturnsGrepFromFileAndStdin() throws Exception {
        String[] args = {PATTERN_APPLES, PATH_MULTI_1};
        OutputStream outputStream = new ByteArrayOutputStream();
        grepApplication.run(args, new ByteArrayInputStream(STDIN_SINGLE_1.getBytes(StandardCharsets.UTF_8)), outputStream);
        assertEquals(TEXT_MULTI_1_1 +
                STRING_NEWLINE +
                TEXT_MULTI_1_3 +
                STRING_NEWLINE +
                TEXT_MULTI_1_6 +
                STRING_NEWLINE, outputStream.toString());
    }
}