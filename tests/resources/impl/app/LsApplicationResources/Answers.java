package resources.impl.app.LsApplicationResources;

public class Answers {
    public static String testRecursive =
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_recursive:\n" +
            "answer.txt\n" +
            "vig1\n" +
            "vig2\n" +
            "vig3\n" +
            "\n" +
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_recursive\\vig1:\n" +
            "vig1doc1.txt\n" +
            "vig1doc2.txt\n" +
            "vig1doc3.txt\n" +
            "vig1vig1\n" +
            "\n" +
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_recursive\\vig1\\vig1vig1:\n" +
            "vig1vig1doc1.txt\n" +
            "\n" +
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_recursive\\vig2:\n" +
            "vig2doc1.txt\n" +
            "vig2doc2.txt\n" +
            "\n" +
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_recursive\\vig3:\n" +
            "vig3doc1.txt\n";

    public static String testRecursiveDirectories =
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_recursive_directories:\n" +
            "vig1\n" +
            "vig2\n" +
            "vig3\n" +
            "\n" +
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_recursive_directories\\vig3:\n" +
            "vig3vig1\n";


    public static String testRecursiveSort =
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort:\n" +
            "vig1\n" +
            "vig2\n" +
            "vig3\n" +
            "answer.txt\n" +
            "\n" +
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort\\vig1:\n" +
            "vig1vig1\n" +
            "test3.doc\n" +
            "vig1doc1.txt\n" +
            "vig1doc3.txt\n" +
            "test.xls\n" +
            "\n" +
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort\\vig1\\vig1vig1:\n" +
            "vig1vig1doc1.txt\n" +
            "vig1vig2doc3.txt\n" +
            "vig2vig1doc2.txt\n" +
            "\n" +
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort\\vig2:\n" +
            "newppt.pptx\n" +
            "newxls.xls\n" +
            "\n" +
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort\\vig3:\n" +
            "vig3vig1\n" +
            "vig3doc1.txt.py.txt\n" +
            "\n" +
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_recursive_sort\\vig3\\vig3vig1:\n" +
            "vig1vig1doc1.txt\n" +
            "vig3vig5vig6.txt\n";

    public static String testFoldersOnlySort =
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_folders_sort:\n" +
            "fafafaf\n" +
            "folder1\n" +
            "folderfsjofjs\n" +
            "new folder\n";

    public static String testFolderOnlySortRecursive =
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_folders_sort_recursive:\n" +
            "folder1\n" +
            "folder2\n" +
            "fsd2\n" +
            "guys\n" +
            "\n" +
            "tests\\resources\\impl\\app\\LsApplicationResources\\test_folders_sort_recursive\\guys:\n" +
            "folder23\n";
}
