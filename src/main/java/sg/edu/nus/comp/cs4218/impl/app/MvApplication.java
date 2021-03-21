package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.MvInterface;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.app.args.MvArguments;
import sg.edu.nus.comp.cs4218.impl.parser.MvArgsParser;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class MvApplication implements MvInterface {

    /**
     * Runs the mv application with the specified arguments.
     *
     * @param args   Array of arguments for the application. Each array element is a file path. The
     *               first few paths are the source path and the last is the destination path.
     * @param stdin  An InputStream. Not used.
     * @param stdout An OutputStream. Not used.
     * @throws MvException If the source files in file paths doesn't exists or file already exists at
     *                      destination path when the do not overwrite flag is used or any IO errors.
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws MvException {

        MvArgsParser parser = new MvArgsParser();

        // Parse all the arguments
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new MvException(e.getMessage(), e);
        }

        MvArguments arguments = parser.getArguments();

        // Check if target exists
        String targetPath = arguments.getDestPath();
        File target = new File(targetPath);

        if (target.exists() && target.isDirectory()) {
            // Second form
            // Expected one or more files at source
            try {
                mvFilesToFolder(arguments.hasOverwrite(), targetPath, arguments.getSourcePaths());
            } catch (MvException mve) {
                throw mve;
            } catch (FileAlreadyExistsException fae) {
                throw new MvException(ERR_TARGET_EXISTS, fae);
            } catch (Exception e) {
                throw new MvException(ERR_MV_GENERIC, e);
            }
        } else {
            // First form
            String sourcePath = arguments.getSourcePaths()[0];
            try {
                mvSrcFileToDestFile(arguments.hasOverwrite(), sourcePath, targetPath);
            } catch (MvException mve) {
                throw mve;
            } catch (FileAlreadyExistsException fae) {
                throw new MvException(ERR_TARGET_EXISTS, fae);
            } catch (Exception e) {
                throw new MvException(ERR_MV_GENERIC, e);
            }
        }
    }

    /**
     * move the file named by the source operand to the destination path named by the target operand
     *
     * @param srcFile  path to source file
     * @param destFile path to destination file
     * @throws Exception If source is not found or not a file or error moving source file to destination
     */
    @Override
    public String mvSrcFileToDestFile(Boolean isOverwrite, String srcFile, String destFile) throws Exception {
        File source = new File(srcFile);

        // Check that source exists and is a file
        if (!source.exists()) {
            throw new MvException(ERR_SOURCE_NOT_FOUND);
        } else if (!source.isFile()) {
            throw new MvException(ERR_SOURCE_NOT_FILE);
        }

        if (isOverwrite) {
            Files.move(Paths.get(srcFile), Paths.get(destFile), REPLACE_EXISTING);
        } else {
            Files.move(Paths.get(srcFile), Paths.get(destFile));
        }
        return "Successful";
    }

    /**
     * move files to destination folder
     *
     * @param destFolder of path to destination folder
     * @param fileName   Array of String of file names
     * @throws Exception If a source is not found or not a file or error moving source file to destination
     */
    @Override
    public String mvFilesToFolder(Boolean isOverwrite, String destFolder, String... fileName) throws Exception {

        for(String sourcePath : fileName) {
            File source = new File(sourcePath);

            // Check that source exists and is a file
            if (!source.exists()) {
                throw new MvException(ERR_SOURCE_NOT_FOUND);
            } else if (!source.isFile()) {
                throw new MvException(ERR_SOURCE_NOT_FILE);
            }

            if (isOverwrite) {
                Files.move(Paths.get(sourcePath), Paths.get(destFolder +
                        StringUtils.fileSeparator() + source.getName()), REPLACE_EXISTING);
            } else {
                Files.move(Paths.get(sourcePath), Paths.get(destFolder +
                        StringUtils.fileSeparator() + source.getName()));
            }
        }
        return "Successful";
    }
}
