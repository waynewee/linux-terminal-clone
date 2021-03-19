package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sg.edu.nus.comp.cs4218.exception.PasteException;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

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
    public void parse_SerialTrue_ReturnsMatching() throws PasteException {
        pasteArguments.parse(FLAG_SERIAL);
        assertTrue(pasteArguments.isSerial());
    }

    @Test
    public void parse_SerialFalse_ReturnsMatching() throws PasteException {
        pasteArguments.parse();
        assertFalse(pasteArguments.isSerial());
    }

    @Test
    public void parse_InvalidFlag_ThrowsInvalidFlagException() {
        Exception exception = assertThrows(Exception.class, ()->{
            pasteArguments.parse(FLAG_INVALID);
        });
        assertEquals(new PasteException(ERR_INVALID_FLAG).getMessage(), exception.getMessage());
    }

    @Test
    public void parse_InputFileOutputFile_ReturnsInputFileOutputFile() throws PasteException {
        pasteArguments.parse(INPUT_FILE, OUTPUT_FILE);
        assertEquals(INPUT_FILE, pasteArguments.getFiles().get(0));
        assertEquals(OUTPUT_FILE, pasteArguments.getFiles().get(1));
    }

}