package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.EchoException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


class EchoApplicationTest {

    String[] argsHelloWorld = {"Hello", "World"};

    String stringHelloWorld = "Hello World";
    String stringCarriageReturnLineFeed = "\r\n";

    private static EchoApplication echoApplication;

    @BeforeAll
    static void setupShell() {
        echoApplication = new EchoApplication();
    }

    @Test
    public void constructResult_NonEmptyString_ReturnsSameString() throws EchoException {
        String[] args = argsHelloWorld;
        String result = echoApplication.constructResult(args);
        assertEquals(result, stringHelloWorld);
    }

    @Test
    public void constructResult_EmptyString_ReturnsCRLF() throws EchoException {
        String[] args = {};
        String result = echoApplication.constructResult(args);
        assertEquals(result, stringCarriageReturnLineFeed);
    }

    @Test
    public void constructResult_NullArgs_ThrowsException() throws EchoException {
        EchoException echoException = assertThrows(EchoException.class, () -> {
            echoApplication.constructResult(null);
        });

        assertEquals(echoException.getMessage(), new EchoException((ERR_NULL_ARGS)).getMessage());
    }

    @Test
    public void run_WorkingInputWorkingOutput_WritesToOutput() throws EchoException {
        InputStream inputStream = null;
        OutputStream outputStream = new ByteArrayOutputStream(1024);
        echoApplication.run(argsHelloWorld, inputStream, outputStream);
        assertEquals(stringHelloWorld, outputStream.toString());
    }

    @Test
    public void run_NullOutputStream_ThrowsException() throws EchoException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        EchoException echoException = assertThrows(EchoException.class, () -> {
            echoApplication.run(argsHelloWorld, inputStream, outputStream);
        });

        assertEquals(echoException.getMessage(), new EchoException(ERR_NO_OSTREAM).getMessage() );
    }
}