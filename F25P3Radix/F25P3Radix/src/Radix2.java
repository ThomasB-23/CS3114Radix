import java.nio.*;
import java.io.*;

// The Radix Sort implementation
// -------------------------------------------------------------------------
/**
 *
 * @author Thomas Bongiorno (thomasb23) and Charles Patton (charpatt5414)
 * @version Milestone 1
 */
public class Radix2 {

    private RandomAccessFile file;
    private PrintWriter stats;
    private int diskReads;
    private int diskWrites;
    private long numRecords;

    private static final int RECORD_SIZE = 8;
    private static final int RADIX = 256;
    private static final int RECORDS_PER_BLOCK = 512;
    private static final int BLOCK_SIZE = 4096;
    private byte[] mem;

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
    public Radix2(RandomAccessFile theFile, PrintWriter s) throws IOException {
        file = theFile;
        stats = s;
        mem = new byte[900000];

        numRecords = file.length() / RECORD_SIZE;
        RandomAccessFile temp = new RandomAccessFile("temp.bin", "rw");

        diskReads = 0;
        diskWrites = 0;

        radixSort(temp, numRecords);
        stats.println("The file input.txt is sorted with a size of " + file
            .length() / BLOCK_SIZE + " bytes!");
        stats.println("Number of Blocks in memory pool: " + numRecords);
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
        ByteBuffer input = ByteBuffer.wrap(mem, 0, 4096);
        ByteBuffer output = ByteBuffer.wrap(mem, 10000, 14096);
        ByteBuffer record = ByteBuffer.wrap(mem, 20000, 20008);
        IntBuffer count = ByteBuffer.wrap(mem, 50000, 51000).asIntBuffer();

        long totalBlocks = totalRecords * RECORD_SIZE / BLOCK_SIZE;

        // For number of blocks
        for (int blockIndex = 0; blockIndex < totalBlocks; blockIndex++) {
            file.seek(blockIndex * BLOCK_SIZE);
            file.readFully(input.array(), 0, BLOCK_SIZE);
            diskReads++;
            
            // For number of records
            for (int pass = 0; pass < 4; pass++) {
                
                for (int i = 0; i < RADIX; i++) {
                    count.put(i, 0);
                }
                
                // Count occurences of each byte value
                input.position(0);
                for (int rec = 0; rec < totalRecords; rec++) {
                    int key = input.getInt();
                    input.getInt();
                    int b = (key >>> (pass * 8)) & 0xFF;
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
                output.position(0);
                for (int rec = 0; rec < RECORDS_PER_BLOCK; rec++) {
                    int key = input.getInt();
                    int data = input.getInt();
                    int b = (key >>> (pass * 8)) & 0xFF;
                    int pos = count.get(b) * RECORD_SIZE;
                    output.position(pos);
                    output.putInt(key);
                    output.putInt(data);
                    count.put(b, count.get(b) + 1);
                }

                // Copy B back into the file
                System.arraycopy(mem, BLOCK_SIZE, mem, 0, BLOCK_SIZE);
            }
            
            file.seek(blockIndex * BLOCK_SIZE);
            file.write(mem, 0, BLOCK_SIZE);
            diskWrites++;
        }

    }
}
