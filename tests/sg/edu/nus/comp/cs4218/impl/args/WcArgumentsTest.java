package sg.edu.nus.comp.cs4218.impl.args;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.app.args.CatArguments;
import sg.edu.nus.comp.cs4218.impl.app.args.WcArguments;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;


class WcArgumentsTest {

    private static WcArguments wcArguments;

    @BeforeAll
    static void setupShell() {
        wcArguments = new WcArguments();
    }

    @Test
    public void parse_WithBytesFlag_OnlyIsBytesReturnsTrue() throws WcException {

        String[] args = {"wc", "filename.txt", "-c"};
        wcArguments.parse(args);
        assertTrue(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertFalse(wcArguments.isWords());

    }

    @Test
    public void parse_WithLinesFlag_IsLinesReturnsTrue() throws WcException {

        String[] args = {"wc", "filename.txt", "-l"};
        wcArguments.parse(args);
        assertFalse(wcArguments.isBytes());
        assertTrue(wcArguments.isLines());
        assertFalse(wcArguments.isWords());

    }

    @Test
    public void parse_WithWordsFlag_IsWordsReturnsTrue() throws WcException {

        String[] args = {"wc", "filename.txt", "-w"};
        wcArguments.parse(args);
        assertFalse(wcArguments.isBytes());
        assertFalse(wcArguments.isLines());
        assertTrue(wcArguments.isWords());

    }

}