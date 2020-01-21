
public class BPlus_Tree<TKey extends Comparable<TKey>, TValue> {
	
	/*
	 *
	 * We are using two classes to store the internal
	 * and external nodes 
	 * 
	 */
	
	/*
	 * TKey and TValue are the data types of the
	 * key and the value stored in the B+ Tree
	 */
	
	private BPlus_Tree_Node<TKey> root;
	
	public BPlus_Tree() {
		this.root = new BPlus_Tree_Leaf_Node<TKey, TValue>();
	}

	/*
	 * Inserting a new key and its value into the B+ tree.
	 */
	public void insert(TKey key, TValue value) {
		BPlus_Tree_Leaf_Node<TKey, TValue> leaf = this.find_leaf_node_has_key(key);
		leaf.insert(key, value);
		
		/*
		 * Check whether the key has overflowed
		 */
		
		if (leaf.isOverflow()) {
			BPlus_Tree_Node<TKey> n = leaf.exceed();
			if (n != null)
				this.root = n; 
		}
	}
	
	/*
	 * Delete a key,value pair from the B+ Tree
	 */
	
	public void delete(TKey key) {
		BPlus_Tree_Leaf_Node<TKey, TValue> l = this.find_leaf_node_has_key(key);
		
		if (l.delete(key) && l.isUnderflow()) {
			BPlus_Tree_Node<TKey> n = l.check_null();
			if (n != null)
				this.root = n; 
		}
	}
	
	
	
	/*
	 * Search the key,value pairs in the B+ Tree
	 */

	public TValue search(TKey key) {
		BPlus_Tree_Leaf_Node<TKey, TValue> leaf = this.find_leaf_node_has_key(key);
		
		int k = leaf.search(key);
		
		if(k == -1) {
			return (null);
		}
		else {
			return(leaf.get_value(k));
		}		
	}
	
	
	/**
	 * Find the mentioned key in leaf node
	 */
	
	private BPlus_Tree_Leaf_Node<TKey, TValue> find_leaf_node_has_key(TKey key) {
		BPlus_Tree_Node<TKey> node = this.root;
		while (node.getNodeType() == TreeNodeType.InnerNode) {
			node = ((BPlus_Tree_Inner_Node<TKey>)node).getChild( node.search(key) );
		}
		
		return ((BPlus_Tree_Leaf_Node<TKey, TValue>)node);
	}
}