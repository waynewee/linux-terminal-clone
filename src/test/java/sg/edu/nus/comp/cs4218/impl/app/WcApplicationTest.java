package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.WcException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;


class WcApplicationTest {

    private static WcApplication wcApplication;

    private static final String PATH = "src/test/resources/impl/app/WcApplicationResources/".replace('/', CHAR_FILE_SEP);

    private static final String FILE_MULTI_1 = "multi-line-1.txt";
    private static final String FILE_MULTI_2 = "multi-line-2.txt";
    private static final String FILE_SINGLE_1 = "single-line-1.txt";
    private static final String FILE_SINGLE_2 = "single-line-2.txt";
    private static final String FILE_NOT_EXIST = "not-exist.txt";
    private static final String FILE_EMPTY = "empty.txt";
    private static final String DIRECTORY = "directory";

    private static final String PATH_MULTI_1 = PATH + FILE_MULTI_1;
    private static final String PATH_MULTI_2 = PATH + FILE_MULTI_2;
    private static final String PATH_SINGLE_1 = PATH + FILE_SINGLE_1;
    private static final String PATH_SINGLE_2 = PATH + FILE_SINGLE_2;
    private static final String PATH_NOT_EXIST = PATH + FILE_NOT_EXIST;
    private static final String PATH_EMPTY = PATH + FILE_EMPTY;
    private static final String PATH_DIRECTORY = PATH + DIRECTORY;

    private static final String STDIN_MULTI_1 = "Leverage benchmark, reinvent recontextualize recontextualize folksonomies communities; social\n" +
            "Facilitate grow partnerships, initiatives best-of-breed, addelivery repurpose user-centric webservices podcasting podcasts integrated vertical, B2B innovate!\n" +
            "Partnerships web services revolutionize initiatives syndicate platforms implement facilitate, reinvent incubate standards-compliant aggregate\n" +
            "Bricks-and-clicks, \"integrateAJAX-enabled ecologies reintermediate communities, deploy; long-tail architectures,\" cross-media--dynamic capture integrateAJAX-enabled integrateAJAX-enabled infrastructures remix synergize mesh enhance end-to-end A-list\n";
    private static final String STDIN_MULTI_2 = "Hello World\nThis is the second line.\nThis is the third line\n";
    private static final String STDIN_SINGLE_1 = "Hello World";
    private static final String STDIN_SINGLE_2 = "This is a test string.";

    private static final String NUMBER_FORMAT = " %7d";
    private static final String NUMBER_FORMAT_1 = " %d";
    private static final String STRING_FORMAT = " %s";

    private static final String STDIN = "stdin";
    private static final String TOTAL = "total";

    private static final String FLAG_BYTES = "-c";
    private static final String FLAG_LINES = "-l";
    private static final String FLAG_WORDS = "-w";

    @BeforeAll
    static void setupShell() {
        wcApplication = new WcApplication();
    }

    @Test
    public void countFromFiles_LinesTrueWordsTrueBytesFalseFilesNone_ReturnsEmpty() throws Exception {
        String result = wcApplication.countFromFiles(false, true, true);
        assertEquals("", result);
    }

    @Test
    public void countFromFiles_LinesTrueWordsFalseBytesTrueFilesNoValid_ReturnsError() throws Exception {
        String result = wcApplication.countFromFiles(true, true, false, PATH_NOT_EXIST);
        assertEquals(new WcException(ERR_FILE_NOT_FOUND).getMessage(), result);
    }

    @Test
    public void countFromFiles_LinesFalseWordsTrueBytesTrueFilesNotAllValid_ReturnsBytesWords() throws Exception {
        String result = wcApplication.countFromFiles(true, false, true, PATH_SINGLE_1, PATH_NOT_EXIST);
        assertEquals(
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 2) +
                String.format(NUMBER_FORMAT, 12) +
                String.format(STRING_FORMAT, FILE_SINGLE_1) +
                STRING_NEWLINE +
                new WcException(ERR_FILE_NOT_FOUND).getMessage() +
                STRING_NEWLINE + String.format(NUMBER_FORMAT, 2) +
                String.format(NUMBER_FORMAT, 12) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

    @Test
    public void countFromFiles_LinesFalseWordsFalseBytesFalseFilesAllValid_ReturnsBytesLinesWords() throws Exception {
        String result = wcApplication.countFromFiles(false, false, false, PATH_MULTI_2, PATH_SINGLE_2);
        assertEquals(
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 2) +
                String.format(NUMBER_FORMAT, 15) +
                String.format(NUMBER_FORMAT, 75) +
                String.format(STRING_FORMAT, FILE_MULTI_2) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 5) +
                String.format(NUMBER_FORMAT, 20) +
                String.format(STRING_FORMAT, FILE_SINGLE_2) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 2) +
                String.format(NUMBER_FORMAT, 20) +
                String.format(NUMBER_FORMAT, 95) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

    @Test
    public void countFromFiles_LinesTrueWordsFalseBytesFalseFilesNotAllValid_ReturnsErrorAndLines() throws Exception {
        String result = wcApplication.countFromFiles(false, true, false, PATH_MULTI_1, PATH_NOT_EXIST);
        assertEquals(
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                STRING_NEWLINE +
                new WcException(ERR_FILE_NOT_FOUND).getMessage() +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

    @Test
    public void countFromFiles_LinesTrueWordsTrueBytesTrueFilesAllValid_ReturnsBytesLinesWords() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, PATH_MULTI_1, PATH_MULTI_2, PATH_SINGLE_2, PATH_SINGLE_1);
        assertEquals(
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) +
                String.format(NUMBER_FORMAT, 55) +
                String.format(NUMBER_FORMAT, 648) +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 2) +
                String.format(NUMBER_FORMAT, 15) +
                String.format(NUMBER_FORMAT, 75) +
                String.format(STRING_FORMAT, FILE_MULTI_2) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 5) +
                String.format(NUMBER_FORMAT, 20) +
                String.format(STRING_FORMAT, FILE_SINGLE_2) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 2) +
                String.format(NUMBER_FORMAT, 12) +
                String.format(STRING_FORMAT, FILE_SINGLE_1) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 6) +
                String.format(NUMBER_FORMAT, 77) +
                String.format(NUMBER_FORMAT, 755) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

    @Test
    public void countFromFiles_LinesFalseWordsFalseBytesTrueFilesNone_ReturnsEmpty() throws Exception {
        String result = wcApplication.countFromFiles(true, false, false);
        assertEquals("", result);
    }

    @Test
    public void countFromFiles_LinesFalseWordsTrueBytesFalseFilesNoValid_ReturnsError() throws Exception {
        String result = wcApplication.countFromFiles(false, false, true, PATH_NOT_EXIST);
        assertEquals(new WcException(ERR_FILE_NOT_FOUND).getMessage(), result);
    }

    @Test
    public void countFromStdin_LinesTrueWordsFalseBytesFalseInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()-> {
            wcApplication.countFromStdin(false, true, false, null);
        });
        assertEquals(new WcException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void countFromStdin_LinesTrueWordsTrueBytesTrueInputStreamValid_ReturnsBytesLinesWords() throws Exception {
        String result = wcApplication.countFromStdin(true, true, true,
                new ByteArrayInputStream(STDIN_MULTI_1.getBytes(StandardCharsets.UTF_8)));
        assertEquals(String.format(NUMBER_FORMAT_1, 4) +
                String.format(NUMBER_FORMAT_1, 55) +
                String.format(NUMBER_FORMAT_1, 644) +
                String.format(STRING_FORMAT, STDIN), result);
    }

    @Test
    public void countFromStdin_LinesFalseWordsFalseBytesTrueInputStreamEmpty_ReturnsBytes() throws Exception {
        String result = wcApplication.countFromStdin(true, false, false, InputStream.nullInputStream());
        assertEquals(String.format(NUMBER_FORMAT_1, 0) + String.format(STRING_FORMAT, STDIN), result);
    }

    @Test
    public void countFromStdin_LinesFalseWordsTrueBytesTrueInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()-> {
            wcApplication.countFromStdin(true, false, true, null);
        });
        assertEquals(new WcException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void countFromStdin_LinesFalseWordsTrueBytesFalseInputStreamValid_ReturnsWords() throws Exception {
        String result = wcApplication.countFromStdin(false, false, true,
                new ByteArrayInputStream(STDIN_SINGLE_2.getBytes(StandardCharsets.UTF_8)));
        assertEquals(String.format(NUMBER_FORMAT_1, 5) + String.format(STRING_FORMAT, STDIN), result);
    }

    @Test
    public void countFromStdin_LinesTrueWordsTrueBytesFalseInputStreamEmpty_ReturnsLinesWords() throws Exception {
        String result = wcApplication.countFromStdin(false, true, true, InputStream.nullInputStream());
        assertEquals(String.format(NUMBER_FORMAT_1, 0) +
                String.format(NUMBER_FORMAT_1, 0) + String.format(STRING_FORMAT, STDIN), result);
    }

    @Test
    public void countFromStdin_LinesTrueWordsFalseBytesTrueInputStreamValid_ReturnsBytesLines() throws Exception {
        String result = wcApplication.countFromStdin(true, true, false,
                new ByteArrayInputStream(STDIN_MULTI_2.getBytes(StandardCharsets.UTF_8)));
        assertEquals(String.format(NUMBER_FORMAT_1, 3) +
                String.format(NUMBER_FORMAT_1, 60) +
                String.format(STRING_FORMAT, STDIN), result);
    }

    @Test
    public void countFromFileAndStdin_LinesTrueWordsFalseBytesFalseFilesNoValidInputStreamValid_ReturnsErrorAndLines() throws Exception {
        String result = wcApplication.countFromFileAndStdin(false, true, false,
                new ByteArrayInputStream(STDIN_MULTI_1.getBytes(StandardCharsets.UTF_8)), PATH_NOT_EXIST);
        assertEquals(STRING_NEWLINE +
                new WcException(ERR_FILE_NOT_FOUND).getMessage() +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) +
                String.format(STRING_FORMAT, STDIN) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

    @Test
    public void countFromFileAndStdin_LinesTrueWordsTrueBytesTrueFilesNotAllValidInputStreamEmptyReturnsErrorAndBytesLinesWords_ReturnsBytesLinesWords() throws Exception {
        String result = wcApplication.countFromFileAndStdin(true, true, true, InputStream.nullInputStream(), PATH_NOT_EXIST, PATH_SINGLE_2);
        assertEquals(STRING_NEWLINE +
                new WcException(ERR_FILE_NOT_FOUND).getMessage() +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 5) +
                String.format(NUMBER_FORMAT, 20) +
                String.format(STRING_FORMAT, FILE_SINGLE_2) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, STDIN) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 5) +
                String.format(NUMBER_FORMAT, 20) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

    @Test
    public void countFromFileAndStdin_LinesFalseWordsFalseBytesTrueFilesAllValidEmptyStreamEmpty_ReturnsBytes() throws Exception {
        String result = wcApplication.countFromFileAndStdin(true, false, false, InputStream.nullInputStream(), PATH_MULTI_2, PATH_SINGLE_2);
        assertEquals(STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 75) +
                String.format(STRING_FORMAT, FILE_MULTI_2) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 20) +
                String.format(STRING_FORMAT, FILE_SINGLE_2) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, STDIN) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 95) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

    @Test
    public void countFromFileAndStdin_LinesFalseWordsTrueBytesFalseFilesNoneInputStreamValid_ReturnsWords() throws Exception {
        String result = wcApplication.countFromFileAndStdin(false, false, true, new ByteArrayInputStream(STDIN_MULTI_1.getBytes(StandardCharsets.UTF_8)));
        assertEquals(STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 55) +
                String.format(STRING_FORMAT, STDIN) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 55) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

    @Test
    public void countFromFileAndStdin_LinesFalseWordsFalseBytesTrueFilesNoValidInputStreamEmpty_ReturnsErrorAndBytes() throws Exception {
        String result = wcApplication.countFromFileAndStdin(true, false, false, InputStream.nullInputStream(), PATH_NOT_EXIST);
        assertEquals(STRING_NEWLINE +
                new WcException(ERR_FILE_NOT_FOUND).getMessage() +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, STDIN) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

    @Test
    public void countFromFileAndStdin_LinesTrueWordsTrueBytesTrueFilesNotAllValidInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            wcApplication.countFromFileAndStdin(true, true, true, null, PATH_NOT_EXIST, PATH_MULTI_1);
        });
        assertEquals(new WcException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void countFromFileAndStdin_LinesTrueWordsFalseBytesFalseFilesAllValidInputStreamValid_ReturnsLines() throws Exception {
        String result = wcApplication.countFromFileAndStdin(false, true, false,
                new ByteArrayInputStream(STDIN_MULTI_1.getBytes(StandardCharsets.UTF_8)),
                PATH_MULTI_1, PATH_MULTI_2);
        assertEquals(STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 2) +
                String.format(STRING_FORMAT, FILE_MULTI_2) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) +
                String.format(STRING_FORMAT, STDIN) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 10) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

    @Test
    public void countFromFileAndStdin_LinesTrueWordsTrueBytesTrueFilesNoneInputStreamEmpty_ReturnsBytesLinesWords() throws Exception {
        String result = wcApplication.countFromFileAndStdin(true, true, true, InputStream.nullInputStream());
        assertEquals(STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, STDIN) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

    @Test
    public void countFromFileAndStdin_LinesTrueWordsFalseBytesFalseFilesNoValidInputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            wcApplication.countFromFileAndStdin(true, true, true, null, PATH_NOT_EXIST);
        });
        assertEquals(new WcException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void countFromFileAndStdin_LinesFalseWordsTrueBytesFalseFilesNotAllValidInputStreamValid_ReturnsErrorAndWords() throws Exception {
        String result = wcApplication.countFromFileAndStdin(false, false, true,
                new ByteArrayInputStream(STDIN_MULTI_1.getBytes(StandardCharsets.UTF_8)),
                PATH_MULTI_2, PATH_NOT_EXIST, PATH_SINGLE_2);
        assertEquals(STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 15) +
                String.format(STRING_FORMAT, FILE_MULTI_2) +
                STRING_NEWLINE +
                new WcException(ERR_FILE_NOT_FOUND).getMessage() +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 5) +
                String.format(STRING_FORMAT, FILE_SINGLE_2) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 55) +
                String.format(STRING_FORMAT, STDIN) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 75) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

    @Test
    public void countFromFileAndStdin_LinesFalseWordsFalseBytesTrueFilesALlValidInputStreamEmpty_ReturnsWords() throws Exception {
        String result = wcApplication.countFromFileAndStdin(false, false, true,
                InputStream.nullInputStream(),
                PATH_MULTI_1, PATH_SINGLE_2);
        assertEquals(STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 55) +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 5) +
                String.format(STRING_FORMAT, FILE_SINGLE_2) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, STDIN) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 60) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

    @Test
    public void countFromFileAndStdin_LinesFalseWordsTrueBytesFalseFilesNoneInputStreamNull_ReturnsWords() throws Exception {
        String result = wcApplication.countFromFileAndStdin(false, false, true, InputStream.nullInputStream());
        assertEquals(STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, STDIN) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

    @Test
    public void countFromFiles_FileEmpty_ReturnsZeroes() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, PATH_EMPTY);
        assertEquals(String.format(NUMBER_FORMAT_1, 0) +
                String.format(NUMBER_FORMAT_1, 0) +
                String.format(NUMBER_FORMAT_1, 0) +
                String.format(STRING_FORMAT, FILE_EMPTY), result);
    }

    @Test
    public void countFromFiles_FileSingleLine_ReturnsCount() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, PATH_SINGLE_1);
        assertEquals(String.format(NUMBER_FORMAT_1, 0) +
                String.format(NUMBER_FORMAT_1, 2) +
                String.format(NUMBER_FORMAT_1, 12) +
                String.format(STRING_FORMAT, FILE_SINGLE_1), result);
    }

    @Test
    public void countFromFiles_FileMultiLines_ReturnsCount() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, PATH_MULTI_1);
        assertEquals(String.format(NUMBER_FORMAT_1, 4) +
                String.format(NUMBER_FORMAT_1, 55) +
                String.format(NUMBER_FORMAT_1, 648) +
                String.format(STRING_FORMAT, FILE_MULTI_1), result);
    }

    @Test
    public void countFromFiles_InputStreamEmpty_ReturnsZeroes() throws Exception {
        String result = wcApplication.countFromStdin(true, true, true, InputStream.nullInputStream());
        assertEquals(String.format(NUMBER_FORMAT_1, 0) +
                String.format(NUMBER_FORMAT_1, 0) +
                String.format(NUMBER_FORMAT_1, 0) +
                String.format(STRING_FORMAT, STDIN), result);
    }

    @Test
    public void countFromFiles_InputStreamSingleLine_ReturnsCount() throws Exception {
        String result = wcApplication.countFromStdin(true, true, true, new ByteArrayInputStream(STDIN_SINGLE_1.getBytes(StandardCharsets.UTF_8)));
        assertEquals(String.format(NUMBER_FORMAT_1, 0) +
                String.format(NUMBER_FORMAT_1, 2) +
                String.format(NUMBER_FORMAT_1, 11) +
                String.format(STRING_FORMAT, STDIN), result);
    }

    @Test
    public void countFromFiles_InputStreamMultiLines_ReturnsCount() throws Exception {
        String result = wcApplication.countFromStdin(true, true, true, new ByteArrayInputStream(STDIN_MULTI_1.getBytes(StandardCharsets.UTF_8)));
        assertEquals(String.format(NUMBER_FORMAT_1, 4) +
                String.format(NUMBER_FORMAT_1, 55) +
                String.format(NUMBER_FORMAT_1, 644) +
                String.format(STRING_FORMAT, STDIN), result);
    }

    @Test
    public void countFromFiles_FileMulti_ReturnsCount() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, PATH_MULTI_1, PATH_SINGLE_1, PATH_SINGLE_2);
        assertEquals(STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) +
                String.format(NUMBER_FORMAT, 55) +
                String.format(NUMBER_FORMAT, 648) +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 2) +
                String.format(NUMBER_FORMAT, 12) +
                String.format(STRING_FORMAT, FILE_SINGLE_1) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 5) +
                String.format(NUMBER_FORMAT, 20) +
                String.format(STRING_FORMAT, FILE_SINGLE_2) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) +
                String.format(NUMBER_FORMAT, 62) +
                String.format(NUMBER_FORMAT, 680) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

    @Test
    public void countFromFileAndStdin_FileMultiInputStream_ReturnsCount() throws Exception {
        String result = wcApplication.countFromFileAndStdin(true, true, true, new ByteArrayInputStream(STDIN_MULTI_2.getBytes(StandardCharsets.UTF_8)), PATH_MULTI_1, PATH_SINGLE_1);
        assertEquals(STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) +
                String.format(NUMBER_FORMAT, 55) +
                String.format(NUMBER_FORMAT, 648) +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 2) +
                String.format(NUMBER_FORMAT, 12) +
                String.format(STRING_FORMAT, FILE_SINGLE_1) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 3) +
                String.format(NUMBER_FORMAT, 12) +
                String.format(NUMBER_FORMAT, 60) +
                String.format(STRING_FORMAT, STDIN) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 7) +
                String.format(NUMBER_FORMAT, 69) +
                String.format(NUMBER_FORMAT, 720) +
                String.format(STRING_FORMAT, TOTAL)
                , result);
    }

    @Test
    public void run_OutputStreamNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            String[] args = {FLAG_BYTES, FLAG_LINES, FLAG_WORDS};
            wcApplication.run(args, new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)), null);
        });
        assertEquals(new WcException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void run_InputStreamNullFilesNotEmpty_ReturnsCountFromFiles() throws WcException {
        String[] args = {FLAG_BYTES, FLAG_LINES, FLAG_WORDS, PATH_MULTI_1, PATH_SINGLE_1};
        OutputStream outputStream = new ByteArrayOutputStream();
        wcApplication.run(args, null, outputStream);
        assertEquals(STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) +
                String.format(NUMBER_FORMAT, 55) +
                String.format(NUMBER_FORMAT, 648) +
                String.format(STRING_FORMAT, FILE_MULTI_1) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 2) +
                String.format(NUMBER_FORMAT, 12) +
                String.format(STRING_FORMAT, FILE_SINGLE_1) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT,4) +
                String.format(NUMBER_FORMAT, 57) +
                String.format(NUMBER_FORMAT, 660) +
                String.format(STRING_FORMAT, TOTAL) +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void run_InputStreamNotNullFilesEmpty_ReturnsCountFromStdin() throws WcException {
        String[] args = {FLAG_BYTES, FLAG_LINES, FLAG_WORDS};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wcApplication.run(args, new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)), outputStream);
        assertEquals(" 0 0 0 stdin" + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void run_InputStreamNotNullFilesNotEmpty_ReturnsCountFromFileAndStdin() throws WcException {
        String[] args = {FLAG_BYTES, FLAG_LINES, FLAG_WORDS, PATH_SINGLE_1};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        wcApplication.run(args, new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)), outputStream);
        assertEquals(STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 2) +
                String.format(NUMBER_FORMAT, 12) +
                String.format(STRING_FORMAT, FILE_SINGLE_1) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, STDIN) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT,0) +
                String.format(NUMBER_FORMAT, 2) +
                String.format(NUMBER_FORMAT, 12) +
                String.format(STRING_FORMAT, TOTAL) +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void countFromFiles_FileNameNull_ThrowsGeneralException() {
        Exception exception = assertThrows(Exception.class, ()->{
           wcApplication.countFromFiles(true, true, true, null);
        });
        assertEquals(new WcException(ERR_GENERAL).getMessage(), exception.getMessage());
    }

    @Test
    public void countFromFiles_FileIsDir_ReturnsDirException() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, PATH_DIRECTORY);
        assertEquals(new WcException(ERR_IS_DIR).getMessage(), result);
    }

    @Test
    public void countFromFileAndStdin_FileNameNull_ThrowsGeneralException() {
        Exception exception = assertThrows(Exception.class, ()->{
            wcApplication.countFromFileAndStdin(true, true, true, new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)), null);
        });
        assertEquals(new WcException(ERR_GENERAL).getMessage(), exception.getMessage());
    }

    @Test
    public void countFromFileAndStdin_FileIsDir_ReturnsDirException() throws Exception {
        String result = wcApplication.countFromFileAndStdin(true, true, true, new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)), PATH_DIRECTORY);
        assertEquals(STRING_NEWLINE +
                new WcException(ERR_IS_DIR).getMessage() +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, STDIN) +
                STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 0) +
                String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, TOTAL), result);
    }

}