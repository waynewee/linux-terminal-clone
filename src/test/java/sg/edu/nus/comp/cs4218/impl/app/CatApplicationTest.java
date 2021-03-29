package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CatException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class CatApplicationTest {

    private static CatApplication catApplication;

    private static final String PATH = "src/test/resources/impl/app/CatApplicationResources/".replace('/', CHAR_FILE_SEP);

    private static final String FILE_MULTI_1 = "multi-line-1.txt";
    private static final String FILE_SINGLE_1 = "single-line-1.txt";
    private static final String FILE_SINGLE_2 = "single-line-2.txt";
    private static final String FILE_NOT_EXIST = "not-exist.txt";
    private static final String FILE_NOT_EXIST_2 = "not-exist-2.txt";
    private static final String FILE_EMPTY = "empty.txt";
    private static final String DIRECTORY = "directory";

    private static final String TEXT_MULTI_1_1 = "Leverage benchmark, reinvent recontextualize recontextualize folksonomies communities; social";
    private static final String TEXT_MULTI_1_2 = "Facilitate grow partnerships, initiatives best-of-breed, addelivery repurpose user-centric webservices podcasting podcasts integrated vertical, B2B innovate!";
    private static final String TEXT_MULTI_1_3 = "Partnerships web services revolutionize initiatives syndicate platforms implement facilitate, reinvent incubate standards-compliant aggregate";
    private static final String TEXT_MULTI_1_4 = "Bricks-and-clicks, \"integrateAJAX-enabled ecologies reintermediate communities, deploy; long-tail architectures,\" cross-media--dynamic capture integrateAJAX-enabled integrateAJAX-enabled infrastructures remix synergize mesh enhance end-to-end A-list";
    private static final String TEXT_SINGLE_1 = "Hello World!";
    private static final String TEXT_SINGLE_2 = "This is a test file.";

    private static final String PATH_MULTI_1 = PATH + FILE_MULTI_1;
    private static final String PATH_SINGLE_1 = PATH + FILE_SINGLE_1;
    private static final String PATH_SINGLE_2 = PATH + FILE_SINGLE_2;
    private static final String PATH_NOT_EXIST = PATH + FILE_NOT_EXIST;
    private static final String PATH_NOT_EXIST_2 = PATH + FILE_NOT_EXIST_2;
    private static final String PATH_EMPTY = PATH + FILE_EMPTY;
    private static final String PATH_DIRECTORY = PATH + DIRECTORY;

    private static final String STDIN_MULTI_1 = "Leverage benchmark, reinvent recontextualize recontextualize folksonomies communities; social\n" +
            "Facilitate grow partnerships, initiatives best-of-breed, addelivery repurpose user-centric webservices podcasting podcasts integrated vertical, B2B innovate!\n" +
            "Partnerships web services revolutionize initiatives syndicate platforms implement facilitate, reinvent incubate standards-compliant aggregate\n" +
            "Bricks-and-clicks, \"integrateAJAX-enabled ecologies reintermediate communities, deploy; long-tail architectures,\" cross-media--dynamic capture integrateAJAX-enabled integrateAJAX-enabled infrastructures remix synergize mesh enhance end-to-end A-list";
    private static final String STDIN_SINGLE_1 = "Hello World";
    private static final String STDIN_SINGLE_2 = "This is a test string.";

    @BeforeAll
    static void setupShell() {
        catApplication = new CatApplication();
    }

    @Test
    public void catFiles_LinesTrueFilesNone_ReturnsEmpty() throws Exception {
        String result = catApplication.catFiles(true);
        assertEquals("", result);
    }

    @Test
    public void catFiles_LinesTrueFilesNoValid_ReturnsError() throws Exception {
        String result = catApplication.catFiles(true,
                PATH_NOT_EXIST,
                PATH_NOT_EXIST_2);
        assertEquals( new CatException(ERR_FILE_NOT_FOUND).getMessage() +
                STRING_NEWLINE +
                new CatException(ERR_FILE_NOT_FOUND).getMessage(), result);
    }

    @Test
    public void catFiles_LinesTrueFilesNotAllValid_ReturnsErrorAndNumberedLines() throws Exception {
        String result = catApplication.catFiles(true,
                PATH_SINGLE_1,
                PATH_NOT_EXIST,
                PATH_SINGLE_2);
        assertEquals(new CatException(ERR_FILE_NOT_FOUND).getMessage() +
                STRING_NEWLINE +
                "1 " + TEXT_SINGLE_1 +
                STRING_NEWLINE +
                "2 " + TEXT_SINGLE_2, result);
    }

    @Test
    public void catFiles_LinesTrueFilesAllValid_ReturnsNumberedLines() throws Exception {
        String result = catApplication.catFiles(true,
                PATH_SINGLE_1,
                PATH_SINGLE_2);
        assertEquals("1 " + TEXT_SINGLE_1 +
                STRING_NEWLINE +
                "2 " + TEXT_SINGLE_2, result);
    }

    @Test
    public void catFiles_LinesFalseFilesNone_ThrowsGeneralException() throws Exception {
        String result = catApplication.catFiles(false);
        assertEquals("", result);
    }

    @Test
    public void catFiles_LinesFalseFilesNoValid_ReturnsError() throws Exception {
        String result = catApplication.catFiles(false,
                PATH_NOT_EXIST,
                PATH_NOT_EXIST_2);
        assertEquals(new CatException(ERR_FILE_NOT_FOUND).getMessage() +
                STRING_NEWLINE +
                new CatException(ERR_FILE_NOT_FOUND).getMessage(), result);
    }

    @Test
    public void catFiles_LinesFalseFilesNotAllValid_ReturnsErrorAndLines() throws Exception {
        String result = catApplication.catFiles(false,
                PATH_SINGLE_1,
                PATH_NOT_EXIST,
                PATH_SINGLE_2);
        assertEquals(new CatException(ERR_FILE_NOT_FOUND).getMessage() +
                STRING_NEWLINE +
                TEXT_SINGLE_1 +
                STRING_NEWLINE +
                TEXT_SINGLE_2, result);
    }

    @Test
    public void catFiles_LinesFalseFilesAllValid_ReturnsLines() throws Exception {
        String result = catApplication.catFiles(false,
                PATH_SINGLE_1,
                PATH_SINGLE_2);
        assertEquals(TEXT_SINGLE_1 + STRING_NEWLINE + TEXT_SINGLE_2, result);
    }

    @Test
    public void catStdin_LinesTrueInputStreamEmpty_ReturnsEmpty() throws Exception {
        String result = catApplication.catStdin(true, InputStream.nullInputStream());
        assertEquals("", result);
    }

    @Test
    public void catStdin_LinesTrueInputStreamNull_ThrowsNulLStreamException() throws Exception {
        Exception exception = assertThrows(Exception.class, ()-> {
            catApplication.catStdin(true, null);
        });
        assertEquals(new CatException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void catStdin_LinesTrueInputStreamValid_ReturnsNumberedLines() throws Exception {
        String result = catApplication.catStdin(true,
                new ByteArrayInputStream(STDIN_MULTI_1.getBytes(StandardCharsets.UTF_8)));
        assertEquals("1 " + TEXT_MULTI_1_1 +
                STRING_NEWLINE +
                "2 " + TEXT_MULTI_1_2 +
                STRING_NEWLINE +
                "3 " + TEXT_MULTI_1_3 +
                STRING_NEWLINE +
                "4 " + TEXT_MULTI_1_4,
                result);
    }

    @Test
    public void catStdin_LinesFalseInputStreamEmpty_ReturnsEmpty() throws Exception {
        String result = catApplication.catStdin(false, InputStream.nullInputStream());
        assertEquals("", result);
    }

    @Test
    public void catStdin_LinesFalseInputStreamNull_ThrowsNullStreamException() {
        Exception exception = assertThrows(Exception.class, ()-> {
            catApplication.catStdin(false, null);
        });
        assertEquals(new CatException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void catStdin_LinesFalseInputStreamValid_ReturnsLines() throws Exception {
        String result = catApplication.catStdin(false, new ByteArrayInputStream(STDIN_SINGLE_1.getBytes(StandardCharsets.UTF_8)));
        assertEquals(STDIN_SINGLE_1, result);
    }

    @Test
    public void catFileAndStdin_LinesTrueFilesNoValidInputStreamNull_ThrowsGeneralException() throws Exception {
        Exception exception = assertThrows(Exception.class, ()-> {
            catApplication.catFileAndStdin(true, null, PATH_NOT_EXIST, PATH_NOT_EXIST);
        });
        assertEquals(new CatException(ERR_GENERAL).getMessage(), exception.getMessage());
    }

    @Test
    public void catFileAndStdin_LinesTrueFilesNotAllValidInputStreamValid_ReturnsErrorAndNumberedLines() throws Exception {
        String result = catApplication.catFileAndStdin(true,
                new ByteArrayInputStream(STDIN_SINGLE_2.getBytes(StandardCharsets.UTF_8)),
                PATH_SINGLE_1,
                PATH_NOT_EXIST,
                PATH_SINGLE_2,
                "-");
        assertEquals(new CatException(ERR_FILE_NOT_FOUND).getMessage() +
                STRING_NEWLINE +
                "1 " + TEXT_SINGLE_1 +
                STRING_NEWLINE +
                "2 " + TEXT_SINGLE_2 +
                STRING_NEWLINE +
                "3 " + STDIN_SINGLE_2, result);
    }

    @Test
    public void catFileAndStdin_LinesFalseFilesNoValidInputStreamValid_ReturnsErrorAndLines() throws Exception {
        String result = catApplication.catFileAndStdin(false,
                new ByteArrayInputStream(STDIN_SINGLE_2.getBytes(StandardCharsets.UTF_8)),
                PATH_NOT_EXIST, "-");
        assertEquals(new CatException(ERR_FILE_NOT_FOUND).getMessage() +
                STRING_NEWLINE +
                STDIN_SINGLE_2, result);
    }

    @Test
    public void catFileAndStdin_LinesFalseFilesNotAllValidInputStreamEmpty_ReturnsErrorAndLines() throws Exception {
        String result = catApplication.catFileAndStdin(false,
                InputStream.nullInputStream(),
                PATH_SINGLE_1,
                PATH_NOT_EXIST,
                PATH_SINGLE_2);
        assertEquals(new CatException(ERR_FILE_NOT_FOUND).getMessage() +
                STRING_NEWLINE +
                TEXT_SINGLE_1 +
                STRING_NEWLINE +
                TEXT_SINGLE_2, result);
    }

    @Test
    public void catFileAndStdin_LinesFalseFilesAllValidInputStreamEmpty_ReturnsLines() throws Exception {
        String result = catApplication.catFileAndStdin(false,
                InputStream.nullInputStream(),
                PATH_SINGLE_1,
                PATH_SINGLE_2);
        assertEquals(TEXT_SINGLE_1 + STRING_NEWLINE + TEXT_SINGLE_2, result);
    }

    @Test
    public void catFileAndStdin_LinesFalseFilesNoneInputStreamNull_ThrowsGeneralException() {
        Exception exception = assertThrows(Exception.class, ()->{
            catApplication.catFileAndStdin(false, null);
        });
        assertEquals(new CatException(ERR_GENERAL).getMessage(), exception.getMessage());
    }

    @Test
    public void catFileAndStdin_LinesTrueFilesAllValidInputStreamNull_ThrowsGeneralException() {
        Exception exception = assertThrows(Exception.class, ()->{
            catApplication.catFileAndStdin(true, null, PATH_SINGLE_2, PATH_SINGLE_1);
        });
        assertEquals(new CatException(ERR_GENERAL).getMessage(), exception.getMessage());
    }

    @Test
    public void catFileAndStdin_LinesTrueFilesNoneInputStreamValid_ReturnsEmpty() throws Exception {
        String result = catApplication.catFileAndStdin(true, new ByteArrayInputStream(STDIN_SINGLE_2.getBytes(StandardCharsets.UTF_8)));
        assertEquals("", result);
    }

    @Test
    public void catFileAndStdin_LinesTrueFilesNoValidInputStreamEmpty_ReturnsError() throws Exception {
        String result = catApplication.catFileAndStdin(true, InputStream.nullInputStream(), PATH_NOT_EXIST);
        assertEquals(new CatException(ERR_FILE_NOT_FOUND).getMessage(), result);
    }

    @Test
    public void catFileAndStdin_LinesFalseFilesAllValidInputStreamValid_ReturnsLines() throws Exception {
        String result = catApplication.catFileAndStdin(false, new ByteArrayInputStream(STDIN_SINGLE_1.getBytes(StandardCharsets.UTF_8)), PATH_SINGLE_1, "-", PATH_SINGLE_2);
        assertEquals(TEXT_SINGLE_1 + STRING_NEWLINE + STDIN_SINGLE_1 + STRING_NEWLINE + TEXT_SINGLE_2, result);
    }

    @Test
    public void catFileAndStdin_LinesFalseFilesNoneInputStreamEmpty_ReturnsEmpty() throws Exception {
        String result = catApplication.catFileAndStdin(false,  InputStream.nullInputStream());
        assertEquals("", result);
    }

    @Test
    public void catFileAndStdin_LinesFalseFilesNoValidInputStreamEmpty_ReturnsError() throws Exception {
        String result = catApplication.catFileAndStdin(false, InputStream.nullInputStream(), PATH_NOT_EXIST);
        assertEquals(new CatException(ERR_FILE_NOT_FOUND).getMessage(), result);
    }

    @Test
    public void catFileAndStdin_LinesFalseFilesNotAllValidInputStreamNull_ThrowsGeneralException() {
        Exception exception = assertThrows(Exception.class, ()->{
            catApplication.catFileAndStdin(false,  null, PATH_SINGLE_2, PATH_NOT_EXIST);
        });
        assertEquals(new CatException(ERR_GENERAL).getMessage(), exception.getMessage());
    }

    @Test
    public void catFiles_FileEmpty_ReturnsEmpty() throws Exception {
        String result = catApplication.catFiles(true, PATH_EMPTY);
        assertEquals("", result );
    }

    @Test
    public void catFiles_FileSingleLine_ReturnsNumberedSingle() throws Exception {
        String result = catApplication.catFiles(true, PATH_SINGLE_1);
        assertEquals("1 " + TEXT_SINGLE_1, result);
    }

    @Test
    public void catFiles_FileMultiLine_ReturnsNumberedMultiple() throws Exception {
        String result = catApplication.catFiles(true, PATH_MULTI_1);
        assertEquals("1 " + TEXT_MULTI_1_1 +
                STRING_NEWLINE +
                "2 " + TEXT_MULTI_1_2 +
                STRING_NEWLINE +
                "3 " + TEXT_MULTI_1_3 +
                STRING_NEWLINE +
                "4 " + TEXT_MULTI_1_4,
                result);
    }

    @Test
    public void catStdin_StdinEmpty_ReturnsEmpty() throws Exception {
        String result = catApplication.catStdin(true, InputStream.nullInputStream());
        assertEquals("", result);
    }

    @Test
    public void catStdin_StdinSingleLine_ReturnsNumberedSingle() throws Exception {
        String result = catApplication.catStdin(true, new ByteArrayInputStream(STDIN_SINGLE_1.getBytes(StandardCharsets.UTF_8)));
        assertEquals("1 " + STDIN_SINGLE_1, result);
    }

    @Test
    public void catStdin_StdinMultiLine_ReturnsNumberedMultiple() throws Exception {
        String result = catApplication.catStdin(true, new ByteArrayInputStream(STDIN_MULTI_1.getBytes(StandardCharsets.UTF_8)));
        assertEquals("1 " + TEXT_MULTI_1_1 +
                STRING_NEWLINE +
                "2 " + TEXT_MULTI_1_2 +
                STRING_NEWLINE +
                "3 " + TEXT_MULTI_1_3 +
                STRING_NEWLINE +
                "4 " + TEXT_MULTI_1_4,
                result);
    }

    @Test
    public void catFiles_FilesMultiple_ReturnsNumberedMultiple() throws Exception {
        String result = catApplication.catFiles(true, PATH_MULTI_1, PATH_SINGLE_1);
        assertEquals("1 " + TEXT_MULTI_1_1 +
                STRING_NEWLINE +
                "2 " + TEXT_MULTI_1_2 +
                STRING_NEWLINE +
                "3 " + TEXT_MULTI_1_3 +
                STRING_NEWLINE +
                "4 " + TEXT_MULTI_1_4 +
                STRING_NEWLINE +
                "5 " + TEXT_SINGLE_1,
                result);
    }

    @Test
    public void catFileAndStdin_FilesMultipleInputStream_ReturnsNumberedMultiple() throws Exception {
        String result = catApplication.catFileAndStdin(true, new ByteArrayInputStream(STDIN_MULTI_1.getBytes(StandardCharsets.UTF_8)), PATH_MULTI_1, PATH_SINGLE_2, "-");
        assertEquals("1 " + TEXT_MULTI_1_1 +
                STRING_NEWLINE +
                "2 " + TEXT_MULTI_1_2 +
                STRING_NEWLINE +
                "3 " + TEXT_MULTI_1_3 +
                STRING_NEWLINE +
                "4 " + TEXT_MULTI_1_4 +
                STRING_NEWLINE +
                "5 " + TEXT_SINGLE_2 +
                STRING_NEWLINE +
                "6 " + TEXT_MULTI_1_1 +
                STRING_NEWLINE +
                "7 " + TEXT_MULTI_1_2 +
                STRING_NEWLINE +
                "8 " + TEXT_MULTI_1_3 +
                STRING_NEWLINE +
                "9 " + TEXT_MULTI_1_4,
                 result);
    }

    @Test
    public void catFiles_FileNull_ThrowsGeneralException() {
        Exception exception = assertThrows(Exception.class, ()->{
            catApplication.catFiles(true, null);
        });
        assertEquals(new CatException(ERR_GENERAL).getMessage(), exception.getMessage());
    }

    @Test
    public void catFiles_FileIsDir_ReturnsIsDirException() throws Exception {
        String result = catApplication.catFiles(true, PATH_DIRECTORY);
        assertEquals(new CatException(ERR_IS_DIR).getMessage(), result);
    }

    @Test
    public void catFileAndStdin_FileNull_ThrowsGeneralException() {
        Exception exception = assertThrows(Exception.class, ()->{
            catApplication.catFileAndStdin(true, new ByteArrayInputStream(PATH_MULTI_1.getBytes(StandardCharsets.UTF_8)), null);
        });
        assertEquals(new CatException(ERR_GENERAL).getMessage(), exception.getMessage());
    }

    @Test
    public void catFileAndStdin_InputStreamNull_ThrowsGeneralException() {
        Exception exception = assertThrows(Exception.class, ()->{
            catApplication.catFileAndStdin(true, null, PATH_SINGLE_2);
        });
        assertEquals(new CatException(ERR_GENERAL).getMessage(), exception.getMessage());
    }

    @Test
    public void catFileAndStdin_FileIsDir_ReturnsIsDirException() throws Exception {
        String result = catApplication.catFileAndStdin(true, new ByteArrayInputStream(STDIN_SINGLE_1.getBytes(StandardCharsets.UTF_8)), PATH_DIRECTORY, "-");
        assertEquals(new CatException(ERR_IS_DIR).getMessage() +
                STRING_NEWLINE +
                "1 " +
                "Hello World", result);
    }

    @Test
    public void run_OutputStreamNull_ThrowsNullStreamsException() {
        String[] args = {};
        Exception exception = assertThrows(Exception.class, ()->{
            catApplication.run(args, new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)), null);
        });
        assertEquals(new CatException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void run_FilesEmpty_ReturnsCatStdin() throws CatException {
        String[] args = {};
        OutputStream outputStream = new ByteArrayOutputStream();
        catApplication.run(args, new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)), outputStream);
        assertEquals(STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void run_FilesNotEmptyInputStreamNull_ReturnsCatFiles() throws CatException {
        String[] args = {PATH_SINGLE_1};
        OutputStream outputStream = new ByteArrayOutputStream();
        catApplication.run(args, null, outputStream);
        assertEquals(TEXT_SINGLE_1 + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void run_FilesNotEmptyInputStreamNotNull_ReturnsCatFileAndStdin() throws CatException {
        String[] args = {PATH_SINGLE_1, "-"};
        OutputStream outputStream = new ByteArrayOutputStream();
        catApplication.run(args, new ByteArrayInputStream(STDIN_SINGLE_2.getBytes(StandardCharsets.UTF_8)), outputStream);
        assertEquals(TEXT_SINGLE_1 + STRING_NEWLINE + "This is a test string." + STRING_NEWLINE, outputStream.toString());
    }

}