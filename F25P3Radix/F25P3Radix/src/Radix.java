import java.nio.*;
import java.io.*; // The Radix Sort implementation //
                  // -------------------------------------------------------------------------

/**
 * * * @author Thomas Bongiorno (thomasb23) and Charles Patton (charpatt5414)
 * * @version Milestone 1
 */
public class Radix {
    private RandomAccessFile file;
    private PrintWriter stats;
    private int diskReads;
    private int diskWrites;
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
     * @throws IOException
     */
    public Radix(RandomAccessFile theFile, PrintWriter s) throws IOException {
        file = theFile;
        stats = s;
        mem = new byte[900000];
        long numRecords = file.length() / RECORD_SIZE;
        RandomAccessFile temp = new RandomAccessFile("temp.bin", "rw");
        diskReads = 0;
        diskWrites = 0;
        radixSort(temp, numRecords);
        stats.println("The file input.txt is sorted with a size of " + file
            .length() / 4096 + " bytes!");
        stats.println("Number of Blocks in memory pool: " + numRecords);
        stats.println("Size of Blocks in Memery Pool: " + RECORD_SIZE);
        stats.println("Disk Writes: " + diskWrites);
        stats.println("Disk Reads: " + diskReads);
        temp.close();
        stats.flush();
    }


    /*
     * * Do a Radix sort *
     * 
     * @param tempFile
     * temporary file used during sorting
     * 
     * @param totalRecords
     * number of records (each 8 bytes)
     * 
     * @throws IOException
     */
    private void radixSort(RandomAccessFile tempFile, long totalRecords)
        throws IOException {
        // block is first 4096 (0, 4095)
        // count is next RADIX/256 (4096, 4351)
        // record is next 8 (4352, 4358)
        // b is not needed (I think)
        // ByteBuffer block = ByteBuffer.allocate(4096);
        // IntBuffer count = IntBuffer.allocate(RADIX);
        // ByteBuffer record = ByteBuffer.allocate(RECORD_SIZE);
        // ByteBuffer B = ByteBuffer.allocate((int)totalRecords * RECORD_SIZE);
        
        

        int totalBlocks = (int)totalRecords * RECORD_SIZE / 4096;
        // For each block
        
        for (int blockIndex = 0; blockIndex < totalBlocks; blockIndex++) {
            tempFile.readFully(mem, 0, 4095);
            
            // For number of records
            for (int pass = 0; pass < 4; pass++) {
               
                // Initialize Count
                for (int i = 0; i < RADIX; i++) {
                    // count.put(i, 0);
                    mem[4096 + i] = 0;
                }
                
                // Count occurences of each byte value
                tempFile.seek(0);
                for (long rec = 0; rec < totalRecords; rec++) {
                    // mem[4352, 4358] = mem[rec * 8, rec + 7]
                    // tempFile.readFully(record.array());
                    System.arraycopy(mem, (int)rec * 8, mem, 4352, 7);
                    // int b = record.get(3 - pass) & 0xFF;
                    int b = mem[4358 - 3 - pass] & 0xFF;
                    
                    // count.put(b, count.get(b) + 1);
                    System.arraycopy(mem, b, mem, 4096, 256);
                    diskReads++;
                    // record.clear(); System.arraycopy(mem, 0, mem, 4352,7);
                }

                // Transform count into starting positions
                int total = (int)totalRecords;
                for (int i = RADIX - 1; i >= 0; i--) {
                    // int oldCount = count.get(i);
                    int oldCount = mem[4096 + i];
                    total -= oldCount;
                    // count.put(i, total);
                    mem[4096 + i] = (byte)total;
                }

                // Put records into bins
                tempFile.seek(0);
                for (long rec = 0; rec < totalRecords; rec++) {
                    // tempFile.readFully(record.array());
                    System.arraycopy(mem, (int)rec * 8, mem, 4352, 7);
                    // int b = record.get(3 - pass) & 0xFF;
                    int b = mem[4358 - 3 - pass] & 0xFF;
                    // int pos = count.get(b) * RECORD_SIZE;
                    int pos = mem[b] * RECORD_SIZE;
                    // B.position(pos);
                    // B.put(record);
                    System.arraycopy(mem, mem[4352], mem, pos, 7);
                    // count.put(b, count.get(b) + 1);
                    System.arraycopy(mem, b, mem, 4096, 256);
                    diskReads++;
                    // record.clear();
                }
                
                // Copy B back into the file
                tempFile.seek(0);
                // B.position(0);
                // byte[] tempArr = new byte[RECORD_SIZE];
                for (long rec = 0; rec < totalRecords; rec++) {
                    // B.get(tempArr);
                    tempFile.write(mem, 0, 4095);
                    diskWrites++;
                }
                
                tempFile.seek(0);
                tempFile.write(mem, 0, 4096);
            }
        }

    }
    private byte memFinder(int start, int end) {
        byte hold = 0;
        for (int i = start; i < end; i++) {
            hold += mem[i];
        }
        return hold;
    }
}
