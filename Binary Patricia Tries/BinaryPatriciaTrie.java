package projects.bpt;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>BinaryPatriciaTrie is a Patricia Trie over the binary alphabet 0, 1. By restricting themselves
 * to this small but terrifically useful alphabet, Binary Patricia Tries combine all the positive
 * aspects of Patricia Tries while shedding the storage cost typically associated with Tries that
 * deal with huge alphabets.</p>
 *
 * @author  Moweizi Xia 
 */
public class BinaryPatriciaTrie {

    Node root;
    int size;
    
    public class Node {
    	Node left;
    	Node right;
    	boolean isKey;
    	String keyRef;
    	
    	public Node() {
    		left = null;
    		right = null;
    		isKey = false;
    		keyRef = null;
    	}
    	
    	public Node(String key, boolean isKey) {
    		left = null;
    		right = null;
    		this.isKey = isKey;
    		keyRef = key;
    	}
    }

    /**
     * Simple constructor that will initialize the internals of <tt>this</tt>.
     */
    BinaryPatriciaTrie() {
        this.root = new Node("", false);
        this.size = 0;
    }
    

    /**
     * Searches the trie for a given <tt>key</tt>.
     *
     * @param key The input String key.
     * @return true if and only if key is in the trie, false otherwise.
     */
    public boolean search(String key) {
        if (key.charAt(0) == '0') return searchHelper(key, root.left);
        else return searchHelper(key, root.right);
    }

    public boolean searchHelper(String key, Node curr) {
    	if (curr == null) return false;
    	
    	if (key.length() == curr.keyRef.length()) {
    		if (key.equals(curr.keyRef) && curr.isKey) return true;
    		else return false;
    	} 
    	else if (key.length() < curr.keyRef.length()) return false;
    	else {
    		String prefix = key.substring(0,curr.keyRef.length());
    		if (!prefix.equals(curr.keyRef)) return false;
    		else {
	    		String temp = key.substring(curr.keyRef.length());
	            if (temp.charAt(0) == '0') return searchHelper(temp, curr.left);
	            else return searchHelper(temp, curr.right);
    		}
    	}
    }
    
    /**
     * Inserts <tt>key</tt> into the trie.
     *
     * @param key The input String key.
     * @return true if and only if the key was not already in the trie, false otherwise.
     */
    public boolean insert(String key) {
        if (search(key)) return false;
        else {
        	size++;
            if (key.charAt(0) == '0') root.left = insertHelper(key, root.left);
            else root.right = insertHelper(key, root.right);
            return true;
        }
    }

    public Node insertHelper(String key, Node curr) {
    	// case 1: empty node
    	if (curr == null) {
    		curr = new Node(key, true);
    		return curr;
    	} 
    	// case 2: stored is equal to the key
    	else if (key.length() == curr.keyRef.length() && key.equals(curr.keyRef)) {
    		curr.isKey = true;
    		return curr;
    	}
    	// case 3: keyRef is a strict prefix
    	else if (key.length() > curr.keyRef.length() && key.substring(0, curr.keyRef.length()).equals(curr.keyRef)) {
    		String temp = key.substring(curr.keyRef.length());
            if (temp.charAt(0) == '0') curr.left = insertHelper(temp, curr.left);
            else curr.right = insertHelper(temp, curr.right);
            return curr;
    	}
    	// case 4: key is a strict prefix of keyRef
    	else if (key.length() < curr.keyRef.length() && key.equals(curr.keyRef.substring(0, key.length()))) {
    		String temp = curr.keyRef.substring(key.length());
    		Node tempLeft = curr.left;
    		Node tempRight = curr.right;
            if (temp.charAt(0) == '0') {
            	curr.left = new Node(temp, curr.isKey);
            	curr.right = null;
            	curr.left.left = tempLeft;
            	curr.left.right = tempRight;
            }
            else {
            	curr.right = new Node(temp, curr.isKey);
            	curr.left = null;
            	curr.right.left = tempLeft;
            	curr.right.right = tempRight;
            }
    		curr.keyRef = key;
    		curr.isKey = true;
    		return curr;
    	} 
    	// case 5
    	else {
    		String tempParent = "";
    		String tempChild1 = "";
    		String tempChild2 = "";
    		Node tempLeft = curr.left;
    		Node tempRight = curr.right;

    		int i = 0;
    		while (i < key.length() && i < curr.keyRef.length() && key.charAt(i) == curr.keyRef.charAt(i)) 
    			tempParent = curr.keyRef.substring(0, ++i);
    			
    		tempChild1 = curr.keyRef.substring(i);
    		tempChild2 = key.substring(i);
    		
    		if (tempChild1.charAt(0) == '0') {
    			curr.left = new Node (tempChild1, curr.isKey);
    			curr.right = new Node (tempChild2, true);
    			curr.left.left = tempLeft;
    			curr.left.right = tempRight;
    		}
    		else {
    			curr.left = new Node (tempChild2, true);
    			curr.right = new Node (tempChild1, curr.isKey);
    			curr.right.left = tempLeft;
    			curr.right.right = tempRight;
    		}
    		
    		curr.keyRef = tempParent;
    		curr.isKey = false;
    		
    		return curr;
    	}
    }
    

    /**
     * Deletes <tt>key</tt> from the trie.
     *
     * @param key The String key to be deleted.
     * @return True if and only if key was contained by the trie before we attempted deletion, false otherwise.
     */
    public boolean delete(String key) {
        if (!search(key)) return false; 
        else {
        	size--;
            if (key.charAt(0) == '0') root.left = deleteHelper(key, root.left);
            else root.right = deleteHelper(key, root.right);       	
        	return true;
        }
    }

    public Node deleteHelper(String key, Node curr) {
    	// case 4: keyRef is a strict prefix
    	if (!key.equals(curr.keyRef) && key.substring(0, curr.keyRef.length()).equals(curr.keyRef)) {
    		String temp = key.substring(curr.keyRef.length());
            if (temp.charAt(0) == '0') curr.left = deleteHelper(temp, curr.left);
            else curr.right = deleteHelper(temp, curr.right);
            return curr;
    	}
    	// case 5
    	else {
    		// no null children
    		if (curr.left != null && curr.right != null) {
    			curr.isKey = false;
    			return curr;
    		}
    		// all null children
    		else if (curr.left == null && curr.right == null) return null;
    		// one null children
    		else {
    			if (curr.left == null) {
    				curr.keyRef = curr.keyRef.concat(curr.right.keyRef);
    				curr.isKey = curr.right.isKey;
    				curr.right = null;
    			}
    			else {
    				curr.keyRef = curr.keyRef.concat(curr.left.keyRef);
    				curr.isKey = curr.left.isKey;
    				curr.left = null;
    			}
    			return curr;
    		}
    	}
    }

    /**
     * Queries the trie for emptiness.
     *
     * @return true if and only if {@link #getSize()} == 0, false otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of keys in the tree.
     *
     * @return The number of keys in the tree.
     */
    public int getSize() {
        return size;
    }

    /**
     * <p>Performs an <i>inorder (symmetric) traversal</i> of the Binary Patricia Trie. Remember from lecture that inorder
     * traversal in tries is NOT sorted traversal, unless all the stored keys have the same length. This
     * is of course not required by your implementation, so you should make sure that in your tests you
     * are not expecting this method to return keys in lexicographic order. We put this method in the
     * interface because it helps us test your submission thoroughly and it helps you debug your code! </p>
     *
     * <p>We <b>neither require nor test </b> whether the {@link Iterator} returned by this method is fail-safe or fail-fast.
     * This means that you  do <b>not</b> need to test for thrown {@link java.util.ConcurrentModificationException}s and we do
     * <b>not</b> test your code for the possible occurrence of concurrent modifications.</p>
     *
     * <p>We also assume that the {@link Iterator} is <em>immutable</em>, i,e we do <b>not</b> test for the behavior
     * of {@link Iterator#remove()}. You can handle it any way you want for your own application, yet <b>we</b> will
     * <b>not</b> test for it.</p>
     *
     * @return An {@link Iterator} over the {@link String} keys stored in the trie, exposing the elements in <i>symmetric
     * order</i>.
     */
    public Iterator<String> inorderTraversal() {
        return new BPTIterator(root);
    }

    public class BPTIterator implements Iterator<String> {

    	ArrayList<String> BPTList;
    	int size;
    	
    	public BPTIterator(Node root) {
    		BPTList = new ArrayList<String>();
    		size = 0;
    		addAll(root, "");
    	}
    	
		@Override
		public boolean hasNext() {
			return size < BPTList.size();
		}

		@Override
		public String next() {
			return BPTList.get(size++);
		}
		
		public void addAll(Node curr, String key) {
			if (curr == null) return;
			
			String temp = key.concat(curr.keyRef);
			addAll(curr.left, temp);
			if (curr.isKey) BPTList.add(temp);
			addAll(curr.right,temp);
		}
    }
    
    /**
     * Finds the longest {@link String} stored in the Binary Patricia Trie.
     *
     * @return <p>The longest {@link String} stored in this. If the trie is empty, the empty string "" should be
     * returned. Careful: the empty string "" is <b>not</b> the same string as " "; the latter is a string
     * consisting of a single <b>space character</b>! It is also <b>not</b> the same as a <tt>null</tt> reference.</p>
     * <p>Ties should be broken in terms of <b>value</b> of the bit string. For example, if our trie contained
     * only the binary strings 01 and 11, <b>11</b> would be the longest string. If our trie contained
     * only 001 and 010, <b>010</b> would be the longest string.</p>
     */
    public String getLongest() {
        if (size == 0) return "";
        
        String longest = "";
        Iterator<String> itr = this.inorderTraversal();
        while (itr.hasNext()) {
        	String curr = itr.next();
        	if (curr.length() > longest.length()) longest = curr;
        	if (curr.length() == longest.length()) {
        		if (curr.compareTo(longest) > 0) longest = curr;
        	}
        }
        return longest;
    }
}