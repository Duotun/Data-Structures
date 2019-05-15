package projects.spatial.nodes;

import projects.spatial.kdpoint.KDPoint;
import projects.spatial.knnutils.BoundedPriorityQueue;
import projects.spatial.knnutils.NNData;

import java.util.Collection;

/**
 * <p>{@link KDTreeNode} is an abstraction over nodes of a KD-Tree. It is used extensively by
 * {@link projects.spatial.trees.KDTree} to implement its functionality.</p>
 *
 * @author   Moweizi Xia 
 *
 * @see projects.spatial.trees.KDTree
 */
public class KDTreeNode {

    private Node root;
    
    public class Node {
    	KDPoint val;
    	Node left;
    	Node right;
    	
    	public Node(KDPoint p) {
    		val = p;
    		left = right = null;
    	}
    }

    /**
     * 1-arg constructor. Stores the provided {@link KDPoint} inside the freshly created node.
     * @param p The {@link KDPoint} to store inside this. Just a reminder: {@link KDPoint}s are
     *          <b>mutable!!!</b>.
     */
    public KDTreeNode(KDPoint p){
        KDPoint point = new KDPoint(p);
        root = new Node(point);
    }

    /**
     * <p>Inserts the provided {@link KDPoint} in the tree rooted at this. To select which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left.</p>
     * @param currDim The current dimension to consider
     * @param dims The total number of dimensions that the space considers.
     * @param pIn The {@link KDPoint} to insert into the node.
     * @see #delete(KDPoint, int, int)
     */
    public void insert(KDPoint pIn, int currDim, int dims){
        insertHelper(pIn, root, currDim, dims);
    }

    private Node insertHelper(KDPoint pIn, Node curr, int currDim, int dims) {
		if (curr == null) 
			curr = new Node(new KDPoint(pIn));
		else if (pIn.coords[currDim] < curr.val.coords[currDim]) 
			curr.left = insertHelper(pIn, curr.left, (currDim + 1) % dims, dims);
		else
			curr.right = insertHelper(pIn, curr.right, (currDim + 1) % dims, dims);
		return curr;
	}

	/**
     * <p>Deletes the provided {@link KDPoint} from the tree rooted at this. To select which subtree to recurse to,
     * the KD-Tree acts as a Binary Search Tree on currDim; it will examine the value of the provided {@link KDPoint}
     * at currDim and determine whether it is larger than or equal to the contained {@link KDPoint}'s relevant dimension
     * value. If so, we recurse right, like a regular BST, otherwise left. There exist two special cases of deletion,
     * depending on whether we are deleting a {@link KDPoint} from a node who either:</p>
     *
     * <ul>
     *      <li>Has a NON-null subtree as a right child.</li>
     *      <li>Has a NULL subtree as a right child.</li>
     * </ul>
     *
     * <p>You should consult the class slides, your notes, and the textbook about what you need to do in those two
     * special cases.</p>
     * @param currDim The current dimension to consider.
     * @param dims The total number of dimensions that the space considers.
     * @param pIn The {@link KDPoint} to insert into the node.
     * @see #insert(KDPoint, int, int)
     * @return A reference to this after the deletion takes place.
     */
    public KDTreeNode delete(KDPoint pIn, int currDim, int dims){
        root = deleteHelper(pIn, root, currDim, dims);
        return this;
    }
    
    private Node deleteHelper(KDPoint pIn, Node curr, int currDim, int dims) {
    	if (curr == null)
    		throw new RuntimeException("Point does not exist!");
    	else if (curr.val.equals(pIn)) {
    		if (curr.right != null) {
    			curr.val = findMin(curr.right, currDim, (currDim + 1) % dims, dims);
    			curr.right = deleteHelper(curr.val, curr.right, (currDim + 1) % dims, dims);
    		}
    		else if (curr.left != null) {
    			curr.val = findMin(curr.left, currDim, (currDim + 1) % dims, dims);
    			curr.right = deleteHelper(curr.val, curr.left, (currDim + 1) % dims, dims);
    			curr.left = null;
    		}
    		else curr = null;
    	}
    	else if (pIn.coords[currDim] < curr.val.coords[currDim])
    		curr.left = deleteHelper(pIn, curr.left, (currDim + 1) % dims, dims);
    	else
    		curr.right = deleteHelper(pIn, curr.right, (currDim + 1) % dims, dims);
    	return curr;
	}

	public KDPoint findMin(Node curr, int i, int currDim, int dims) {
    	if (curr == null) return null;
    	if (currDim == i) {
    		if (curr.left == null) return curr.val;
    		else return findMin(curr.left, i, (currDim + 1) % dims, dims);
    	}
    	else {
    		return minimum (curr.val,
    					findMin(curr.left, i, (currDim + 1) % dims, dims),
    					findMin(curr.right, i, (currDim + 1) % dims, dims), i);
    	}
    }

    private KDPoint minimum(KDPoint val, KDPoint left, KDPoint right, int i) {
    	if (val == null && left == null && right == null) return null;
    	else if (val == null && left == null) return right;
    	else if (val == null && right == null) return left;
    	else if (left == null && right == null) return val;
    	else if (val == null) {
    		if (left.coords[i] <= right.coords[i]) return left;
    		else return right;
    	}
    	else if (left == null) {
    		if (val.coords[i] <= right.coords[i]) return val;
    		else return right;
    	} else if (right == null) {
    		if (left.coords[i] <= val.coords[i]) return left;
    		else return val;
    	} else {
    		if (left.coords[i] <= right.coords[i] && left.coords[i] <= val.coords[i]) return left;
    		else if (val.coords[i] <= right.coords[i] && val.coords[i] <= right.coords[i]) return val;
    		else return right;
    	}
	}

	/**
     * Searches the subtree rooted at the current node for the provided {@link KDPoint}.
     * @param pIn The {@link KDPoint} to search for.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @return true iff pIn was found in the subtree rooted at this, false otherwise.
     */
    public boolean search(KDPoint pIn, int currDim, int dims){
    	return searchHelper(pIn, root, currDim, dims);
    }

    private boolean searchHelper(KDPoint pIn, Node curr, int currDim, int dims) {
		if (curr == null) 
			return false;
		else if (pIn.equals(curr.val))
			return true;
		else if (pIn.coords[currDim] < curr.val.coords[currDim]) 
			return searchHelper(pIn, curr.left, (currDim + 1) % dims, dims);
		else
			return searchHelper(pIn, curr.right, (currDim + 1) % dims, dims);
	}

	/**
     * <p>Executes a range query in the given {@link KDTreeNode}. Given an &quot;anchor&quot; {@link KDPoint},
     * all {@link KDPoint}s that have a {@link KDPoint#distance(KDPoint) distance} of <b>at most</b> range
     * <b>INCLUSIVE</b> from the anchor point <b>except</b> for the anchor itself should be inserted into the {@link Collection}
     * that is passed.</p>
     *
     * <p>Remember: range queries behave <em>greedily</em> as we go down (approaching the anchor as &quot;fast&quot;
     * as our currDim allows and <em>prune subtrees</em> that we <b>don't</b> have to visit as we backtrack. Consult
     * all of our resources if you need a reminder of how these should work.</p>
     * @param anchor The centroid of the hypersphere that the range query implicitly creates.
     * @param results A {@link Collection} that accumulates all the {@link }
     * @param currDim The current dimension examined by the {@link KDTreeNode}.
     * @param dims The total number of dimensions of our {@link KDPoint}s.
     * @param range The <b>INCLUSIVE</b> range from the &quot;anchor&quot; {@link KDPoint}, within which all the
     *              {@link KDPoint}s that satisfy our query will fall. The distance metric used} is defined by
     *              {@link KDPoint#distance(KDPoint)}.
     */
    public void range(KDPoint anchor, Collection<KDPoint> results,
                      double range, int currDim , int dims){
        rangeHelper(root, anchor, results, range, currDim, dims);
    }


    private void rangeHelper(Node curr, KDPoint anchor, Collection<KDPoint> results, double range, int currDim,
			int dims) {
    	if (curr == null) return;
    	else if (Math.sqrt(anchor.distance(curr.val)) <= range && !anchor.equals(curr.val)) results.add(new KDPoint(curr.val));
    	
		if (anchor.coords[currDim] < curr.val.coords[currDim]) {
			rangeHelper(curr.left, anchor, results, range, (currDim + 1) % dims, dims);
			if (Math.abs(anchor.coords[currDim] - curr.val.coords[currDim]) < range)
				rangeHelper(curr.right, anchor, results, range, (currDim + 1) % dims, dims);
		}
		else {
			rangeHelper(curr.right, anchor, results, range, (currDim + 1) % dims, dims);
			if (Math.abs(anchor.coords[currDim] - curr.val.coords[currDim]) < range)
				rangeHelper(curr.left, anchor, results, range, (currDim + 1) % dims, dims);
		}
	}

	/**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#distance(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>best solution</b>, which is passed as
     * an argument. This approach is known in Computer Science as &quot;branch-and-bound&quot; and it helps us solve an
     * otherwise exponential complexity problem (nearest neighbors) efficiently. Remember that when we want to determine
     * if we need to recurse to a different subtree, it is <b>necessary</b> to compare the distance reported by
     * {@link KDPoint#distance(KDPoint)} and coordinate differences! Those are comparable with each other because they
     * are the same data type ({@link Double}).</p>
     *
     * @return An object of type {@link NNData}, which exposes the pair (distance_of_NN_from_anchor, NN),
     * where NN is the nearest {@link KDPoint} to the anchor {@link KDPoint} that we found.
     *
     * @param anchor The &quot;ancor&quot; {@link KDPoint}of the nearest neighbor query.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @param n An object of type {@link NNData}, which will define a nearest neighbor as a pair (distance_of_NN_from_anchor, NN),
     *      * where NN is the nearest neighbor found.
     *
     * @see NNData
     * @see #kNearestNeighbors(int, KDPoint, BoundedPriorityQueue, int, int)
     */
    public  NNData<KDPoint> nearestNeighbor(KDPoint anchor, int currDim,
                                            NNData<KDPoint> n, int dims){
        n = new NNData<KDPoint>(null, Integer.MAX_VALUE);
        
        n = nearestHelper(root, anchor, currDim, n, dims);
        
        return n;
    }

    private NNData<KDPoint> nearestHelper(Node curr, KDPoint anchor, int currDim, NNData<KDPoint> n, int dims) {
    	if (curr == null) return n;
    	else {
    		if (anchor.distance(curr.val) < n.bestDist && !anchor.equals(curr.val))
    			n = new NNData<KDPoint>(new KDPoint(curr.val), anchor.distance(curr.val));
    	}
    	
		if (anchor.coords[currDim] < curr.val.coords[currDim]) {
			n = nearestHelper(curr.left, anchor, (currDim + 1) % dims, n, dims);
			if (Math.abs(anchor.coords[currDim] - curr.val.coords[currDim]) < Math.sqrt(n.bestDist))
				n = nearestHelper(curr.right, anchor, (currDim + 1) % dims, n, dims);
		}
		else {
			n = nearestHelper(curr.right, anchor, (currDim + 1) % dims, n, dims);
			if (Math.abs(anchor.coords[currDim] - curr.val.coords[currDim]) < Math.sqrt(n.bestDist)) 
				n = nearestHelper(curr.left, anchor, (currDim + 1) % dims, n, dims);
		}
		return n;
	}

	/**
     * <p>Executes a nearest neighbor query, which returns the nearest neighbor, in terms of
     * {@link KDPoint#distance(KDPoint)}, from the &quot;anchor&quot; point.</p>
     *
     * <p>Recall that, in the descending phase, a NN query behaves <em>greedily</em>, approaching our
     * &quot;anchor&quot; point as fast as currDim allows. While doing so, it implicitly
     * <b>bounds</b> the acceptable solutions under the current <b>worst solution</b>, which is maintained as the
     * last element of the provided {@link BoundedPriorityQueue}. This is another instance of &quot;branch-and-bound&quot;
     * Remember that when we want to determine if we need to recurse to a different subtree, it is <b>necessary</b>
     * to compare the distance reported by* {@link KDPoint#distance(KDPoint)} and coordinate differences!
     * Those are comparable with each other because they are the same data type ({@link Double}).</p>
     *
     * <p>The main difference of the implementation of this method and the implementation of
     * {@link #nearestNeighbor(KDPoint, int, NNData, int)} is the necessity of using the class
     * {@link BoundedPriorityQueue} effectively. Consult your various resources
     * to understand how you should be using this class.</p>
     *
     * @param k The total number of neighbors to retrieve. It is better if this quantity is an odd number, to
     *          avoid ties in Binary Classification tasks.
     * @param anchor The &quot;anchor&quot; {@link KDPoint} of the nearest neighbor query.
     * @param currDim The current dimension considered.
     * @param dims The total number of dimensions considered.
     * @param queue A {@link BoundedPriorityQueue} that will maintain at most k nearest neighbors of
     *              the anchor point at all times, sorted by distance to the point.
     *
     * @see BoundedPriorityQueue
     */
    public void kNearestNeighbors(int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue, int currDim, int dims){
        queue = kNearestHelper(root, k, anchor, queue, currDim, dims);
    }

    private BoundedPriorityQueue<KDPoint> kNearestHelper(Node curr, int k, KDPoint anchor, BoundedPriorityQueue<KDPoint> queue, int currDim,
			int dims) {
    	if (curr == null) return queue;
    	
    	if (!anchor.equals(curr.val))
    		queue.enqueue(new KDPoint(curr.val), anchor.distance(curr.val));

    	if (anchor.coords[currDim] < curr.val.coords[currDim]) {
    		queue = kNearestHelper(curr.left, k, anchor, queue, (currDim + 1) % dims, dims);
			if (queue.size() == k) {
				if (Math.abs(anchor.coords[currDim] - curr.val.coords[currDim]) < Math.sqrt(queue.lastElement().getPriority()))
					queue = kNearestHelper(curr.right, k, anchor, queue, (currDim + 1) % dims, dims);
			}
			else 
				queue = kNearestHelper(curr.right, k, anchor, queue, (currDim + 1) % dims, dims);
    	}
    	else {
    		queue = kNearestHelper(curr.right, k, anchor, queue, (currDim + 1) % dims, dims);
			if (queue.size() == k) {
				if (Math.abs(anchor.coords[currDim] - curr.val.coords[currDim]) < Math.sqrt(queue.lastElement().getPriority()))
					queue = kNearestHelper(curr.left, k, anchor, queue, (currDim + 1) % dims, dims);
			}
			else 
				queue = kNearestHelper(curr.left, k, anchor, queue, (currDim + 1) % dims, dims);	
    	}
    	
    	return queue;
	}

	/**
     * +
     * Returns the height of the subtree rooted at the current node. Recall our definition of height for binary trees:
     * <ol>
     *     <li>A null tree has a height of -1.</li>
     *     <li>A non-null tree has a height equal to max(height(left_subtree), height(right_subtree)) + 1 </li>
     * </ol>
     * @return the height of the subtree rooted at the current node.
     */
    public int height(){
        return heightHelper(root);
    }


    private int heightHelper(Node curr) {
    	if (curr == null) return -1;
    	return Math.max(heightHelper(curr.left), heightHelper(curr.right)) + 1;
	}

	/**
     * A simple getter for the {@link KDPoint} held by the current node. Remember: {@link KDPoint}s ARE
     * IMMUTABLE, SO WE NEED TO DO DEEP COPIES!!!
     * @return The {@link KDPoint} held inside this.
     */
    public KDPoint getPoint(){
        return new KDPoint(root.val);
    }
}