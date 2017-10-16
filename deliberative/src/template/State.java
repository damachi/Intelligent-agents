package template;

import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

public class State {
	
	private City position;	// Current position
	private TaskSet tasksAvailable;	// Available tasks at my position
	private TaskSet tasksToDo;	// Tasks I am currently doing
	private int capacity;	// actuall capacity of the vehicle
	
	
	public State(City pos, TaskSet ta, TaskSet ttd, int cap) {
		this.position = pos;
		this.tasksAvailable = ta;
		this.tasksToDo = ttd;
		this.capacity = cap;
	}
	
	public City getPosition() {
		return this.position;
	}
	
	// Given a task we want to do, tells if we have enough capacity
	public boolean canTakeTask(Task t) {
		int remainingCapacity = this.capacity - t.weight;
		return (remainingCapacity - t.weight) >= 0;
	}
	
	// Given a set of tasks we want to do, tells if we have enough capacity
	public boolean canTakeTasks(TaskSet ts) {
		int remainingCapacity = this.capacity - ts.weightSum();
		return (remainingCapacity - ts.weightSum()) >= 0;
	}
	
	public TaskSet getTaskAvailable() {
		return this.tasksAvailable;
	}
	
	public TaskSet getTaskToDo() {
		return this.tasksToDo;
	}
	
	public int getCap() {
		return this.capacity;
	}

	
	/// ATTENTION: Checker si on peut utiliser la method TaskSet.equals(Object o) !!! Pas sur
	public boolean equal(State s) {
		boolean sameCity = false;
		boolean sameTaskAv = false;
		boolean sameTaskToDo = false;
		boolean sameCap = false;
		
		// Check position
		if(this.position.id == s.position.id) {
			sameCity = true;
		}
		
		if(this.getTaskAvailable().equals(s.getTaskAvailable())) {
			sameTaskAv = true;
		}
		
		if(this.getTaskToDo().equals(s.getTaskToDo())) {
			sameTaskToDo = true;
		}
		if(this.capacity == s.getCap()) {
			sameCap = true;
		}
		
		return (sameCity && sameTaskAv && sameTaskToDo && sameCap);
	}
	

}
