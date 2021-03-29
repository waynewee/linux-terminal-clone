package Hackathon;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.EnvironmentUtil;
import sg.edu.nus.comp.cs4218.exception.*;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class Team12Test {

    private static ShellImpl shellImpl;
    private static final String root = EnvironmentUtil.currentDirectory;
    private static final String path = "src/test/java/Hackathon/resources";
    private static final String pathFormat = path + "/%s";
    private static ByteArrayOutputStream outputStream;

    @BeforeAll
    public static void setupShell() throws AbstractApplicationException, ShellException {
        shellImpl = new ShellImpl();
        shellImpl.parseAndEvaluate("cd " + path, outputStream);
    }

    @BeforeEach
    public void beforeEach() {
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    public void team12bug2_GrepApplicationEmptyPattern_ReturnsMatch() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("grep \"\" a.txt", outputStream);
        assertEquals("a.txt: Hello sir" +
                STRING_NEWLINE +
                "a.txt: -----end----" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug3_GrepApplicationSpacePattern_ReturnsMatch() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("grep \" \" a.txt", outputStream);
        assertEquals("a.txt: Hello sir" +
                STRING_NEWLINE +
                "a.txt: -----end----" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug4_GrepApplicationDashPattern_ReturnsMatch() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("grep - a.txt", outputStream);
        assertEquals("a.txt: -----end----" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug5_GrepApplicationDashFile_ReturnsGrepFromStdin() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("grep - < a.txt", outputStream);
        assertEquals("(standard input): -----end----" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug6_GrepApplicationFile_ReturnsGrepFromFile() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("grep \"\" grep_test", outputStream);
        assertEquals(STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug7_GrepApplicationNonExistingFile_ReturnsAppropriateMessage() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("grep \"\" \"    grep_test1.txt\"", outputStream);
        assertEquals("grep:     grep_test1.txt: No such file or directory" +
                STRING_NEWLINE, outputStream.toString());
    }

    //8 duplicate of 6

    @Test
    public void team12bug9_GrepApplicationGrepFiles_ReturnsFileNamesPrepended() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("grep awesome grep_test1.txt grep_test2.txt", outputStream);
        assertEquals("grep_test1.txt: awesomee \uD83D\uDE0A\uD83D\uDE0A\uD83D\uDE0A\uD83D\uDE0A" +
                STRING_NEWLINE +
                "grep_test1.txt: awesomeeee" +
                STRING_NEWLINE +
                "grep_test2.txt:     awesomee \uD83D\uDE0A\uD83D\uDE0A\uD83D\uDE0A" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug10_GrepApplicationCombinedFlags_ReturnsMatch() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("grep -cHi hello a.txt", outputStream);
        assertEquals("a.txt: 1" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug11_GrepApplicationGrepCount_ReturnsCount() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("grep -c awesome grep_test1.txt grep_test2.txt", outputStream);
        assertEquals("grep_test1.txt: 2" +
                STRING_NEWLINE +
                "grep_test2.txt: 1" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug16_EchoApplication_ReturnsTerminationWithNewLine() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("echo hello", outputStream);
        assertEquals("hello" + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug23_WcApplicationFileNameWhiteSpace_ReturnsFileNotFoundException() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("wc \"\"", outputStream);
        assertEquals(new WcException(ERR_FILE_NOT_FOUND).getMessage() + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug24_WcApplicationFileNameWhiteSpace_ReturnsFileNotFoundException() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("wc \" \"", outputStream);
        assertEquals(new WcException(ERR_FILE_NOT_FOUND).getMessage() + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug25_WcApplicationInputFile_ReturnsCountFromInputFile() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("wc wc_test1.txt", outputStream);
        assertEquals(" 6 5 49 wc_test1.txt" + STRING_NEWLINE, outputStream.toString());
    }

    //26 duplicate of 25

    @Test
    public void team12bug27_WcApplication_ReturnsRelativeFilePath() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("cd ..", outputStream);
        shellImpl.parseAndEvaluate("wc resources/wc_test1.txt", outputStream);
        shellImpl.parseAndEvaluate("cd " + root + "/" + path, outputStream);
        assertEquals(" 6 5 49 resources/wc_test1.txt" + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug28_UniqApplicationFile_ReturnsUniqFromFile() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("uniq uniq_test1.txt", outputStream);
        assertEquals("   A" +
                STRING_NEWLINE +
                "" +
                STRING_NEWLINE +
                "B" +
                STRING_NEWLINE +
                "C" +
                STRING_NEWLINE +
                "   A" +
                STRING_NEWLINE +
                "B" +
                STRING_NEWLINE +
                "   A" +
                STRING_NEWLINE, outputStream.toString());
    }

    //29 fixed by 28

    @Test
    public void team12bug30_UniqApplicationCombinedFlags_ReturnsUniq() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("uniq -dc uniq_test1.txt", outputStream);
        assertEquals("2    A" +
                STRING_NEWLINE +
                "2 " +
                STRING_NEWLINE +
                "2 B" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug31_UniqApplicationAllDuplicateCount_ThrowsMeaningless() {
        UniqException uniqException = assertThrows(UniqException.class, ()->{
            shellImpl.parseAndEvaluate("uniq -c -D uniq_test1.txt", outputStream);
        });
        assertEquals(new UniqException(ERR_UNIQ_MEANINGLESS).getMessage(), uniqException.getMessage());
    }

    @Test
    public void team12bug32_UniqApplicationFileNameWhiteSpace_ThrowsFileNotFoundException() {
        UniqException uniqException = assertThrows(UniqException.class, ()->{
            shellImpl.parseAndEvaluate("uniq \"\"", outputStream);
        });
        assertEquals(new UniqException(ERR_FILE_NOT_FOUND).getMessage(), uniqException.getMessage());
    }

    @Test
    public void team12bug33_UniqApplicationFileNameDash_ReturnsUniqFromStdin() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("uniq - < uniq_test1.txt", outputStream);
        assertEquals("   A" +
                STRING_NEWLINE +
                "" +
                STRING_NEWLINE +
                "B" +
                STRING_NEWLINE +
                "C" +
                STRING_NEWLINE +
                "   A" +
                STRING_NEWLINE +
                "B" +
                STRING_NEWLINE +
                "   A" +
                STRING_NEWLINE, outputStream.toString());
    }

    //34 not fixed, 35 duplicate of 34

    @Test
    public void team12bug39_CatApplicationFile_ReturnsCatFile() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("cat cat_test1.txt", outputStream);
        assertEquals("awesomee \uD83D\uDE0A\uD83D\uDE0A\uD83D\uDE0A\uD83D\uDE0A" +
                STRING_NEWLINE +
                " aweso" +
                STRING_NEWLINE +
                "" +
                STRING_NEWLINE +
                "" +
                STRING_NEWLINE +
                "     awesomeeee" +
                STRING_NEWLINE +
                "aw" + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug40_CatApplicationFileNameWhiteSpace_ReturnsFileNotFoundException() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("cat \"\"", outputStream);
        assertEquals(new CatException(ERR_FILE_NOT_FOUND).getMessage() + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug41_CatApplicationFileNameWhiteSpace_ReturnsFileNotFoundException() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("cat \" \"", outputStream);
        assertEquals(new CatException(ERR_FILE_NOT_FOUND).getMessage() + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug42_CatApplicationFileNameDash_ReturnsCatFromStdin() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("cat - < cat_test1.txt", outputStream);
        assertEquals("awesomee \uD83D\uDE0A\uD83D\uDE0A\uD83D\uDE0A\uD83D\uDE0A" +
                STRING_NEWLINE +
                " aweso" +
                STRING_NEWLINE +
                "" +
                STRING_NEWLINE +
                "" +
                STRING_NEWLINE +
                "     awesomeeee" +
                STRING_NEWLINE +
                "aw" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug43_CatApplicationFileNameDashWithFile_ReturnsCatFromFileAndStdin() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("cat cat_test1.txt - < cat_test2.txt", outputStream);
        assertEquals("awesomee \uD83D\uDE0A\uD83D\uDE0A\uD83D\uDE0A\uD83D\uDE0A" +
                STRING_NEWLINE +
                " aweso" +
                STRING_NEWLINE +
                "" +
                STRING_NEWLINE +
                "" +
                STRING_NEWLINE +
                "     awesomeeee" +
                STRING_NEWLINE +
                "aw" +
                STRING_NEWLINE +
                "cat cat cat" +
                STRING_NEWLINE +
                " meow" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug44_CatApplicationFileNameDashPosition_ReturnsCatProperlyArranged() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("cat cat_test1.txt - cat_test2.txt < c.txt", outputStream);
        assertEquals("awesomee \uD83D\uDE0A\uD83D\uDE0A\uD83D\uDE0A\uD83D\uDE0A" +
                STRING_NEWLINE +
                " aweso" +
                STRING_NEWLINE +
                "" +
                STRING_NEWLINE +
                "" +
                STRING_NEWLINE +
                "     awesomeeee" +
                STRING_NEWLINE +
                "aw" +
                STRING_NEWLINE +
                "hello wello" +
                STRING_NEWLINE +
                "hello sir" +
                STRING_NEWLINE +
                "sir" +
                STRING_NEWLINE +
                "cat cat cat" +
                STRING_NEWLINE +
                " meow" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team12bug58_PasteApplicationFileDash_ReturnsPasteFromStdin() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("paste - < a.txt", outputStream);
        assertEquals("Hello sir" +
                STRING_NEWLINE +
                "-----end----" +
                STRING_NEWLINE, outputStream.toString());
    }


}
