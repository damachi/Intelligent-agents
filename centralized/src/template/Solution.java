package template;

import java.util.LinkedList;
import java.util.List;

/**
 * Encodes a solution as a list of list of tasks. Therefore each vehicle as its tasklist
 * @author francisdamachi
 *
 */
public class Solution {
	
	List<LinkedList<TaskObject>> assignments;
	
	int companyCost;
	
	public Solution(List<LinkedList<TaskObject>> assignments) {
		this.assignments = assignments;
	}
	
	@Override 
	public String toString() {
		return assignments.toString();
	}
	
	

}
