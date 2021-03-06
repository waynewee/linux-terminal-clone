package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CatException;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;


class CatArgumentsTest {

    private static final String FILE = "filename.txt";
    private static final String FLAG_LINES = "-n";
    private static final String FLAG_INVALID = "-x";

    private static CatArguments catArguments;

    @BeforeAll
    static void setupShell() {
        catArguments = new CatArguments();
    }

    @Test
    public void parse_LinesTrue_ReturnsIsLineNumberTrue() throws CatException {
        catArguments.parse(FLAG_LINES, FILE);
        assertTrue(catArguments.isLineNumber());
    }

    @Test
    public void parse_LinesFalse_ReturnsIsLineNumberFalse() throws CatException {
        catArguments.parse(FILE);
        assertFalse(catArguments.isLineNumber());
    }

    @Test
    public void parse_FlagInvalid_ThrowsInvalidFlagException() {
        CatException catException = assertThrows(CatException.class, () -> {
            catArguments.parse(FLAG_INVALID, FILE);
        });
        assertEquals(catException.getMessage(), new CatException(ERR_INVALID_FLAG).getMessage());
    }

    @Test
    public void parse_FlagAfterFile_ReturnsIsLineNumberFalse() throws CatException {
        catArguments.parse(FILE, FLAG_LINES);
        assertFalse(catArguments.isLineNumber());
    }

}