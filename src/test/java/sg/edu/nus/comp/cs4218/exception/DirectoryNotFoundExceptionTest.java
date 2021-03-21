package sg.edu.nus.comp.cs4218.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_GENERAL;

public class DirectoryNotFoundExceptionTest {
    @Test
    void getMessage_GenericMessage_GetsBackMessage() {
        DirectoryNotFoundException exception = new DirectoryNotFoundException(ERR_GENERAL);
        String expectedMessage = ERR_GENERAL;
        assertEquals(expectedMessage, exception.getMessage());
    }
}
