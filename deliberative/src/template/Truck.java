package template;

import logist.task.TaskSet;
import logist.topology.Topology.City;

public class Truck {
	
	private City position;
	private TaskSet toDo;
	private TaskSet available;
	private double cap;
	
	public Truck(City pos, TaskSet t, TaskSet a, double c) {
		this.position = pos;
		this.toDo = t;
		this.available = a;
		this.cap = c;
	}
	
	
	public City getPosition() {
		return this.position;
	}
	
	public void setPosition(City pos) {
		this.position = pos;
	}
	
	public TaskSet getTaskToDo() {
		return this.toDo;
	}
	
	public void setTaskToDo(TaskSet t) {
		this.toDo = t;
	}
	
	public TaskSet getTaskAvailable() {
		return this.available;
	}
	
	public double getCap() {
		return this.cap;
	}

}
