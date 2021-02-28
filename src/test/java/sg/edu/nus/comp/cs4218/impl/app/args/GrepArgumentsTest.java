package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.impl.app.args.GrepArguments;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;


class GrepArgumentsTest {

    private static final String FILE = "filename.txt";
    private static final String FLAG_CASE = "-i";
    private static final String FLAG_COUNT = "-c";
    private static final String FLAG_PREFIX = "-H";
    private static final String FLAG_INVALID = "-x";

    private static GrepArguments grepArguments;

    @BeforeEach
    void setupShell() {
        grepArguments = new GrepArguments();
    }

    @AfterEach
    void tearDownShell() { grepArguments = null; }

    @Test
    public void parse_CaseInsensitiveTrueLinesTruePrefixFileNameTrue_ReturnsMatching() throws Exception {
        grepArguments.parse(FLAG_CASE, FLAG_COUNT, FLAG_PREFIX, FILE);
        assertTrue(grepArguments.isCaseInsensitive());
        assertTrue(grepArguments.isCountOfLinesOnly());
        assertTrue(grepArguments.isPrefixFileName());
    }

    @Test
    public void parse_CaseInsensitiveTrueLinesFalsePrefixFileNameFalse_ReturnsMatching() throws Exception {
        grepArguments.parse(FLAG_CASE, FILE);
        assertTrue(grepArguments.isCaseInsensitive());
        assertFalse(grepArguments.isCountOfLinesOnly());
        assertFalse(grepArguments.isPrefixFileName());
    }

    @Test
    public void parse_CaseInsensitiveFalseLinesFalsePrefixFileNameTrue_ReturnsMatching() throws Exception {
        grepArguments.parse(FLAG_PREFIX, FILE);
        assertFalse(grepArguments.isCaseInsensitive());
        assertFalse(grepArguments.isCountOfLinesOnly());
        assertTrue(grepArguments.isPrefixFileName());
    }

    @Test
    public void parse_FlagsAllAfterFile_ReturnsMatching() throws Exception {
        grepArguments.parse(FILE, FLAG_CASE, FLAG_COUNT, FLAG_PREFIX);
        assertFalse(grepArguments.isCaseInsensitive());
        assertFalse(grepArguments.isCountOfLinesOnly());
        assertFalse(grepArguments.isPrefixFileName());
    }

    @Test
    public void parse_FlagsOneBeforeFileRestAfterFile_ReturnsMatching() throws Exception {
        grepArguments.parse(FLAG_CASE, FILE, FLAG_COUNT, FLAG_PREFIX);
        assertTrue(grepArguments.isCaseInsensitive());
        assertFalse(grepArguments.isCountOfLinesOnly());
        assertFalse(grepArguments.isPrefixFileName());
    }

    @Test
    public void parse_FlagInvalid_ThrowsInvalidFlagException() {
        GrepException grepException = assertThrows(GrepException.class, () -> {
            grepArguments.parse(FLAG_INVALID, FILE);
        });
        assertEquals(grepException.getMessage(), new GrepException(ERR_INVALID_FLAG).getMessage());
    }

}