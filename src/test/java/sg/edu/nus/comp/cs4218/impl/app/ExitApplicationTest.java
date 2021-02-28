package sg.edu.nus.comp.cs4218.impl.app;

import com.ginsberg.junit.exit.ExpectSystemExit;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.ExitException;

class ExitApplicationTest {

    @Test
    @ExpectSystemExit
    void run_NormalExecution_ExitsShell() throws ExitException {
        ExitApplication app = new ExitApplication();
        app.run(new String[0], System.in, System.out);
    }
}