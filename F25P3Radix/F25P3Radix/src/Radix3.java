import java.nio.*;
import java.io.*;

// The Radix Sort implementation
// -------------------------------------------------------------------------
/**
 *
 * @author Thomas Bongiorno (thomasb23) and Charles Patton (charpatt5414)
 * @version Milestone 1
 */
public class Radix3 {

    private RandomAccessFile file;
    private PrintWriter stats;
    private int diskReads;
    private int diskWrites;
    private long totalRecords;
    
    private static final int RECORD_SIZE = 8;
    private static final int RADIX = 256;
    private static final int BLOCK_SIZE = 4096;
    private static byte[] mem;

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
    public Radix3(RandomAccessFile theFile, PrintWriter s) throws IOException {
        file = theFile;
        stats = s;
        mem = new byte[900000];
        
        totalRecords = file.length() / RECORD_SIZE;
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
        ByteBuffer input = ByteBuffer.wrap(mem, 0, (int)totalRecords * RECORD_SIZE);
        IntBuffer count = ByteBuffer.wrap(mem, (int)totalRecords * RECORD_SIZE, RADIX * 4).asIntBuffer();
        ByteBuffer output = ByteBuffer.wrap(mem, (int)totalRecords * RECORD_SIZE + RADIX * 4, BLOCK_SIZE + BLOCK_SIZE + 1);
        
        
        file.readFully(mem, 0, (int)totalRecords * RECORD_SIZE);
        
        
        // For number of records
        for (int pass = 0; pass < 4; pass++) {
            // Initializes Count
            for (int i = 0; i < RADIX; i++) {
                count.put(i, 0);
            }

            // Count occurences of each byte value
            input.position(0);
            for (int rec = 0; rec < totalRecords; rec++) {
                int b = (input.getInt() >> (pass * 8)) & 0xFF;
                input.getInt();
                count.put(b, count.get(b) + 1);
            }

         // Transform count into starting positions
            int total = (int)totalRecords;
            for (int i = RADIX - 1; i >= 0; i--) {
                int oldCount = count.get(i);
                total -= oldCount;
                count.put(i, total);
            }

            // Put records into bins
            input.position(0);
            output.limit(BLOCK_SIZE + RECORD_SIZE);
            for (int rec = 0; rec < (int)totalRecords; rec++) {
                int key = input.getInt();
                int data = input.getInt();
                int b = (key >> (pass * 8)) & 0xFF;
                int pos = count.get(b) * RECORD_SIZE;
                insertInt(key, 50000 + pos);
                insertInt(data, 50004 + pos);
                count.put(b, count.get(b) + 1);
            }

            // Copy B back into the file
            
            input.position(0);
            for (int i = 0; i < BLOCK_SIZE; i++) {
                input.put(i,mem[50000 + i]);
            }

            file.seek(0);
            file.write(mem, 0, BLOCK_SIZE);
            diskWrites += (int)totalRecords * RECORD_SIZE / BLOCK_SIZE;
        }
        
    }
    /**
     * puts an int in specific memory. We had issues with the built in putInt
     * for buffers
     */
    private void insertInt(int a, int b) {
        mem[b] = 0;
        mem[b + 1] = 0;
        mem[b + 2] = 0;
        mem[b + 3] = 0;

        for (int i = 3; i >= 0; i--) {
            mem[b + (3 - i)] = (byte)(a >> i * 8);
        }
    }
}