package sg.edu.nus.comp.cs4218.impl.cmd;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

class SequenceCommandTest {

    OutputStream mockOutputStream;
    InputStream mockInputStream;


    @BeforeEach
    void setUp() {
        mockOutputStream = new ByteArrayOutputStream();
        mockInputStream = Mockito.mock(InputStream.class);
    }

    @Test
    void evaluate_nullSequence_noErrors() {
        SequenceCommand commandUnderTest = new SequenceCommand(null);
        assertDoesNotThrow(() -> commandUnderTest.evaluate(mockInputStream, mockOutputStream));
    }
    @Test
    void evaluate_emptySequence_noErrors() {
        List<Command> emptyCommandSequence = new ArrayList<>();
        SequenceCommand commandUnderTest = new SequenceCommand(emptyCommandSequence);
        assertDoesNotThrow(() -> commandUnderTest.evaluate(mockInputStream, mockOutputStream));
    }

    @Test
    void evaluate_singleCallCommandSequence_noErrors() throws AbstractApplicationException, ShellException {
        ArrayList<String> argsList = new ArrayList<>(Collections.singletonList("HI"));
        ApplicationRunner mockAppRunner = Mockito.mock(ApplicationRunner.class);
        doThrow(NullPointerException.class).when(mockAppRunner).runApp(anyString(), any(), any(), any());
        ArgumentResolver mockArgumentResolver = Mockito.mock(ArgumentResolver.class);
        CallCommand singleCallCommand = new CallCommand(argsList, mockAppRunner, mockArgumentResolver);
        List<Command> commands = new ArrayList<>(Collections.singletonList(singleCallCommand));
        SequenceCommand commandUnderTest = new SequenceCommand(commands);
        commandUnderTest.evaluate(mockInputStream, mockOutputStream);
        assertDoesNotThrow(() -> commandUnderTest.evaluate(mockInputStream, mockOutputStream));
    }
}