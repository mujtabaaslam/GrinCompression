import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanTree {
	public static class Node implements Comparable<Node> {
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

		public Node(short val) {
			this.val = val;
			left = null;
			right = null;
		}

		public Node(Node left, Node right) {
			this.freq = left.freq + right.freq;
			this.left = left;
			this.right = right;
		}

		/**
		 * 
		 * @param n a Node
		 * @return an Integer
		 */
		public int compareTo(Node n) {
			if (this.freq < n.freq) {
				return -1;
			} else if (this.freq > n.freq) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	private Node root;
	private Map<Short, String> encodes;

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
		this.encodes = new HashMap<Short, String>();
		buildEncodes(this.root, "");
	}

	public HuffmanTree(BitInputStream in) {
		root = HuffmanTreeH(in);
	}

	/**
	 * 
	 * @param in a BitInputStream
	 * @return a Node
	 */
	public Node HuffmanTreeH(BitInputStream in) {
		int temp = in.readBit();
		if (temp == 1) {
			return new Node(HuffmanTreeH(in), HuffmanTreeH(in));
		} else if (temp == 0) {
			return new Node((short) in.readBits(9));
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * 
	 * @param out a BitOutputStream
	 */
	public void serialize(BitOutputStream out) {
		serializeH(out, root);
	}

	/**
	 * 
	 * @param out a BitOutputStream
	 * @param cur a Node
	 */
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

	/**
	 * 
	 * @param node a Node
	 * @param str a String
	 */
	private void buildEncodes(Node node, String str) {
		if (node.left == null && node.right == null) {
			encodes.put(node.val, str);
			str = "";
		}
		if (node.left != null) {
			buildEncodes(node.left, str + "0");
		}
		if (node.right != null) {
			buildEncodes(node.right, str + "1");
		}
	}

	/**
	 * 
	 * @param in a BitInputStream
	 * @param out a BitOutputStream
	 */
	public void encode(BitInputStream in, BitOutputStream out) {
		short value = (short) in.readBits(8);
		while (value != -1) {
			String treePath = this.encodes.get((short) value);
			for (int i = 0; i < treePath.length(); i++) {
				if (treePath.charAt(i) == '1') {
					out.writeBit(1);
				} else if (treePath.charAt(i) == '0') {
					out.writeBit(0);
				}
			}
			value = (short) in.readBits(8);
		}
		String eof = encodes.get((short) 256);
		for (int i = 0; i < eof.length(); i++) {
			if (eof.charAt(i) == '1') {
				out.writeBit(1);
			} else if (eof.charAt(i) == '0') {
				out.writeBit(0);
			}
		}
	}

	/**
	 * 
	 * @param in a BitInputStream
	 * @param out a BitOutputStream
	 */
	public void decode(BitInputStream in, BitOutputStream out) {
		while (in.hasBits()) {
			Node node = this.root;
			while (node.left != null || node.right != null) {
				if (in.readBit() == 0) {
					node = node.left;
				} else {
					node = node.right;
				}
			}
			Short sh = (short) (int) node.val;
			if (sh == 256) {
				return;
			} else {
				out.writeBits(sh, 8);
			}
		}
	}
}