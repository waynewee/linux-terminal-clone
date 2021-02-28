package sg.edu.nus.comp.cs4218.unimpl.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NOT_SUPPORTED;

class ArgumentResolverTestUnimplemented {

    private static ArgumentResolver argumentResolver;

    @BeforeAll
    static void setupArgumentResolver() {
        argumentResolver = new ArgumentResolver();
    }

    @Test
    void resolveOneArgument_backquotedSubCommand_isEvaluated() throws
            AbstractApplicationException, ShellException {
        List<String> resolvedArguments = argumentResolver.resolveOneArgument("`echo hi`");
        assertEquals(1, resolvedArguments.size());
        assertEquals("hi", resolvedArguments.get(0));
    }

    @Test
    void resolveOneArgument_backquotedSubCommandWithQuotes_doesNotInterpretQuotesAsSpecialCharacters() throws
            AbstractApplicationException, ShellException {
        List<String> resolvedArguments = argumentResolver.resolveOneArgument("`echo \"`this is not special`\"`");
        assertEquals(1, resolvedArguments.size());
        assertEquals("`this is not special`", resolvedArguments.get(0));
    }

}