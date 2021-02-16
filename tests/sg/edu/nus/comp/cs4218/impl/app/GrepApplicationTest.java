package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GrepApplicationTest {

    private static GrepApplication grepApplication;

    @BeforeAll
    static void setupShell() {
        grepApplication = new GrepApplication();
    }

    @Test
    public void grepFromStdin_NormalGreppingProcedure_ReturnsGreppedString() throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("test test".getBytes(StandardCharsets.UTF_8));
        String result = grepApplication.grepFromStdin("fuck", true, true, false,  inputStream);
        System.out.println(result);
    }

}