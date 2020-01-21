
class BPlus_Tree_Leaf_Node<TKey extends Comparable<TKey>, TValue> extends BPlus_Tree_Node<TKey> {

	private Object[] values;

	final static int LORDER = 4;

	public BPlus_Tree_Leaf_Node() {
		this.keys = new Object[LORDER + 1];
		this.values = new Object[LORDER + 1];
	}

	public TValue get_value(int index) {
		return (TValue) this.values[index];
	}

	public void set_value(int index, TValue value) {
		this.values[index] = value;
	}

	@Override
	public TreeNodeType getNodeType() {
		return TreeNodeType.LeafNode;
	}

	@Override
	public int search(TKey key) {
		for (int k = 0; k < this.key_cnt(); k++) {

			int i = this.getKey(k).compareTo(key);

			if (i == 0) {
				return k;
			} else if (i > 0) {
				return (-1);
			}
		}

		return (-1);
	}

	/*
	 * The following code is for insertion
	 */

	public void insert(TKey key, TValue value) {

		int i = 0;

		while (i < this.key_cnt() && this.getKey(i).compareTo(key) < 0) {
			i = i + 1;
		}

		this.insert_in(i, key, value);
	}

	private void insert_in(int loc, TKey key, TValue val) {

		for (int i = this.key_cnt() - 1; i >= loc; --i) {
			this.set_key(i + 1, this.getKey(i));
			this.set_value(i + 1, this.get_value(i));
		}

		// insert the new Key,Value pair

		this.set_key(loc, key);
		this.set_value(loc, val);
		this.key_cnt = this.key_cnt + 1;
	}

	/*
	 * When splits a leaf node, the middle key is kept on new node and be pushed to
	 * parent node.
	 */

	@Override
	public BPlus_Tree_Node<TKey> split() {
		int j = this.key_cnt() / 2;

		BPlus_Tree_Leaf_Node<TKey, TValue> n = new BPlus_Tree_Leaf_Node<TKey, TValue>();
		for (int i = j; i < this.key_cnt(); i++) {
			n.set_key(i - j, this.getKey(i));
			n.set_value(i - j, this.get_value(i));
			this.set_key(i, null);
			this.set_value(i, null);
		}
		n.key_cnt = this.key_cnt() - j;
		this.key_cnt = j;

		return n;
	}

	/* The codes below are used to support deletion operation */

	public boolean delete(TKey key) {
		int index = this.search(key);
		if (index == -1)
			return false;

		this.remove_at(index);
		return true;
	}

	@Override
	public BPlus_Tree_Node<TKey> traverse_above(TKey key, BPlus_Tree_Node<TKey> leftChild,
			BPlus_Tree_Node<TKey> rightNode) {
		throw new UnsupportedOperationException();
	}

	private void remove_at(int index) {
		int i = index;
		for (i = index; i < this.key_cnt() - 1; i++) {
			this.set_key(i, this.getKey(i + 1));
			this.set_value(i, this.get_value(i + 1));
		}
		this.set_key(i, null);
		this.set_value(i, null);
		--this.key_cnt;
	}

	@Override
	public TKey transfer_adjacent(TKey key_1, BPlus_Tree_Node<TKey> key, int i) {
		BPlus_Tree_Leaf_Node<TKey, TValue> sn = (BPlus_Tree_Leaf_Node<TKey, TValue>) key;

		this.insert(sn.getKey(i), sn.get_value(i));
		sn.remove_at(i);

		if (i == 0) {
			return (getKey(0));
		} else {
			return (this.getKey(0));
		}
	}

	@Override
	public BPlus_Tree_Node<TKey> merge_child(BPlus_Tree_Node<TKey> left_child, BPlus_Tree_Node<TKey> right_child) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void shift_child(BPlus_Tree_Node<TKey> a, BPlus_Tree_Node<TKey> b, int i) {
		throw new UnsupportedOperationException();
	}

	/*
	 * We have to the key of the parent.
	 */
	@Override
	public void merge_adjacent(TKey key_1, BPlus_Tree_Node<TKey> rightSibling) {
		BPlus_Tree_Leaf_Node<TKey, TValue> n = (BPlus_Tree_Leaf_Node<TKey, TValue>) rightSibling;

		int j = this.key_cnt();
		for (int i = 0; i < n.key_cnt(); i++) {
			this.set_key(j + i, n.getKey(i));
			this.set_value(j + i, n.get_value(i));
		}
		this.key_cnt = this.key_cnt + n.key_cnt();

		this.setRightSibling(n.rightSibling);
		if (n.rightSibling != null)
			n.rightSibling.set_left_adjacent(this);
	}

}