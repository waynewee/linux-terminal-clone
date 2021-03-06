package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MvArgumentsTest {

    private static final String SRC_ROOT = "root";
    private static final String SUB_PATH = "subPath";
    private static final String ARG1 = "hello";
    private static final String ARG2 = "world";

    @Test
    void getSourcePaths_SingleSource_ReturnsStringArrayWithOneElement() {
        MvArguments test = new MvArguments(new String[]{SRC_ROOT}, null, true);
        String[] expected = {SRC_ROOT};
        assertTrue(Arrays.equals(expected, test.getSourcePaths()));
    }

    @Test
    void getSourcePaths_TwoSources_ReturnsStringArrayWithTwoElement() {
        MvArguments test = new MvArguments(new String[]{SRC_ROOT, SUB_PATH}, null, true);
        String[] expected = {SRC_ROOT, SUB_PATH};
        assertTrue(Arrays.equals(expected, test.getSourcePaths()));
    }

    @Test
    void getDestPath_ProperPath_ReturnsStringDestPath() {
        MvArguments test = new MvArguments(null, SRC_ROOT, true);
        String expected = SRC_ROOT;
        assertEquals(expected, test.getDestPath());
    }

    @Test
    void equals_Null_NotEqual() {
        MvArguments test = new MvArguments(new String[]{ARG1}, SRC_ROOT, true);
        assertNotEquals(null, test);
    }

    @Test
    void equals_SameParameter_Equal() {
        MvArguments test = new MvArguments(new String[]{ARG1}, SRC_ROOT, true);
        MvArguments other = new MvArguments(new String[]{ARG1}, SRC_ROOT, true);
        assertEquals(other, test);
    }

    @Test
    void equals_DifferentSourcesSameDestinationPath_NotEqual() {
        MvArguments test = new MvArguments(new String[]{ARG1}, SRC_ROOT, true);
        MvArguments other = new MvArguments(new String[]{ARG1, ARG2}, SRC_ROOT, true);
        assertNotEquals(other, test);
    }

    @Test
    void equals_DifferentDestinationPath_NotEqual() {
        MvArguments test = new MvArguments(new String[]{ARG1}, SRC_ROOT, true);
        MvArguments other = new MvArguments(new String[]{ARG1}, SUB_PATH, true);
        assertNotEquals(other, test);
    }

    @Test
    void equals_SamePathSameDestinationDifferentFlag_NotEqual() {
        MvArguments test = new MvArguments(new String[]{ARG1}, SRC_ROOT, true);
        MvArguments other = new MvArguments(new String[]{ARG1}, SRC_ROOT, false);
        assertNotEquals(other, test);
    }

    @Test
    void equal_DifferentObjects_NotEqual() {
        MvArguments test = new MvArguments(new String[]{ARG1}, SRC_ROOT, true);
        Integer other = 99999999;
        assertNotEquals(test, other);
    }
}