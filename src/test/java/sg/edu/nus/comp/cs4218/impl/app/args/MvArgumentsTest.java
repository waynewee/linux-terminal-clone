package sg.edu.nus.comp.cs4218.impl.app.args;

import main.java.sg.edu.nus.comp.cs4218.impl.app.args.MvArguments;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MvArgumentsTest {

    @Test
    void getSourcePaths_SingleSource_ReturnsStringArrayWithOneElement() {
        MvArguments test = new MvArguments(new String[]{"root"}, null, true);
        String[] expected = {"root"};
        assertTrue(Arrays.equals(expected, test.getSourcePaths()));
    }

    @Test
    void getSourcePaths_TwoSources_ReturnsStringArrayWithTwoElement() {
        MvArguments test = new MvArguments(new String[]{"root", "subPath"}, null, true);
        String[] expected = {"root", "subPath"};
        assertTrue(Arrays.equals(expected, test.getSourcePaths()));
    }

    @Test
    void getDestPath_ProperPath_ReturnsStringDestPath() {
        MvArguments test = new MvArguments(null, "root", true);
        String expected = "root";
        assertEquals(expected, test.getDestPath());
    }

    @Test
    void equals_Null_NotEqual() {
        MvArguments test = new MvArguments(new String[]{"Hello"}, "World", true);
        assertNotEquals(null, test);
    }

    @Test
    void equals_SameParameter_Equal() {
        MvArguments test = new MvArguments(new String[]{"Hello"}, "World", true);
        MvArguments other = new MvArguments(new String[]{"Hello"}, "World", true);
        assertEquals(other, test);
    }

    @Test
    void equals_DifferentParameter_NotEqual() {
        MvArguments test = new MvArguments(new String[]{"Hello"}, "World", true);
        MvArguments other = new MvArguments(new String[]{"Hello", "World"}, "World", true);
        assertNotEquals(other, test);
    }
}