package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.UniqException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class UniqApplicationTest {

    private static UniqApplication uniqApplication;

    private static final String PATH = "src/test/resources/impl/app/UniqApplicationResources/".replace('/', CHAR_FILE_SEP);

    private static final String FILE_DUP_ADJ = "duplicate-adjacent.txt";
    private static final String FILE_DUP_NO_ADJ = "duplicate-no-adjacent.txt";
    private static final String FILE_NO_DUP = "no-duplicate.txt";
    private static final String FILE_OUTPUT = "output.txt";
    private static final String FILE_NO_EXIST = "no-exist.txt";

    private static final String PATH_DUP_ADJ = PATH + FILE_DUP_ADJ;
    private static final String PATH_DUP_NO_ADJ = PATH + FILE_DUP_NO_ADJ;
    private static final String PATH_NO_DUP = PATH + FILE_NO_DUP;
    private static final String PATH_OUTPUT = PATH + FILE_OUTPUT;
    private static final String PATH_NO_EXIST = PATH + FILE_NO_EXIST;

    private static final String STDIN_LINE_1 = "This is a sentence";
    private static final String STDIN_LINE_2 = "This is a line";
    private static final String STDIN_LINE_3 = "Hello World!";

    private static final String STDIN_DUP_ADJ = STDIN_LINE_1 +
            STRING_NEWLINE +
            STDIN_LINE_1 +
            STRING_NEWLINE +
            STDIN_LINE_2 +
            STRING_NEWLINE +
            STDIN_LINE_3 +
            STRING_NEWLINE +
            STDIN_LINE_3 +
            STRING_NEWLINE +
            STDIN_LINE_3;

    private static final String STDIN_DUP_NO_ADJ = STDIN_LINE_1 +
            STRING_NEWLINE +
            STDIN_LINE_2 +
            STRING_NEWLINE +
            STDIN_LINE_1 +
            STRING_NEWLINE +
            STDIN_LINE_2;

    private static final String STDIN_NO_DUP = STDIN_LINE_1 +
            STRING_NEWLINE +
            STDIN_LINE_2 +
            STRING_NEWLINE +
            STDIN_LINE_3;

    @BeforeAll
    static void setupShell() {
        uniqApplication = new UniqApplication();
    }

    @Test
    public void uniqFromFile_CountTrueRepeatedFalseAllRepeatedFalseFileDupNoAdj_ReturnsDupRemovedWithCount() throws Exception {
        String result = uniqApplication.uniqFromFile(true, false, false, PATH_DUP_NO_ADJ, null);
        assertEquals("1 This file contains duplicate lines" +
                STRING_NEWLINE +
                "1 But none of them are adjacent" +
                STRING_NEWLINE +
                "1 This file contains duplicate lines" +
                STRING_NEWLINE +
                "1 But none of them are adjacent" +
                STRING_NEWLINE +
                "1 This file contains duplicate lines" +
                STRING_NEWLINE +
                "1 But none of them are adjacent" +
                STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromFile_CountTrueRepeatedTrueAllRepeatedTrueFileNoDup_ReturnsNewLine() throws Exception {
        String result = uniqApplication.uniqFromFile(true, true, true, PATH_NO_DUP, null);
        assertEquals(STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromFile_CountFalseRepeatedFalseAllRepeatedTrueFileDupAdj_ReturnsAllDup() throws Exception {
        String result = uniqApplication.uniqFromFile(false, false, true, PATH_DUP_ADJ, null);
        assertEquals("This file contains multiple duplicate lines" +
                STRING_NEWLINE +
                "This file contains multiple duplicate lines" +
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
                "And they are adjacent" +
                STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromFile_CountFalseRepeatedTrueAllRepeatedTrueFileDupNoAdj_ReturnsNewLine() throws Exception {
        String result = uniqApplication.uniqFromFile(false, true, true, PATH_DUP_NO_ADJ, null);
        assertEquals(STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromFile_CountFalseRepeatedTrueAllRepeatedFalseFileNoDup_ReturnsNewLine() throws Exception {
        String result = uniqApplication.uniqFromFile(false, true, false, PATH_NO_DUP, null);
        assertEquals(STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromFile_CountTrueRepeatedTrueAllRepeatedFalseFileDupAdj_ReturnsDupWithCount() throws Exception {
        String result = uniqApplication.uniqFromFile(true, true, false, PATH_DUP_ADJ, null);
        assertEquals("2 This file contains multiple duplicate lines" +
                STRING_NEWLINE +
                "3 And they are adjacent" +
                STRING_NEWLINE +
                "2 This file does contain duplicates" +
                STRING_NEWLINE +
                "2 And they are adjacent" +
                STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromFile_CountTrueRepeatedFalseAllRepeatedTrueFileNoDup_ReturnsNewLine() throws Exception {
        String result = uniqApplication.uniqFromFile(true, false, true, PATH_NO_DUP, null);
        assertEquals(STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromStdin_CountTrueRepeatedTrueAllRepeatedFalseFileNoDup_ReturnsNewLine() throws Exception {
        String result = uniqApplication.uniqFromStdin(true, true, false, new ByteArrayInputStream(STDIN_NO_DUP.getBytes(StandardCharsets.UTF_8)), null);
        assertEquals(STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromStdin_CountTrueRepeatedFalseAllRepeatedTrueFileDupAdj_ReturnsNewLine() throws Exception {
        String result = uniqApplication.uniqFromStdin(true, false, true, new ByteArrayInputStream(STDIN_DUP_ADJ.getBytes(StandardCharsets.UTF_8)), null);
        assertEquals("2 This is a sentence" +
                STRING_NEWLINE +
                "2 This is a sentence" +
                STRING_NEWLINE +
                "3 Hello World!" +
                STRING_NEWLINE +
                "3 Hello World!" +
                STRING_NEWLINE +
                "3 Hello World!" +
                STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromStdin_CountFalseRepeatedTrueAllRepeatedTrueFileDupNoAdj_ReturnsNewLine() throws Exception {
        String result = uniqApplication.uniqFromStdin(false, true, true, new ByteArrayInputStream(STDIN_DUP_NO_ADJ.getBytes(StandardCharsets.UTF_8)), null);
        assertEquals(STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromStdin_CountFalseRepeatedFalseAllRepeatedTrueFileNoDup_ReturnsNewLine() throws Exception {
        String result = uniqApplication.uniqFromStdin(false, false, true, new ByteArrayInputStream(STDIN_NO_DUP.getBytes(StandardCharsets.UTF_8)), null);
        assertEquals(STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromStdin_CountFalseRepeatedFalseAllRepeatedFalseFileDupAdj_ReturnsNewLine() throws Exception {
        String result = uniqApplication.uniqFromStdin(false, false, false, new ByteArrayInputStream(STDIN_DUP_ADJ.getBytes(StandardCharsets.UTF_8)), null);
        assertEquals("This is a sentence" +
                STRING_NEWLINE +
                "This is a line" +
                STRING_NEWLINE +
                "Hello World!" +
                STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromStdin_CountTrueRepeatedFalseAllRepeatedFalseFileDupNoAdj_ReturnsNewLine() throws Exception {
        String result = uniqApplication.uniqFromStdin(true, false, false, new ByteArrayInputStream(STDIN_DUP_NO_ADJ.getBytes(StandardCharsets.UTF_8)), null);
        assertEquals("1 This is a sentence" +
                STRING_NEWLINE +
                "1 This is a line" +
                STRING_NEWLINE +
                "1 This is a sentence" +
                STRING_NEWLINE +
                "1 This is a line" +
                STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromStdin_CountTrueRepeatedTrueAllRepeatedTrueFileDupAdj_ReturnsNewLine() throws Exception {
        String result = uniqApplication.uniqFromStdin(true, true, true, new ByteArrayInputStream(STDIN_DUP_ADJ.getBytes(StandardCharsets.UTF_8)), null);
        assertEquals("2 This is a sentence" +
                STRING_NEWLINE +
                "2 This is a sentence" +
                STRING_NEWLINE +
                "3 Hello World!" +
                STRING_NEWLINE +
                "3 Hello World!" +
                STRING_NEWLINE +
                "3 Hello World!" +
                STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromFile_OutputFileSpecified_ReturnsWriteToOutputFile() throws Exception {
        clearOutputFile();
        uniqApplication.uniqFromFile(true, true, true, PATH_DUP_ADJ, PATH_OUTPUT);
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
                "2 And they are adjacent" +
                STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromStdin_OutputFileSpecified_ReturnsWriteToOutputFile() throws Exception {
        clearOutputFile();
        uniqApplication.uniqFromStdin(true, true, true, new ByteArrayInputStream(STDIN_DUP_ADJ.getBytes(StandardCharsets.UTF_8)), PATH_OUTPUT);
        String result = readOutputFile();
        assertEquals("2 This is a sentence" +
                STRING_NEWLINE +
                "2 This is a sentence" +
                STRING_NEWLINE +
                "3 Hello World!" +
                STRING_NEWLINE +
                "3 Hello World!" +
                STRING_NEWLINE +
                "3 Hello World!" +
                STRING_NEWLINE, result);
    }

    @Test
    public void uniqFromFile_FileNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            uniqApplication.uniqFromFile(false, false, false, null, null);
        });
        assertEquals(new UniqException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void uniqFromStdin_StdinNull_ThrowsNullStreamsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            uniqApplication.uniqFromStdin(false, false, false, null, null);
        });
        assertEquals(new UniqException(ERR_NULL_STREAMS).getMessage(), exception.getMessage());
    }

    @Test
    public void uniqFromFile_FileNoExist_ThrowsFileNotFoundException() {
        Exception exception = assertThrows(Exception.class, ()->{
            uniqApplication.uniqFromFile(false, false, false, PATH_NO_EXIST, null);
        });
        assertEquals(new UniqException(ERR_FILE_NOT_FOUND).getMessage(), exception.getMessage());
    }

    private String readOutputFile() throws FileNotFoundException {
        StringBuilder result = new StringBuilder();
        File file = new File(PATH_OUTPUT);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            result.append(scanner.nextLine()).append(STRING_NEWLINE);
        }
        scanner.close();
        return result.toString();
    }

    private void clearOutputFile() throws IOException {
        FileWriter fileWriter = new FileWriter(PATH_OUTPUT);
        fileWriter.write("");
        fileWriter.close();
    }


}