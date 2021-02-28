package sg.edu.nus.comp.cs4218.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.time.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class ShellImplTest {

    private static Shell testShell;

    @BeforeAll
    static void setupShell() {
        testShell = new ShellImpl();
    }
}