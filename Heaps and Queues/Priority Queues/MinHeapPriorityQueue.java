package projects.pqueue.priorityqueues; 
import projects.pqueue.InvalidPriorityException;
import projects.pqueue.heaps.ArrayMinHeap;
import projects.pqueue.heaps.EmptyHeapException;
import projects.pqueue.heaps.MinHeap;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * <p><tt>MinHeapPriorityQueue</tt> is a {@link PriorityQueue} implemented using a {@link MinHeap}.</p>
 *
 * @author Moweizi Xia
 *
 * @param <T> The Type held by the container.
 *
 * @see LinearPriorityQueue
 * @see MinHeap
 */
public class MinHeapPriorityQueue<T> implements PriorityQueue<T>{ 

	private ArrayMinHeap<Integer> queue;
	private ArrayList<T> priorities;
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
	 * Simple default constructor.
	 */
	public MinHeapPriorityQueue(){
		queue = new ArrayMinHeap<Integer>();
		priorities = new ArrayList<T>();
		modificationFlag = false;
	}

	@Override
	public void enqueue(T element, int priority) throws InvalidPriorityException{
		if (priority < 0) throw new InvalidPriorityException("enqueue: priority is negative");
		
		//Element new_element = new Element(element, priority);
		
		queue.insert(priority);
		//System.out.println("Priority of: " + priority + ".  Val of: " + element);
		//System.out.println("Index of: " + queue.getArrayList().lastIndexOf(priority));
		priorities.add(queue.getArrayList().lastIndexOf(priority), element);
		modificationFlag = true;
	}


	@Override
	public T dequeue() throws EmptyPriorityQueueException {
		if (isEmpty()) throw new EmptyPriorityQueueException("dequeue: Queue is empty");
		
		T removed = priorities.remove(0);
		try {
			queue.deleteMin();
		} catch (EmptyHeapException e) {
			e.printStackTrace();
		}
		modificationFlag = true;
		return removed;
	}
	
	public void printQueue() {
		for (T obj: priorities) {
			System.out.print(obj + " ");
		}
	}
	
	public void printPriority() {
		try {
			this.queue.printHeap();
		} catch (EmptyHeapException e) {
			e.printStackTrace();
		}
	}

	@Override
	public T getFirst() throws EmptyPriorityQueueException {
		if (isEmpty()) throw new EmptyPriorityQueueException("getFirst: Queue is empty");
		
		return priorities.get(0);
	}

	@Override
	public Iterator<T> iterator() {
		return new MinHeapPriorityQueueIterator();
	}
	
	class MinHeapPriorityQueueIterator implements Iterator<T> {

		int current;
		
		public MinHeapPriorityQueueIterator() {
			current = 0;
			modificationFlag = false;
		}
		
		@Override
		public boolean hasNext() {
			if (current < priorities.size())
				return true;
			else
				return false;
		}

		@Override
		public T next() {
			if (modificationFlag) throw new ConcurrentModificationException("next(): Attempted to traverse a heap after removal.");
			return priorities.get(current++);
		}
		
	}

	@Override
	public int size() {
		return priorities.size();
	}

	@Override
	public boolean isEmpty() {
		return queue.size() == 0;
	}

	@Override
	public void clear() {
		queue.clear();
		priorities.clear();
		modificationFlag = true;
	}
}
