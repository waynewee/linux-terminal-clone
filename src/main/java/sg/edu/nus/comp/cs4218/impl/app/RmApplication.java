package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.RmInterface;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.impl.parser.RmArgsParser;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class RmApplication implements RmInterface {
    /**
     * Runs application with specified input data and specified output stream.
     *
     * @param args Array of arguments for the application. Each array element is a file path.
     * @param stdin An InputStream. Not used.
     * @param stdout An OutputStream. Not used.
     * @throws RmException If the file paths doesn't exists or removing non-empty directories or any IO errors.
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws RmException {
        // Parse the arguments with RmParser
        RmArgsParser parser = new RmArgsParser();
        try {
            parser.parse(args);
            if (parser.getFilesPaths().length == 0) {
                throw new InvalidArgsException("File name missing");
            }
        } catch (InvalidArgsException iae) {
            throw new RmException(iae.getMessage());
        }
        // Give it to helper function
        remove(parser.isDirectory(), parser.isRecursive(), parser.getFilesPaths());
    }

    /**
     * Remove the file. (It does not remove folder by default)
     *
     * @param isEmptyFolder Boolean option to delete a folder only if it is empty
     * @param isRecursive   Boolean option to recursively delete the folder contents (traversing
     *                      through all folders inside the specified folder)
     * @param fileName      Array of String of file names
     * @throws RmException
     */
    @Override
    public void remove(Boolean isEmptyFolder, Boolean isRecursive, String... fileName) throws RmException {
        // TODO: Deletion does not stop when there is error. Just skip
        StringBuilder errorMsg = new StringBuilder();
        for (String pathString : fileName) {
            try {
                File file = new File(pathString);
                if (file.isFile()) {
                    if (!file.delete()) {
                        throw new Exception("File removal failed. File: " + file.getPath());
                    }
                } else if (file.isDirectory()) {
                    // Is directory
                    if (isEmptyFolder) {
                        File[] fileList = file.listFiles();
                        if (fileList == null || fileList.length > 0) {
                            throw new RmException("Attempting to remove non-empty directory.");
                        }
                        if (!file.delete()) {
                            throw new Exception("File removal failed. File: " + file.getPath());
                        }
                    } else if (isRecursive) {
                        removeRecursive(file);
                    } else {
                        throw new RmException("Unable to remove directory without flags");
                    }
                } else {
                    throw new RmException("Unknown file type: " + file.getPath());
                }
            } catch (Exception e) {
                // Collate all the exception messages
                errorMsg.append(e.getMessage());
                errorMsg.append("\n");
            }
        }

        if (errorMsg.length() > 0) {
            // Exception was thrown
            throw new RmException(errorMsg.toString());
        }
    }

    private void removeRecursive(File file) throws RmException {

        File[] subFiles = file.listFiles();
        if (subFiles != null) {
            for (File subFile : file.listFiles()) {
                removeRecursive(subFile);
            }
        }
        try {
            // At this point, either file is a file or an empty directory
            if (!file.delete()) {
                throw new Exception("File removal failed. File: " + file.getPath());
            }
        } catch (Exception e) {
            throw new RmException(e.getMessage());
        }
    }
}
