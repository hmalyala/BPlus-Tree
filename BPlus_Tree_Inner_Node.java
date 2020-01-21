
/*
 * This class takes care of the insert, delete and
 * search operations of the internal nodes i.e.
 * between root and the leaf nodes
 */

class BPlus_Tree_Inner_Node<TKey extends Comparable<TKey>> extends BPlus_Tree_Node<TKey> {

	public final static int INNERORDER = 4;
	public Object[] children;

	/*
	 * Constructor determining the node structure of the internal nodes
	 */
	public BPlus_Tree_Inner_Node() {
		this.keys = new Object[INNERORDER + 1];
		this.children = new Object[INNERORDER + 2];
	}

	public BPlus_Tree_Node<TKey> getChild(int index) {
		return (BPlus_Tree_Node<TKey>) this.children[index];
	}

	public void setChild(int index, BPlus_Tree_Node<TKey> child) {
		this.children[index] = child;
		if (child != null)
			child.setParent(this);
	}

	/*
	 * The following member functions are being overridden from the class
	 * BPlus_Tree_Node as and when a delete or insert operation takes place
	 */

	@Override
	public TreeNodeType getNodeType() {
		return TreeNodeType.InnerNode;
	}

	@Override
	public int search(TKey key) {
		int index = 0;
		for (index = 0; index < this.key_cnt(); ++index) {
			int cmp = this.getKey(index).compareTo(key);
			if (cmp == 0) {
				return index + 1;
			} else if (cmp > 0) {
				return index;
			}
		}

		return index;
	}

	/*
	 * The methods below are used to support insertion operation
	 * 
	 */

	private void insert_in(int index, TKey key, BPlus_Tree_Node<TKey> leftChild, BPlus_Tree_Node<TKey> rightChild) {
		// move space for the new key
		for (int i = this.key_cnt() + 1; i > index; --i) {
			this.setChild(i, this.getChild(i - 1));
		}
		for (int i = this.key_cnt(); i > index; --i) {
			this.set_key(i, this.getKey(i - 1));
		}

		// insert the new key
		this.set_key(index, key);
		this.setChild(index, leftChild);
		this.setChild(index + 1, rightChild);
		this.key_cnt += 1;
	}

	@Override
	public BPlus_Tree_Node<TKey> traverse_above(TKey key, BPlus_Tree_Node<TKey> leftChild,
			BPlus_Tree_Node<TKey> rightNode) {
		// find the target position of the new key
		int index = this.search(key);

		// insert the new key
		this.insert_in(index, key, leftChild, rightNode);

		// check whether current node need to be split
		if (this.isOverflow()) {
			return this.exceed();
		} else {
			return this.getParent() == null ? this : null;
		}
	}

	/* The codes below are used to support delete operation */

	private void remove_at(int index) {
		int i = 0;
		for (i = index; i < this.key_cnt() - 1; i++) {
			this.set_key(i, this.getKey(i + 1));
			this.setChild(i + 1, this.getChild(i + 2));
		}
		this.set_key(i, null);
		this.setChild(i + 1, null);
		--this.key_cnt;
	}

	/*
	 * In case of split, the middle key is moved to parent
	 */
	@Override
	public BPlus_Tree_Node<TKey> split() {
		int k = this.key_cnt() / 2;

		BPlus_Tree_Inner_Node<TKey> temp = new BPlus_Tree_Inner_Node<TKey>();

		for (int i = k + 1; i < this.key_cnt(); i++) {
			temp.set_key(i - k - 1, this.getKey(i));
			this.set_key(i, null);
		}

		for (int i = k + 1; i <= this.key_cnt(); i++) {
			temp.setChild(i - k - 1, this.getChild(i));
			temp.getChild(i - k - 1).setParent(temp);
			this.setChild(i, null);
		}

		this.set_key(k, null);
		temp.key_cnt = this.key_cnt() - k - 1;
		this.key_cnt = k;

		return temp;
	}

	@Override
	public BPlus_Tree_Node<TKey> merge_child(BPlus_Tree_Node<TKey> left_child, BPlus_Tree_Node<TKey> right_child) {

		int j = 0;
		while (j < this.key_cnt() && this.getChild(j) != left_child)
			j = j + 1;
		TKey key_1 = this.getKey(j);

		// merge two children and the sink key into the left child node
		left_child.merge_adjacent(key_1, right_child);

		// remove the sink key, keep the left child and abandon the right child
		this.remove_at(j);

		// check whether need to propagate borrow or fusion to parent
		if (this.isUnderflow()) {
			if (this.getParent() == null) {
				// current node is root, only remove keys or delete the whole root node
				if (this.key_cnt() == 0) {
					left_child.setParent(null);
					return left_child;
				} else {
					return null;
				}
			}

			return this.check_null();
		}

		return null;
	}

	@Override
	public void shift_child(BPlus_Tree_Node<TKey> temp_1, BPlus_Tree_Node<TKey> temp_2, int loc) {

		int i_count = 0;

		while (i_count < this.key_cnt() + 1 && this.getChild(i_count) != temp_1) {

			i_count = i_count + 1;
		}

		if (loc == 0) {
			// borrow from right node
			TKey upKey = temp_1.transfer_adjacent(this.getKey(i_count), temp_2, loc);
			this.set_key(i_count, upKey);
		} else {
			// borrow from left node
			TKey upKey = temp_1.transfer_adjacent(this.getKey(i_count - 1), temp_2, loc);
			this.set_key(i_count - 1, upKey);
		}
	}

	@Override
	public void merge_adjacent(TKey key_1, BPlus_Tree_Node<TKey> rightSibling) {
		BPlus_Tree_Inner_Node<TKey> rightSiblingNode = (BPlus_Tree_Inner_Node<TKey>) rightSibling;

		int j = this.key_cnt();
		this.set_key(j++, key_1);

		for (int i = 0; i < rightSiblingNode.key_cnt(); ++i) {
			this.set_key(j + i, rightSiblingNode.getKey(i));
		}
		for (int i = 0; i < rightSiblingNode.key_cnt() + 1; ++i) {
			this.setChild(j + i, rightSiblingNode.getChild(i));
		}
		this.key_cnt += 1 + rightSiblingNode.key_cnt();

		this.setRightSibling(rightSiblingNode.rightSibling);
		if (rightSiblingNode.rightSibling != null)
			rightSiblingNode.rightSibling.set_left_adjacent(this);
	}

	@Override
	public TKey transfer_adjacent(TKey key_1, BPlus_Tree_Node<TKey> sibling, int loc) {
		BPlus_Tree_Inner_Node<TKey> siblingNode = (BPlus_Tree_Inner_Node<TKey>) sibling;

		TKey upKey = null;
		if (loc == 0) {
			// borrow the first key from right sibling, append it to tail
			int index = this.key_cnt();
			this.set_key(index, key_1);
			this.setChild(index + 1, siblingNode.getChild(loc));
			this.key_cnt += 1;

			upKey = siblingNode.getKey(0);
			siblingNode.remove_at(loc);
		} else {
			// borrow the last key from left sibling, insert it to head
			this.insert_in(0, key_1, siblingNode.getChild(loc + 1), this.getChild(0));
			upKey = siblingNode.getKey(loc);
			siblingNode.remove_at(loc);
		}

		return upKey;
	}
}