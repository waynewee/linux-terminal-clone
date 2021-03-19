package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.PasteException;

import static org.junit.jupiter.api.Assertions.*;


class PasteArgumentsTest {

    private static final String INPUT_FILE = "input_file.txt";
    private static final String OUTPUT_FILE = "output_file.txt";
    private static final String FLAG_SERIAL = "-s";
    private static final String FLAG_INVALID = "-x";

    private static PasteArguments pasteArguments;

    @BeforeEach
    void setupShell() {
        pasteArguments = new PasteArguments();
    }

    @AfterEach
    void tearDownShell() { pasteArguments = null; }

    @Test
    public void test() throws PasteException {
        pasteArguments.parse(INPUT_FILE);
        assertFalse(pasteArguments.isSerial());
    }

}