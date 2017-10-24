package template;

import java.util.Comparator;


import logist.topology.Topology.City;

public class TaskComparator <T extends City > implements Comparator<City> {
	
	City state;

	public TaskComparator (City state) {
		// TODO Auto-generated constructor stub
		
		this.state = state;
	}
	@Override
	public int compare(City o1, City o2) {
		// TODO Auto-generated method stub
		

		
		double distanceO1 = state.distanceTo(o1);
		double distanceO2 = state.distanceTo(o2);
		return Double.compare(distanceO1, distanceO2);
	}

}
