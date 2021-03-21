package sg.edu.nus.comp.cs4218.impl;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.EnvironmentUtil;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShellImplTest {

    private static InputStream inputStream;
    private static OutputStream outStream;
    private static InputStream sysIn;
    private static PrintStream sysOut;

    @BeforeAll
    static void setUpEnvironment() throws Exception {
        sysIn = System.in;
        sysOut = System.out;
    }
    @BeforeEach
    public void setUp() {
        outStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outStream));
    }

    @AfterEach
    public void tearDown() {
        System.setIn(sysIn);
        System.setOut(sysOut);
    }

    private String getAllFilesInCurrentDirectory() throws Exception{
        StringBuilder output = new StringBuilder();
        File[] files = new File(EnvironmentUtil.currentDirectory).listFiles();
        if (files == null) {
            throw new Exception();
        }
        for(File file : files){
            if (file.isHidden()) {
                continue;
            }
            output.append(file.getName());
            output.append(StringUtils.STRING_NEWLINE);
        }
        return output.toString();
    }

    @Test
    void main_CdCommand_Runs() {
        String command = "cd src" + StringUtils.STRING_NEWLINE;
        inputStream = new ByteArrayInputStream(command.getBytes());
        System.setIn(inputStream);
        try {
            ShellImpl.main();
            assertEquals(System.getProperty("user.dir") + File.separator + "src", EnvironmentUtil.currentDirectory);
        } finally {
            EnvironmentUtil.currentDirectory = System.getProperty("user.dir");;
        }
    }

    @Test
    void main_EmptyCommand_Runs() {
        String command = "" + StringUtils.STRING_NEWLINE;
        inputStream = new ByteArrayInputStream(command.getBytes());
        System.setIn(inputStream);
        ShellImpl.main();
    }

    @Test
    void main_InvalidCommand_ExceptionHandled() {
        String command = "move here there" + StringUtils.STRING_NEWLINE;
        inputStream = new ByteArrayInputStream(command.getBytes());
        System.setIn(inputStream);
        ShellImpl.main();
    }


}