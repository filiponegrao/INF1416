package model;

public class TreeNode {
	
	public String value;
	public TreeNode left = null;
	public TreeNode right = null;
	
	public TreeNode(String value) {
		this.value = value;
	}
	
	public void insertLeafs(String opcoes) {
		
		if (this.right == null && this.left == null) {
			
			this.left = new TreeNode("" + opcoes.charAt(0));
			this.right = new TreeNode("" + opcoes.charAt(2));
			
		} else {
			
			this.left.insertLeafs(opcoes);
			this.right.insertLeafs(opcoes);
		}
	}
}
