package Hackathon;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.EnvironmentUtil;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertEquals("-----end----" +
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


}
