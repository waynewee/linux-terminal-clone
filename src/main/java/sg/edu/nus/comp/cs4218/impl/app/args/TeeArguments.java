package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.exception.TeeException;

import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_SYNTAX;

public class TeeArguments {
    private final List<String> files;
    private boolean isAppend;

    public TeeArguments() {
        this.files = new ArrayList<>();
        this.isAppend = false;
    };

    // TODO: make closure to make sure this only runs once
    public void parse(String... args) throws TeeException {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (i == 0 && arg.equals("-a")) {
                this.isAppend = true;
            } else {
                this.files.add(arg);
            }
        }
    }

    public String[] getFiles() {
        return files.toArray(new String[0]);
    }

    public boolean isAppend() {
        return isAppend;
    }
}
