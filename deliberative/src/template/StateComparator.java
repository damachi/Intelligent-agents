package template;

import java.util.Comparator;


public class StateComparator<T extends State > implements Comparator<State> {

	@Override
	public int compare(State o1, State o2) {
		
	    return Double.compare(o1.heuristicValue,o2.heuristicValue);
		// TODO Auto-generated method stub
		
	}

}
