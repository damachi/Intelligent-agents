
import uchicago.src.sim.analysis.DataSource;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;

import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.gui.Value2DDisplay;

import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.util.SimUtilities;

import java.awt.Color;
import java.util.ArrayList;
/**
 * Class that implements the simulation model for the rabbits grass
 * simulation.  This is the first class which needs to be setup in
 * order to run Repast simulation. It manages the entire RePast
 * environment and the simulation.
 *
 * @author 
 */


public class RabbitsGrassSimulationModel extends SimModelImpl {	
	
	
	  private static final int NUMAGENTS  = 1;
	  private static final int WORLDXSIZE = 20;
	  private static final int WORLDYSIZE = 20;
	  private static final int TOTALGRASS = 1000;
	  
	  private static final int AGENT_MIN_LIFESPAN = 30;
	  private static final int AGENT_MAX_LIFESPAN = 50;
	  private static final int AGENT_ENERGY_THRESHOLD = 100;
	  
	  private int numAgents  =  NUMAGENTS;
	  private int worldXSize = WORLDXSIZE;
	  private int worldYSize = WORLDYSIZE;
	  private int grass = TOTALGRASS;
	  private int agentMinLifespan = AGENT_MIN_LIFESPAN;
	  private int agentMaxLifespan = AGENT_MAX_LIFESPAN;
	  private int agentEnergyThreshold = AGENT_ENERGY_THRESHOLD;
	  
	  private int livingAgents;
	  private Schedule schedule;
	  
	  private RabbitsGrassSimulationSpace rgSpace;    
	  private ArrayList<RabbitsGrassSimulationAgent> agentList;
	  
	  //create a display surface which are basically windows
	  private DisplaySurface displaySurf;
	  
	  private OpenSequenceGraph amountOfGrassInSpace;
	  private OpenSequenceGraph amountOfRabbitsInSpace;
	  
	  class grassInSpace implements DataSource,Sequence{	

		public double getSValue() {
			// TODO Auto-generated method stub		
			return (double)rgSpace.getTotalGrass();
		}
		
		public Object execute() {
			// TODO Auto-generated method stub
			return new Double(getSValue());
		}
		  
	  }
	  
	  class rabbitsInSpace implements DataSource,Sequence{

		@Override
		public double getSValue() {
			// TODO Auto-generated method stub
			return (double)livingAgents;
		}

		@Override
		public Object execute() {
			// TODO Auto-generated method stub
			return new Double(getSValue());
		}
		  
	  }
	  
	  public String getName(){
	    return "Carry And Drop";
	  }

	  public void setup(){
		  
		  
		  System.out.println("Running setup");		  
		  rgSpace = null;
		  agentList = new ArrayList<>();
		  
		  schedule = new Schedule(1);
		  
		  //Done by tearing the display surface down in the set up method
		  if(displaySurf != null) {
			  displaySurf.dispose();
		  }
		  
		  displaySurf = null;
		  
		  if(amountOfGrassInSpace != null) {
			  amountOfGrassInSpace.dispose();
		  }
		  
		  if(amountOfRabbitsInSpace != null) {
			  amountOfRabbitsInSpace.dispose();
		  }
		  
		  amountOfGrassInSpace = null;
		  amountOfRabbitsInSpace = null;
	
		  
		  // we create the display surface object
		  displaySurf = new DisplaySurface(this, "Rabbit grass Model Window 1");
		  amountOfGrassInSpace = new OpenSequenceGraph("Amount of Grass in Space", this);
		  amountOfRabbitsInSpace = new OpenSequenceGraph("Amount of Rabbit in Space",this);
		  
		  //Register displays 
		  registerDisplaySurface("Rabbit grass Model Winodow 1", displaySurf);
	  	  this.registerMediaProducer("Plot", amountOfGrassInSpace);
	  	  this.registerMediaProducer("Plot", amountOfRabbitsInSpace);
		  
	  }

	  public void begin(){
	    buildModel();
	    buildSchedule();
	    buildDisplay();
	    
	    displaySurf.display();
	    amountOfGrassInSpace.display();
	    amountOfRabbitsInSpace.display();
	  }

	  public void buildModel(){
		  
		  System.out.println("Running BuildModel");
		  rgSpace = new RabbitsGrassSimulationSpace(worldXSize, worldYSize);
		  rgSpace.spreadGrass(grass);
		  
		  for(int i = 0 ; i < numAgents; i++) {
			  addNewAgent();
		  }
		  
		  for(int i = 0 ; i< agentList.size();i++) {
			  agentList.get(i).report();
		  }
	  }

	  private void addNewAgent() {
		// TODO Auto-generated method stub
		  
		  RabbitsGrassSimulationAgent a = new RabbitsGrassSimulationAgent(agentMinLifespan, agentMaxLifespan);
          agentList.add(a);
          rgSpace.addAgent(a);
          
      }
   // if I create an inner class in a method. instantiating has to be done immediatly
	  
	public void buildSchedule(){
		
		System.out.println("Running BuildSchedule");
		
		/*For every time the clock increases, the execute function */
		
		class RabbitGrassStep extends BasicAction {
		      public void execute() {
		        SimUtilities.shuffle(agentList);
		        for(int i = 0; i < agentList.size(); i++){
		          RabbitsGrassSimulationAgent rga = (RabbitsGrassSimulationAgent)agentList.get(i);
		          rga.step();
		        }
		        
		        //returns the number of agents that have died and we respawn them
		        //int deadAgents = 
		        	
		        	reapDeadAgents();
		        	agentsGiveBirth();
		        
		        //updates the display each time execute is called
		        displaySurf.updateDisplay();
		      }
		      
		    
		    private void agentsGiveBirth() {
		    
		    		for (int i = 0; i < agentList.size();i++) {
		    			RabbitsGrassSimulationAgent rga = (RabbitsGrassSimulationAgent)agentList.get(i);
		    			if(rga.getEnergy() >= agentEnergyThreshold) {
		    			   
		    				addNewAgent();
		    				rga.reduceEnergyLevel();
		    			}
		    			
		    		}
		    }

		      // if an agent dies we spread the agents grass around the space 
			private int reapDeadAgents() {
				// TODO Auto-generated method stub
				int count = 0;
				
				for (int i = (agentList.size() -1); i >= 0; i--) {
					
					RabbitsGrassSimulationAgent rga = (RabbitsGrassSimulationAgent)agentList.get(i);
					if(rga.getStepsToLive() < 1) {
						rgSpace.removeAgentAt(rga.getX(),rga.getY());
						
						agentList.remove(i);
						count++;
					}
					
				}
				return count;
			}
		  }
		
		schedule.scheduleActionBeginning(0, new RabbitGrassStep());
		
		class RabbitGrassCountLiving extends BasicAction {
			public void execute() {
				countLivingAgents();
				
				amountOfGrassInSpace.step();
				amountOfRabbitsInSpace.step();
			}

			private int countLivingAgents() {
				// TODO Auto-generated method stub
			    livingAgents = 0;
				for(int i = 0; i < agentList.size();i++) {
					RabbitsGrassSimulationAgent rga = (RabbitsGrassSimulationAgent)agentList.get(i);
					if(rga.getStepsToLive() > 0 ) {
						livingAgents++;
					}
				}
				
		
				System.out.println("Number of living agents is : " + livingAgents);
				
				return livingAgents;
			}
	
	}
		
		schedule.scheduleActionAtInterval(1, new RabbitGrassCountLiving());
		
	
	 class RabbitGrassUpdateGrassInSpace extends BasicAction{

		private int grassRate = 10;

		@Override
		public void execute() {
			// TODO Auto-generated method stub

			
			rgSpace.spreadGrass(grassRate );
		}
		 
	 }
		
		

	
    schedule.scheduleActionAtInterval(20, new RabbitGrassUpdateGrassInSpace());
	
	}

	  public void buildDisplay(){
		  
		  System.out.println("Running BuildDisplay");
		  
		  ColorMap map = new ColorMap();
		  
		  for(int i = 1; i < 16; i ++) {
			  map.mapColor(i, new Color((int)(i * 8 + 127),0,0));
		  }
		  
		  map.mapColor(0, Color.white);
		  
		  Value2DDisplay displayGrass = new Value2DDisplay(rgSpace.getCurrentGrassSpace(),map);
		  Object2DDisplay displayAgents = new Object2DDisplay(rgSpace.getCurrentGrassSpace());
		  
		  displayAgents.setObjectList(agentList);
		
		  
		  displaySurf.addDisplayableProbeable(displayGrass, "Grass");
		  displaySurf.addDisplayableProbeable(displayAgents, "Agents");
		  
		  amountOfGrassInSpace.addSequence("Grass in Space", new grassInSpace());
		  amountOfRabbitsInSpace.addSequence("rabbits in space", new rabbitsInSpace());
	  }

	  public Schedule getSchedule(){
	    return schedule;
	  }

	  public String[] getInitParam(){
	    String[] initParams = { "NumAgents" , "WorldXSize", "WorldYSize","grass","AgentMinLifespan","AgentMaxLifespan","AgentEnergyThreshold"};
	    return initParams;
	  }

	  public int getNumAgents(){
	    return numAgents;
	  }
	  
	  public int getGrass() {
		  return grass;
	  }
	  
	  public void setGrass(int i) {
		  grass = i;
	  }

	  public void setNumAgents(int na){
	    numAgents = na;
	  }

	  public int getWorldXSize(){
	    return worldXSize;
	  }

	  public void setWorldXSize(int wxs){
	    worldXSize = wxs;
	  }

	  public int getWorldYSize(){
	    return worldYSize;
	  }

	  public void setWorldYSize(int wys){
	    worldYSize = wys;
	  }
	  
	  public int getAgentMinLifespan() {
			return agentMinLifespan;
	   }

	   public void setAgentMinLifespan(int agentMinLifespan) {
			this.agentMinLifespan = agentMinLifespan;
	   }
	   
	   public int getAgentMaxLifespan() {
			return agentMaxLifespan;
	   }

	    public void setAgentMaxLifespan(int agentMaxLifespan) {
			this.agentMaxLifespan = agentMaxLifespan;
	    }

	  public static void main(String[] args) {
	    SimInit init = new SimInit();
	    init.loadModel(new RabbitsGrassSimulationModel(), "", false);
	  }

	public int getAgentEnergyThreshold() {
		return agentEnergyThreshold;
	}

	public void setAgentEnergyThreshold(int agentEnergyThreshold) {
		this.agentEnergyThreshold = agentEnergyThreshold;
	}
		
}
