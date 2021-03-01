package sg.edu.nus.comp.cs4218.impl.app.args;

import java.util.Arrays;
import java.util.Objects;

public class MvArguments {
    private final String[] sourcePaths;
    private final String destPath;
    private final boolean overwrite;

    public MvArguments(String[] sourcePaths, String destPath, boolean overwrite) {
        this.sourcePaths = sourcePaths;
        this.destPath = destPath;
        this.overwrite = overwrite;
    }

    public String[] getSourcePaths() {
        return sourcePaths;
    }

    public String getDestPath() {
        return destPath;
    }

    public boolean hasOverwrite() {
        return overwrite;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MvArguments)) {
            return super.equals(obj);
        }
        MvArguments other = (MvArguments) obj;
        return destPath.equals(other.destPath)
                && Arrays.equals(sourcePaths, other.sourcePaths)
                && overwrite == other.overwrite;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourcePaths, destPath, overwrite);
    }
}
