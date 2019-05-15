package projects.spatial.knnutils;

import projects.spatial.kdpoint.KDPoint;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * <p>{@link BoundedPriorityQueue} is an {@link Iterable} priority queue whose number of elements
 * is bounded above. Insertions are such that if the queue's provided capacity is surpassed,
 * its length is not expanded, but rather the maximum priority element is ejected
 * (which could be the element just attempted to be enqueued).</p>
 *
 * @author  Moweizi Xia 
 *
 */
public class BoundedPriorityQueue<T> implements Iterable<T>{

	private int max_size;
	private int curr_size;
	private PriorityQueue<Element> queue;

	public class Element {
		private T val;
		private double priority;
		
		public Element(T val, double priority) {
			this.val = val;
			this.priority = priority;
		}
		
		public double getPriority() {
			return this.priority;
		}
	}

	public class BPQComparator implements Comparator<Element> {

		@Override
		public int compare(Element a, Element b) {
			if (a.priority < b.priority) return -1;
			else if (a.priority > b.priority) return 1;
			else return 0;
		}
		
	}

	/**
	 * Standard constructor. Creates a {@link BoundedPriorityQueue} of the provided size.
	 * @param size The number of elements that the {@link BoundedPriorityQueue} instance is allowed to store.
	 * @throws RuntimeException if size &lt; 1.
	 */
	public BoundedPriorityQueue(int size){
		if(size < 1)
			throw new RuntimeException("Size cannot be less than 1.");
		max_size = size;
		curr_size = 0;
		queue = new PriorityQueue<Element>(new BPQComparator());
		
	}

	/**
	 * <p>Insert element in the Priority Queue, according to its priority.
	 * <b>Lower is better.</b> We allow for <b>non-integer priorities</b> such that the Priority Queue
	 * can be used for orderings where the prioritization is <b>not</b> rounded to integer quantities, such as
	 * Euclidean Distances in KNN queries. </p>
	 *
	 * @param element The element to insert in the queue.
	 * @param priority The priority of the element.
	 *
	 * @see projects.spatial.kdpoint.KDPoint#distance(KDPoint)
	 */
	public void enqueue(T element, double priority) {
		Element new_element = new Element(element, priority);
		
		if (curr_size < max_size) {
			queue.add(new_element);
			curr_size++;
		}
		else {
			if (this.lastElement().priority > priority) {
				this.removeLast();
				queue.add(new_element);
			}
		}
	}

	/**
	 * Return the <b>minimum priority element</b> in the queue, <b>simultaneously removing it</b> from the structure.
	 * @return The minimum priority element in the queue, or null if the queue is empty.
	 */
	public T dequeue() {
		curr_size--;
		return queue.poll().val;
	}

	/**
	 * Return, <b>but don't remove</b>, the <b>minimum priority element</b> from the queue.
	 * @return The minimum priority element of the queue, or null if the queue is empty.
	 */
	public T first() {
		if (this.isEmpty()) return null;
		return queue.peek().val;
	}


	/**
	 * <p>Return, <b>but don't remove</b>, the <b>maximum priority element</b> from the queue. This operation is inefficient
	 * in MinHeap - based Priority Queues. That's fine for the purposes of our project; you should feel free to
	 * implement your priority queue in any way provides correctness and elementary efficiency of operations.</p>
	 * @return The maximum priority element of the queue, or null if the queue is empty.
	 */
	public T last() {
		if (this.isEmpty()) return null;
		
		PriorityQueue<Element> temp = new PriorityQueue<Element>(new BPQComparator());
		Element target;
		
		while(queue.size() > 1) temp.add(queue.poll());
		target = queue.poll();
		temp.add(target);
		queue = temp;
		return target.val;
	}
	
	/**
	 * Removes the last element.
	 */
	public void removeLast() {
		PriorityQueue<Element> temp = new PriorityQueue<Element>(new BPQComparator());
		
		while(queue.size() > 1) temp.add(queue.poll());
		queue.clear();
		queue = temp;
	}
	
	/**
	 * Like last put returns the element instead.
	 */
	public Element lastElement() {
		PriorityQueue<Element> temp = new PriorityQueue<Element>(new BPQComparator());
		Element target;
		
		while(queue.size() > 1) temp.add(queue.poll());
		target = queue.poll();
		temp.add(target);
		queue = temp;
		return target;
	}

	/**
	 * Query the queue about its size. <b>Empty queues have a size of 0.</b>
	 * @return The size of the queue. Returns 0 if the queue is empty.
	 */
	public int size() {
		return curr_size;
	}

	/**
	 * Query the queue about emptiness. A queue is empty <b>iff</b> it contains <b>0 (zero)</b> elements.
	 * @return true iff the queue contains <b>0 (zero)</b> elements.
	 */
	public boolean isEmpty() {
		return curr_size == 0;
	}

	@Override
	public Iterator<T> iterator() {
		return new BPQIterator();
	}
	
	class BPQIterator implements Iterator<T> {

		int current;
		Iterator<Element> itr;
		PriorityQueue<Element> copy;
		
		public BPQIterator(){
			current = 0;
			copy = new PriorityQueue<>(queue);
			itr = copy.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return itr.hasNext(); 
		}

		@Override
		public T next() {
			return itr.next().val;
		} 
	}
}
