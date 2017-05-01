import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Grin {
    public static void decode(String infile, String outfile) throws IOException{
        BitInputStream in = new BitInputStream(infile);
        BitOutputStream out = new BitOutputStream(outfile);
        int magic = in.readBits(32);
        if (magic != 1846) {
            throw new IllegalArgumentException("Not a .grin file");
        }
        HuffmanTree tree = new HuffmanTree(in);
        tree.decode(in, out);
    }
    
   public static Map<Short, Integer> createFrequencyMap(String file) throws IOException{
       Map<Short, Integer> m = new HashMap<>();
       BitInputStream in = new BitInputStream(file);
     while (in.hasBits()){
         int temp = in.readBits(8);
         if(m.containsKey((short) temp)){
             m.put((short)temp, m.get((short) temp) + 1);
         }
         else{
             m.put((short)temp, 1);
         }
     }
     in.close();
       return m; 
    }
   
   public static void encode(String infile, String outfile) throws IOException{
       HuffmanTree tree = new HuffmanTree(createFrequencyMap(infile));
       BitInputStream in = new BitInputStream(infile);
       BitOutputStream out = new BitOutputStream(outfile);
       out.writeBits(1846, 32);
       tree.serialize(out);
       tree.encode(in, out);
       in.close();
       out.close();
   }
   
   public static void main(String args[]) throws IOException{
       encode("input.txt", "output");
       decode("output", "input.txt");
   }
   
    
}
