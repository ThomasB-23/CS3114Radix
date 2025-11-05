import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import student.TestCase;

/**
 * This class was designed to test the Radix class by generating a random
 * ascii and binary file, sorting both and then checking each one with a file
 * checker.
 *
 * @author Thomas Bongiorno (thomasb23) and Charles Patton (charpatt5414)
 * @version Milestone 1
 */
public class RadixProjTest extends TestCase {
    private CheckFile fileChecker;
    private RadixProj idk;

    /**
     * This method sets up the tests that follow.
     */
    public void setUp() {
        idk = new RadixProj();
        fileChecker = new CheckFile();
    }


    /**
     * Fail a sort
     *
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testFailSort() throws Exception {
        FileGenerator it = new FileGenerator();
        it.generateFile("input.txt", 1, "b");
        assertFalse(fileChecker.checkFile("input.txt"));
        System.out.println("Done testFailSort");
    }


    /**
     * Test Sort A
     * 
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testSortA() throws Exception {
        FileGenerator it = new FileGenerator();
        it.generateFile("input.txt", 1, "a");
        String[] arr = {"input.txt", "testStats.txt"};
        idk.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
        assertTrue(fileChecker.checkFileStrong("input.txt"));
    }


    /**
     * Test Sort B
     * 
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testSortB() throws Exception {
        FileGenerator it = new FileGenerator();
        it.generateFile("input.txt", 1, "b");
        String[] arr = {"input.txt", "testStats.txt"};
        idk.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
        assertTrue(fileChecker.checkFileStrong("input.txt"));
    }


    /**
     * Test Sort C
     * 
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testSortC() throws Exception {
        FileGenerator it = new FileGenerator();
        it.generateFile("input.txt", 1, "c");
        String[] arr = {"input.txt", "testStats.txt"};
        idk.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
        assertTrue(fileChecker.checkFileStrong("input.txt"));
    }


//    /**
//     * Test Sort D
//     * 
//     * @throws Exception
//     *             either a IOException or FileNotFoundException
//     */
//    public void testSortD() throws Exception {
//        FileGenerator it = new FileGenerator();
//        it.generateFile("input.txt", 1, "d");
//        String[] arr = {"input.txt", "testStats.txt"};
//        idk.main(arr);
//        assertTrue(fileChecker.checkFile("input.txt"));
//        assertTrue(fileChecker.checkFileStrong("input.txt"));
//    }
}
