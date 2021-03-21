package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.UniqException;
import sg.edu.nus.comp.cs4218.exception.WcException;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;


class UniqArgumentsTest {

    private static final String INPUT_FILE = "input_file.txt";
    private static final String OUTPUT_FILE = "output_file.txt";
    private static final String FLAG_COUNT = "-c";
    private static final String FLAG_REPEATED = "-d";
    private static final String FLAG_ALL_REPEATED = "-D";
    private static final String FLAG_INVALID = "-x";

    private static UniqArguments uniqArguments;

    @BeforeEach
    void setupShell() {
        uniqArguments = new UniqArguments();
    }

    @AfterEach
    void tearDownShell() { uniqArguments = null; }

    @Test
    public void parse_CountTrueRepeatedTrueAllRepeatedTrueInputExistsOutputExists_ReturnsMatching() throws UniqException {
        uniqArguments.parse(FLAG_COUNT, FLAG_REPEATED, FLAG_ALL_REPEATED, INPUT_FILE, OUTPUT_FILE);
        assertTrue(uniqArguments.isCount());
        assertTrue(uniqArguments.isRepeated());
        assertTrue(uniqArguments.isAllRepeated());
        assertEquals(INPUT_FILE, uniqArguments.getInputFile());
        assertEquals(OUTPUT_FILE, uniqArguments.getOutputFile());
    }

    @Test
    public void parse_CountTrueRepeatedFalseAllRepeatedFalseInputNotExistsOutputNotExists_ReturnsMatching() throws UniqException {
        uniqArguments.parse(FLAG_COUNT);
        assertTrue(uniqArguments.isCount());
        assertFalse(uniqArguments.isRepeated());
        assertFalse(uniqArguments.isAllRepeated());
        assertNull(uniqArguments.getInputFile());
        assertNull(uniqArguments.getOutputFile());
    }

    @Test
    public void parse_CountFalseRepeatedFalseAllRepeatedTrueInputNotExistsOutputExists_ReturnsMatching() throws UniqException {
        uniqArguments.parse( FLAG_ALL_REPEATED, "-", OUTPUT_FILE);
        assertFalse(uniqArguments.isCount());
        assertFalse(uniqArguments.isRepeated());
        assertTrue(uniqArguments.isAllRepeated());
        assertNull(uniqArguments.getInputFile());
        assertEquals(OUTPUT_FILE, uniqArguments.getOutputFile());
    }

    @Test
    public void parse_CountFalseRepeatedTrueAllRepeatedFalseInputExistsOutputNotExists_ReturnsMatching() throws UniqException {
        uniqArguments.parse(FLAG_REPEATED, INPUT_FILE);
        assertFalse(uniqArguments.isCount());
        assertTrue(uniqArguments.isRepeated());
        assertFalse(uniqArguments.isAllRepeated());
        assertEquals(INPUT_FILE, uniqArguments.getInputFile());
        assertNull(uniqArguments.getOutputFile());
    }

    @Test
    public void parse_ArgsNull_ThrowsNullArgsException() {
        UniqException uniqException = assertThrows(UniqException.class,()->{
            uniqArguments.parse(null);
        });
        assertEquals(new UniqException(ERR_NULL_ARGS).getMessage(), uniqException.getMessage());
    }

    @Test
    public void parse_FlagInvalid_ThrowsInvalidFlagException() {
        UniqException uniqException = assertThrows(UniqException.class, ()->{
            uniqArguments.parse(FLAG_INVALID);
        });
        assertEquals(new UniqException(ERR_INVALID_FLAG).getMessage(), uniqException.getMessage());
    }

}