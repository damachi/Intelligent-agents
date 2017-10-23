package template;

import logist.plan.Action;
import logist.topology.Topology.City;

public class Movement {
	
	private State from;
	private Action how;
	private double cost;
	
	public Movement(State f, Action h, double c) {
		this.from = f;
		this.how = h;
		this.cost = c;
	}
	
	public State getStartingState() {
		return this.from;
	}
	
	public Action getAction() {
		return this.how;
	}
	

}
