import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanTree {

	public class Node {
		private short val;
		private int freq;
		private Node left;
		private Node right;

		public Node(short val, int freq) {
			this.val = val;
			this.freq = freq;
			left = null;
			right = null;
		}

		public Node(Node left, Node right) {
			this.freq = left.freq + right.freq;
			this.left = left;
			this.right = right;
		}
	}

	public Node root;

	public HuffmanTree(Map<Short, Integer> m) {
		PriorityQueue<Node> queue = new PriorityQueue<>();
		Iterator<Short> iter = m.keySet().iterator();
		while (iter.hasNext()) {
			short key = iter.next();
			queue.add(new Node(key, m.get(key)));
		}
		queue.add(new Node((short) 256, 1));

		while (queue.size() > 1) {
			root = new Node(queue.poll(), queue.poll());
			queue.add(root);
		}
	}

	public HuffmanTree(BitInputStream in) {
		root = HuffmanTreeH(in, root);
	}

	public Node HuffmanTreeH(BitInputStream in, Node cur) {
		if (in.readBit() == 1) {
			cur = new Node(HuffmanTreeH(in, cur.left), HuffmanTreeH(in, cur.right));
		} else if (in.readBit() == 0) {
			cur = new Node((short) in.readBits(9), (Integer) null);
		}
		return cur;
	}

	public void serialize(BitOutputStream out) {
		serializeH(out, root);
	}

	public void serializeH(BitOutputStream out, Node cur) {
		if (cur.left == null && cur.right == null) {
			out.writeBit(0);
			out.writeBits((int) cur.val, 9);
		} else {
			out.writeBit(1);
			serializeH(out, cur.left);
			serializeH(out, cur.right);
		}
	}
	
	public void encode(BitInputStream in, BitOutputStream out){
		Map<Short, Integer> m = new HashMap<>();
		while (in.hasBits()){
			int temp = in.readBits(8);
			if(m.containsKey(temp)){
				m.put((short)temp, m.get(temp) + 1);
			}
			else{
				m.put((short)temp, 1);
			}
		}
		
		HuffmanTree tree = new HuffmanTree(m);
		tree.serialize(out);
		
	}

}

// void serialize(BitOutputStream out): writes the HuffmanTree to the given file
// as a stream of bits in a serialized format (see “Serializing Huffman Trees”
// below).
// void encode(BitInputStream in, BitOutputStream out): Encodes the file given
// as a stream of bits into a compressed format using this Huffman tree. The
// encoded values are written, bit-by-bit to the given BitOuputStream.
// void decode(BitInputStream in, BitOutputStream out): Decodes a stream of
// huffman codes from a file given as a stream of bits into their uncompressed
// form, saving the results to the given output stream. Note that the EOF
// character is not written to out because it is not a valid 8-bit chunk (it is
// 9 bits).
