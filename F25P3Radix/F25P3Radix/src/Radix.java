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
    private long totalRecords;
    
    private static final int RECORD_SIZE = 8;
    private static final int RADIX = 256;
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
    public Radix(RandomAccessFile theFile, PrintWriter s) throws IOException {
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
        ByteBuffer B = ByteBuffer.allocate((int)totalRecords * RECORD_SIZE);
        IntBuffer count = IntBuffer.allocate(RADIX);
        ByteBuffer record = ByteBuffer.allocate(RECORD_SIZE);
        
        // For number of records
        for (int pass = 0; pass < 4; pass++) {
            // Initialize Count
            for (int i = 0; i < RADIX; i++) {
                count.put(i, 0);
            }

            // Count occurences of each byte value
            file.seek(0);
            for (long rec = 0; rec < totalRecords; rec++) {
                file.readFully(record.array());
                int b = record.get(3 - pass) & 0xFF;
                count.put(b, count.get(b) + 1);
                diskReads++;
                record.clear();
            }

            // Transform count into starting positions
            int total = (int) totalRecords;
            for (int i = RADIX - 1; i >= 0; i--) {
                int oldCount = count.get(i);
                total -= oldCount;
                count.put(i, total);
            }

            // Put records into bins
            file.seek(0);
            for (long rec = 0; rec < totalRecords; rec++) {
                record.clear();
                file.readFully(record.array());
                int b = record.get(3 - pass) & 0xFF;
                int pos = count.get(b) * RECORD_SIZE;
                B.position(pos);
                B.put(record);
                count.put(b, count.get(b) + 1);
                diskReads++;
            }

            // Copy B back into the file
            file.seek(0);
            B.position(0);
            byte[] tempArr = new byte[RECORD_SIZE];
            for (long rec = 0; rec < totalRecords; rec++) {
                B.get(tempArr);
                file.write(tempArr);
                diskWrites++;
            }

        }
    }
}
