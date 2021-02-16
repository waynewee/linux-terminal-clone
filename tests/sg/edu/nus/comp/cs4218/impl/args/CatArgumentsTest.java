package sg.edu.nus.comp.cs4218.impl.args;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.impl.app.args.CatArguments;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;


class CatArgumentsTest {

    private static CatArguments catArguments;

    @BeforeAll
    static void setupShell() {
        catArguments = new CatArguments();
    }

    // TEMPLATE_BUG
    @Test
    public void parse_WithShowLinesFlag_IsLinesShownReturnsTrue() throws CatException {

        String[] args = {"cat", "filename.txt", "-n"};
        catArguments.parse(args);
        assertTrue(catArguments.isLineNumber());

    }

    @Test
    public void parse_WithoutShowLinesFlag_IsLinesShownReturnsFalse() throws CatException {

        String[] args = {"cat", "filename.txt"};
        catArguments.parse(args);
        assertFalse(catArguments.isLineNumber());

    }

    @Test
    public void parse_InvalidFlag_ThrowsException() throws CatException {

        String[] args = {"cat", "filename.txt", "-x"};

        CatException catException = assertThrows(CatException.class, () -> {
            catArguments.parse(args);
        });

        assertEquals(catException.getMessage(), new CatException(ERR_INVALID_FLAG).getMessage());

    }



}