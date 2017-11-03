package template;

//the list of imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
    /*    long time_start = System.currentTimeMillis();
        
//		System.out.println("Agent " + agent.id() + " has tasks " + tasks);
        Plan planVehicle1 = naivePlan(vehicles.get(0), tasks);

        List<Plan> plans = new ArrayList<Plan>();
        plans.add(planVehicle1);
        
        //you have generate a list of plans for all the vehicles in the same order
        while (plans.size() < vehicles.size()) {
            plans.add(Plan.EMPTY);
        }
        
        
        long time_end = System.currentTimeMillis();
        long duration = time_end - time_start;
        System.out.println("The plan was generated in "+duration+" milliseconds.");
        
        return plans;
        
        */
    		Solution A = selectInitialSolution(vehicles, tasks);
    
    		System.out.println(A);
    		
    		//TODO TEST DEEP COPY
    		
    		List<LinkedList<TaskObject>> t = copy(A.assignments);
    		System.out.println(t);
    		t.get(0).get(0).id = 7777;
    		
    		System.out.println(A);
    		System.out.println(t);
    		


    		
  
    		//TODO TEST CHANGING VEHICLES
    		
    		
    	
    		return null;
    }
    
    
    private Solution SLS(List<Vehicle> vehicles, TaskSet tasks){
		
    		int iterations = 0; 
    		
    		Solution A = selectInitialSolution(vehicles, tasks);
    		/*List<VehicleObject> cars = new ArrayList<VehicleObject>();
    		
    		for(Vehicle v : vehicles) {
    			cars.add(new VehicleObject(v));
    		}
    	 		
    		while(iterations  <  iterations_MAX) {
    		  Solution A_old = A;
    		  
    		  List<Solution> N = chooseNeighbours(A_old,cars);
    		  A =  localChoice(N);
    		}
    		
    		*/
    		
    		return A;
    		
    		

    		
    				
    	
    }
    
    
    private List<Solution> chooseNeighbours(Solution a_old, List<VehicleObject> cars) {

    		List<Solution> N = new ArrayList<Solution>();
    		Random random = new Random();
    		int randomCar = random.nextInt(cars.size());
    		
    		List<TaskObject> tasksForVi  = new ArrayList<TaskObject>();
    		
    		do {
    		 	tasksForVi = a_old.assignments.get(randomCar);
    		}while(tasksForVi.size()== 0);
    		
    		//----------Applying the change operator-----------------//
    		for (int i = 0 ; i < cars.size() ; i++) {
    			if(i!=randomCar) {
    				VehicleObject vi = cars.get(randomCar);
    				VehicleObject vj = cars.get(i);
    				
    				Solution  A = changingVehicle(a_old,vi,vj,randomCar,i);
    				if(A!=null) {
    					N.add(A);
    				}
    			}
    		}
    		
    		//---------Applying the changing task order operator------//
    	    int length = a_old.assignments.get(randomCar).size();
    	    
    	    if(length >= 2) {
	    		for (int tIdx1 = 0; tIdx1 < length-1; tIdx1++) {
	    			for(int tIdx2 = tIdx1+1; tIdx2 < length; tIdx2++) {
	    				int vi = randomCar;
	    				Solution A = changingTaskOrder(a_old,vi ,tIdx1,tIdx2);
	    				
	    				if(A != null) {
	    					N.add(A);
	    				}
	    			}  			
	    		}
    	    }
    		return N;
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
	
	 //TODO
	private Solution changingTaskOrder(Solution a_old, int vi, int tIdx1, int tIdx2) {
			
		return null;
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

	private LinkedList<TaskObject> update(LinkedList<TaskObject> taskI) {
		
		int time = 0;
		for(TaskObject task : taskI) {
			task.time = time;
			time++;
		}
		
		return taskI;
	}

	//TESTED and works correctly
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
    private Solution localChoice(List<Solution> n) {

    		return null;
	}

    private Solution selectInitialSolution(List<Vehicle> vehicles, TaskSet t) {
    		//Initialize Vehicle Object list
    		List<VehicleObject> cars = new ArrayList<VehicleObject>();
    		List<TaskObject> totalTasks = new ArrayList<TaskObject>();
    	
    		
    		for(Vehicle v : vehicles) {
    			cars.add(new VehicleObject(v));
    		}
    		
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
    		int car = 0;
    		
    		int time = 0;
    		int numberOfTasks = totalTasks.size();
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
    				
    				//Don't need to update capacity of the car. Since we deliver immediatly
    				//VehicleObject vehicle = cars.get(car);	
    				//cars.set(car, vehicle);
    				
    				i++;
    	
    			}else {
    				
    				car++;
    				//if we don't have anymore cars
    				if(car == vehicles.size()) {
    					return null;
    				}   				
    			}
    		}
    		
    		if(checkConstraints(totalTasks,cars, taskList)) {	
    			return new Solution(taskList);
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
	    		capacity = capacity + task.weight;
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
}
