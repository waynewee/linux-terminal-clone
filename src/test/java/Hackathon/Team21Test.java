package Hackathon;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.EnvironmentUtil;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class Team21Test {

    private static ShellImpl shellImpl;
    private static final String root = EnvironmentUtil.currentDirectory;
    private static final String path = "src/test/java/Hackathon/resources";
    private static final String pathFormat = path + "/%s";
    private static ByteArrayOutputStream outputStream;

    @BeforeEach
    public void beforeEach() {
        shellImpl = new ShellImpl();
        outputStream = new ByteArrayOutputStream();
    }


    /**
     * Bug #7
     * Description:
     * The wc application reads from standard input every time, even when the argument FILES has
     * been specified, and also when FILES is -.
     * Violation:
     * This bug violates the specification for the wc application provided in the project description.
     * Note the specification on page 14 of the project description: “With no FILES, or when FILES is -,
     * read standard input.” This specification (along with the given examples) instruct that the wc
     * application should only read from standard input in the case when there are no files provided as
     * arguments or when the argument provided is a hyphen. This difference in behaviour has not
     * been addressed in the team’s assumptions.
     */
    @Test
    public void team21bug7_WcApplication_ReturnsCountFromFile() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("wc " + String.format(pathFormat, "a.txt"), outputStream);
        assertEquals(" 1 3 23 " + String.format(pathFormat, "a.txt") + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team21bug7_WcApplication_ReturnsCountFromFileAndStandardInput() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("wc - " + String.format(pathFormat, "a.txt") + " < " + String.format(pathFormat, "b.txt"), outputStream);
        assertEquals("       2       4      21 " +
                STRING_NEWLINE +
                "       1       3      23 " + String.format(pathFormat, "a.txt") +
                STRING_NEWLINE +
                "       3 7      44 total" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team21bug7_WcApplication_ReturnsCountFromStandardInput() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("wc < " + String.format(pathFormat, "a.txt"), outputStream);
        assertEquals(" 1 3 23" + STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Bug #8
     * Description:
     * When running the wc application on a file that is not located in the current directory, it prints only
     * the terminal pathFormat name, instead of the relative pathFormat name, as expected in Unix shell behaviour.
     * Violation:
     * This bug violates the Unix shell behaviour, which is that the printed filename should be the
     * relative pathFormat name, instead of just the terminal pathFormat. Even though the specification of the cp
     * application in the project description does not specifically address this, the group should refer to
     * page 13 of the project description: “If the applications specification is not comprehensive
     * enough, you are supposed to further specify the requirements in your Assumptions.pdf file AND
     * code comments. When in doubt or when the specification is not clear, you must follow the UNIX
     * shell specification of the applications.” However, no such clarification was mentioned in the
     * team’s assumptions, hence the expected behaviour should be Unix shell behaviour.
     */
    @Test
    public void team21bug8_WcApplication_ReturnsRelativePathName() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("wc " + String.format(pathFormat, "a.txt"), outputStream);
        assertEquals(" 1 3 23 " + String.format(pathFormat, "a.txt") + STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Bug #9
     * Description:
     * The wc application prints stdin as the filename for standard input (e.g. 1 2 10 stdin), instead
     * of <empty> as described in the project description (e.g. 1 2 10 ).
     * Violation:
     * This bug violates the specification of the wc application in the project description. On page 14,
     * the display format is provided as: lines words bytes filename (filename is empty for
     * standard input). Take note of the underlined specification, which suggests that stdin should
     * not be printed as the filename.
     */
    @Test
    public void team21bug9_WcApplication_ReturnsFileNameEmptyWhenStdin() throws AbstractApplicationException, ShellException {
         shellImpl.parseAndEvaluate("wc < " + String.format(pathFormat, "a.txt"), outputStream);
         assertEquals(" 1 3 23" + STRING_NEWLINE, outputStream.toString());
     }

    /**
     * Bug #11
     * Description:
     * Echo does not output a newline for non-empty arguments.
     * Violation:
     * This is different from behaviour of almost all shells which would be expected to print the
     * arguments and then the prompt on a new line. The current code combines the arguments and
     * the prompt. This is also inconsistent with the behaviour given empty arguments, which actually
     * prints a new line.
     */
    @Test
    public void team21bug11_EchoApplication_OutputsNewlineForNonEmptyArguments() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("echo hello", outputStream);
        assertEquals("hello" + STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Bug #19
     * Description:
     * The cat application reads from standard input every time, even when the argument FILES has
     * been specified, and also when FILES is -.
     * Violation:
     * This bug violates the specification for the cat application provided in the project description.
     * Note the specification on page 15 of the project description: “With no FILES, or when FILES is -,
     * read standard input.” This specification (along with the given examples) instruct that the cat
     * application should only read from standard input in the case when there are no files provided as
     * arguments or when the argument provided is a hyphen. This difference in behaviour has not
     * been addressed in the team’s assumptions.
     */
    @Test
    public void team21bug19_CatApplication_ReturnsCatFromFile() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("cat " + String.format(pathFormat, "a.txt"), outputStream);
        assertEquals("Hello sir" +
                STRING_NEWLINE +
                "-----end----" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team21bug19_CatApplication_ReturnsCatFromFileAndStandardInput() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("cat - " + String.format(pathFormat, "a.txt") + " < " + String.format(pathFormat, "b.txt"), outputStream);
        assertEquals("cat" +
                STRING_NEWLINE +
                "bear" +
                STRING_NEWLINE +
                "rabbit cow" +
                STRING_NEWLINE +
                "Hello sir" +
                STRING_NEWLINE +
                "-----end----" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team21bug19_CatApplication_ReturnsCatFromStandardInput() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("cat < " + String.format(pathFormat, "a.txt"), outputStream);
        assertEquals("Hello sir" +
                STRING_NEWLINE+
                "-----end----" +
                STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Bug #21
     * Description:
     * The cat application throws an unexpected “Invalid flag option supplied” error when FILES is -
     * instead of running catStdin.
     * Violation:
     * This bug violates the specification for the cat application provided in the project description.
     * Note the command format: cat [-n] [FILES], and the specification on page 15 of the project
     * description: “With no FILES, or when FILES is -, read standard input.” This specification
     * instructs that “-” could be taken as an argument for cat without the presence of flags or other file
     * names. However the current implementation violates this specification.
     */
    @Test
    public void team21bug21_CatApplication_ReturnsCatStdin() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("cat - < " + String.format(pathFormat, "a.txt"), outputStream);
        assertEquals("Hello sir" +
                STRING_NEWLINE+
                "-----end----" +
                STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Bug #22
     * Description:
     * The cat application throws an unexpected “No such file or directory” error when the command is
     * run with a file and a “-” for standard input.
     * Violation:
     * This bug violates the specification for the cat application provided in the project description.
     * Note the command format: cat [-n] [FILES], and the specification on page 15 of the project
     * description: “With no FILES, or when FILES is -, read standard input.” This specification
     * instructs the command could take a file name and a “-” to return the concatenated result of the
     * file and the given standard input. However the current implementation parses “-” as a filename
     * and throws an exception that is not as instructed in the specification when “-” is found to not be
     * a file.
     */
    @Test
    public void team21bug22_CatApplication_ReturnsCatFileAndStdin() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("cat - " + String.format(pathFormat, "b.txt") + " < " + String.format(pathFormat, "a.txt"), outputStream);
        assertEquals("Hello sir" +
                STRING_NEWLINE +
                "-----end----" +
                STRING_NEWLINE +
                "cat" +
                STRING_NEWLINE +
                "bear" +
                STRING_NEWLINE +
                "rabbit cow" +
                STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Bug #25
     * Description:
     * The paste application throws an unexpected “Invalid flag option supplied” error when FILES is -
     * instead of running mergeStdin.
     * Violation:
     * This bug violates the specification for the paste application provided in the project description.
     * Note the command format: paste [-s] [FILES], and the specification on page 20 of the
     * project description: “With no FILES, or when FILES is -, read standard input.” This specification
     * instructs that “-” could be taken as an argument for paste without the presence of flags or other
     * file names. However the current implementation violates this specification.
     */
    @Test
    public void team21bug25_PasteApplication_ReturnsMergeStdin() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("paste - < " + String.format(pathFormat, "a.txt"), outputStream);
        assertEquals("Hello sir" +
                STRING_NEWLINE +
                "-----end----" +
                STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Bug #29
     * Description:
     * In the uniq, mv and paste applications, the relative file pathFormats (arguments) are not resolved
     * against the current directory (Environment.currentDirectory), causing the applications to
     * not be able to read/write to the files, and as a result, severely breaking the functionality for
     * those applications.
     * Violation:
     * This bug directly violates the specification for all the mentioned applications, since it essentially
     * breaks the functionality for those applications. This is due to erroneous code that fails to resolve
     * the relative file pathFormats (arguments) against the current directory. Instead, it may attempt to
     * read/write the file to the system’s root directory. Essentially, these applications are unable to
     * read/write to the specified files
     */
    //TODO - add test for mv application
    @Test
    public void team21bug29_CdAndUniqApplication_ReturnsNoReadError() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("cd " + path, outputStream);
        shellImpl.parseAndEvaluate("uniq a.txt", outputStream);
        shellImpl.parseAndEvaluate("cd " + root, outputStream);
        assertEquals("Hello sir" +
                STRING_NEWLINE +
                "-----end----" +
                STRING_NEWLINE, outputStream.toString());
    }

    @Test
    public void team21bug29_CdAndPasteApplication_ReturnsNoReadError() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("cd " + path, outputStream);
        shellImpl.parseAndEvaluate("paste a.txt", outputStream);
        shellImpl.parseAndEvaluate("cd " + root, outputStream);
        assertEquals("Hello sir" +
                STRING_NEWLINE +
                "-----end----" +
                STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Bug #33
     * Description:
     * Grep reads from standard input and processes it even when a file is given as input.
     * Violation:
     * This behaviour is different from normal unix shell grep. In the project description for grep: “With
     * no FILES, or when FILES is -, read standard input.” Standard input should not be read from all
     * the time.
     */
    @Test
    public void team21bug33_GrepApplication_ReturnsGrepFromFiles() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("grep Hello " + String.format(pathFormat, "a.txt"), outputStream);
        assertEquals(String.format(pathFormat, "a.txt") + ": Hello sir" + STRING_NEWLINE, outputStream.toString());
    }

    /**
     * Bug #34
     * Description:
     * Uniq prevents other applications from deleting the input file.
     * Violation:
     * This causes other applications such as rm to fail.
     */
    @Test
    public void team21bug34_UniqApplication_AllowsRmAfter() throws AbstractApplicationException, ShellException, IOException {
        FileWriter fileWriter = new FileWriter(String.format(pathFormat, "team21bug34.txt"));
        fileWriter.write("");
        fileWriter.close();
        shellImpl.parseAndEvaluate("uniq " + String.format(pathFormat, "team21bug34.txt"), outputStream);
        shellImpl.parseAndEvaluate("rm " + String.format(pathFormat, "team21bug34.txt"), outputStream);
        File file = new File(String.format(pathFormat, "team21bug34.txt"));
        assertFalse(file.exists());
    }

    /**
     * Bug #35
     * Description:
     * Uniq prints null on empty files.
     * Violation:
     * According to the project description, uniq prints lines from the file, however, null is not a line in
     * the file.
     */
    @Test
    public void team21bug35_UniqApplication_PrintsNewLineOnEmptyFile() throws AbstractApplicationException, ShellException {
        shellImpl.parseAndEvaluate("uniq " + String.format(pathFormat, "empty.txt"), outputStream);
        assertEquals(STRING_NEWLINE, outputStream.toString());
    }
}
