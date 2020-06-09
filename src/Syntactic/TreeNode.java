package Syntactic;

import Lexer.Token;

import java.util.ArrayList;
import java.util.Collections;

public class TreeNode {

    private Parser.nodeType nodeType;
    private TreeNode parent;
    private ArrayList<TreeNode> children;
    private Token value;

    public TreeNode() {
        children = new ArrayList<>();
    }

    public TreeNode(Parser.nodeType nodeType, Token token, TreeNode parent) {
        this.value = token;
        this.nodeType = nodeType;
        this.parent = parent;
        children = null;
    }

    public TreeNode(Parser.nodeType nodeType) {
        this.nodeType = nodeType;
        children = new ArrayList<>();
        parent = new TreeNode();
    }

    public TreeNode(Parser.nodeType nodeType, TreeNode parent) {
        this.nodeType = nodeType;
        children = new ArrayList<>();
        this.parent = parent;
    }

    public Parser.nodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(Parser.nodeType nodeType) {
        this.nodeType = nodeType;
    }

    public ArrayList<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<TreeNode> children) {
        this.children = children;
    }

    public Token getValue() {
        return value;
    }

    public void setValue(Token value) {
        this.value = value;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public TreeNode getParent() {
        return parent;
    }

    public TreeNode makeLeaf(Parser.nodeType nodeType, Token value, TreeNode parent) {
        return new TreeNode(nodeType, value, parent);
    }

    public void addChild(TreeNode node) {
        children.add(node);
    }

    public TreeNode getLastChild() {
        return children.get(children.size() - 1);
    }

    public void replaceLastChild(TreeNode child) {
        children.remove(children.size() - 1);
        children.add(child);
    }

    public static void printTree(TreeNode tree) {
        if (tree.getNodeType().name().equals(Parser.nodeType.Root.name())) {
            printSubtree(tree);
        } else if (tree.getNodeType().name().equals(Parser.nodeType.leaf.name())) {
            printOrigin(getOrigin(tree));
        } else {
            printSubtree(tree);
        }
    }

    public static void printSubtree(TreeNode tree) {
        for (TreeNode node : tree.getChildren()) printTree(node);
    }

    public static void printOrigin(ArrayList<String> s) {
        for (String str : s) {
            System.out.print("> " + str + " ");
        }
        System.out.println();
    }

    public static ArrayList<String> getOrigin(TreeNode node) {
        ArrayList<String> s = new ArrayList<>();
        s.add("[" + node.getValue().getData() + "]");
        while (node.getParent() != null) {
            s.add(node.getNodeType().name());
            node = node.getParent();
        }
        s.add(node.getNodeType().name());
        Collections.reverse(s);
        return s;
    }
    public boolean isLeaf(){
        return children == null;
    }

}
