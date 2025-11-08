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
    private static final int BLOCK_SIZE = 4096;
    private static byte[] mem;

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

    private void radixSort(RandomAccessFile tempFile, long totalRecords)
        throws IOException {

        // FIX: adjust layout so buffers do not overlap
        // layout:
        // [0 .. totalRecords*8) — input
        // [totalRecords*8 .. totalRecords*8 + RADIX*4) — count[]
        // [totalRecords*8 + RADIX*4 .. end) — output temp area
        ByteBuffer input = ByteBuffer.wrap(mem, 0, (int)totalRecords * RECORD_SIZE);
        IntBuffer count = ByteBuffer.wrap(mem, (int)totalRecords * RECORD_SIZE, RADIX * 4).asIntBuffer();
        ByteBuffer output = ByteBuffer.wrap(mem, (int)totalRecords * RECORD_SIZE + RADIX * 4, (int)totalRecords * RECORD_SIZE);  // FIX: output space same size as input
        
        file.readFully(mem, 0, (int)totalRecords * RECORD_SIZE);
        diskReads++;

        for (int pass = 0; pass < 4; pass++) {
            for (int i = 0; i < RADIX; i++) {
                count.put(i, 0);
            }

            input.position(0);
            for (int rec = 0; rec < totalRecords; rec++) {
                int key = input.getInt();
                input.getInt(); // skip data
                int b = (key >> (pass * 8)) & 0xFF;
                count.put(b, count.get(b) + 1);
            }

            int total = 0;
            for (int i = 0; i < RADIX; i++) {
                int oldCount = count.get(i);
                count.put(i, total);
                total += oldCount;
            }

            input.position(0);
            for (int rec = 0; rec < totalRecords; rec++) {
                int key = input.getInt();
                int data = input.getInt();
                int b = (key >> (pass * 8)) & 0xFF;
                int pos = count.get(b);
                count.put(b, pos + 1);

                int dest = pos * RECORD_SIZE;
                // FIX: now write inside output buffer region directly
                insertInt(key, (int)totalRecords * RECORD_SIZE + RADIX * 4 + dest);
                insertInt(data, (int)totalRecords * RECORD_SIZE + RADIX * 4 + dest + 4);
            }

            // FIX: copy back from output region to input region for next pass
            System.arraycopy(mem, (int)totalRecords * RECORD_SIZE + RADIX * 4, mem, 0, (int)totalRecords * RECORD_SIZE);
        }

        file.seek(0);
        file.write(mem, 0, (int)totalRecords * RECORD_SIZE);
        diskWrites++;
    }

    private void insertInt(int a, int b) {
        for (int i = 3; i >= 0; i--) {
            mem[b + (3 - i)] = (byte)(a >> (i * 8));
        }
    }
}
