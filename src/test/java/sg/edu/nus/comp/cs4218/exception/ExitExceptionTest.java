package sg.edu.nus.comp.cs4218.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_GENERAL;

public class ExitExceptionTest {
    @Test
    void getMessage_GenericMessage_GetsBackMessage() {
        ExitException exception = new ExitException(ERR_GENERAL);
        String expectedMessage = "exit: " + ERR_GENERAL;
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }
}
