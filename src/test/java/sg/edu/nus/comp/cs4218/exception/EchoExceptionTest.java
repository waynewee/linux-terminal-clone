package sg.edu.nus.comp.cs4218.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_GENERAL;

public class EchoExceptionTest {
    @Test
    void getMessage_GenericMessage_GetsBackMessage() {
        EchoException exception = new EchoException(ERR_GENERAL, new Exception());
        String expectedMessage = "echo: " + ERR_GENERAL;
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }
}
