package sg.edu.nus.comp.cs4218.integration;

import org.junit.jupiter.api.BeforeAll;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

public class CdLsIntegrationTest {

    private static Shell testShell;

    @BeforeAll
    static void setupShell() {
        testShell = new ShellImpl();
    }

}
