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
		//return assignments.toString();
		
		return Integer.toString(companyCost);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assignments == null) ? 0 : assignments.hashCode());
		result = prime * result + companyCost;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
	
		if(obj == null) {
			return false;
		}else if(!(obj instanceof Solution)) {
			return false;
		}else {
			Solution that = (Solution)obj;
			
			if(this.companyCost != that.companyCost) {
				return false;
			}else if(this.assignments.size() != that.assignments.size()) {
				return false;
			}else {
				
				for (int i = 0 ; i < assignments.size();i++) {
					if(!equal(this.assignments.get(i),that.assignments.get(i))) {
						return false;
					}
				}
				
				return true;
			}
			
		}
	}

	private boolean equal(LinkedList<TaskObject> linkedList, LinkedList<TaskObject> linkedList2) {
		// TODO Auto-generated method stub
		
		for(int i = 0 ; i< linkedList.size();i++) {
			TaskObject t1= linkedList.get(i);
			TaskObject t2 = linkedList2.get(i);
			
			if(!t1.equals(t2)) {
				return false;
			}
		}
		
		return true;
	}
	
	

}
