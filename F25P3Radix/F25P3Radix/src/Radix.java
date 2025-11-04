import java.nio.*;
import java.io.*;

// The Radix Sort implementation
// -------------------------------------------------------------------------
/**
 *
 * @author Thomas Bongiorno (thomasb23) and Charles Patton (charpatt5414)
 * @version Milestone 1
 */
public class Radix {

    private RandomAccessFile file;
    private PrintWriter stats;
    private int diskReads;
    private int diskWrites;

    private static final int RECORD_SIZE = 8;
    private static final int RADIX = 256;
    private static final int NUM_PASSES = 8;

    /**
     * Create a new Radix object.
     * 
     * @param theFile
     *            The RandomAccessFile to be sorted
     * @param s
     *            The stats PrintWriter
     *
     * @throws IOException
     */
    public Radix(RandomAccessFile theFile, PrintWriter s) throws IOException {
        file = theFile;
        stats = s;

        long totalRecords = file.length() / RECORD_SIZE;
        RandomAccessFile temp = new RandomAccessFile("temp.bin", "rw");

        

        diskReads = 0;
        diskWrites = 0;

        radixSort(temp, totalRecords);
        stats.println("The file input.txt is sorted with a size of " + ""
            + " bytes!");
        stats.println("Number of Blocks in memory pool: " + totalRecords);
        stats.println("Size of Blocks in Memery Pool: " + RECORD_SIZE);
        stats.println("Disk Writes: " + diskWrites);
        stats.println("Disk Reads: " + diskReads);

        temp.close();
        stats.close();
        stats.flush();
    }


    /**
     * Do a Radix sort
     * 
     * @param tempFile
     *            temporary file used during sorting
     * @param totalRecords
     *            number of records (each 8 bytes)
     * @throws IOException
     */
    private void radixSort(RandomAccessFile tempFile, long totalRecords)
        throws IOException {
        byte[] B = new byte[(int)totalRecords * RECORD_SIZE];
        int[] count = new int[RADIX];
        byte[] record = new byte[RECORD_SIZE];

        // For number of records
        for (int pass = 0; pass < NUM_PASSES; pass++) {
            // Initialize Count
            for (int i = 0; i < RADIX; i++) {
                count[i] = 0;
            }

            // Count occurences of each byte value
            file.seek(0);
            for (long rec = 0; rec < totalRecords; rec++) {
                file.readFully(record);
                int b = record[pass] & 0xFF;
                count[b]++;
                diskReads++;
            }

            // Transform count into starting positions
            int total = 0;
            for (int i = 0; i < RADIX; i++) {
                int oldCount = count[i];
                count[i] = total;
                total += oldCount;
            }

            // Put records into bins
            file.seek(0);
            for (long rec = 0; rec < totalRecords; rec++) {
                file.readFully(record);
                int b = record[pass] & 0xFF;
                int pos = count[b] * RECORD_SIZE;
                System.arraycopy(record, 0, B, pos, RECORD_SIZE);
                count[b]++;
                diskReads++;
            }

            // Copy B back into the file
            file.seek(0);
            for (long rec = 0; rec < totalRecords; rec++) {
                int pos = (int)rec * RECORD_SIZE;
                file.write(B, pos, RECORD_SIZE);
                diskWrites++;
            }

        }
    }
}
