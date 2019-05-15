package projects.spatial.nodes;

import java.util.ArrayList;
import java.util.Collection;
import projects.spatial.kdpoint.KDPoint;
import projects.spatial.trees.PRQuadTree;

/** <p>A {@link PRQuadGrayNode} is a gray (&quot;mixed&quot;) {@link PRQuadNode}. It
 * maintains the following invariants: </p>
 * <ul>
 *      <li>Its children pointer buffer is non-null and has a length of 4.</li>
 *      <li>If there is at least one black node child, the total number of {@link KDPoint}s stored
 *      by <b>all</b> of the children is greater than the bucketing parameter (because if it is equal to it
 *      or smaller, we can prune the node.</li>
 * </ul>
 *
 *  @author  Moweizi Xia 
 */
public class PRQuadGrayNode extends PRQuadNode{

    private PRQuadNode NW, NE, SW, SE;

    /**
     * Creates a {@link PRQuadGrayNode}  with the provided {@link KDPoint} as a centroid;
     * @param centroid A {@link KDPoint} that will act as the centroid of the space spanned by the current
     *                 node.
     * @param k The See {@link PRQuadTree#PRQuadTree(int, int)} for more information on how this parameter works.
     * @param bucketingParam The bucketing parameter fed to this by {@link PRQuadTree}.
     * @see PRQuadTree#PRQuadTree(int, int)
     */
    public PRQuadGrayNode(KDPoint centroid, int k, int bucketingParam){
        super(centroid, k, bucketingParam); // Call to the super class' protected constructor to properly initialize the object!
        NW = NE = SW = SE = null;
    }


	/**
     * <p>Insertion into a {@link PRQuadGrayNode} consists of navigating to the appropriate child
     * and recursively inserting elements into it. If the child is a white node, memory should be allocated for a
     * {@link PRQuadBlackNode} which will contain the provided {@link KDPoint} If it's a {@link PRQuadBlackNode},
     * refer to {@link PRQuadBlackNode#insert(KDPoint, int)} for details on how the insertion is performed. If it's a {@link PRQuadGrayNode},
     * the current method would be called recursively. Polymorphism will allow for the appropriate insert to be called
     * based on the child object's runtime object.</p>
     * @param p A {@link KDPoint} to insert into the subtree rooted at the current {@link PRQuadGrayNode}.
     * @param k The side length of the quadrant spanned by the <b>current</b> {@link PRQuadGrayNode}. It will need to be updated
     *          per recursive call to help guide the input {@link KDPoint}  to the appropriate subtree.
     * @return The subtree rooted at the current node, potentially adjusted after insertion.
     * @see PRQuadBlackNode#insert(KDPoint, int)
     */
    @Override
    public PRQuadNode insert(KDPoint p, int k) {
    	if (p.coords[0] < centroid.coords[0] && p.coords[1] >= centroid.coords[1]) { //NW
    		if (NW == null) NW = new PRQuadBlackNode(new KDPoint(centroid.coords[0] - Math.pow(2, k-2), 
    				centroid.coords[1] + Math.pow(2, k-2)), k-1, bucketingParam, p);
    		else NW = NW.insert(p, k-1);
    	}
    	else if (p.coords[0] >= centroid.coords[0] && p.coords[1] >= centroid.coords[1]) { //NE
    		//System.out.println("in here with " + p);
        	if (NE == null) NE = new PRQuadBlackNode(new KDPoint(centroid.coords[0] + Math.pow(2, k-2), 
    				centroid.coords[1] + Math.pow(2, k-2)), k-1, bucketingParam, p);
    		else NE = NE.insert(p, k-1);
        } 
    	else if (p.coords[0] < centroid.coords[0] && p.coords[1] < centroid.coords[1]) { //SW
        	if (SW == null) SW = new PRQuadBlackNode(new KDPoint(centroid.coords[0] - Math.pow(2, k-2), 
        			centroid.coords[1] - Math.pow(2, k-2)), k-1, bucketingParam, p);
        	else SW = SW.insert(p, k-1);
        }
    	else if (p.coords[0] >= centroid.coords[0] && p.coords[1] < centroid.coords[1]) { //SE
    		//System.out.println("in here with " + p);
        	if (SE == null) SE = new PRQuadBlackNode(new KDPoint(centroid.coords[0] + Math.pow(2, k-2), 
        			centroid.coords[1] - Math.pow(2, k-2)), k-1, bucketingParam, p);
        	else SE = SE.insert(p, k-1);
    	}
    	return this;
    }


    /**
     * <p>Deleting a {@link KDPoint} from a {@link PRQuadGrayNode} consists of recursing to the appropriate
     * {@link PRQuadBlackNode} child to find the provided {@link KDPoint}. If no such child exists, the search has
     * <b>necessarily failed</b>; <b>no changes should then be made to the subtree rooted at the current node!</b></p>
     *
     * <p>Polymorphism will allow for the recursive call to be made into the appropriate delete method.
     * Importantly, after the recursive deletion call, it needs to be determined if the current {@link PRQuadGrayNode}
     * needs to be collapsed into a {@link PRQuadBlackNode}. This can only happen if it has no gray children, and one of the
     * following two conditions are satisfied:</p>
     *
     * <ol>
     *     <li>The deletion left it with a single black child. Then, there is no reason to further subdivide the quadrant,
     *     and we can replace this with a {@link PRQuadBlackNode} that contains the {@link KDPoint}s that the single
     *     black child contains.</li>
     *     <li>After the deletion, the <b>total</b> number of {@link KDPoint}s contained by <b>all</b> the black children
     *     is <b>equal to or smaller than</b> the bucketing parameter. We can then similarly replace this with a
     *     {@link PRQuadBlackNode} over the {@link KDPoint}s contained by the black children.</li>
     *  </ol>
     *
     * @param p A {@link KDPoint} to delete from the tree rooted at the current node.
     * @return The subtree rooted at the current node, potentially adjusted after deletion.
     */
    @Override
    public PRQuadNode delete(KDPoint p) {
    	if (p.coords[0] < centroid.coords[0] && p.coords[1] > centroid.coords[1]) { 
    		NW = NW.delete(p);
    	}
    	else if (p.coords[0] > centroid.coords[0] && p.coords[1] > centroid.coords[1]) {
    		NE = NE.delete(p);
        } 
    	else if (p.coords[0] < centroid.coords[0] && p.coords[1] < centroid.coords[1]) {
    		SW = SW.delete(p);
        }
    	else {
    		SE = SE.delete(p);
    	}
    	
    	int total = 0;
    	
    	if (NW != null) {
    		if (NW.height() != 0) return this;
    		else total += NW.count();
    	}
    	if (NE != null) {
    		if (NE.height() != 0) return this;
    		else total += NE.count();
    	}
    	if (SW != null) {
    		if (SW.height() != 0) return this;
    		else total += SW.count();
    	}
    	if (SE != null) {
    		if (SE.height() != 0) return this;
    		else total += SE.count();
    	}
    	
    	if (total <= bucketingParam) {
    		Collection<KDPoint> childrens = new ArrayList<KDPoint>();
    		if (NW != null) childrens.addAll(((PRQuadBlackNode) NW).getPoints());
    		if (NE != null) childrens.addAll(((PRQuadBlackNode) NE).getPoints());
    		if (SW != null) childrens.addAll(((PRQuadBlackNode) SW).getPoints());
    		if (SE != null) childrens.addAll(((PRQuadBlackNode) SE).getPoints());
    		PRQuadBlackNode temp = new PRQuadBlackNode(centroid, k, bucketingParam, childrens);
    		return temp;
    	}
    	return this;
    }

    @Override
    public boolean search(KDPoint p){
    	if (p.coords[0] < centroid.coords[0] && p.coords[1] > centroid.coords[1]) { 
    		if (NW != null) return NW.search(p);
    		else return false;
    	}
    	else if (p.coords[0] > centroid.coords[0] && p.coords[1] > centroid.coords[1]) {
    		if (NE != null) return NE.search(p);
    		else return false;
        } 
    	else if (p.coords[0] < centroid.coords[0] && p.coords[1] < centroid.coords[1]) {
    		if (SW != null) return SW.search(p);
    		else return false;
        }
    	else {
    		if (SE != null) return SE.search(p);
    		else return false;
    	}
    }

    @Override
    public int height(){
        int NWheight = -1;
        int NEheight = -1; 
        int SWheight = -1;
        int SEheight = -1;
        int height = -1;
        int height2 = -1;
        
        if (NW != null) NWheight = NW.height() + 1;
        if (NE != null) NEheight = NE.height() + 1;
        if (SW != null) SWheight = SW.height() + 1;
        if (SE != null) SEheight = SE.height() + 1;
        
        height = Math.max(NWheight, NEheight); 
        height2 = Math.max(SWheight, SEheight);
        return Math.max(height, height2);
    }

    @Override
    public int count(){
        int total = 0;
        
    	if (NW != null) {
    		if (NW.height() != 0) total += NW.count();
    		else total += NW.count();
    	}
    	if (NE != null) {
    		if (NE.height() != 0) total += NE.count();
    		else total += NE.count();
    	}
    	if (SW != null) {
    		if (SW.height() != 0) total += SW.count();
    		else total += SW.count();
    	}
    	if (SE != null) {
    		if (SE.height() != 0) total += SE.count();
    		else total += SE.count();
    	}
        
        return total;
    }
}
