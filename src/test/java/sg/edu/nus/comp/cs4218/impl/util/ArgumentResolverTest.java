package sg.edu.nus.comp.cs4218.impl.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
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

    @Test
    void parseArguments_oneNormalArgAndOneBackquotedArg_isEvaluated() throws
            AbstractApplicationException, ShellException {
        List<String> fakeArgs = Arrays.asList("echo", "`echo hi`");
        List<String> resolvedArguments = argumentResolver.parseArguments(fakeArgs);
        assertEquals(2, resolvedArguments.size());
        assertEquals("hi", resolvedArguments.get(1));
    }

    @Test
    void parseArguments_oneBackquotedArg_isEvaluated() throws
            AbstractApplicationException, ShellException {
        List<String> args = Collections.singletonList("`echo hi`");
        List<String> resolvedArguments = argumentResolver.parseArguments(args);
        assertEquals(1, resolvedArguments.size());
        assertEquals("hi", resolvedArguments.get(0));
    }

    @Test
    void parseArguments_backquotedSubCommandWithQuotes_doesNotInterpretQuotesAsSpecialCharacters()
            throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "`echo \"'quote is not interpreted as special character'\"`");
        List<String> resolvedArguments = argumentResolver.parseArguments(args);
        assertEquals(8, resolvedArguments.size());
        assertEquals("echo", resolvedArguments.get(0));
        assertEquals("'quote is not interpreted as special character'",
                resolvedArguments.subList(1, resolvedArguments.size()).stream().reduce(
                        (String s1, String s2) -> s1 + ' ' + s2).get());
    }

    @Test
    void parseArguments_backquotedSubCommandInDoubleQuotes_isEvaluated() throws AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "\"walao`echo \"1 2 3\"`xyz\"");
        List<String> resolvedArguments = argumentResolver.parseArguments(args);
        assertEquals(2, resolvedArguments.size());
        assertEquals("walao1 2 3xyz", resolvedArguments.get(1));
    }

    /**
     * TODO: add more tests for edge cases, such as:
     * - multiple backquoted tokens
     * - double quote within single quote
     * - multiline input (?)
     */
}