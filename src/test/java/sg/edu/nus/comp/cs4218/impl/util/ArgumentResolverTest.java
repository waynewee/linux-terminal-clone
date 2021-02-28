package sg.edu.nus.comp.cs4218.impl.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

class ArgumentResolverTest {

    private static ArgumentResolver argumentResolver;

    @BeforeAll
    static void setupArgumentResolver() {
        argumentResolver = new ArgumentResolver();
    }

    @Test
    void resolveOneArgument_singleQuotedArg_isOneArgWithoutQuotes() throws AbstractApplicationException,
            ShellException {
        List<String> resolvedArguments = argumentResolver.resolveOneArgument("'hello world'");
        assertEquals("hello world", resolvedArguments.get(0));
    }

    @Test
    void resolveOneArgument_singleQuotedArgWithBackquotes_backquotesNotExecuted() throws AbstractApplicationException,
            ShellException {
        List<String> resolvedArguments = argumentResolver.resolveOneArgument("'this is space:`echo \" \"`.'");
        assertEquals("this is space:`echo \" \"`.", resolvedArguments.get(0));
    }

    @Test
    void resolveOneArgument_doubleQuotedArg_isOneArgWithoutQuotes() throws AbstractApplicationException,
            ShellException {
        List<String> resolvedArguments = argumentResolver.resolveOneArgument("\"hello world\"");
        assertEquals("hello world", resolvedArguments.get(0));
    }

    @Test
    void resolveOneArgument_doubleQuotedArgWithBackquotes_backquotesExecuted() throws AbstractApplicationException,
            ShellException {
        List<String> resolvedArguments = argumentResolver.resolveOneArgument("\"this is space:`echo \" \"`.\"");
        assertEquals("this is space: .", resolvedArguments.get(0));
    }

    @Test
    void resolveOneArgument_singleQuotesNestedInDoubleQuotes_singleQuotesNotStripped() throws
            AbstractApplicationException, ShellException {
        List<String> resolvedArguments = argumentResolver.resolveOneArgument("\"this is space:' '.\"");
        assertEquals("this is space:' '.", resolvedArguments.get(0));
    }

    @Test
    void resolveOneArgument_backquotesAndSingleQuotesInDoubleQuotes_backquotesNotDisabled() throws
            AbstractApplicationException, ShellException {
        List<String> resolvedArguments = argumentResolver.resolveOneArgument("\"'this is space:`echo \" \"`.'\"");
        assertEquals("'this is space: .'", resolvedArguments.get(0));
    }

    @Test
    void resolveOneArgument_backquotesAndDoubleQuotesInSingleQuotes_backquotesDisabled() throws
            AbstractApplicationException, ShellException {
        List<String> resolvedArguments = argumentResolver.resolveOneArgument("'\"this is space:`echo \" \"`.\"'");
        assertEquals("\"this is space:`echo \" \"`.\"", resolvedArguments.get(0));
    }

    /**
     * TODO: add more tests for edge cases, such as:
     * - multiple backquoted tokens
     * - double quote within single quote
     * - multiline input (?)
     */
}