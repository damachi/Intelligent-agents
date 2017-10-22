package template;

/* import table */
import logist.simulation.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
		return ASTAR(vehicle,tasks);
	}
	
	private Plan ASTAR(Vehicle vehicle, TaskSet tasks) {
		
		List<Task> availableTaskList = new ArrayList<Task>();
		
		System.out.println("Vehicle v : " +vehicle.getCurrentCity());
		System.out.println("Vehicle v : " +vehicle.getCurrentTasks());
		
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
		
		//************ ASTAR*****************//
		
		List<State> Q = new ArrayList<State>();
		List<State> explored = new ArrayList<State>();
			
		Q.add(initialState);
		
		do {
			State n = Q.get(0);
			Q.remove(0);
			if(!explored.contains(n) || betterPath(explored, n) ) {
				if(goalState(n)) {
					return n.plan;
				}
				explored.add(n);
				
				List<State> S = n.successor();
				
				S.sort(new StateComparator<State>());
				
				//S.addAll(Q);			
				//Merge part
				S.sort(new StateComparator<State>());			
				Q.addAll(S);
				
				Q.sort(new StateComparator<State>());
			
			}
			
		}while(!Q.isEmpty());
		
		
		
		return null;
	}
	
	
	private boolean betterPath(List <State> explored,State n) {
		// TODO Auto-generated method stub

	    for(State nPrime : explored) {
	    		if(nPrime.equals(n)) { 
	    			return n.heuristicValue < nPrime.heuristicValue;
			}
		}
	    
	    return false;
	    			  

	}

	private Plan BFS(Vehicle vehicle,TaskSet tasks) {
		
		//SET UP THE TASK LIST AND INITIAL STATE
		
		List<Task> availableTaskList = new ArrayList<Task>();
		
		System.out.println("Vehilcle v : " +vehicle.getCurrentCity());
		System.out.println(tasks);
		
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
		
		List<State> explored = new ArrayList<State>();
			
		Q.add(initialState);

		do {
			
			State n = Q.get(0);
			explored.add(n);
			Q.remove(0);
			
			//if n is a goal state, return n
			//keep track of explored already
			if(goalState(n)) {
				return n.plan;
			}
						
			List<State> S = n.successor();
			
			//(Position: St-Gallen, Cost: 2650.0, Resulting action: Move, Capacity : 27)
			for(State s : S) {
				//we want to prevent to revisit states already explored 
				//TODO perhaps check if the cost is less. for that State s.
				if(toVisit(explored, s)) {
					
					Q.add(s);
				}
			}
			
		}while(Q.size() != 0);
		
		return null;
	
	}
	
	private boolean toVisit(List <State> explored, State s) {
		
		for(State states : explored) {
			
			//we only consider task in which the overall cost is less.
			if(states.equals(s)) {
			   if(states.cost < s.cost) {
				   return false;  
				 }			
			}
		}
		
		return true;
		
	}
	
	private boolean goalState(State n) {
		// TODO Auto-generated method stub
		
		int numberOfDelivered = n.totalTask;
		int available = n.availableTask.size();
		
		// a goal state is when we don't have anymore more tasks and all tasks are delivered
		return n.carrying.size()== 0 && n.availableTask.size() == 0;
		
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
			
			new ArrayList<Task>(new HashSet<Task>(carryingTaskList));
		}
		
	}
}
