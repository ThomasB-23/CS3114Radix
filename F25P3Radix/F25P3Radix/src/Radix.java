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
    private  byte[] mem;

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
        int numRecords = (int)file.length() / RECORD_SIZE; //assumed size is smaller than long
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
    private void radixSort(RandomAccessFile tempFile, int totalRecords)
        throws IOException {
        // block is first 4096 (0, 4095)
        // count is next RADIX/256 (4096, 4351)
        // record is next 8 (4352, 4358)
        // b is not needed (I think)
//        ByteBuffer block = ByteBuffer.wrap(mem);
//        IntBuffer count = IntBuffer.allocate(RADIX);
//        ByteBuffer record = ByteBuffer.wrap(mem);
//        ByteBuffer B = ByteBuffer.wrap(mem);
        ByteBuffer count = ByteBuffer.wrap(mem);
        
        

        int totalBlocks = totalRecords * RECORD_SIZE / 4096;
        // For each block
        
        for (int blockIndex = 0; blockIndex < totalBlocks; blockIndex++) {
            tempFile.readFully(mem, 0, 4096);
            
            // For number of records
            for (int pass = 0; pass < 4; pass++) {
               
//                // Initialize Count
//                for (int i = 0; i < RADIX; i++) {
//                    // count.put(i, 0);
//                    mem[4096 + i] = 0;
//                }
                // our count is going to be from mem[4096] - mem[4096+RADIX*4]
                
                
                // Count occurences of each byte value
                //tempFile.seek(0);
                for (int rec = 0; rec < totalRecords; rec++) {
                    // mem[4352, 4358] = mem[rec * 8, rec + 7]
                    for (int j = 4352; j < 4356; j++) {
                        mem[j] = mem[rec * 8 + (j - 4352)];
                    }
//                    // tempFile.readFully(record.array());
//                   System.arraycopy(mem, (int)rec * 8, mem, 4352, 8);
                  
                    // int b = record.get(3 - pass) & 0xFF;
                    int b = mem[4355 - pass] & 0xFF;
                  
//                    count.get(mem, 80000 + b, 4);
                    
//                    intConverter(count.getInt());
//                    
//                    for(int i = 0; i <= 3; i++) {
//                        count.put(80000 + (b * 4) + i, mem[50000 + i]);
//                    }
                    
                    //count.put(b, count.get(b) + 1);
                    //count is at location 80,000 - 80,000 + 256 in byte array
                    
                     
                    
                     //uses the fact that ~n = -(n + 1) to find n+1 without weird conversions
                     //intConverter(Math.abs(~count.getInt()));
                    count.position(80000 + (b * 4));
                    intConverter(count.getInt() + 1, 50000);
                    
                     for(int i = 0; i <= 3; i++) {
                         count.put(80000 + (b * 4) + i, mem[50000 + i]);
                     }
                     diskReads++;
                    // record.clear(); System.arraycopy(mem, 0, mem, 4352,7);
                }

                // Transform count into starting positions
                int total = totalRecords;
                for (int i = (RADIX * 4) - 1; i >= 0; i--) {
                    int oldCount = count.get(80000 + i);
                    total -= oldCount;
                    intConverter(total, 50000);
                    for (int k = 0; k < 3; k++) {
                        count.put((i * 4) + 80000 + k, mem[50000 + k]);
                    }
                }

                // Put records into bins
                for (int rec = 0; rec < totalRecords; rec++) {
                    // tempFile.readFully(record.array());
//                    System.arraycopy(mem, (int)rec * 8, mem, 4352, 8);
                    for (int j = 4352; j < 4360; j++) {
                        mem[j] = mem[rec * 8 + (j - 4352)];
                    }
                    
                    // int b = record.get(3 - pass) & 0xFF;
//                    int b = mem[4358 - 3 - pass] & 0xFF;
                    
                    
                    // int pos = count.get(b) * RECORD_SIZE;
                   // int pos = mem[b] * RECORD_SIZE;
                    count.put(50000, (byte)(count.get(4355 - pass) & 0xFF));
                    
                    //count.position(50000 + pass);
                    //count.put(520000, )
                    //count.put(52000, count.get(count.getInt() * RECORD_SIZE));
                    
                    //mem = mem[b*rec]
                    // B.position(pos);
                    // B.put(record);
                    // 4352
                    
                    System.out.println(count.get(4355 - pass));
                    count.position(100000 + (count.get(50000) * 8));
                    for (int i = 0; i < 8; i++) {
                        count.put(count.get(4352 + i) + 1);
                    }
                    //count.position(count.get(count.getInt() * RECORD_SIZE));
                    
                    
//                    System.arraycopy(mem, mem[4096 + RADIX * blockIndex], mem, pos + 4096 + RADIX * blockIndex, 7);
//                    byte storeByte = memFinder(4096 + RADIX * blockIndex, pos);
//                    
//                    // count.put(b, count.get(b) + 1);
//                    System.arraycopy(mem, b, mem, 4096, 256);
//                    diskReads++;
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
    
    /**
     * finds a single byte 
     * @param start
     * @param end
     * @return
     */
    private byte memFinder(int start, int end) {
        byte hold = 0;
        for (int i = start; i < end; i++) {
            hold += mem[i];
        }
        return hold;
    }
    
    /**
     * converts a byte into an int and stores it into our working memory
     * @param a is the int to be converted
     */
    private void intConverter(int a, int b) {
        mem[b] = 0;
        mem[b + 1] = 0;
        mem[b + 2] = 0;
        mem[b + 3] = 0;
        for (int i = 3; i >= 0; i--) {
            mem[b + i] = (byte)(a >> i * 8);
        }
    }
}
