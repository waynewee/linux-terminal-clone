package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.WcException;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;


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

}