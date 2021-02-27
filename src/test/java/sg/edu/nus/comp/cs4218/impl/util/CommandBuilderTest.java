package sg.edu.nus.comp.cs4218.impl.util;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.io.FileNotFoundException;
import java.time.Duration;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CommandBuilderTest {

    private static Shell testShell;

    @BeforeAll
    static void setupShell() {
    }

    @Test
    public void testSingleInvalidCommand() throws AbstractApplicationException, ShellException, FileNotFoundException {
        assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
            Command command = CommandBuilder.parseCommand("Invalid Command", new ApplicationRunner());
            assertEquals("Invalid Syntax", command.toString());
        });
    }
}