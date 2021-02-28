package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.app.args.MvArguments;

import static org.junit.jupiter.api.Assertions.*;

class MvArgsParserTest {

    private MvArgsParser parser;

    @BeforeEach
    void setup() {
        parser = new MvArgsParser();
    }
    @Test
    void parse_SingleSourceOverwrite_Success() throws Exception {
        String[] commandTokens = {"root/resource/test1", "root/resource/test2"};
        MvArguments expectedResult = new MvArguments(
                new String[] {"root/resource/test1"},
                "root/resource/test2",
                true);
        parser.parse(commandTokens);
        assertEquals(expectedResult, parser.getArguments());
    }

    @Test
    void parse_MultipleSourceOverwrite_Success() throws Exception {
        String[] commandTokens = {"root/resource/test1", "root/resource/test2", "root/resource/test3"};
        MvArguments expectedResult = new MvArguments(
                new String[] {"root/resource/test1", "root/resource/test2"},
                "root/resource/test3",
                true);
        parser.parse(commandTokens);
        assertEquals(expectedResult, parser.getArguments());
    }

    @Test
    void parse_MultipleSourceSingleDestNoOverwrite_Success() throws Exception {
        String[] commandTokens = {"-n", "root/resource/test1", "root/resource/test2", "root/resource/test3"};
        MvArguments expectedResult = new MvArguments(
                new String[] {"root/resource/test1", "root/resource/test2"},
                "root/resource/test3",
                false);

        parser.parse(commandTokens);
        assertEquals(expectedResult, parser.getArguments());

    }

    @Test
    void parse_InsufficientArgument_ThrowException() {
        String[] commandTokens = {"root/resource/test1"};
        MvArguments expectedResult = new MvArguments(null, "root/resource/test1", true);
        assertThrows(InvalidArgsException.class, () -> {
            parser.parse(commandTokens);
        });
    }

    @Test
    void parse_InsufficientArgumentWithFlag_ThrowException() {
        String[] commandTokens = {"-n", "root/resource/test1"};
        MvArguments expectedResult = new MvArguments(
                new String[] {"root/resource/test1", "root/resource/test2"},
                "root/resource/test3",
                false);
        assertThrows(InvalidArgsException.class, () -> {
            parser.parse(commandTokens);
        });
    }

    @Test
    void parse_InvalidFlag_ThrowsException() {
        String[] commandTokens = {"-w", "root/resource/test1", "root/resource/test2"};
        assertThrows(InvalidArgsException.class, () -> {
            parser.parse(commandTokens);
        });
    }

}