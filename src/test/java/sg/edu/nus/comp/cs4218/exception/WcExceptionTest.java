package sg.edu.nus.comp.cs4218.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_GENERAL;

public class WcExceptionTest {
    @Test
    void getMessage_GenericMessage_GetsBackMessage() {
        WcException exception = new WcException(new Exception(), ERR_GENERAL);
        String expectedMessage = "wc: " + ERR_GENERAL;
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }
}
