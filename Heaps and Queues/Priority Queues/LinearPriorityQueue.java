package projects.pqueue.priorityqueues; 
import projects.pqueue.InvalidPriorityException;
import projects.pqueue.InvalidCapacityException;
import projects.pqueue.fifoqueues.FIFOQueue;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * <p><tt>LinearPriorityQueue</tt> is a priority queue implemented as a linear {@link java.util.Collection}
 * of common {@link FIFOQueue}s, where the {@link FIFOQueue}s themselves hold objects
 * with the same priority (in the order they were inserted).</p>
 *
 * @param <T> The type held by the container.
 * 
 * @author Moweizi Xia
 *
 * @see MinHeapPriorityQueue
 *
 */
public class LinearPriorityQueue<T> implements PriorityQueue<T> { 

	private ArrayList<Element> queue;
	private int size;
	protected boolean modificationFlag;
	
	public class Element {
		private T val;
		private int priority;
		
		public Element(T val, int priority) {
			this.val = val;
			this.priority = priority;
		}
	}

	/**
	 * Default constructor initializes the data structure with
	 * a default capacity. This default capacity will be the default capacity of the
	 * underlying data structure that you will choose to use to implement this class.
	 */
	public LinearPriorityQueue(){
		this.queue = new ArrayList<Element>();
		this.size = 0;
		modificationFlag = false;
	}

	/**
	 * Non-default constructor initializes the data structure with 
	 * the provided capacity. This provided capacity will need to be passed to the default capacity
	 * of the underlying data structure that you will choose to use to implement this class.
	 * @see #LinearPriorityQueue()
	 * @param capacity The initial capacity to endow your inner implementation with.
	 * @throws InvalidCapacityException if the capacity provided is negative.
	 */
	public LinearPriorityQueue(int capacity) throws InvalidCapacityException{
		if (capacity < 0) throw new InvalidCapacityException("constructor: capacity is negative");
		
		this.queue = new ArrayList<Element>();
		this.size = 0;
		modificationFlag = false;
	}

	@Override
	public void enqueue(T element, int priority) throws InvalidPriorityException{
		if (priority < 0) throw new InvalidPriorityException("enqueue: priority is negative");
		
		this.size++;
		Element new_element = new Element(element, priority);
		
		if (this.size == 1) {
			queue.add(new_element);
		}
		else {
			int i = 0;
			while(i < queue.size() && queue.get(i).priority <= new_element.priority) {
				i++;
			}
			//i++;
			queue.add(i, new_element);
		}
		modificationFlag = true;
	}

	@Override
	public T dequeue() throws EmptyPriorityQueueException {
		if (isEmpty()) throw new EmptyPriorityQueueException("dequeue: Queue is empty");
		
		Element removed = queue.remove(0);
		size--;
		
		modificationFlag = true;
		return removed.val;
	}

	@Override
	public T getFirst() throws EmptyPriorityQueueException {
		if (isEmpty()) throw new EmptyPriorityQueueException("getFirst: Queue is empty");
		
		return queue.get(0).val;
	}


	@Override
	public int size() {
		return this.size;
	}

	@Override
	public boolean isEmpty() {
		return this.size == 0;
	}

	@Override
	public void clear() {
		queue.clear();
		size = 0;
		modificationFlag = true;
	}

	@Override
	public Iterator<T> iterator() {
		return new LinearPriorityQueueIterator();
	}
	
	class LinearPriorityQueueIterator implements Iterator<T> {

		int current;
		
		public LinearPriorityQueueIterator() {
			current = 0;
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
			return queue.get(current++).val;
		}
		
	}

}