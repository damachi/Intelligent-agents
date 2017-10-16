package template;

import java.util.ArrayList;

import logist.plan.Plan;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology.City;

public class BFS_Plan {
	
	private Plan plan;
	private State initialState;
	private Node rootPlan;
	private Node goalNode;
	
	public BFS_Plan(State s, Node r) {
		this.initialState = s;
		this.rootPlan = r;
		this.goalNode = findGoalNode(this.initialState, this.rootPlan);
		this.plan = findPlan(this.goalNode);
		
	}

	// This method actually implement the BFS algorithm to find the goal Node
	private Node findGoalNode(State initialState, Node root) {
		
		Node goal = null;
		Node pointer = null;		// This is just a pointer Node to go through the tree of the plan
		ArrayList<Node> Q = new ArrayList<Node>();	// The queue with all the Node of the tree
		ArrayList<State> S = new ArrayList<State>();	// This array of state contains the state already visited
		
		// Start with the root node
		Q.add(root);
		while(!Q.isEmpty()) {
			pointer = Q.remove(0);	// Queue => FIFO => we pop the first element of the array, which was the first added in the queue
			
			/*
			 * When we are at a node, we check the possible next nodes. If there isn't any, we have found the goal node.
			 * Otherwise, we go through the list of the possible state from the current node, check if we already saw this state,
			 * if not, we add the new Node in the queue and update the list of visited state
			 * */
			if( nextStates(pointer.getState()) == null) {
				goal = pointer;
			} else {
				for(State s : nextStates(pointer.getState())) {
					if(!check(s, S)) {								// Check if we already visited this state
						Q.add(new Node(s, pointer));
						S.add(s);
					}
				}
			}
		}
		return goal;
	}
	
	// This function find the next possible states from a current node
	private ArrayList<State> nextStates(State s) {
		ArrayList<State> S = new ArrayList<State>();
		ArrayList<City> nextCities = new ArrayList<City>(); // List of next possible cities
		
		/*
		 * Remarks: the only next possible cities for the next states are the cities in the availableTask list or in the
		 * TaskToDo list. The idea is that we will only move to cities where we know there are tasks or where we know that 
		 * we have a task to delivers
		 * */
		
		// If the state have no current Tasks and no possible tasks, then there are no possible next states !
		if(s.getTaskAvailable().isEmpty() && s.getTaskToDo().isEmpty()) {
			S = null;
			return S;
		}
		
		// Now let's look at the task we have to do
		for(Task t : s.getTaskToDo()) {
			// If we are at the right city, we can deliver the task and remove the task from the list of task to do and add
			// a next possible Node in the Q list
			if(t.deliveryCity.id == s.getPosition().id) {
				
				// Take a copy of the task list in order to modify it and create the next state
				TaskSet newT = TaskSet.copyOf(s.getTaskToDo());
				newT.remove(t);
				int newCap = s.getCap() + t.weight; // Because we deliver, the vehicle has more capacity
				
				State nS = new State(s.getPosition(), s.getTaskAvailable(), newT, newCap);
				S.add(nS);
			}
			
			// Update the next possible city list
			nextCities.add(t.deliveryCity);
		}
		
		// Now let's look at the task we can pick up
		for(Task t: s.getTaskAvailable()) {
			// If our position correspond to the city where the task is available
			// and if we have enough capacity to take the task, we can take the task and we have a next possible state
			if( (t.deliveryCity.id == s.getPosition().id) && (s.canTakeTask(t)) ) {
				
				// Update the TaskSets
				TaskSet newToDo = TaskSet.copyOf(s.getTaskToDo());
				TaskSet newAv = TaskSet.copyOf(s.getTaskAvailable());
				
				newAv.remove(t);
				newToDo.add(t);
				int newCap = s.getCap() - t.weight;
				
				State nS = new State(s.getPosition(), newAv, newToDo, newCap);
				S.add(nS);
			}
			
			// Update the next possible city list
			nextCities.add(t.pickupCity);
		}
		
		// Finally, we will go through all the possible next cities and add to S all the possible new states
		for(City c: nextCities) {
			State nS = new State(c, s.getTaskAvailable(),  s.getTaskToDo(), s.getCap());
			S.add(nS);
		}
		
		// Final sanity check, if we added a state in S that is equal to the current state we are looking at, we just remove this
		// state from S
		for(State s_ : S) {
			if(s_.equal(s)) {
				S.remove(s_);
			}
		}
		
		return S;
	}
	
	// This function checks if a given state is in a list of states
	private boolean check(State s, ArrayList<State> S) {
		boolean itsIn = false;
		
		for(State s_ : S) {
			if(s_.equal(s)) {
				itsIn = true;
			}
		}
		return itsIn;
	}
	
	private Plan findPlan(Node goalNode2) {
		// TODO Auto-generated method stub
		return null;
	}

}
