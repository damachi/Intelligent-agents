package template;

//the list of imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import logist.LogistSettings;

import logist.Measures;
import logist.behavior.AuctionBehavior;
import logist.behavior.CentralizedBehavior;
import logist.agent.Agent;
import logist.config.Parsers;
import logist.simulation.Vehicle;
import logist.plan.Plan;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

/**
 * A very simple auction agent that assigns all tasks to its first vehicle and
 * handles them sequentially.
 *
 */
@SuppressWarnings("unused")
public class CentralizedTemplate implements CentralizedBehavior {

    private Topology topology;
    private TaskDistribution distribution;
    private Agent agent;
    private long timeout_setup;
    private long timeout_plan;
	private int SEED = 0;
    private int iterations_MAX = 1000;
    
    @Override
    public void setup(Topology topology, TaskDistribution distribution,
            Agent agent) {
        
        // this code is used to get the timeouts
        LogistSettings ls = null;
        try {
            ls = Parsers.parseSettings("config/settings_default.xml");
        }
        catch (Exception exc) {
            System.out.println("There was a problem loading the configuration file.");
        }
        
        // the setup method cannot last more than timeout_setup milliseconds
        timeout_setup = ls.get(LogistSettings.TimeoutKey.SETUP);
        // the plan method cannot execute more than timeout_plan milliseconds
        timeout_plan = ls.get(LogistSettings.TimeoutKey.PLAN);
        
        this.topology = topology;
        this.distribution = distribution;
        this.agent = agent;
        
    }

    @Override
    public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {

    		//TODO for initial solution if a task doesn't fit in anyof the cars
 
    		//OK TEST CHANGE ORDER 
    		 
    		//OK TEST DEEP COPY
    	
    		//OK TEST hash map
    	
    		//OK TEST define cost

    		//OK implement LocalChoice
    	
    		//OK TEST LocalChoice
    	

    		long time_start = System.currentTimeMillis();
  
    		Solution solution = SLS(vehicles, tasks);
    	   
    		if(solution != null) {
    			List<Plan> plan = new ArrayList<Plan>();
    			
    			for(int i = 0; i<  vehicles.size() ;i++) {
    				Plan builtPlan = buildPlan(solution.assignments.get(i),vehicles.get(i));
    				plan.add(builtPlan);
    			}
    		
	    		long time_end = System.currentTimeMillis();
	    		long duration = time_end - time_start;
	    		System.out.println("The plan was generated in "+duration+" milliseconds.");	
	    		
	    		System.out.println(solution.companyCost);
    		
	    		return plan;
    		}else {
    			return null;
    		}
    }
    
     
    private Plan buildPlan(LinkedList<TaskObject> linkedList, Vehicle vehicle) {
		// TODO Auto-generated method stub
		
    		City currentCity = vehicle.getCurrentCity();
    		Plan plan = new Plan(currentCity);
    		
    		for(int i = 0; i< linkedList.size();i++) {
    			if(linkedList.get(i).action == Action.PICKUP) {
    				//we first move to the pickup city
    				for(City city :currentCity.pathTo(linkedList.get(i).city)) {
    					plan.appendMove(city);
    				}
    				plan.appendPickup(linkedList.get(i).task);
    				currentCity = linkedList.get(i).city;
    				
    			}else {
    				//we move to the delivery city
    				for(City city :currentCity.pathTo(linkedList.get(i).city)) {
    					plan.appendMove(city);
    				}
    				plan.appendDelivery(linkedList.get(i).task);
    				currentCity = linkedList.get(i).city;
    					
    			}
    		}
    		
    		return plan;
	}

	private Solution SLS(List<Vehicle> vehicles, TaskSet tasks){
    	
    		double probability = 0.5;
		
    		int iterations = 0; 
    		
    	
    		
    		Solution A = selectInitialSolution(vehicles, tasks,SEED);

    		List<VehicleObject> cars = new ArrayList<VehicleObject>();
    		for(Vehicle v : vehicles) {
    			cars.add(new VehicleObject(v));
    		}
    	 		
    		while(iterations  <  iterations_MAX) {
    		  Solution A_old = A;
    		  
    		  List<Solution> N = chooseNeighbours(A_old,cars);
    		  A =  localChoice(N,A_old, probability);
    		  System.out.println(A.companyCost);
    		  iterations++;
    		}
    		
    		return A;  	
    }
      
    private List<Solution> chooseNeighbours(Solution a_old, List<VehicleObject> cars) {

    		List<Solution> N = new ArrayList<Solution>();
    		Random random = new Random();
    		int randomCar = random.nextInt(cars.size());
    		
    		List<TaskObject> tasksForVi  = new ArrayList<TaskObject>();
    		
    		do {
    			randomCar = random.nextInt(cars.size());
    		 	tasksForVi = a_old.assignments.get(randomCar);
    		 	
    		}while(tasksForVi.size()== 0);
    		
    		//----------Applying the change operator-----------------//
    		for (int i = 0 ; i < cars.size() ; i++) {
    			if(i!=randomCar) {
    				VehicleObject vi = cars.get(randomCar);
    				VehicleObject vj = cars.get(i);
    				
    				Solution  A = changingVehicle(a_old,vi,vj,randomCar,i);
    				if(A!=null) {
    					A.companyCost = cost(A,cars);
    					N.add(A);
    				}
    			}
    		}
    		
    		//---------Applying the changing task order operator------//
    	    int length = a_old.assignments.get(randomCar).size();
    	    
    	    Map<Integer, List<TaskObject>> map = buildHashMapForVi(a_old.assignments.get(randomCar));
    	    
    	    if(length >= 2) {
	    		for (int tIdx1 = 0; tIdx1 < length-1; tIdx1++) {
	    			for(int tIdx2 = tIdx1+1; tIdx2 < length; tIdx2++) {
	    				int carIndex = randomCar;
	    				Solution A = changingTaskOrder(a_old,carIndex ,tIdx1,tIdx2,cars.get(carIndex).capacity,map);
	    				
	    				if(A != null) {
	    					A.companyCost = cost(A,cars);
	    					N.add(A);
	    				}
	    			}  			
	    		}
    	    }
    		return N;
	}
      
    //TODO TEST
	private Map<Integer, List<TaskObject>> buildHashMapForVi(LinkedList<TaskObject> taskList) {
			
		Map<Integer,List<TaskObject>> taskMap = new HashMap<Integer, List<TaskObject>>();
		
		for(TaskObject task : taskList) {
			List<TaskObject> list = taskMap.getOrDefault(task.id, new ArrayList<TaskObject>());
			list.add(task);
			taskMap.put(task.id, list);			
		}
		return taskMap;
	}

	private Solution changingVehicle(Solution a_old, VehicleObject vi, VehicleObject vj, int i, int j) {
		//we  create a copy
		List<LinkedList<TaskObject>> A = copy(a_old.assignments) ;
		
		LinkedList<TaskObject> carI = A.get(i);
		LinkedList<TaskObject> carj = A.get(j);
	
		//we take out the pickup and delivery task in the carI
		 List<TaskObject> taskInCarI= removePickUpAndDeliverTask(carI);
		
		for (TaskObject task : taskInCarI) {
			carj.addFirst(task);
		}
		
		boolean loadGood = goodCapacityPerVehicle(vj.capacity, carj);
		
	    if(loadGood) {
	    		Solution alteredSolution = updateTime(A,i,j);	    		
	    		return alteredSolution;
	    	  	
	    }else {
	    		return null;
	    }
	}	
	
	private List<LinkedList<TaskObject>> copy(List<LinkedList<TaskObject>> toClone){
		
		List<LinkedList<TaskObject>> list = new ArrayList<LinkedList<TaskObject>>();
		
		for(int i = 0 ; i< toClone.size() ; i++) {
			list.add(copy(toClone.get(i)));
		}	
		return list;
	}
	
	 //TODO TEST
	private Solution changingTaskOrder(Solution a_old, int carIndex, int tIdx1, int tIdx2, int carTotalCapacity,Map<Integer,List<TaskObject>> map) {
		
		//we create a copy because we don't want to alter A_old outside of this method
		List<LinkedList<TaskObject>> tasksList = copy(a_old.assignments);
		
		//we retrieve the tasklist of that specific car
		LinkedList<TaskObject> tasks = tasksList.get(carIndex);
		
		TaskObject t1 = tasks.get(tIdx1);
		TaskObject t2 = tasks.get(tIdx2);
		//we can't only swap if both tasks have different ID's. Meaning they aren't the same
		if(t1.id != t2.id) {
			int time1 = t1.time;
			int time2 = t2.time;
			
			if(check(t1,time2,map) && check(t2,time1,map)) {
				
				Collections.swap(tasks, tIdx1, tIdx2);
			    if(goodCapacityPerVehicle(carTotalCapacity, tasks)) {
			    		//update the times
			    	 	update(tasks);
			    		return new Solution(tasksList);
			    }else {
			    		//TESTED CASE
			    		return null;
			    }			
			}else {
				//TESTED CASE
				return null;
			}
		}else {
			//TESTED CASE
			return null;
		}
	}
	
	private boolean check(TaskObject t1, int time2, Map<Integer,List<TaskObject>> map) {
		
	  if(t1.action == Action.PICKUP) {
		  int deliverTime = map.get(t1.id).get(1).time;
		  return time2 < deliverTime ;

	  }else {
		  //Action.deliver
		  int pickupTime = map.get(t1.id).get(0).time;
		  return pickupTime < time2;	  
	  }
	}

	//TODO TEST 
	private Solution updateTime(List<LinkedList<TaskObject>> a, int i, int j) {
		
		//we create a copy, we don't want to modify A
		List<LinkedList<TaskObject>> tasks = copy(a);
		
		LinkedList<TaskObject> taskI = tasks.get(i);
		LinkedList<TaskObject> taskJ = tasks.get(j);
		
		tasks.set(i, update(taskI));
		tasks.set(j, update(taskJ));
		
		return new Solution(tasks);
	}
	
	private LinkedList<TaskObject> copy(LinkedList<TaskObject> toClone){
		
		LinkedList<TaskObject> cloned = new LinkedList<TaskObject>();
		
		for(TaskObject task : toClone) {
			cloned.add(new TaskObject(task));
		}
		
		return cloned;
		
	}
	//TESTED THOUGHROULY NOT NEED FOR BREAKPOINTS
	private LinkedList<TaskObject> update(LinkedList<TaskObject> taskI) {
		
		int time = 0;
		for(TaskObject task : taskI) {
			task.time = time;
			time++;
		}
		
		return taskI;
	}
	
	private int cost(Solution solution,List<VehicleObject> vehiclesList) {
		
		int totalCost = 0; 
		
		for(int i = 0 ; i < solution.assignments.size();i++) {
			
			totalCost+=computeCost(solution.assignments.get(i), vehiclesList.get(i));
		}
		return totalCost;
	}
	


	/**
	 * @param carI which will be modified in the function. Therefore does a copy creation
	 * @return
	 */
	private List<TaskObject> removePickUpAndDeliverTask(LinkedList<TaskObject> carI) {
		
	  assert(carI!=null);
	  
	  TaskObject task = carI.getFirst();
	  int id = task.id;
	  
	  List<TaskObject> taskRemoved = new ArrayList<TaskObject>();
	  
	  Iterator<TaskObject> it = carI.iterator();
	  //we always move both pickup and delivery
	  int removed = 2;
	  while(it.hasNext()) {
		 
		  TaskObject taskToRemove = it.next();
		  if(taskToRemove.id == id) {
			  it.remove();
			  taskRemoved.add(taskToRemove);
			  removed--;
			  if(removed == 0) {
				  Collections.reverse(taskRemoved);
				  return taskRemoved;
				  
			  }
		  }
	  }
	  return null; 
	}

    //TODO
    private Solution localChoice(List<Solution> n,Solution a_old, double probablity ) {
    		//we sort the list of solutions based on there cost
    		
    		Collections.sort(n, new Comparator<Solution>() {
    				@Override
				public int compare(Solution o1, Solution o2) {

					return Integer.compare(o1.companyCost, o2.companyCost);
				}
			});
    		int minCost = n.get(0).companyCost;
    		List<Solution> filtered = filter(minCost,n);
    		
    		Random random = new Random();
    		int randomSolution = random.nextInt(filtered.size());
    		
    		Solution solution = filtered.get(randomSolution);
    		
    		double proba = random.nextDouble();
    		
    		if(probablity >= proba) {
    			return solution;
    		}else {
    			return a_old;
    		}
    		
	}
    
    private List<Solution> filter(int mininumCost, List<Solution> s){
    		List<Solution> filteredSolution = new ArrayList<Solution>();
    		int i = 0;
    		int minimumCost = s.get(i).companyCost;
    		  		
    		while((i < s.size()) &&minimumCost == s.get(i).companyCost) {
    			filteredSolution.add(s.get(i));
    			i++;
    		}
    		
    		
    		return filteredSolution;
    	
    }

    private Solution selectInitialSolution(List<Vehicle> vehicles, TaskSet ti,int seed) {
    		//Initialize Vehicle Object list
    		List<VehicleObject> cars = new ArrayList<VehicleObject>();
    		List<TaskObject> totalTasks = new ArrayList<TaskObject>();
    	
    		List<Task> t = new ArrayList<Task>();
    		
    		for(Vehicle v : vehicles) {
    			cars.add(new VehicleObject(v));
    		}
    		t.addAll(ti);
    		Collections.shuffle(t, new Random(seed));
    		
    		for(Task task : t) {
    			totalTasks.add(new TaskObject(task, Action.PICKUP));
    			totalTasks.add(new TaskObject(task, Action.DELIVER));
    		}
  		
    		//each vehicle has task list
    		List<LinkedList<TaskObject>> taskList = new ArrayList<LinkedList<TaskObject>>();
    		
    		for(int i = 0; i< vehicles.size();i++) {
    			taskList.add(new LinkedList<TaskObject>());
    		}
    		

    		
    		int i = 0;
    		Random rand = new Random();
    		
    		int car = 0;
    		//int car = rand.nextInt(cars.size());
    		int time = 0;
    		int numberOfTasks = totalTasks.size();
    		int carsFull = 0;
    		while(i < numberOfTasks) {
    			//it means we have space
    			if(totalTasks.get(i).task.weight <= cars.get(car).capacity) {
    				
    				TaskObject taskPickup = totalTasks.get(i);
    				i++;
    				TaskObject taskDeliver = totalTasks.get(i);
    				
    				//We set the times of the task  
    				taskPickup.time = time;
    				time++;
    				taskDeliver.time = time;
    				time++;
    				
    				taskPickup.assigned = true;
    				taskDeliver.assigned = true;
    				
    				taskList.get(car).add(taskPickup);
    				taskList.get(car).add(taskDeliver);

    				i++;
    			//	car = rand.nextInt(cars.size());
    				//We just distribute the task equally amongst the cars*/
    				if(car==cars.size()-1) {
    					car = 0;
    				}else {
    					car++;
    				}
    				carsFull = 0;
    			}else {
    				car++;
    				carsFull++;
    				//if we don't have anymore cars
    				if(carsFull == vehicles.size()) {
    					return null;
    				}   				
    			}
    		}
    		
    		if(checkConstraints(totalTasks,cars, taskList)) {	
    			Solution solution = new Solution(taskList);
    			solution.companyCost = cost(solution, cars);
    			
    			return solution;
    		}else {
    			throw new IllegalArgumentException();
    		}		
	}

	/**
	 * It must check the constraints
	 * @param totalTasks number of tasks
	 * @param vehicles  all the vehicles
	 * @param tasks  a list of list of tasks. Basically each vehicle has a task list
	 * @return true if we assigned all tasks, and capacity of per vehicle
	 */
	private boolean checkConstraints(List<TaskObject> totalTasks, List<VehicleObject> vehicles, List<LinkedList<TaskObject>> tasks) {
		
	  //we check if all tasks are assigned	   	
	   for (TaskObject t : totalTasks) {
		   if(!t.assigned) {
			   return false;
		   }
	   }
	    
	   //we check the capacity of our plan
	   for(int i = 0 ; i < tasks.size();i++) {
		   int capacity = vehicles.get(i).capacity;
		   if(!goodCapacityPerVehicle(capacity,tasks.get(i))) {
			   return false;
		   }	   
	   }
		return true;
	}
	
	/**
	 * @param totalTasks the total amount of tasks
	 * @return true if all tasks are assigned else returns false
	 */
	private boolean allAssigned(List<TaskObject> totalTasks) {
		for (TaskObject t : totalTasks) {
			   if(!t.assigned) {
				   return false;
			   }
		   }
		return true;
	}
	
	/**
	 * @param totalCapacityOfVehicle the capacity of the vehicle without carrying anything
	 * @param tasks the tasks of the certain vehicle 
	 * @return true if the car is able to execute tasks in order based on the weight
	 */
	private boolean goodCapacityPerVehicle(int totalCapacityOfVehicle, LinkedList<TaskObject> tasks) {
		
	    int capacity = totalCapacityOfVehicle;
	    for (TaskObject task : tasks) {
	    		capacity = capacity - task.weight;
	    		if(capacity < 0) {
	    			return false;
	    		}
	    }
		return true;
	}

	private Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
        City current = vehicle.getCurrentCity();
        Plan plan = new Plan(current);

        for (Task task : tasks) {
            // move: current city => pickup location
        	    System.out.println(task.id);
            for (City city : current.pathTo(task.pickupCity)) {
                plan.appendMove(city);
            }

            plan.appendPickup(task);

            // move: pickup location => delivery location
            for (City city : task.path()) {
                plan.appendMove(city);
            }

            plan.appendDelivery(task);

            // set current city
            current = task.deliveryCity;
        }
        return plan;
    }
	
	//the cost is based on the distance traversed
	private int computeCost(LinkedList<TaskObject> taskList, VehicleObject vehicleObject) {	
		
		City currentCity = vehicleObject.vehicle.getCurrentCity();
		
		int totalCost = 0;
		for(int i = 0 ; i< taskList.size()-1 ; i++) {
			//shortest cost from going from a point to a point
			City c1 = taskList.get(i).city;
			City c2 = taskList.get(i+1).city;
			totalCost += (c1.distanceTo(c2)) * vehicleObject.vehicle.costPerKm();	
		}
		//cost of reaching the first pickup¨
		if(!taskList.isEmpty()) {
			totalCost +=currentCity.distanceTo(taskList.get(0).city) * vehicleObject.vehicle.costPerKm();
		}
		
		return totalCost;	
	}
}
