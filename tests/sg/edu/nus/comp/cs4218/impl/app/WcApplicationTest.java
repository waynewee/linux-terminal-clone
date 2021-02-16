package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.WcException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;


class WcApplicationTest {

    private static WcApplication wcApplication;

    @BeforeAll
    static void setupShell() {
        wcApplication = new WcApplication();
    }

    @Test
    public void getCountReport_SingleLineInputStream_ReturnsData() throws Exception {
        InputStream inputStream = new ByteArrayInputStream("Hello World".getBytes(StandardCharsets.UTF_8));
        long[] data = wcApplication.getCountReport(inputStream);
        long[] expected = {0, 2, 10};
        assertArrayEquals(expected, data);
    }

    @Test
    public void getCountReport_NullInputStream_ThrowsException(){

        Exception exception = assertThrows(Exception.class, () -> {
            wcApplication.getCountReport(null);
        });

        assertEquals(exception.getMessage(), new Exception(ERR_NULL_STREAMS).getMessage());

    }

}