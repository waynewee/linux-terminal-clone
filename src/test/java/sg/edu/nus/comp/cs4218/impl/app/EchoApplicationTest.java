package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.EchoException;


class EchoApplicationTest {

    private static final String[] ARGS = {"Hello"};
    private static final String[] ARGS_MULTI = {"Hello", "World"};
    private static final String[] ARGS_QUOTE = {"\"A*B*C\""};
    private static final String[] ARGS_QUOTE_MULTI = {"ABC", "\"AB_C\"", "\"DEF\""};
    private static final String[] ARGS_EMPTY = {};

    private static final String TEXT_QUOTE = "'\"A*B*C\"'";
    private static final String TEXT_QUOTE_MULTI = "ABC '\"AB_C\"' '\"DEF\"'";

    private static EchoApplication echoApplication;

    @BeforeAll
    static void setupShell() {
        echoApplication = new EchoApplication();
    }

    @Test
    public void constructResult_ArgumentsNone_ThrowsNullArgsException() {
        Exception exception = assertThrows(Exception.class, ()->{
            echoApplication.constructResult(null);
        });
        assertEquals(new EchoException(ERR_NULL_ARGS).getMessage(), exception.getMessage());
    }

    @Test
    public void constructResult_ArgumentsEmpty_ReturnsNewLine() throws EchoException {
        String result = echoApplication.constructResult(ARGS_EMPTY);
        assertEquals(STRING_NEWLINE, result);
    }

    @Test
    public void constructResult_ArgumentsSingle_ReturnsArguments() throws EchoException {
        String result = echoApplication.constructResult(ARGS);
        assertEquals(ARGS[0], result);
    }

    @Test
    public void constructResult_ArgumentsMulti_ReturnsArguments() throws EchoException {
        String result = echoApplication.constructResult(ARGS_MULTI);
        assertEquals(ARGS_MULTI[0] + " " + ARGS_MULTI[1], result);
    }

    @Test
    public void constructResult_ArgumentsQuoteSingle_ReturnsArguments() throws EchoException {
        String result = echoApplication.constructResult(ARGS_QUOTE);
        assertEquals(TEXT_QUOTE, result);
    }

    @Test
    public void constructResult_ArgumentsQuoteMulti_ReturnsArguments() throws EchoException {
        String result = echoApplication.constructResult(ARGS_QUOTE_MULTI);
        assertEquals(TEXT_QUOTE_MULTI, result);
    }
}