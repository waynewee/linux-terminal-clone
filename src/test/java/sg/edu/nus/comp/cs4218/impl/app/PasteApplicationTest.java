package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.exception.UniqException;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class PasteApplicationTest {

    private static PasteApplication pasteApplication;

    private static final String PATH = "src/test/resources/impl/app/PasteApplicationResources/".replace('/', CHAR_FILE_SEP);

    private static final String FILE_ABC = "abc.txt";
    private static final String FILE_123 = "123.txt";
    private static final String FILE_WXYZ = "wxyz.txt";
    private static final String FILE_NO_EXIST = "no-exist.txt";

    private static final String PATH_ABC = PATH + FILE_ABC;
    private static final String PATH_123 = PATH + FILE_123;
    private static final String PATH_WXYZ = PATH + FILE_WXYZ;
    private static final String PATH_NO_EXIST = PATH + FILE_NO_EXIST;

    private static final String STDIN_ABC = "a\nb\nc";
    private static final String STDIN_QWERTY = "q\nw\ne\nr\nt\ny";
    private static final String STDIN_WORDS = "Hello\nWorld\nHow's\nLife";

    private static final String FLAG_SERIAL = "-s";

    @BeforeAll
    static void setupShell() {
        pasteApplication = new PasteApplication();
    }

    @Test
    public void mergeFile_SerialTrueFileMultiple_ReturnsSerializedMultiple() throws Exception {
        String result = pasteApplication.mergeFile(true, PATH_ABC, PATH_WXYZ, PATH_123);
        assertEquals("a\tb\tc" +
                STRING_NEWLINE +
                "w\tx\ty\tz" +
                STRING_NEWLINE +
                "1\t2\t3" +
                STRING_NEWLINE, result);
    }

    @Test
    public void mergeFile_SerialTrueFileSingle_ReturnsSerializedSingle() throws Exception {
        String result = pasteApplication.mergeFile(true, PATH_ABC);
        assertEquals("a\tb\tc" + STRING_NEWLINE , result);
    }

    @Test
    public void mergeFile_SerialFalseFileMultiple_ReturnsUnserializedMultiple() throws Exception {
        String result = pasteApplication.mergeFile(false, PATH_ABC, PATH_WXYZ, PATH_123);
        assertEquals("a\tw\t1" +
                STRING_NEWLINE +
                "b\tx\t2" +
                STRING_NEWLINE +
                "c\ty\t3" +
                STRING_NEWLINE +
                "\tz\t" +
                STRING_NEWLINE, result);
    }

    @Test
    public void mergeFile_SerialFalseFileSingle_ReturnsUnserializedSingle() throws Exception {
        String result = pasteApplication.mergeFile(false, PATH_123);
        assertEquals("1" +
                STRING_NEWLINE +
                "2" +
                STRING_NEWLINE +
                "3" +
                STRING_NEWLINE, result);
    }

    @Test
    public void mergeFile_SerialTrueFileNotExist_ThrowsFileNotFoundException() {
        Exception exception = assertThrows(Exception.class, ()->{
           pasteApplication.mergeFile(true, PATH_NO_EXIST);
        });
        assertEquals(new PasteException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }

    @Test
    public void mergeStdin_SerialTrue_ReturnsSerializedInput() throws Exception {
        String result = pasteApplication.mergeStdin(true, new ByteArrayInputStream(STDIN_ABC.getBytes(StandardCharsets.UTF_8)));
        assertEquals("a\tb\tc" + STRING_NEWLINE, result);
    }

    @Test
    public void mergeStdin_SerialFalse_ReturnsUnserializedInput() throws Exception {
        String result = pasteApplication.mergeStdin(false, new ByteArrayInputStream(STDIN_WORDS.getBytes(StandardCharsets.UTF_8)));
        assertEquals("Hello" +
                STRING_NEWLINE +
                "World" +
                STRING_NEWLINE +
                "How's" +
                STRING_NEWLINE +
                "Life" +
                STRING_NEWLINE, result);
    }

    @Test
    public void mergeFilesAndStdin_SerialTrueFileMultiple_ReturnsSerialized() throws Exception {
        String result = pasteApplication.mergeFileAndStdin(true, new ByteArrayInputStream(STDIN_ABC.getBytes(StandardCharsets.UTF_8)), PATH_123, "-", PATH_WXYZ);
        assertEquals("1\t2\t3" +
                STRING_NEWLINE +
                "a\tb\tc" +
                STRING_NEWLINE +
                "w\tx\ty\tz" +
                STRING_NEWLINE, result);
    }

    @Test
    public void mergeFilesAndStdin_SerialTrueFileSingle_ReturnsSerialized() throws Exception {
        String result = pasteApplication.mergeFileAndStdin(true, new ByteArrayInputStream(STDIN_WORDS.getBytes(StandardCharsets.UTF_8)), "-", PATH_WXYZ);
        assertEquals("Hello\tWorld\tHow's\tLife" +
                STRING_NEWLINE +
                "w\tx\ty\tz" +
                STRING_NEWLINE, result);
    }

    @Test
    public void mergeFilesAndStdin_SerialFalseFileMultiple_ReturnsUnserialized() throws Exception {
        String result = pasteApplication.mergeFileAndStdin(false, new ByteArrayInputStream(STDIN_ABC.getBytes(StandardCharsets.UTF_8)), PATH_ABC, "-", PATH_WXYZ);
        assertEquals("a\ta\tw" +
                STRING_NEWLINE +
                "b\tb\tx" +
                STRING_NEWLINE +
                "c\tc\ty" +
                STRING_NEWLINE +
                "\t\tz" +
                STRING_NEWLINE, result);
    }

    @Test
    public void mergeFilesAndStdin_SerialFalseFileSingle_ReturnsUnserialized() throws Exception {
        String result = pasteApplication.mergeFileAndStdin(false, new ByteArrayInputStream(STDIN_WORDS.getBytes(StandardCharsets.UTF_8)), PATH_WXYZ, "-");
        assertEquals("w\tHello" +
                STRING_NEWLINE +
                "x\tWorld" +
                STRING_NEWLINE +
                "y\tHow's" +
                STRING_NEWLINE +
                "z\tLife" +
                STRING_NEWLINE, result);
    }

    @Test
    public void mergeFilesAndStdin_InputFileLocationNone_ReturnsNoInput() throws Exception {
        String result = pasteApplication.mergeFileAndStdin(false, new ByteArrayInputStream(STDIN_WORDS.getBytes(StandardCharsets.UTF_8)), PATH_WXYZ);
        assertEquals("w" +
                STRING_NEWLINE +
                "x" +
                STRING_NEWLINE +
                "y" +
                STRING_NEWLINE +
                "z" +
                STRING_NEWLINE, result);
    }

    @Test
    public void mergeFilesAndStdin_InputFileLocationSingle_ReturnsInput() throws Exception {
        String result = pasteApplication.mergeFileAndStdin(false, new ByteArrayInputStream(STDIN_WORDS.getBytes(StandardCharsets.UTF_8)), PATH_WXYZ, "-", PATH_ABC);
        assertEquals("w\tHello\ta" +
                STRING_NEWLINE +
                "x\tWorld\tb" +
                STRING_NEWLINE +
                "y\tHow's\tc" +
                STRING_NEWLINE +
                "z\tLife\t" +
                STRING_NEWLINE, result);
    }

    @Test
    public void mergeFilesAndStdin_InputFileLocationMultiple_ReturnsFormattedInput() throws Exception {
        String result = pasteApplication.mergeFileAndStdin(false, new ByteArrayInputStream(STDIN_QWERTY.getBytes(StandardCharsets.UTF_8)), "-", PATH_123, "-", "-");
        assertEquals("q\t1\tw\te" +
                STRING_NEWLINE +
                "r\t2\tt\ty" +
                STRING_NEWLINE +
                "\t3\t\t" +
                STRING_NEWLINE, result);
    }

    @Test
    public void mergeFile_FileNoExist_ThrowsFileNotFoundException() {
        Exception exception = assertThrows(Exception.class, ()->{
            pasteApplication.mergeFile(false, PATH_NO_EXIST);
        });
        assertEquals(new PasteException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }

    @Test
    public void mergeFile_FileNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            pasteApplication.mergeFile(false, null);
        });
        assertEquals(new PasteException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void mergeStdin_StdinNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            pasteApplication.mergeStdin(false, null);
        });
        assertEquals(new PasteException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void mergeFileAndStdin_FileNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            pasteApplication.mergeFileAndStdin(false, new ByteArrayInputStream(STDIN_WORDS.getBytes(StandardCharsets.UTF_8)), null);
        });
        assertEquals(new PasteException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void mergeFileAndStdin_StdinNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            pasteApplication.mergeFileAndStdin(false, null, PATH_ABC);
        });
        assertEquals(new PasteException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void run_OutputStreamNull_ThrowsNullStreamsException() {
        String[] args = {};
        Exception exception = assertThrows(Exception.class, ()->{
            pasteApplication.run(args, new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)), null);
        });
        assertEquals(new PasteException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void run_FilesEmpty_ReturnsMergeStdin() throws PasteException {
        String[] args = {FLAG_SERIAL};
        OutputStream outputStream = new ByteArrayOutputStream();
        pasteApplication.run(args, new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)), outputStream);
        assertEquals(STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void run_FilesNotEmptyInputStreamNull_ReturnsMergeFile() throws PasteException {
        String[] args = {PATH_123};
        OutputStream outputStream = new ByteArrayOutputStream();
        pasteApplication.run(args, null, outputStream);
        assertEquals("1" +
                STRING_NEWLINE +
                "2" +
                STRING_NEWLINE +
                "3" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void run_FilesNotEmptyInputStreamNotNull_ReturnsMergeFileAndStdin() throws PasteException {
        String[] args = {PATH_123, "-"};
        OutputStream outputStream = new ByteArrayOutputStream();
        pasteApplication.run(args, new ByteArrayInputStream(STDIN_WORDS.getBytes(StandardCharsets.UTF_8)), outputStream);
        assertEquals("1\tHello" +
                STRING_NEWLINE +
                "2\tWorld" +
                STRING_NEWLINE +
                "3\tHow's" +
                STRING_NEWLINE +
                "\tLife" +
                STRING_NEWLINE, outputStream.toString());
    }
}