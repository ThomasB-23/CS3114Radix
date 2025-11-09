import java.nio.*;
import java.io.*;

// The Radix Sort implementation
// -------------------------------------------------------------------------
/**
 * The Radix class that is used to perform a radix sort over a binary file
 * 
 * @author Thomas Bongiorno (thomasb23) & Charles Patton (charpatt5414)
 * @version Oct 20, 2025
 */
public class Radix {

    private RandomAccessFile file;
    private PrintWriter stats;
    private int diskReads;
    private int diskWrites;
    private int totalRecords;

    private static final int RECORD_SIZE = 8;
    private static final int RADIX = 256;
    private static byte[] mem;

    // ----------------------------------------------------------
    /**
     * Creates a Radix object that can perform a radix sort on a binary file
     * 
     * @param theFile
     *            The input file that we are analyzing and sorting through
     * @param s
     *            The printwriter for the general statistics file
     * @throws IOException
     */
    public Radix(RandomAccessFile theFile, PrintWriter s) throws IOException {
        file = theFile;
        stats = s;
        mem = new byte[900000];

        totalRecords = (int)file.length() / RECORD_SIZE;
        RandomAccessFile temp = new RandomAccessFile("temp.bin", "rw");

        diskReads = 0;
        diskWrites = 0;

        radixSort(temp);
        stats.println("The file input.txt is sorted with a size of " + ""
            + " bytes!");
        stats.println("Number of Blocks in memory pool: " + totalRecords);
        stats.println("Size of Blocks in Memery Pool: " + RECORD_SIZE);
        stats.println("Disk Writes: " + diskWrites);
        stats.println("Disk Reads: " + diskReads);

        temp.close();
        stats.flush();
    }


    /**
     * Helper method that sorts a block of data from the input given by the
     * index
     * 
     * @param tempFile
     *            The temporary file where we will be storing the sorted block
     */
    private void radixSort(RandomAccessFile tempFile) throws IOException {

        ByteBuffer input = ByteBuffer.wrap(mem, 0, totalRecords * RECORD_SIZE);
        IntBuffer count = ByteBuffer.wrap(mem, totalRecords * RECORD_SIZE, RADIX
            * 4).asIntBuffer();

        file.readFully(mem, 0, totalRecords * RECORD_SIZE);
        diskReads++;

        // for byte in key
        for (int pass = 0; pass < 4; pass++) {

            // Reset count
            for (int i = 0; i < RADIX; i++) {
                count.put(i, 0);
            }

            // Increment count for value of specified byte
            input.position(0);
            for (int rec = 0; rec < totalRecords; rec++) {
                int key = input.getInt();
                input.getInt(); // skip data
                count.put((key >> (pass * 8)) & 0xFF, count.get((key >> (pass
                    * 8)) & 0xFF) + 1);
            }

            // Do a rolling summation of count
            int total = 0;
            for (int i = 0; i < RADIX; i++) {
                int oldCount = count.get(i);
                count.put(i, total);
                total += oldCount;
            }

            // loop through records
            input.position(0);
            for (int rec = 0; rec < totalRecords; rec++) {
                // Store data and increment count at position
                int key = input.getInt();
                int pos = count.get((key >> (pass * 8)) & 0xFF);
                count.put((key >> (pass * 8)) & 0xFF, pos + 1);

                // write to the output buffer
                // using insertInt becuase putInt isn't working with us
                insertInt(key, totalRecords * RECORD_SIZE + RADIX * 4 + pos
                    * RECORD_SIZE);
                insertInt(input.getInt(), totalRecords * RECORD_SIZE + RADIX * 4
                    + pos * RECORD_SIZE + 4);
            }

            // write from output back to input within the memory
            input.position(0);
            for (int i = 0; i < totalRecords * RECORD_SIZE; i++) {
                input.put(i, mem[totalRecords * RECORD_SIZE + RADIX * 4 + i]);
            }
        }

        // Write from the input buffer to the input file
        file.seek(0);
        file.write(mem, 0, totalRecords * RECORD_SIZE);
        diskWrites++;
    }


    /**
     * Private helper method that puts an integer directly into the memory as
     * bytes (Using this since output.put wasn't working)
     * 
     * @param a
     *            The integer we are turning into bytes
     * @param b
     *            The location where we want to store the integer in mem
     */
    private void insertInt(int a, int b) {
        for (int i = 3; i >= 0; i--) {
            mem[b + (3 - i)] = (byte)(a >> (i * 8));
        }
    }
}
