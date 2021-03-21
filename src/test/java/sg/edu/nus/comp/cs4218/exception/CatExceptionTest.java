package sg.edu.nus.comp.cs4218.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_GENERAL;

public class CatExceptionTest {
    @Test
    void getMessage_GenericMessage_GetsBackMessage() {
        CatException exception = new CatException(ERR_GENERAL, new Exception());
        String expectedMessage = ERR_GENERAL;
        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }
}
