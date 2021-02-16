package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.EchoException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class CatApplicationTest {

    private static CatApplication catApplication;

    String path = "tests/resources/impl/app/CatApplicationResources";
    String file_HelloWorld = path + "/CatApplicationTest_HelloWorld.txt";
    String file_SingleLine = path + "/CatApplicationTest_SingleLine.txt";
    String file_MultiLine = path + "/CatApplicationTest_MultiLine.txt";
    String file_MultiLine2 = path + "/CatApplicationTest_MultiLine2.txt";

    @BeforeAll
    static void setupShell() {
        catApplication = new CatApplication();
    }

    @Test
    public void getCatResult_SingleLineInput_ReturnsString() throws Exception {
        InputStream inputStream = new ByteArrayInputStream("Hello World".getBytes(StandardCharsets.UTF_8));
        String result = catApplication.getCatResult(false, inputStream);
        assertEquals("Hello World", result);
    }

    @Test
    public void catFiles_TwoSingleLineFiles_ReturnsString() throws Exception {
        String result = catApplication.catFiles(false, file_HelloWorld, file_SingleLine);
        assertEquals("Hello World!This is a test file.", result);
    }

    @Test
    public void catFiles_TwoMultiLineFiles_ReturnsString() throws Exception {
        String result = catApplication.catFiles(true, file_MultiLine, file_MultiLine2);
        System.out.println(result);
        assertEquals(
                "Hello,\r\n" +
                "My name is Wayne.\r\n" +
                "I love riding bikes on the beach and eating seesaws.Hello,\r\n" +
                "I am Darren. I am gay.\r\n\r\n" +
                "It is pleased to meet you.",
                result
        );
    }

}