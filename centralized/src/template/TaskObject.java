package template;

import logist.task.Task;



public class TaskObject {

	Task task;
	int id;
	Action action;
	int time;
	int weight;
	
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
		}else {
			weight = -task.weight;
		}
	}
	//Copy constructor
	public TaskObject(TaskObject task2) {
		
		this.task = task2.task;
		this.id = task2.id;
		this.action = task2.action;
		this.time  = task2.time;
		this.weight = task2.weight;
				
		
	}
	@Override
	public String toString() {
		return "(TaskId: " +id+ " Action :" +action+")"; 
	}
	
   	
}
