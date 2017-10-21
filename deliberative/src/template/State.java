package template;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import logist.plan.Action;
import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;

import logist.topology.Topology.City;

enum Actionss{
	PICKUP, DELIVER, MOVE,NOTHING;
	
	public String toString() {
		
		switch(this) {
		
			case PICKUP:   return "PickUp";
			case DELIVER : return "Deliver";
			case MOVE : 	   return "Move";
			default :      return "no action";
		
		}		
	}
}


public class State {
	static int deliveredTasksssss_ = 0;
	
	int totalTask;
	
	City position;	
	double cost;
	
	City initialCity;
	

	int capacity = 0;
	List<Task>carrying;
	List<Task> availableTask;
	//we want the tasks delivered be visible around all states instantiated
	
	Vehicle vehicle;
	
	Plan plan;
	
	
	// actions that resulted in reaching this state.
    Actionss action;
    
    Task task = null;
    
    
	
	
	
	
	
	public State(int totalTask, Vehicle v,City position, double cost, List<Task> availableTask,List<Task>carrying,Actionss action,Plan plan ) {
		
		this.position = position;
		this.cost = cost;
		this.availableTask = availableTask;
		this.vehicle = v;
		this.carrying = carrying;
		this.action = action;
		this.totalTask = totalTask;
		this.plan = plan;
		
	
	}
	
	
	@Override
	public String toString() {
		
		return "(Position: "+ this.position.toString() + ", Cost: " +cost+ ", Resulting action: " +action.toString() +", Capacity : "+this.capacity+")";
	}
	
	
	public void setCapacity(int cap) {
		this.capacity = cap;
	}
	
	//change uses of capacity to depend on the state
	
	/*
	 * Returns a list of successors based on actions
	 * */
	public List<State> successor(){
		
		List <State> succ = new ArrayList<State>();
	
		//check if we are in goal state first the algorithm BFS will determine if we are in a goal state or not */
		
		//We check the moves we are able to do given that we are in a state*/
        

		//Check  if the deliver action is possible 	
		if(this.carrying.size() > 0) {
			
			/*find if there is a task that the delivery city is and where the agent is */
			boolean couldNotDeliver = true; ;
			int i = 0;
			while( couldNotDeliver && i < carrying.size()) {
				
				Task carry = carrying.get(i);
				
				//check if I can deliver the task that i am carrying 
				if(carry.deliveryCity.id == position.id ) {
					
					City position = carry.deliveryCity;	
					double cost = this.cost;
					
					List<Task> carr = new ArrayList<Task>(this.carrying);
					carr.remove(i);
					//This value is static normally this should exist across all states. We want it global
					
					//TODO Check this link of code it seems wrong 
					deliveredTasksssss_++;
					
					
					// actions that resulted in reaching this state.
				    Actionss action = Actionss.DELIVER;
				    
				
				    //TOD
				    Plan plan = buildNextPlan();
				    plan.appendDelivery(carry);
				    
				    State nextState = new State(totalTask,vehicle,position,cost,availableTask,carr,action,plan);
				    
				    //since we delivered we have more space
				    nextState.capacity = nextState.capacity +carry.weight;
				    succ.add(nextState);
				    nextState.task = carry;
				    nextState.initialCity = this.initialCity;
				    
				  
				    
				    couldNotDeliver = false;
				}
				i++;				
			}		
		}	
		
		/* Check  if the pickup action is possible */
		
		//pick up all tasks on that state
		for (int i = 0 ; i< availableTask.size() ; i++) {
			
			List<Task> avaltasks = new ArrayList<Task>(availableTask);
			Task pickupTask = avaltasks.get(i);
			avaltasks.remove(i);
			
			//we can only pick if pick up city is equals to where we are and also if we have enough capacity
			
			if(pickupTask.pickupCity.id == this.position.id && this.capacity >= pickupTask.weight) {
				
				 City pickupCity = pickupTask.pickupCity;
				 //Create a copy
				 List<Task> carr = new ArrayList<Task>(this.carrying);
				 carr.add(pickupTask);
				 
				 Actionss action = Actionss.PICKUP;			 
				 
				 double cost = this.cost;
				 
				 Plan plan  = buildNextPlan();
				 plan.appendPickup(pickupTask);
				 
				 State nextState = new State(totalTask,vehicle, pickupCity, cost, avaltasks, carr, action,plan);
				//set capacity of vehicle if you pick up task 
				 nextState.capacity = this.capacity - pickupTask.weight;
				 nextState.task = pickupTask;
				 nextState.initialCity = this.initialCity;
				 succ.add(nextState);
					
			}
			
		}
		
		//if we are carrying a task we we can move to deliver the task OR
		//we can either move towards another task where we can pick it up if we have enough space.

		 
		if(carrying.size() > 0) {
			//we move towards the delivery city of the task
			
			for(Task carrying : carrying) {
				
				//TODO CHECK IF SAME CITY as position
				City carry = carrying.deliveryCity;
				
				List<City >cityPathtoAvailableTask = position.pathTo(carry);
				
				if(cityPathtoAvailableTask.size() > 0 ) {
					City nextCityToMove = position.pathTo(carry).get(0);
					
					double cost = this.cost + (position.distanceTo(nextCityToMove))*vehicle.costPerKm();
					Actionss action = Actionss.MOVE;
					
					Plan p = buildNextPlan();
					p.appendMove(nextCityToMove);
					
					State nextState = new State(totalTask,this.vehicle,nextCityToMove,cost,availableTask,this.carrying,action,p);	
					nextState.capacity = this.capacity;
					nextState.initialCity = this.initialCity;
					succ.add(nextState);		
				}
			}
			
		}

			//we move towards another task make attempt to carry another task
			
			for (int i = 0 ; i < availableTask.size();i++ ) {
				
				Task availableTask_ = availableTask.get(i);
				
				//Check if you have enough space in your car
				if(capacity - availableTask_.weight >= 0) {
					
					List<City >cityPathtoAvailableTask = position.pathTo(availableTask_.pickupCity);
		
					if(cityPathtoAvailableTask.size() > 0) {
					    City nextCityAvailableTask = cityPathtoAvailableTask.get(0);			
						
					    double cost = this.cost + (position.distanceTo(nextCityAvailableTask)*vehicle.costPerKm());
					    Actionss action = Actionss.MOVE;
						
					    Plan p = buildNextPlan();
					    p.appendMove(nextCityAvailableTask);
					    
						State nextState = new State(totalTask,vehicle, nextCityAvailableTask, cost, availableTask, carrying, action,p);	
						nextState.capacity = this.capacity;
						nextState.initialCity = this.initialCity;
						succ.add(nextState);
						
				}
				
				
				}
			}
				
		return new ArrayList<State>(new HashSet<State>(succ));	
	
	}
	
	private Plan buildNextPlan() {
		// TODO Auto-generated method stub
		
		 List<Action> actions = new ArrayList<Action>();
	     Iterator<Action> it = this.plan.iterator();
	     
	     /*Iterator through the whole plan*/ 
	     while(it.hasNext()) {	    	 
	    	 	actions.add(it.next());	 
	     }
	     
	     //add the next action to do 
	     
	     Plan plan = new Plan(this.initialCity);
	     
	     for( Action act : actions) {
	    	 	plan.append(act);
	    	 
	     }
	     
	     
	     return plan;
	     
	     
	     
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((availableTask == null) ? 0 : availableTask.hashCode());
		result = prime * result + capacity;
		result = prime * result + ((carrying == null) ? 0 : carrying.hashCode());
		long temp;
		temp = Double.doubleToLongBits(cost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		return result;
	}
	


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (action != other.action)
			return false;
		if (availableTask == null) {
			if (other.availableTask != null)
				return false;
		} else if (!availableTask.equals(other.availableTask))
			return false;
		if (capacity != other.capacity)
			return false;
		if (carrying == null) {
			if (other.carrying != null)
				return false;
		} else if (!carrying.equals(other.carrying))
			return false;
		if (Double.doubleToLongBits(cost) != Double.doubleToLongBits(other.cost))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}



}
	

