package sg.edu.nus.comp.cs4218.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.time.Duration;

import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class ShellImplTest {

    private static Shell testShell;

    @BeforeAll
    static void setupShell() {
        testShell = new ShellImpl();
    }

    // Reference for how to time out a test in JUnit5:
    // https://stackoverflow.com/questions/57116801/how-to-fail-a-test-after-a-timeout-is-exceeded-in-junit-5
    @Test
    public void testSingleInvalidCommand() throws AbstractApplicationException, ShellException, FileNotFoundException {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            ByteArrayOutputStream testStream = new ByteArrayOutputStream();
            // Expect Exception
            testShell.parseAndEvaluate("invalidCommand", testStream);
        });
    }
}