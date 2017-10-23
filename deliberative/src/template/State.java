package template;

import java.util.ArrayList;
import java.util.List;

import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

public class State {
	
	private City position;	// Current position
	private Truck truck;
	private List<City> neighbors;
	private TaskSet tasksAvailable;	// Available tasks at my position
	private TaskSet tasksToDo;	// Tasks I am currently doing
	
	private ArrayList<Movement> children;
	private ArrayList<State> ancestors;
	
	
	
	public State(Truck truck, TaskSet tasks) {
		
		this.position = truck.getPosition();
		this.truck = truck;
		this.neighbors = position.neighbors();
		this.tasksAvailable = tasks;
		this.tasksToDo = truck.getTaskToDo();
		
	}
	
	public void findNextStates() {
		
	}
	
	// Find the capacity of the truck
	public double findCapacityTruck(Truck truck) {
		double cumulativeWeight = 0;
		for(Task t: truck.getTaskToDo()) {
			cumulativeWeight += t.weight;
		}
		return truck.getCap() - cumulativeWeight;
	}
	
	// According to the task list and the current position of the truck,
	// we check if our city correspond the pickup city for each tasks.
	// If not, we just remove this task from the list
	public TaskSet taskToPickup(City currentCity, TaskSet taskList, Truck truck) {
		TaskSet availableNewTasks = taskList.clone();
		double cap = findCapacityTruck(this.truck);
		
		for(Task t: taskList) {
			if( (currentCity.id != t.pickupCity.id) || (t.weight > cap) ) {
				availableNewTasks.remove(t);
			}
		}
		return availableNewTasks;
	}
	
	// According to the task list and our current position, if the delivery city of the task 
	// is the same as our current position, we can deliver the task. So, if the id's are not the same,
	// we remove the task from the list of task to deliver
	public TaskSet taskToDeliver(City currentCity, TaskSet taskList) {
		TaskSet possibleTaskToDeliver = taskList.clone();
		for(Task t: taskList) {
			if(t.deliveryCity.id != this.position.id) {
				possibleTaskToDeliver.remove(t);
			}
		}
		return possibleTaskToDeliver;
		
	}
	
	public City getPosition() {
		return this.position;
	}
	
	
	public TaskSet getTaskAvailable() {
		return this.tasksAvailable;
	}
	
	public TaskSet getTaskToDo() {
		return this.tasksToDo;
	}

	
	/*/// ATTENTION: Checker si on peut utiliser la method TaskSet.equals(Object o) !!! Pas certain
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
	}*/
	

}
