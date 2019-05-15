package projects.pqueue.heaps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import projects.pqueue.priorityqueues.LinearPriorityQueue.Element;
import projects.pqueue.priorityqueues.MinHeapPriorityQueue;
import projects.pqueue.trees.EmptyTreeException;
/**
 * <p><tt>ArrayMinHeap</tt> is a {@link MinHeap} implemented using an internal array. Since projects.pqueue.heaps are <b>complete</b>
 * binary projects.pqueue.trees, using contiguous storage to store them is an excellent idea, since with such storage we avoid
 * wasting bytes per <tt>null</tt> pointer in a linked implementation.</p>
 *
 * @author Moweizi Xia 
 *
 * @see MinHeap
 * @see ArrayMinHeap
  */
public class ArrayMinHeap<T extends Comparable<T>> implements MinHeap<T> { 

	private ArrayList<T> heap;
	private int size;
	private T root;
	protected boolean modificationFlag;

	/**
	 *  Default constructor.
	 */
	public ArrayMinHeap(){
		this.heap = new ArrayList<T>();
		this.size = 0;
		this.root = null;
		modificationFlag = false;
	}

	/**
	 *  Second, non-default constructor.
	 *  @param rootElement the element to create the root with.
	 */
	public ArrayMinHeap(T rootElement){
		this.heap = new ArrayList<T>();
		this.heap.add(rootElement);
		this.size = 1;
		this.root = rootElement;
		modificationFlag = false;
	}
	
	/**
	 *  Third, non-default constructor.
	 *  Initializes from an ArrayList
	 *  @param heap.
	 */
	public ArrayMinHeap(ArrayList<T> heap, int size){
		this.heap = new ArrayList<T>();
		for (T obj: heap) {
			this.heap.add(obj);
		}
		this.size = size;
		this.root = heap.get(0);
		modificationFlag = false;
	}

	/**
	 * Copy constructor initializes the current MinHeap as a carbon
	 * copy of the parameter.
	 *
	 * @param other The MinHeap to copy the elements from.
	 */
	
	
	public ArrayMinHeap(MinHeap<T> other){
		//MinHeap<T> temp = other;
		
		if (other.isEmpty()) {
			this.heap = new ArrayList<T>();
			this.size = 0;
			this.root = null;
		}
		else if (other.size() == 1) {
			try {
				this.root = other.getMin();
			} catch (EmptyHeapException e) {
				e.printStackTrace();
			}
			this.size = 1;
			try {
				this.heap.add(other.getMin());
			} catch (EmptyHeapException e) {
				e.printStackTrace();
			}
		}
		else {
			this.heap = new ArrayList<T>();
			ArrayList<T> temp = new ArrayList<T>();
			T current = null;
			
			try {
				current = other.deleteMin();
			} catch (EmptyHeapException e1) {
				e1.printStackTrace();
			}
			
			this.root = current;
			temp.add(current);
			
			this.heap.add(root);
			this.size = 1;
			
			while (!other.isEmpty()) {
				try {
					current = other.deleteMin();
				} catch (EmptyHeapException e) {
					e.printStackTrace();
				}
				this.heap.add(current);
				temp.add(current);
				this.size++;
			}
			
			for (T obj: temp) {
				other.insert(obj);
			}
		}
		modificationFlag = false;
	}

	public ArrayList<Integer> getArrayList() {
		return (ArrayList<Integer>) this.heap;
		
	}
	
	/**
	 * Standard equals() method.
	 *
	 * @return true If the parameter Object and the current MinHeap
	 * are identical Objects.
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MinHeap)) {
			return false;
		} else if (((MinHeap<T>) other).size() != this.size){
			return false;
		} else {
			MinHeap<T> temp = new ArrayMinHeap<T>(this.heap, this.size);
			ArrayList<T> temp2 = new ArrayList<T>();
			T current = null;
			while(!((MinHeap<T>) other).isEmpty()) {
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
		return size == 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void clear() {
		this.heap.clear();
		this.size = 0;
		this.root = null;
		modificationFlag = true;
	}

	@Override
	public void insert(T element) {
		if (this.size == 0) {
			this.heap.add(element);
			this.root = element;
			this.size++;
		}
		else {
			this.heap.add(element);
			this.size++;
			heapifyAdd(this.heap, element);
			this.root = heap.get(0);
		}
		modificationFlag = true;
	}

	private void heapifyAdd(ArrayList<T> heap, T element) {
		int index = size - 1;
		int parent_index = (index - 1) / 2;
		//System.out.println("Out here before while! Index is " + index + ". Parent index is: " + parent_index);
		while(element.compareTo(this.heap.get(parent_index)) < 0 && index > 0) {
			this.heap.set(index, this.heap.get(parent_index));
			this.heap.set(parent_index, element);
			
			index = parent_index;
			parent_index = (index - 1) / 2;	
		}
		//System.out.println("Out here! Index is " + index + ". Parent index is: " + parent_index);
		if (index == (parent_index * 2 + 2)) {
			//System.out.println("In here!");
			T left_child = this.heap.get(index - 1);
			if (left_child.compareTo(this.heap.get(index)) > 0) {
				this.heap.set(index, left_child);
				this.heap.set(index - 1, element);
			}
		}
	}

	@Override
	public T getMin() throws EmptyHeapException {
		if(isEmpty())
			throw new EmptyHeapException("getMin: Heap is empty.");
		
		return this.root;
	}

	@Override
	public T deleteMin() throws EmptyHeapException {	
		if(isEmpty())
			throw new EmptyHeapException("deleteMin: Heap is empty.");
		
		T deleted = this.root;
		T temp_root = this.heap.get(size - 1);
		
		if (size == 1) {
			this.root = null;
			this.heap.clear();
			this.size = 0;
			modificationFlag = true;
			return deleted;
		}
		
		this.heap.set(0, temp_root);
		this.heap.remove(size - 1);
		this.size--;
		heapifyDelete(this.heap, temp_root);
		this.root = this.heap.get(0);
		
		modificationFlag = true;
		return deleted;
	}

	public void printHeap() throws EmptyHeapException {
		for (T obj: this.heap) {
			System.out.print(obj + " ");
		}
		System.out.print("\n");
	}


	private void heapifyDelete(ArrayList<T> heap, T temp_root) {
		int index = 0;
		int left_child_index = 1;
		int right_child_index = 2;
		
		while((left_child_index < heap.size() && this.heap.get(left_child_index).compareTo(temp_root) < 0) || 
				(right_child_index < heap.size() && this.heap.get(right_child_index).compareTo(temp_root) < 0)) {
			T min;
			int new_index;
			
			if (left_child_index < heap.size() && right_child_index >= heap.size()) {
				min = this.heap.get(left_child_index);
				new_index = left_child_index;
			}

			else {
				if (this.heap.get(left_child_index).compareTo(this.heap.get(right_child_index)) <= 0) {
					min = this.heap.get(left_child_index);
					new_index = left_child_index;
				}
				else {
					min = this.heap.get(right_child_index);
					new_index = right_child_index;
				}
			}
				//system.out.println("Swapping with " + min);
				this.heap.set(index, min);
				this.heap.set(new_index, temp_root);
				
				index = new_index;
				left_child_index = (index * 2) + 1;
				right_child_index = (index * 2) + 2;
		}
	}
	
	public void incSize() {
		this.size++;
	}
	
	public void decSize() {
		this.size--;
	}

	@Override
	public Iterator<T> iterator() {
		ArrayList<T> sorted = new ArrayList<T>();
		sorted.addAll(this.heap);
		Collections.sort(sorted);
		return new ArrayMinHeapIterator(sorted);
	}
	
	class ArrayMinHeapIterator implements Iterator<T> {
		
		int current;
		ArrayList<T> list;
		
		public ArrayMinHeapIterator(ArrayList<T> sorted) {
			current = 0;
			modificationFlag = false;
			list = sorted;
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
