package template;

/* import table */
import logist.simulation.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import logist.agent.Agent;
import logist.behavior.DeliberativeBehavior;
import logist.plan.Plan;
import logist.task.Task;
import logist.plan.Action;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

/**
 * An optimal planner for one vehicle.
 */
@SuppressWarnings("unused")
public class DeliberativeTemplate implements DeliberativeBehavior {

	enum Algorithm { BFS, ASTAR }
	
	/* Environment */
	Topology topology;
	TaskDistribution td;
	TaskSet carryingTask;
	
	List<Task> carryingTaskList = new ArrayList<Task>(); 
	
	boolean notstarted = true;
	int totalTasks;
	
	/* the properties of the agent */
	Agent agent;
	int capacity;

	/* the planning class */
	Algorithm algorithm;
	
	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {
		this.topology = topology;
		this.td = td;
		this.agent = agent;
		
		// initialize the planner
		int capacity = agent.vehicles().get(0).capacity();
		String algorithmName = agent.readProperty("algorithm", String.class, "ASTAR");
		
		// Throws IllegalArgumentException if algorithm is unknown
		algorithm = Algorithm.valueOf(algorithmName.toUpperCase());
		
		
	}
	
	@Override
	public Plan plan(Vehicle vehicle, TaskSet tasks) {
		
		
		
		
		
		
		 

		// Compute the plan with the selected algorithm.
		/*switch (algorithm) {
		case ASTAR:
			// ...
			plan = naivePlan(vehicle, tasks);
			break;
		case BFS:
			// ...
			plan = BFS(vehicle,tasks);
			break;
		default:
			throw new AssertionError("Should not happen.");
		}	
		*/	
		return BFS(vehicle,tasks);
	}
	
	
	private Plan BFS(Vehicle vehicle,TaskSet tasks) {
		
		//SET UP THE TASK LIST AND INITIAL STATE
		
		List<Task> availableTaskList = new ArrayList<Task>();
		
		Iterator<Task> it = tasks.iterator();
		
		while(it.hasNext()) {
			availableTaskList.add(it.next());
		}
			
		if(notstarted) {			
			totalTasks = availableTaskList.size();
			notstarted = false;
		}
		
		Plan plan = new Plan (vehicle.getCurrentCity());
		
		State initialState = new State(totalTasks,vehicle, vehicle.getCurrentCity(), 0, availableTaskList, carryingTaskList, Actionss.NOTHING,plan);	
		initialState.capacity = vehicle.capacity();
		initialState.initialCity = vehicle.getCurrentCity();
			
		//*************BFS********************************//
		List<State> Q = new ArrayList<State>();
			
		Q.add(initialState);

		do {
			
			State n = Q.get(0);
			Q.remove(0);
			//if n is a goal state, return n
			//keep track of explored already
			if(goalState(n)) {
				return n.plan;
			}
						
			List<State> S = n.successor();
			
			for(State s : S) {
				Q.add(s);
			}
			
		}while(Q.size() != 0);
		
		return null;
	
	}
	
	private boolean goalState(State n) {
		// TODO Auto-generated method stub
		
		int numberOfDelivered = n.totalTask;
		int available = n.availableTask.size();
		
		// a goal state is when we don't have anymore more tasks and all tasks are delivered
		return numberOfDelivered == State.deliveredTasksssss_ && available == 0;
		
	}

	private Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
		
		
		//TODO Remember to use vehicle.getCurrentTasks() to get the number of tasks the vehicle is holding 
		
		//System.out.println(
		
		City current = vehicle.getCurrentCity();
		Plan plan = new Plan(current);

		for (Task task : tasks) {
			// move: current city => pickup location
			for (City city : current.pathTo(task.pickupCity))
				plan.appendMove(city);

			plan.appendPickup(task);

			// move: pickup location => delivery location
			for (City city : task.path())
				plan.appendMove(city);

			plan.appendDelivery(task);

			// set current city
			current = task.deliveryCity;
		}
		return plan;
	}

	@Override
	public void planCancelled(TaskSet carriedTasks) {
				
		//System.out.println("Carried tasks" +carriedTasks);
		
		if (!carriedTasks.isEmpty()) {
			// This cannot happen for this simple agent, but typically
			// you will need to consider the carriedTasks when the next
			// plan is computed.
			
			Iterator<Task> it = carriedTasks.iterator();
			
			while(it.hasNext()) {	
				carryingTaskList.add(it.next());
			}		
		}
	}
}
