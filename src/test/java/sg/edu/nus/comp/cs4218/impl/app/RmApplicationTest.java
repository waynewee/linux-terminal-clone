package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.io.TempDir;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.RmException;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class RmApplicationTest {

    private final static String FLAG_RECURSIVE = "-r";
    private final static String FLAG_DIRECTORY = "-d";

    @Test
    void run_SingleFile_DeletesFile(@TempDir Path root) throws Exception {
        Path fileA = root.resolve("a.txt");
        Path fileB = root.resolve("bobby");
        Files.createFile(fileA);
        Files.createFile(fileB);

        String[] args = {fileA.toString()};
        new RmApplication().run(args, System.in, System.out);

        assertTrue(Files.notExists(fileA));
        assertTrue(Files.exists(fileB));
    }

    @Test
    void run_SpaceInName_DeletesFile(@TempDir Path root) throws Exception {
        Path fileC = root.resolve("c   c");
        Files.createFile(fileC);

        String[] args = {fileC.toString()};
        new RmApplication().run(args, System.in, System.out);

        assertTrue(Files.notExists(fileC));
    }

    @Test
    void run_MultipleFiles_DeletesFiles(@TempDir Path root) throws Exception {
        Path fileD = root.resolve("d.txt");
        Path fileE = root.resolve("eerie");
        Files.createFile(fileD);
        Files.createFile(fileE);

        String[] args = {fileD.toString(), fileE.toString()};
        new RmApplication().run(args, System.in, System.out);

        assertTrue(Files.notExists(fileD));
        assertTrue(Files.notExists(fileE));
    }

    @Test
    void run_EmptyDirectory_DeletesDirectory(@TempDir Path root) throws Exception {
        Path folder = root.resolve("folder");
        Files.createDirectory(folder);

        String[] args = {FLAG_DIRECTORY, folder.toString()};
        new RmApplication().run(args, System.in, System.out);

        assertTrue(Files.notExists(folder));
    }

    @Test
    void run_MultipleFilesEmptyDirectories_DeletesAll(@TempDir Path root) throws Exception {
        Path fileG = root.resolve("g.txt");
        Path fileH = root.resolve("high");
        Path directoryA = root.resolve("directoryA");
        Path directoryB = root.resolve("directoryB");
        Files.createFile(fileG);
        Files.createFile(fileH);
        Files.createDirectory(directoryA);
        Files.createDirectory(directoryB);

        String[] args = {FLAG_DIRECTORY, fileG.toString(), fileH.toString(), directoryA.toString(), directoryB.toString()};
        new RmApplication().run(args, System.in, System.out);

        assertTrue(Files.notExists(fileG));
        assertTrue(Files.notExists(fileH));
        assertTrue(Files.notExists(directoryA));
        assertTrue(Files.notExists(directoryB));
    }

    @Test
    void run_DirectoryWithFiles_DeletesDirectory(@TempDir Path root) throws Exception {
        Path directory = root.resolve("directory");
        Path directoryFileA = directory.resolve("dwf.txt");
        Path directoryFileB = directory.resolve("dwf2.txt");
        Files.createDirectory(directory);
        Files.createFile(directoryFileA);
        Files.createFile(directoryFileB);

        String[] args = {FLAG_RECURSIVE, directory.toString()};
        new RmApplication().run(args, System.in, System.out);

        assertTrue(Files.notExists(directory));
    }

    @Test
    void run_DirectoryInDirectory_DeletesDirectory(@TempDir Path root) throws Exception {
        Path directory = root.resolve("directoryC");
        Path subFile = directory.resolve("did.txt");
        Path secondLevelDirectory = directory.resolve("directoryDid");
        Path thirdLevelDirectory = secondLevelDirectory.resolve("directoryDid");
        Path thirdLevelFileA = thirdLevelDirectory.resolve("did.txt");
        Path thirdLevelFileB = thirdLevelDirectory.resolve("did2.txt");
        Files.createDirectory(directory);
        Files.createFile(subFile);
        Files.createDirectory(secondLevelDirectory);
        Files.createDirectory(thirdLevelDirectory);
        Files.createFile(thirdLevelFileA);
        Files.createFile(thirdLevelFileB);

        String[] args = {FLAG_RECURSIVE, directory.toString()};
        new RmApplication().run(args, System.in, System.out);

        assertTrue(Files.notExists(directory));
    }

    @Test
    void run_MultipleFilesDirectories_DeletesAll(@TempDir Path root) throws Exception {
        Path directoryD = root.resolve("directoryD");
        Path directoryFile = directoryD.resolve("mfd.txt");
        Path secondLevelDirectory = directoryD.resolve("directoryMfd");
        Path thirdLevelDirectory = secondLevelDirectory.resolve("directoryMfd");
        Path thirdLevelFileA = thirdLevelDirectory.resolve("mfd.txt");
        Path thirdLevelFileB = thirdLevelDirectory.resolve("mfd2.txt");
        Path directoryB = root.resolve("empty");
        Path fileI = root.resolve("ii");
        Path fileJ = root.resolve("jar");
        Files.createDirectory(directoryD);
        Files.createFile(directoryFile);
        Files.createDirectory(secondLevelDirectory);
        Files.createDirectory(thirdLevelDirectory);
        Files.createFile(thirdLevelFileA);
        Files.createFile(thirdLevelFileB);
        Files.createDirectory(directoryB);
        Files.createFile(fileI);
        Files.createFile(fileJ);

        String[] args = {FLAG_RECURSIVE, directoryD.toString(), directoryB.toString(), fileI.toString(), fileJ.toString()};
        new RmApplication().run(args, System.in, System.out);

        assertTrue(Files.notExists(directoryD));
        assertTrue(Files.notExists(directoryB));
        assertTrue(Files.notExists(fileI));
        assertTrue(Files.notExists(fileJ));
    }

    @Test
    void run_RelativePath_DeletesDirectory() throws Exception {
        Path tempDirectory = Files.createDirectory(Path.of(System.getProperty("user.dir")).resolve("temp"));
        Path subDirectory = Files.createDirectory( tempDirectory.resolve("inner"));

        String[] args = {FLAG_RECURSIVE, tempDirectory.getFileName().toString()};
        try {
            new RmApplication().run(args, System.in, System.out);
            assertTrue(Files.notExists(tempDirectory));
        } catch (Exception e) {
            fail();
        } finally {
            // Clean up
            Files.delete(subDirectory);
            Files.delete(tempDirectory);
        }
    }

    @Test
    void run_MultipleFilesWithNonEmptyDirectoryWithDFlag_ThrowsErrorFilesDeleted(@TempDir Path root) throws Exception {
        Path fileA = root.resolve("a.txt");
        Path fileB = root.resolve("b.txt");
        Path fileC = root.resolve("c.txt");
        Path directory = root.resolve("directory");
        Path fileInDirectory = directory.resolve("file.txt");
        Files.createDirectory(directory);
        Files.createFile(fileA);
        Files.createFile(fileB);
        Files.createFile(fileC);
        Files.createFile(fileInDirectory);

        String[] args = {"-d", directory.toString(), fileA.toString(), fileB.toString(), fileC.toString()};

        assertThrows(RmException.class, () -> new RmApplication().run(args, System.in, System.out));
        assertTrue(Files.notExists(fileA));
        assertTrue(Files.notExists(fileB));
        assertTrue(Files.notExists(fileC));
    }

    @Test
    void run_ZeroArguments_Throws() {
        String[] args = {""};

        assertThrows(RmException.class, () -> new RmApplication().run(args, System.in, System.out));
    }

    @Test
    void run_FlagOnly_Throws() {
        String[] args = {"-d"};

        assertThrows(RmException.class, () -> new RmApplication().run(args, System.in, System.out));
    }

    @Test
    void run_UnknownFlag_Throws(@TempDir Path root) throws Exception {
        Environment.currentDirectory = root.toString();
        Path fileK = root.resolve("kick");
        Files.createFile(fileK);

        String[] args = {"-x", fileK.toString()};

        assertThrows(RmException.class, () -> new RmApplication().run(args, System.in, System.out));
        assertTrue(Files.exists(fileK));
    }

    @Test
    void run_NonexistentFile_Throws() {
        String[] args = {"not exist"};

        assertThrows(RmException.class, () -> new RmApplication().run(args, System.in, System.out));
    }

    @Test
    void run_DirectoryWithoutFlag_Throws(@TempDir Path root) throws Exception {
        Path directoryF = root.resolve("directoryF");
        Files.createDirectory(directoryF);

        String[] args = {directoryF.toString()};

        assertThrows(RmException.class, () -> new RmApplication().run(args, System.in, System.out));
    }

    @Test
    void run_NonemptyDirectoryWithDFlag_Throws(@TempDir Path root) throws Exception {
        Path directoryG = root.resolve("directoryG");
        Path file = directoryG.resolve("a.txt");
        Files.createDirectory(directoryG);
        Files.createFile(file);

        String[] args = {"-d", directoryG.toString()};

        assertThrows(RmException.class, () -> new RmApplication().run(args, System.in, System.out));
    }

    @Test
    void run_FileWithDFlag_Throws(@TempDir Path root) throws Exception {
        Path file = root.resolve("test.txt");
        Files.createFile(file);

        String[] args = {"-d", file.toString()};

        assertThrows(RmException.class, () -> new RmApplication().run(args, System.in, System.out));
    }

    @Test
    void run_FileWithRFlag_Throws(@TempDir Path root) throws Exception {
        Path file = root.resolve("test.txt");
        Files.createFile(file);

        String[] args = {"-r", file.toString()};

        assertThrows(RmException.class, () -> new RmApplication().run(args, System.in, System.out));
    }
}
