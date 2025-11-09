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
 * @version Oct 20, 2025
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
     * Test Sort A for 1 block
     * 
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testSortA1() throws Exception {
        FileGenerator it = new FileGenerator();
        it.generateFile("input.txt", 1, "a");
        String[] arr = {"input.txt", "testStats.txt"};
        idk.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
        assertTrue(fileChecker.checkFileStrong("input.txt"));
    }
    
    /**
     * Test Sort A for 10 blocks
     * 
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testSortA10() throws Exception {
        FileGenerator it = new FileGenerator();
        it.generateFile("input.txt", 10, "a");
        String[] arr = {"input.txt", "testStats.txt"};
        idk.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
        assertTrue(fileChecker.checkFileStrong("input.txt"));
    }
    
    /**
     * Test Sort A for 101 blocks
     * 
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testSortA101() throws Exception {
        FileGenerator it = new FileGenerator();
        it.generateFile("input.txt", 101, "a");
        String[] arr = {"input.txt", "testStats.txt"};
        idk.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
        assertTrue(fileChecker.checkFileStrong("input.txt"));
    }

    /**
     * Test Sort B for 1 block
     * 
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testSortB1() throws Exception {
        FileGenerator it = new FileGenerator();
        it.generateFile("input.txt", 1, "b");
        String[] arr = {"input.txt", "testStats.txt"};
        idk.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
        assertTrue(fileChecker.checkFileStrong("input.txt"));
    }
    
    /**
     * Test Sort B for 10 blocks
     * 
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testSortB10() throws Exception {
        FileGenerator it = new FileGenerator();
        it.generateFile("input.txt", 10, "b");
        String[] arr = {"input.txt", "testStats.txt"};
        idk.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
        assertTrue(fileChecker.checkFileStrong("input.txt"));
    }
    
    /**
     * Test Sort B for 101 blocks
     * 
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testSortB101() throws Exception {
        FileGenerator it = new FileGenerator();
        it.generateFile("input.txt", 101, "b");
        String[] arr = {"input.txt", "testStats.txt"};
        idk.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
        assertTrue(fileChecker.checkFileStrong("input.txt"));
    }
    
    /**
     * Test Sort C for 1 block
     * 
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testSortC1() throws Exception {
        FileGenerator it = new FileGenerator();
        it.generateFile("input.txt", 1, "c");
        String[] arr = {"input.txt", "testStats.txt"};
        idk.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
        assertTrue(fileChecker.checkFileStrong("input.txt"));
    }
    
    /**
     * Test Sort C for 10 blocks
     * 
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testSortC10() throws Exception {
        FileGenerator it = new FileGenerator();
        it.generateFile("input.txt", 10, "c");
        String[] arr = {"input.txt", "testStats.txt"};
        idk.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
        assertTrue(fileChecker.checkFileStrong("input.txt"));
    }
    
    /**
     * Test Sort C for 101 blocks
     * 
     * @throws Exception
     *             either a IOException or FileNotFoundException
     */
    public void testSortC101() throws Exception {
        FileGenerator it = new FileGenerator();
        it.generateFile("input.txt", 101, "c");
        String[] arr = {"input.txt", "testStats.txt"};
        idk.main(arr);
        assertTrue(fileChecker.checkFile("input.txt"));
        assertTrue(fileChecker.checkFileStrong("input.txt"));
    }
}
