enum TreeNodeType {
	
	InnerNode,
	LeafNode
}

/*
 * Abstract class which determines the date structure 
 * used for implementing the B+ Tree 
 * and its members 
 * 
 */

abstract class BPlus_Tree_Node<TKey extends Comparable<TKey>> {
	
	public Object[] keys;
	public int key_cnt;

	public BPlus_Tree_Node<TKey> rightSibling;
	public BPlus_Tree_Node<TKey> leftSibling;
	public BPlus_Tree_Node<TKey> parent_node;
	

	/*
	 * Constructor to initialize a
	 * Node
	 * 
	 */
	
	public BPlus_Tree_Node() {
		
		this.key_cnt = 0;
		this.parent_node = null;
		this.leftSibling = null;
		this.rightSibling = null;
	}

	/*
	 * keeps track of the size of the node
	 */
	public int key_cnt() {
		
		return this.key_cnt;
	}
	
	/*
	 * Returns only the key for the non leaf nodes 
	 */
	
	public TKey getKey(int index) {
		return (TKey)this.keys[index];
	}

	/*
	 * Inserts a key at the index specified 
	 */
	
	public void set_key(int index, TKey key) {
		
		this.keys[index] = key;
	}	

	/*
	 * This member function is used to set the parent of the node 
	 * calling this function
	 */
	
	public void setParent(BPlus_Tree_Node<TKey> parent) {
		
		this.parent_node = parent;
	}	
	
	/*
	 * This returns the parent of the node 
	 * calling this function
	 */
	
	public BPlus_Tree_Node<TKey> getParent() {
		
		return this.parent_node;
	}	
	
	
	/*
	 * Find key at the present node, return the position when found 
	 * else send -1 to leaf node
	 * return index of the child node to the internal node.
	 */
	
	public abstract int search(TKey key);
	
	public abstract TreeNodeType getNodeType();
	
	/* The following code is for insertion */
	
	public boolean isOverflow() {
		
		return this.key_cnt() == this.keys.length;
	}
	
	public BPlus_Tree_Node<TKey> exceed() {
		
		int m = this.key_cnt() / 2;
		
		TKey upKey = this.getKey(m);
		
		BPlus_Tree_Node<TKey> temp = this.split();
				
		if (this.getParent() == null) {
			this.setParent(new BPlus_Tree_Inner_Node<TKey>());
		}
		temp.setParent(this.getParent());
		
		// keeps track of adjacent nodes
		
		temp.set_left_adjacent(this);
		temp.setRightSibling(this.rightSibling);
		
		if (this.get_right_adjacent() != null) {
			this.get_right_adjacent().set_left_adjacent(temp);
		}
		
		this.setRightSibling(temp);
		
		/* 
		 *shift key to parent's node		
		 */
		
		return this.getParent().traverse_above(upKey, this, temp);
	}
	
	/*
	 * This member function splits the node that is overflowed
	 */
	
	public abstract BPlus_Tree_Node<TKey> split();
	
	/*
	 * This method is for traversing up the current node
	 */
	public abstract BPlus_Tree_Node<TKey> traverse_above(TKey key, BPlus_Tree_Node<TKey> leftChild, BPlus_Tree_Node<TKey> rightNode);
	
	

	
	/* 
	 * The following functions are for deleting.
	 * 
	 */
	
	/*
	 * These functions are overridden by the classes 
	 * which performing the delete operation 
	 */
	
	public boolean isUnderflow() {
		return this.key_cnt() < (this.keys.length / 2);
	}
	
	public boolean sub_key() {
		return this.key_cnt() > (this.keys.length / 2);
	}
	
	public BPlus_Tree_Node<TKey> get_left_adjacent() {
		if (this.leftSibling != null && this.leftSibling.getParent() == this.getParent())
			return this.leftSibling;
		return null;
	}

	public void set_left_adjacent(BPlus_Tree_Node<TKey> sibling) {
		this.leftSibling = sibling;
	}

	public BPlus_Tree_Node<TKey> get_right_adjacent() {
		if (this.rightSibling != null && this.rightSibling.getParent() == this.getParent())
			return this.rightSibling;
		return null;
	}

	public void setRightSibling(BPlus_Tree_Node<TKey> silbling) {
		this.rightSibling = silbling;
	}
	
	public BPlus_Tree_Node<TKey> check_null() {
		if (this.getParent() == null)
			return null;
		
		// try to borrow a key from sibling
		BPlus_Tree_Node<TKey> leftSibling = this.get_left_adjacent();
		if (leftSibling != null && leftSibling.sub_key()) {
			this.getParent().shift_child(this, leftSibling, leftSibling.key_cnt() - 1);
			return null;
		}
		
		BPlus_Tree_Node<TKey> rightSibling = this.get_right_adjacent();
		if (rightSibling != null && rightSibling.sub_key()) {
			this.getParent().shift_child(this, rightSibling, 0);
			return null;
		}
		
		/*
		* If no borrow is available then merge with the adjacent node
		*/
		if (leftSibling != null) {
			return this.getParent().merge_child(leftSibling, this);
		}
		else {
			return this.getParent().merge_child(this, rightSibling);
		}
	}
	
	
	public abstract TKey transfer_adjacent(TKey key_1, BPlus_Tree_Node<TKey> sibling, int i);
	
	public abstract BPlus_Tree_Node<TKey> merge_child(BPlus_Tree_Node<TKey> left_child, BPlus_Tree_Node<TKey> right_child);
	
	public abstract void merge_adjacent(TKey key_1, BPlus_Tree_Node<TKey> right_sibling);
	
	public abstract void shift_child(BPlus_Tree_Node<TKey> i, BPlus_Tree_Node<TKey> j, int k);
}