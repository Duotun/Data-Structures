package projects.pqueue.heaps; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
/**
 * <p>A <tt>LinkedMinHeap</tt> is a tree (specifically, a <b>complete</b> binary tree) where every nodes is
 * smaller than or equal to its descendants (as defined by the <tt>compareTo() </tt>overridings of the type T).
 * Percolation is employed when the root is deleted, and insertions guarantee are performed in a way that guarantees
 * that the heap property is maintained. </p>
 *
 * @author  Moweizi Xia 
 *
 * @param <T> The {@link Comparable} type of object held by the <tt>LinkedMinHeap</tt>.
 *
 * @see projects.pqueue.trees.LinkedBinarySearchTree
 * @see MinHeap
 * @see ArrayMinHeap
 */
public class LinkedMinHeap<T extends Comparable<T>> implements MinHeap<T> { 

	private Node root;
	private int size;
	protected boolean modificationFlag;
	
	public class Node {
		private T val;
		private Node left;
		private Node right;
		private Node parent;
		
		private Node(T val) {
			this.val = val;
			this.left = null;
			this.right = null;
			this.parent = null;
		}		
		
		private Node(T val, Node left, Node right, Node parent) {
			this.val = val;
			this.left = left;
			this.right = right;
			this.parent = parent;
		}
		
		public T getVal() {
			return this.val;
		}
		
		public Node getLeft() {
			return this.left;
		}
		
		public Node getRight() {
			return this.right;
		}
		
		public Node getParent() {
			return this.parent;
		}
	}
	
	/**
	 *  Default constructor.
	 */
	public LinkedMinHeap(){
		this.root = null;
		this.size = 0;
		modificationFlag = false;
	}

	/**
	 *  Second, non-default constructor.
	 *  @param rootElement the element to create the root with.
	 */
	public LinkedMinHeap(T rootElement){
		this.root = new Node(rootElement);
		this.size = 1;
		modificationFlag = false;
	}

	/**
	 * Copy constructor initializes the current MinHeap as a carbon
	 * copy of the parameter.
	 *
	 * @param other The MinHeap to copy the elements from.
	 */
	public LinkedMinHeap(MinHeap<T> other){
		if (other.isEmpty()) {
			this.root = null;
			this.size = 0;
		} else if (other.size() == 1) {
			try {
				this.root = new Node(other.getMin());
			} catch (EmptyHeapException e) {
				e.printStackTrace();
			}
			this.size = 1;
		} else {
			ArrayList<T> temp = new ArrayList<T>();
			T val = null;
			
			while (!other.isEmpty()) {
				try {
					val = other.deleteMin();
				} catch (EmptyHeapException e) {
					e.printStackTrace();
				}
				
				temp.add(val);
				insert(val);
			}
			
			for (T obj: temp) {
				other.insert(obj);
			}
		}
		modificationFlag = false;
	}

	public LinkedMinHeap(Node root, int size) {
		this.root = deepCopy(root);
		this.size = size;
		modificationFlag = false;
	}

	public Node deepCopy(Node root) {
        Node temp_left = null;
        Node temp_right = null;
        
        if (root.left != null) {
        	temp_left = deepCopyHelper(root.left, root);
        }
        if (root.right != null) {
        	temp_right = deepCopyHelper(root.right, root);
        }
        
        return new Node(root.val, temp_left, temp_right, null);
	}
	
	public Node deepCopyHelper(Node root, Node parent) {
        Node temp_left = null;
        Node temp_right = null;
        Node temp_parent = root;
        
        if (root.left != null) {
        	temp_left = deepCopyHelper(root.left, root);
        }
        if (root.right != null) {
        	temp_right = deepCopyHelper(root.right, root);
        }
        return new Node(root.val, temp_left, temp_right, temp_parent);
	}
	
	/**
	 * Standard equals() method.
	 *
	 * @return true If the parameter Object and the current MinHeap
	 * are identical Objects.
	 */
	@Override
	public boolean equals(Object other){
		if (!(other instanceof MinHeap)) {
			return false;
		} else if (((MinHeap<T>) other).size() != this.size){
			return false;
		} else {
			MinHeap<T> temp = new LinkedMinHeap<T>(this.root, this.size);
			ArrayList<T> temp2 = new ArrayList<T>();
			T current = null;
			
			while(!((MinHeap) other).isEmpty()) {
				try {
					current = ((MinHeap<T>) other).deleteMin();
				} catch (EmptyHeapException e) {
					e.printStackTrace();
				}
				
				temp2.add(current);
				
				try {
					if (temp.deleteMin().compareTo(current) != 0) {
						return false;
					}
				} catch (EmptyHeapException e) {
					e.printStackTrace();
				}
			}
			for (T obj: temp2) {
				((MinHeap<T>) other).insert(obj);
			}
			return true;
		}
	}

	@Override
	public boolean isEmpty() {
		return (this.size == 0);
	}

	@Override
	public int size() {
		return this.size;
	}
	
	@Override
	public void clear() {
		clear_helper(root);
		size = 0;
		modificationFlag = true;
	}

	public void clear_helper(Node root) {
		if (root != null) {
			clear_helper(root.left);
			clear_helper(root.right);
			root = null;
		}
	}

	@Override
	public void insert(T element) {
		this.size++;
		Node new_node = new Node(element);

		if (this.root == null || this.size == 1) {
			this.root = new_node;
		} else {
			String direction = Integer.toBinaryString(size);
			
			direction = direction.substring(1);
			
			insertHelper(root, direction, new_node);
			heapifyInsert(new_node);
		}
		modificationFlag = true;
	} 

	private void heapifyInsert(Node new_node) {
		while (new_node.parent != null && new_node.parent.val.compareTo(new_node.val) > 0) {
			T temp = new_node.parent.val;
			new_node.parent.val = new_node.val;
			new_node.val = temp;
			new_node = new_node.parent;
		}
	}

	private void insertHelper(Node root, String direction, Node new_node) {
		boolean left;
		
		if (direction.charAt(0) == '0') 
			left = true;
		else
			left = false;
		
		if (direction.length() == 1) {
			new_node.parent = root;
			if (left)
				root.left = new_node;
			else
				root.right = new_node;
		} else {
			if (left)
				insertHelper(root.left, direction.substring(1), new_node);
			else
				insertHelper(root.right, direction.substring(1), new_node);
		}
	}

	@Override
	public T getMin() throws EmptyHeapException {
		if(isEmpty())
			throw new EmptyHeapException("getMin: Heap is empty.");
		
		return this.root.getVal();
	}

	@Override
	public T deleteMin() throws EmptyHeapException {
		if(isEmpty())
			throw new EmptyHeapException("deleteMin: Heap is empty.");
		
		T min = getMin();
		
		if (size == 1) {
			clear();
			modificationFlag = true;
			return min;
		}
		
		String direction = Integer.toBinaryString(size);
		direction = direction.substring(1);
		size--;
		
		Node temp = this.root;
		T new_root;
		
		while (direction.length() > 1) {
			if (direction.charAt(0) == '0') 
				temp = temp.left;
			else
				temp = temp.right;
			direction = direction.substring(1);
		}
		
		if (temp.right == null) {
			new_root = temp.left.val;
			temp.left = null;
		} else {
			new_root = temp.right.val;
			temp.right = null;
		}
		
		root.val = new_root;
		
		heapifyDelete(root);
		modificationFlag = true;
		return min;
	}	
	


	private void heapifyDelete(Node root) {
		while ((root.left != null && root.left.val.compareTo(root.val) < 0) || (root.right != null && root.right.val.compareTo(root.val) < 0)) {
			T min;
			if (root.right == null) {
				min = root.left.val;
				root.left.val = root.val;
				root.val = min;
				root = root.left;
			} else {
				if (root.right.val.compareTo(root.left.val) < 0) {
					min = root.right.val;
					root.right.val = root.val;
					root.val = min;
					root = root.right;
				} else {
					min = root.left.val;
					root.left.val = root.val;
					root.val = min;
					root = root.left;
				}
			}
		}
	}

	public ArrayList<T> iteratorHelper(Node root) {
		
		if (root == null)
			return new ArrayList<T>();
		
		ArrayList<T> temp = new ArrayList<T>();
		temp.add(root.val);
		temp.addAll(iteratorHelper(root.left));
		temp.addAll(iteratorHelper(root.right));
		
		return temp;
	}
	
	@Override
	public Iterator<T> iterator() {
		ArrayList<T> sorted = new ArrayList<T>();
		sorted = iteratorHelper(this.root);
		Collections.sort(sorted);

		return new LinkedMinHeapIterator(sorted);
	}

	class LinkedMinHeapIterator implements Iterator<T> {

		int current;
		ArrayList<T> list;
		
		public LinkedMinHeapIterator(ArrayList<T> sorted) {
			current = 0;
			list = sorted;
			modificationFlag = false;
		}
		
		@Override
		public boolean hasNext() {
			if (current < size)
				return true;
			else
				return false;
		}

		@Override
		public T next() {
			if (modificationFlag) throw new ConcurrentModificationException("next(): Attempted to traverse a heap after removal.");
			return list.get(current++);
		}
		
	}
	
}



