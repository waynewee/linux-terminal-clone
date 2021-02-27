package sg.edu.nus.comp.cs4218.exception;

import main.java.sg.edu.nus.comp.cs4218.exception.MvException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_MV_GENERIC;

class MvExceptionTest {

    @Test
    void getMessage_RegularMessage_GetsBackMessage() {
        MvException e = new MvException(ERR_MV_GENERIC);
        String expectedMessage = "mv: " + ERR_MV_GENERIC;
        assertEquals(expectedMessage, e.getMessage());
    }

}