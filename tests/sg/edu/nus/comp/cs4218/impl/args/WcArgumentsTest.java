package sg.edu.nus.comp.cs4218.impl.args;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.app.args.CatArguments;
import sg.edu.nus.comp.cs4218.impl.app.args.GrepArguments;
import sg.edu.nus.comp.cs4218.impl.app.args.WcArguments;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;


class WcArgumentsTest {

    private static final String FILE = "filename.txt";
    private static final String FLAG_BYTES = "-c";
    private static final String FLAG_LINES = "-l";
    private static final String FLAG_WORDS = "-w";
    private static final String FLAG_INVALID = "-x";
    private static final String FLAG_BYTES_WORDS = "-cw";
    private static final String FLAG_WORDS_LINES = "-wl";
    private static final String FLAG_BYTES_LINES = "-cl";

    private static WcArguments wcArguments;

    @BeforeEach
    void setupShell() {
        wcArguments = new WcArguments();
    }

    @AfterEach
    void tearDownShell() { wcArguments = null; }

    @Test
    public void parse_LinesTrueWordsTrueBytesTrue_ReturnsMatching() throws Exception {
        wcArguments.parse(FLAG_BYTES, FLAG_LINES, FLAG_WORDS, FILE);
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
    }

    @Test
    public void parse_LinesTrueWordsFalseBytesFalse_ReturnsMatching() throws Exception {
        wcArguments.parse(FLAG_LINES, FILE);
        assertFalse(wcArguments.isWords());
        assertFalse(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
    }

    @Test
    public void parse_LinesFalseWordsFalseBytesTrue_ReturnsMatching() throws Exception {
        wcArguments.parse(FLAG_BYTES, FLAG_LINES, FLAG_WORDS, FILE);
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
    }

    @Test
    public void parse_CombinedWordsTrueLinesTrue_ReturnsMatching() throws Exception {
        wcArguments.parse(FLAG_WORDS_LINES, FILE);
        assertTrue(wcArguments.isWords());
        assertFalse(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
    }

    @Test
    public void parse_CombinedBytesTrueLinesTrue_ReturnsMatching() throws Exception {
        wcArguments.parse(FLAG_BYTES_LINES, FILE);
        assertFalse(wcArguments.isWords());
        assertTrue(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
    }

    @Test
    public void parse_CombinedBytesTrueWordsTrue_ReturnsMatching() throws Exception {
        wcArguments.parse(FLAG_BYTES_WORDS, FILE);
        assertTrue(wcArguments.isWords());
        assertTrue(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
    }

    @Test
    public void parse_FlagsAllAfterFile_ReturnsMatching() throws Exception {
        wcArguments.parse(FILE, FLAG_BYTES, FLAG_LINES, FLAG_WORDS);
        assertFalse(wcArguments.isWords());
        assertFalse(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
    }

    @Test
    public void parse_FlagsOneBeforeFileRestAfterFile_ReturnsMatching() throws Exception {
        wcArguments.parse(FLAG_BYTES, FILE, FLAG_LINES, FLAG_WORDS);
        assertFalse(wcArguments.isWords());
        assertTrue(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
    }

    @Test
    public void parse_FlagInvalid_ThrowsInvalidFlagException() {
        WcException wcException = assertThrows(WcException.class, () -> {
            wcArguments.parse(FLAG_INVALID, FILE);
        });
        assertEquals(wcException.getMessage(), new WcException(ERR_INVALID_FLAG).getMessage());
    }

}