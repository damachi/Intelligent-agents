package template;

import java.util.ArrayList;

public class Node {
	
	private State actualState;
	private Node parent;		// previous node in the plan
	private ArrayList<Node> children;	// All the possible next nodes in the plan
	private double cost;		// Cost of the plan up to this node
	
	// Beginning of the plan, we have no cost
	public Node(State s, Node p) {
		this.actualState = s;
		this.parent = p;
		this.cost = Double.MIN_VALUE;
	}
	
	// This is not the root node of the plan so we know the cost up to this node
	public Node(State s, Node p, double c) {
		this.actualState = s;
		this.parent = p;
		this.cost = c;
	}
	
	public void setChildren(ArrayList<Node> c) {
		this.children = c;
	}
	
	public State getState() {
		return this.actualState;
	}

}
