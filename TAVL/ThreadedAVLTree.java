package projects.tavl;
import java.util.ConcurrentModificationException;
import java.util.Iterator;     
import projects.tavl.ThreadedAVLTree.Node;

/**<p> {@link ThreadedAVLTree} implements threaded <a href="https://en.wikipedia.org/wiki/AVL_tree">Adelson-Velsky-Landis (AVL) trees</a>
 * (shorthand: TAVL trees). These trees:</p>
 * <ol>
 *      <li> Allow for efficient lookup, insertion and deletion in <em>O(logn)</em> time, by virtue
 *       of being AVL trees.</li>
 *       <li>Perform a full inorder traversal in <em>O(n)</em> time, by virtue of being threaded trees.</li>
 * </ol>
 * <p>Hence, two powerful ideas that we have talked about in lecture will now be combined in one data structure. </p>
 *
 * @author Moweizi Xia
 * @see #inorderTraversal()
 * @see StudentTests
 * @param <T> The {@link java.lang.Comparable} type held by the data structure.
 */
public class ThreadedAVLTree<T extends Comparable<T>> {

    private Node root;
    protected boolean modificationFlag;

    public class Node {
    	private T val;
    	private Node left;
    	private Node right;
    	private int height;
    	private boolean left_thread;
    	private boolean right_thread;
    	
		private Node(T val) {
			this.val = val;
			this.left = null;
			this.right = null;
			this.left_thread = true;
			this.right_thread = true;
			this.height = 0;
		}	
    }

    /**
     * Default constructor. Your code should allow for one, since the unit tests
     * depend on the presence of a default constructor.
     */
    public ThreadedAVLTree(){
    	this.root = null;
    	this.modificationFlag = false;
    }

    public Node getLeft(Node r) {
    	return r.left;
    }
    
    public Node getRight(Node r) {
    	return r.right;
    }
    
    public T getVal(Node r) {
    	return r.val;
    }
    
    public Node getRootNode() {
    	return this.root;
    }
    
    /**
     * Insert <tt>key</tt> in the tree.
     * @param key The key to insert in the tree.
     */
    public void insert(T key){
    	try {    		  		
    		Node temp = new Node(key);
    		if (getHeight(this.root) == -1) {
    			this.root = new Node(key);
    		}
    		else if (getHeight(this.root) == 0) {
    			if (key.compareTo(this.root.val) < 0) {
    				temp.left = this.root.left;
    				temp.right = this.root;
    				this.root.left = temp;
    				this.root.left_thread = false;
    			} else {
    				temp.right = this.root.right;
    				temp.left = this.root;
    				this.root.right = temp;
    				this.root.right_thread = false;
    			}
    			
    			this.root.height = 1;
    			
    		}
    		else if (getHeight(this.root) == 1 && (this.root.left_thread || this.root.right_thread)){
    			if (this.root.left_thread && key.compareTo(this.root.val) < 0) {
        			temp.left = this.root.left;
        			temp.right = this.root;
        			this.root.left = temp;
        			this.root.left_thread = false;
    			} else if (this.root.right_thread && key.compareTo(this.root.val) > 0) {
        			temp.right = this.root.right;
        			temp.left = this.root;
        			this.root.right = temp;
        			this.root.right_thread = false;
    			} else {
    				this.root = insertHelper(key, this.root);	
    			}
    		}
    		else {
    			this.root = insertHelper(key, this.root);	
    		}
    		modificationFlag = true;
    	} catch (RuntimeException e) {
    		System.out.println("Duplicate key");
    	}
    }
    
    public int getHeight(Node t){
    	return (t == null) ? -1 : t.height;
    }
    
    public Node insertHelper(T key, Node curr) {
    	int left_height = -1;
    	int right_height = -1;

    	if (key.compareTo(curr.val) < 0) {
    		if (curr.left_thread) {
    			Node temp = new Node(key);
    			temp.left = curr.left;
    			temp.right = curr;
    			curr.left = temp;
    			curr.left_thread = false;
    			
    	    	if (!curr.right_thread) right_height = getHeight(curr.right);
    	    	else right_height = -1;
    	    	if (!curr.left_thread) left_height = getHeight(curr.left);    	
    	    	else left_height = -1;
    	    	
    			curr.height = right_height < left_height ? left_height + 1 : right_height + 1;
    			
    			return curr;
    		}
    		else curr.left = insertHelper(key, curr.left);
    		
	    	if (!curr.right_thread) right_height = getHeight(curr.right);
	    	else right_height = -1;
	    	if (!curr.left_thread) left_height = getHeight(curr.left);    	
	    	else left_height = -1;
    		
    		if (left_height - right_height == 2) {
    			if(key.compareTo(curr.left.val) < 0)
    				curr = rotateRight(curr);
    			else
    				curr = rotateLR(curr);
    		}
    	} else {
    		if (curr.right_thread) {
    			Node temp = new Node(key);
    			temp.right = curr.right;
    			temp.left = curr;
    			curr.right = temp;
    			curr.right_thread = false;
    	    	   	
    	    	
    	    	if (!curr.right_thread) right_height = getHeight(curr.right);
    	    	else right_height = -1;
    	    	if (!curr.left_thread) left_height = getHeight(curr.left);    	
    	    	else left_height = -1;
    	    	
    			curr.height = right_height < left_height ? left_height + 1 : right_height + 1;

    			return curr;
    		}
    		else {
    			curr.right = insertHelper(key, curr.right);
    		}

	    	if (!curr.right_thread) right_height = getHeight(curr.right);
	    	else right_height = -1;
	    	if (!curr.left_thread) left_height = getHeight(curr.left);    	
	    	else left_height = -1;
	    	
    		if (left_height - right_height == -2) {
    			if (key.compareTo(curr.right.val) > 0) {
    				curr = rotateLeft(curr);
    			}
    			else
    				curr = rotateRL(curr);
    		}
    	}
    	
    	
    	if (!curr.right_thread) right_height = getHeight(curr.right);
    	else right_height = -1;
    	if (!curr.left_thread) left_height = getHeight(curr.left);    	
    	else left_height = -1;
    	
    	curr.height = right_height < left_height ? left_height + 1 : right_height + 1;

    	return curr;
    }
    
    public Node rotateRight(Node r) {
    	Node temp = r.left;
    	int left_height = -1;
    	int right_height = -1;
    	int left_height2 = -1;
    	int right_height2 = -1;
    	
    	if (temp.right_thread) {
    		System.out.println("in here if");
    		temp.right = r;
    		temp.right_thread = false;
    		r.left = temp;
    		r.left_thread = true;
    	}
    	else {
    		r.left = temp.right;
    		temp.right = r;
    	}
    	
    	if (!r.right_thread) right_height = getHeight(r.right);
    	else right_height = -1;
    	if (!r.left_thread) left_height = getHeight(r.left);    	
    	else left_height = -1;
    	
    	if (!temp.right_thread) right_height2 = getHeight(temp.right);
    	else right_height2 = -1;
    	if (!temp.left_thread) left_height2 = getHeight(temp.left);    	
    	else left_height2 = -1;
    	
    	r.height = right_height < left_height ? left_height + 1 : right_height + 1;
    	temp.height = right_height2 < left_height2 ? left_height2 + 1 : right_height2 + 1;
    	System.out.println("temp.val: " + temp.val);
    	return temp;
    }
    
    public Node rotateLeft(Node r) {
    	Node temp = r.right;
    	int left_height = -1;
    	int right_height = -1;
    	int left_height2 = -1;
    	int right_height2 = -1;
    	
    	if (temp.left_thread) {
    		temp.left = r;
    		temp.left_thread = false;
    		r.right = temp;
    		r.right_thread = true;
    	}
    	else { 
    		r.right = temp.left;
    		temp.left = r;
    	}
    	
    	
    	if (!r.right_thread) right_height = getHeight(r.right);
    	else right_height = -1;
    	if (!r.left_thread) left_height = getHeight(r.left);    	
    	else left_height = -1;
    	
    	if (!temp.right_thread) right_height2 = getHeight(temp.right);
    	else right_height2 = -1;
    	if (!temp.left_thread) left_height2 = getHeight(temp.left);    	
    	else left_height2 = -1;
    	
    	r.height = right_height < left_height ? left_height + 1 : right_height + 1;
    	temp.height = right_height2 < left_height2 ? left_height2 + 1 : right_height2 + 1;

    	return temp;
    }
    
    public Node rotateLR(Node r) {
    	r.left = rotateLeft(r.left);
    	r = rotateRight(r);
    	return r;
    }
    
    public Node rotateRL(Node r) {
    	r.right = rotateRight(r.right);
    	r = rotateLeft(r);
    	return r;
    }

    /**
     * Delete the key from the data structure and return it to the caller. Note that it is assumed that there are no
     * duplicate keys in the tree. That is, if a key is deleted from the tree, it should no longer be found in it.
     * @param key The key to deleteRec from the structure.
     * @return The key that was removed, or <tt>null</tt> if the key was not found.
     */
    public T delete(T key){
    	if (this.search(key) == null || this.isEmpty()) return null;
    	
    	if (key.compareTo(this.root.val) == 0 && getHeight(this.root) == 0) {
    		this.root.val = null;
    		this.root = null;
    		modificationFlag = true;
    		return key;
    	}
    	else if (key.compareTo(this.root.val) == 0 && getHeight(this.root) > 0) {
    		Node curr = this.root;
    		if (curr.right_thread && !curr.left_thread) {
    			Node new_root = curr.left;
    			new_root.right = curr.right;
    			curr = null;
    			this.root = new_root;
    			
    			if (balance(this.root) == -2) {
    				if (balance(this.root.right) <= 0) this.root = rotateLeft(this.root);
    				else this.root = rotateRL(this.root);
    			} else if (balance(this.root) == 2) {
    				if (balance(this.root.left) >= 0) this.root = rotateRight(this.root);
    				else this.root = rotateLR(this.root);
    			}
    			
    			this.root.height = updateHeight(this.root);
    		}
    		else if (!curr.right_thread && curr.right.left_thread) {
				Node replace = curr.right; // temp/target node's right child
				replace.left = curr.left;
				replace.left_thread = curr.left_thread;
				
				if (!curr.left_thread) {
					Node pred = curr.left;
					while (!pred.right_thread) {
						pred = pred.right;
					}
				
					pred.right = replace;
				}
				curr = null;
				
				if (balance(replace) == -2) {
					if (balance(replace.right) <= 0) replace = rotateLeft(replace);
					else replace = rotateRL(replace);
				} else if (balance(replace) == 2) {
					if (balance(replace.left) >= 0) replace = rotateRight(replace);
					else replace = rotateLR(replace);
				}
				
				replace.height = updateHeight(replace);
				this.root = replace;
				this.root.height = updateHeight(this.root);
    		}
    		else {
    			System.out.println("in here delete else");
				T min = getMin(curr.right).val;
				Node temp2 = curr.right;
				curr.val = min;
				curr.right = deleteInSucc(temp2, curr);
				
				System.out.println("min: " + min);
				
 				System.out.println(this.root.val);
 				System.out.println(this.root.left.val + " " + this.root.right.val);
 				System.out.println(this.root.left.left.val + " " + this.root.left.right.val);
 				System.out.println(this.root.left.left_thread);
				
				if (balance(curr) == -2) {
					if (balance(curr.right) <= 0) curr = rotateLeft(curr);
					else curr = rotateRL(curr);
				} else if (balance(curr) == 2) {
					if (balance(curr.left) >= 0) curr = rotateRight(curr);
					else curr = rotateLR(curr);
				}
				
				this.root = curr;
				curr.height = updateHeight(curr);
				this.root.height = updateHeight(this.root);
    		}
    		modificationFlag = true;
    		return key;
    	}
    	
    	this.root = deleteHelper(this.root, key);
    	
		if (balance(this.root) == -2) {
			if (balance(this.root.right) <= 0) this.root = rotateLeft(this.root);
			else this.root = rotateRL(this.root);
		} else if (balance(this.root) == 2) {
			if (balance(this.root.left) >= 0) this.root = rotateRight(this.root);
			else this.root = rotateLR(this.root);
		}
		
		this.root.height = updateHeight(this.root);
		modificationFlag = true;
    	return key;
    }
    
    public int updateHeight (Node curr) {
    	int left_height = -1;
    	int right_height = -1;
    	int updated_height = -1;
    	
    	if (!curr.right_thread) right_height = getHeight(curr.right);

    	if (!curr.left_thread) left_height = getHeight(curr.left);    	

    	updated_height = right_height < left_height ? left_height + 1 : right_height + 1;
    	
    	return updated_height;
    }
    
    public int balance (Node curr) {
    	int left_height = -1;
    	int right_height = -1;
    	int balance = 0;
    	
    	if (!curr.right_thread) right_height = getHeight(curr.right);

    	if (!curr.left_thread) left_height = getHeight(curr.left);    	
    	
    	balance = left_height - right_height;
    	
    	return balance;
    }
    
    public void isLoop (T key, Node curr) {
    	if (key.compareTo(curr.val) == 0){
    		if (curr.left != null && key.compareTo(curr.left.val) == 0)
    			System.out.println("INFINITE LEFT THREAD LOOP FOR KEY: " + key + "!!!!!!!!!");
    		else if (curr.right != null && key.compareTo(curr.right.val) == 0)
    			System.out.println("INFINITE RIGHT THREAD LOOP FOR KEY: " + key + "!!!!!!!!!");
    		else {
    			System.out.println("NO LOOP DETECTED for curr.val: " + curr.val + "Left value of: " + curr.left.val + ". And right value of: " + curr.right.val);
    			System.out.println("left thread: " + curr.left_thread + ". right thread: " + curr.right_thread);
    		}
    	}
    	else if (key.compareTo(curr.val) < 0) {
    		 isLoop(key, curr.left);
    	}
    	else
    		 isLoop(key, curr.right);
    }
    
    /* My deleteHelper looks ahead for the target instead of checking if the current node it's on contains the value to be deleted. In hindsight I should've just made it check 
    the current node like I originally had for my first submission. Essentially, if the node to be deleted was the root of the tree, this deleteHelper method would not work.
    However, I account for this redundantly in my delete method as a base case with very similar code and logic to my deleteHelper */

     public Node deleteHelper(Node curr, T key) {

     	System.out.println("currently deleting key: " + key + ". Currently on node value: " + curr.val + "");
     	
     	if (key.compareTo(curr.val) < 0) { // if the key is smaller than current node's value
     		if (key.compareTo(curr.left.val) == 0) { // if the current node's left node contains the key
     			
     			Node temp = curr.left; // temp is the current node's left child containing the key
     			
     			if (temp.left_thread && temp.right_thread) { // case 1: target node is a leaf & has both a left and right thread 
     				//System.out.println("1 In here deleting: " + key );
     				curr.left_thread = true; // since the target node is a leaf node, simply change the pointer to it into a thread 
     				curr.left = temp.left; // point the pointer to the target at the target's left pointer
     				temp = null;
     				curr.height = updateHeight(curr);
     				
     				return curr;
     			}
     			else if (!temp.left_thread && temp.right_thread) { // case 2: target has a left child but a right thread
     				Node pred = temp.left; // predecessor of target node
     				curr.left = temp.left; // since the target has a right thread and a left child, simply point to the target's left child instead
     				
     				//System.out.println("2 In here deleting: " + key );
     				
     				while (!pred.right_thread) { // we need to find the correct predecessor to update the predecessor's thread pointer
     					pred = pred.right;
     				}
     				
     				pred.right = temp.right; // we update the predecessor's right thread so it points to curr instead of the target to be deleted
     				temp = null;
     				curr.height = updateHeight(curr);
     				
     				return curr;
     			}
     			else if (!temp.right_thread && temp.right.left_thread) { // case 3: target has a right child but the right child has a left thread pointing to the target
 				/* in this case, we simply replace the target with it's right child. This is similar to case 4, but the inorder successor of
 				   the target is just it's right child */
     				Node replace = temp.right; // temp/target node's right child
     				replace.left = temp.left; // update the target's right child's left thread so it points to curr instead of the target
     				replace.left_thread = temp.left_thread;
     				curr.left = replace;
     				
     				//System.out.println("In here! The left child of node: " + curr.val + " is " + key);
     				//System.out.println("The target of node: " + temp.val + ", has right node val  " + temp.right.val);
     				//System.out.println("The target of node: " + temp.val + ", has left node val  " + temp.left.val);
     				
     				if (!temp.left_thread) { // again we need to update any threads pointing to the target so it points to it's replacement instead 
     					// this should only happen if the target's left child is NOT a thread
     					Node pred = temp.left;
     				
     					while (!pred.right_thread) {
     						pred = pred.right;
     					}
     				
     					pred.right = replace;
     				}
     				temp = null;

     				// checks for rotations. I should've made this a helper method! Sorry!
     				if (balance(replace) == -2) { // we need to check for inbalance at the replacement node 
     					if (balance(replace.right) <= 0) replace = rotateLeft(replace);
     					else replace = rotateRL(replace);
     				} else if (balance(replace) == 2) {
     					if (balance(replace.left) >= 0)  replace = rotateRight(replace); //System.out.println("rotated in here");}
     					else replace = rotateLR(replace);
     				}

     				curr.left = replace;
     				
     				replace.height = updateHeight(replace);
     				curr.height = updateHeight(curr);
     				return curr;
     			}
     			else { // case 4: target's right child has a left child, so we must find the target's in order successor
     				//System.out.println("4 In here deleteing: " + key );
     				//System.out.println(temp.right.val);
     				
 				/* This is the hardest case, I think my error might be here.
 				   In this case, we need to find the inorder successor of the target and replace 
 				   the target with it before deleting inorder sucessor */

     				T min = getMin(temp.right).val; // this is the target's inorder successor located in the right subtree of the target
     				Node temp2 = temp.right; // temp2 is the right subtree of the target
     				temp.val = min; // we replace the value at the target with the variable min or its inorder successor
     				temp.right = deleteInSucc(temp2, temp); // we delete the inorder sucessor for the right subtree
     				curr.height = updateHeight(curr);
     				return curr;
     			}
     		} else {
     			curr.left = deleteHelper(curr.left, key);
     		}
 			if (balance(curr) == -2) { // check for balance and rotate if necessary after exiting recursion
 				if (balance(curr.right) <= 0) curr = rotateLeft(curr);
 				else curr = rotateRL(curr);
 			} else if (balance(curr) == 2) {
 				if (balance(curr.left) >= 0) curr = rotateRight(curr);
 				else curr = rotateLR(curr);
 			}
 			curr.height = updateHeight(curr);
     	} else if (key.compareTo(curr.val) > 0) { // if the key is greater than current node's value
    		if (key.compareTo(curr.right.val) == 0) { // if the current node's right node contains the key
    			Node temp = curr.right; // temp is the current node's right child containing the key
    			
    			if (temp.left_thread && temp.right_thread) { // case 1: symmetric
    				curr.right_thread = true;
    				curr.right = temp.right;
    				temp = null;
    				curr.height = updateHeight(curr);
    				
    				return curr;
    			}
    			else if (!temp.left_thread && temp.right_thread) { // case 2: symmetric
    				Node pred = temp.left;
    				curr.right = temp.left;
    				
    				while (!pred.right_thread) {
    					pred = pred.right;
    				}
    				
    				pred.right = temp.right;
    				temp = null;
    				curr.height = updateHeight(curr);
    				
    				return curr;
    			}
    			else if (!temp.right_thread && temp.right.left_thread) { // case 3: symmetric 
    				//System.out.println(temp.left.right_thread);
    				Node replace = temp.right; // temp/target node's right child
    				replace.left = temp.left;
    				replace.left_thread = temp.left_thread;
    				curr.right = replace;
    				
    				if (!temp.left_thread) {
    					System.out.println("in pred while");
    					Node pred = temp.left;
    				
    					while (!pred.right_thread) {
    						pred = pred.right;
    					}
    					System.out.println(replace.val);
    					pred.right = replace;
    				}

    				
    				temp = null;
    				//replace.height = updateHeight(replace);
    				
    				if (balance(replace) == -2) {
    					if (balance(replace.right) <= 0) replace = rotateLeft(replace);
    					else replace = rotateRL(replace);
    				} else if (balance(replace) == 2) {
    					if (balance(replace.left) >= 0) replace = rotateRight(replace);
    					else replace = rotateLR(replace);
    				}
    				
    				curr.right = replace;
    				replace.height = updateHeight(replace);
    				curr.height = updateHeight(curr);
    				return curr;
    			}
    			else { // case 4: symmetric
    				T min = getMin(temp.right).val;
    				Node temp2 = temp.right;
    				temp.val = min;
    				temp.right = deleteInSucc(temp2, temp);
    				curr.height = updateHeight(curr);
    				return curr;
    			}
    		} else {
    			curr.right = deleteHelper(curr.right, key);
    		}
			if (balance(curr) == -2) {
				if (balance(curr.right) <= 0) curr = rotateLeft(curr);
				else curr = rotateRL(curr);
			} else if (balance(curr) == 2) {
				if (balance(curr.left) >= 0) curr = rotateRight(curr);
				else curr = rotateLR(curr);
			}
			curr.height = updateHeight(curr);
    	}
    	
    	curr.height = updateHeight(curr);
    	return curr;
	}


    public Node deleteInSucc(Node r, Node root) {
    	//System.out.println("in deleteInSucc");
    	if ((r.left_thread || r.left == null) && !r.right_thread) { // case 1: the inorder successor has a right child, so just simply return the right child and update its thread
		r.right.left = root; // the right child's thread should now point to the root of the tree/subtree
    		r.right.left_thread = true;
    		return r.right; 
	}
    	else if (r.left_thread && r.right_thread) { // case 2: the inorder successor has no right or left child
		/* if this was the case, we need to update the pointer of the parent that points to the inorder sucessor so that it points to the root of the tree/subtree */
    		//System.out.println("r.right has value: " + r.right.val);
    		r.right.left_thread = true; // the parent's left pointer should now be a thread
    		return root; 
    	}
    		r.left = deleteInSucc(r.left, root);
    		r.height = updateHeight(r);
    		
			if (balance(r) == -2) {
				if (balance(r.right) <= 0) r = rotateLeft(r);
				else r = rotateRL(r);
			} else if (balance(r) == 2) {
				if (balance(r.left) >= 0) r = rotateRight(r);
				else r = rotateLR(r);
			}
		r.height = updateHeight(r);
    		return r;
    }
    
    public Node getMin(Node r) {
    	if (r.left_thread || r.left == null) return r;
    	else return getMin(r.left);
    }

    /**
     * Search for <tt>key</tt> in the tree. Return a reference to it if it's in there,
     * or <tt>null</tt> otherwise.
     * @param key The key to look for in the tree.
     * @return <tt>key</tt> if <tt>key</tt> is in the tree, or <tt>null</tt> otherwise.
     */
    public T search(T key){
    	if (this.root == null) return null;
    	else return searchHelper(key, this.root);
    }

    public T searchHelper(T key, Node curr) {
    	if (curr == null) {
    		return null;
    	}
    	else if (key.compareTo(curr.val) == 0){
    		return curr.val;
    	}
    	else if (key.compareTo(curr.val) < 0) {
    		if (curr.left_thread) return null;
    		else return searchHelper(key, curr.left);
    	}
    	else {
    		if (curr.right_thread) return null;
    		else return searchHelper(key, curr.right);
    	}
    }

    /**
     * Return the height of the tree. The height of the tree is defined as the length of the
     * longest path between the root and the leaf level. By definition of path length, a
     * stub tree has a height of 0, and we define an empty tree to have a height of -1.
     * @return The height of the tree.
     */
    public int height(){
    	return (this.root == null) ? -1 : this.root.height;
    }

    /**
     * Query the tree for emptiness. A tree is empty iff it has zero keys stored.
     * @return <tt>true</tt> if the tree is empty, <tt>false</tt> otherwise.
     */
    public boolean isEmpty(){
    	return this.root == null;
    }

    /**
     * Return the key at the tree's root node.
     * @return The key at the tree's root node, or <tt>null</tt> if the tree is empty.
     */
    public T getRoot(){
    	if (isEmpty()) return null;
    	else return this.root.val;
    }

    /**
     * Generate an inorder traversal over the tree's stored keys. This should be done
     * by using the tree's threads, to be able to find every inorder successor in amortized constant
     * time. TO GET MORE THAN 50&#37; CREDIT IN THIS PROJECT, YOU <b>MUST</b> IMPLEMENT YOUR TREE AS A THREADED TREE.
     * IN PARTICULAR, TO GET ANY CREDIT FOR THIS METHOD, YOUR CODE <b>MUST</b> PASS THE RELEVANT UNIT TESTS AND
     * YOU MUST BE MAKING NO CALLS TO ANY STACK, YOURS OR THE SYSTEM'S!
     *
     * @return An {@link Iterator} over <tt>T</tt>s, which exposes the elements in
     * ascending order. If the tree is empty, the {@link Iterator}'s first call to {@link Iterator#hasNext()}
     * will return <tt>false</tt>. The behavior of {@link Iterator#remove()} is <b>undefined</b>; we do <b>not</b> test
     * for removal of elements through the returned {@link Iterator}, so you can implement {@link Iterator#remove()} in
     * <b>any way you please</b>.
     */
    public Iterator<T> inorderTraversal(){
    	
    	return new ThreadedAVLTreeIterator(this.root);
    }
    
    class ThreadedAVLTreeIterator implements Iterator<T> {

    	Node current;
    	
    	public ThreadedAVLTreeIterator(Node root) {
    		while (!root.left_thread)
    			root = root.left;
    		current = root;
    		modificationFlag = false;
    	}
    	
		@Override
		public boolean hasNext() {
			if (current == null) return false;
			else return true;
		}

		@Override
		public T next() {
			if (modificationFlag) throw new ConcurrentModificationException("next(): Attempted to traverse a tree after removal.");
			
			T temp = current.val;
			
			if (current.right_thread)
				current = current.right;
			else {
				current = current.right;
				while (!current.left_thread)
					current = current.left;
			}
				
			return temp;
		}
    	
  }
}