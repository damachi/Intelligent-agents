package template;

import logist.task.Task;
import logist.topology.Topology.City;



public class TaskObject {

	Task task;
	int id;
	Action action;
	int time;
	int weight;
	City city;
	boolean assigned = false;
	
	
	/**
	 * @param task : the task object
	 * @param action : the action that you have to perform on this task
	 * @param time:  the time it will be executed 
	 */
	public TaskObject(Task task ,Action action){
		
		this.task = task;
		this.id = task.id;
		this.action = action;
		if(action==Action.PICKUP) {
			weight = task.weight;
			city = task.pickupCity;
		}else {
			weight = -task.weight;
			city = task.deliveryCity;
		}
	}
	//Copy constructor
	public TaskObject(TaskObject task2) {
		
		this.task = task2.task;
		this.id = task2.id;
		this.action = task2.action;
		this.time  = task2.time;
		this.weight = task2.weight;
		this.city = task2.city;
		this.assigned = task2.assigned;
				
		
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + (assigned ? 1231 : 1237);
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + id;
		result = prime * result + time;
		result = prime * result + weight;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TaskObject))
			return false;
		TaskObject other = (TaskObject) obj;
		if (action != other.action)
			return false;
		if (assigned != other.assigned)
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (id != other.id)
			return false;
		if (time != other.time)
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "(TaskId: " +id+ " Action :" +action+")"; 
	}
	
   	
}
