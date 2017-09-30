/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * @author 
 */

import uchicago.src.sim.space.Object2DGrid;

public class RabbitsGrassSimulationSpace {

private Object2DGrid grassSpace;
private Object2DGrid agentSpace;

private static int max_grass_in_cell = 20;

	public RabbitsGrassSimulationSpace(int xSize, int ySize) {
		this.grassSpace = new Object2DGrid(xSize, ySize);
		this.agentSpace = new Object2DGrid(xSize, ySize);
		
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize ; j++) {
				
				grassSpace.putObjectAt(i, j, new Integer(0));
			
			}
			
		}
	}
	
	public void spreadGrass(int grass ) {
		
		for (int i = 0; i < grass ; i++) {
			
			//Choose coordinates 
		    int  x = (int) (Math.random()* (grassSpace.getSizeX()));
		    int  y = (int) (Math.random()* (grassSpace.getSizeY()));
		    
		    
		   //Get the value of the object at those coordinates 
		    
		    int currentGrass = getGrassAt(x, y);
		    
		    if(currentGrass < max_grass_in_cell) {
		    //Replace the integer object with another on with the new value
		    
		    // this is done in order to increase the amount of grass
		    grassSpace.putObjectAt(x, y, new Integer (currentGrass + 1));
		    }
		}
	}
	
	public boolean isCellOccupied(int x , int y) {
		
		return agentSpace.getObjectAt(x, y) != null;
	}
	
	
	public boolean addAgent(RabbitsGrassSimulationAgent agent) {
		
		boolean retVal = false;
		int count = 0;
		int countLimit = 10 * agentSpace.getSizeX() * agentSpace.getSizeY();
	
		while(!retVal &&(count < countLimit)) {
			
			int x = (int) (Math.random()*(agentSpace.getSizeX()));
			int y = (int) (Math.random()*(agentSpace.getSizeY()));
			
			if(!isCellOccupied(x, y)) {
				
				//space object pointer to agent
				agentSpace.putObjectAt(x, y, agent);
				agent.setXY(x, y);
				agent.setRabbitGrassSpace(this);
				retVal = true;
			}
			
			count++;
		}
		
		return retVal;
	
	}
	
	
	public int getGrassAt(int x , int y ) {
		int i;  
	    //grid is correctly initialized but we still check for null condition.
	    if(grassSpace.getObjectAt(x, y)!= null) {
	    		i = ((Integer)grassSpace.getObjectAt(x, y)).intValue();
	    }else {
	    		i = 0;
	    }
	    
	    return i;
	}
	
	public Object2DGrid getCurrentGrassSpace() {
		return grassSpace;
	}
	
	public Object2DGrid getCurrentAgentSpace() {
		return agentSpace;
	}

	public void removeAgentAt(int x, int y) {
		// TODO Auto-generated method stub
		
		agentSpace.putObjectAt(x, y, null);
		
	}

	public int takeGrassAt(int x, int y) {
		// TODO Auto-generated method stub
	    int  grass = getGrassAt(x, y);
	    grassSpace.putObjectAt(x, y, new Integer(0));
	    
	    return grass;
	}

	public boolean moveAgentAt(int x, int y, int newX, int newY) {
		// TODO Auto-generated method stub
		boolean retVal = false;
		
		if(!isCellOccupied(newX, newY)) {
			RabbitsGrassSimulationAgent rga = (RabbitsGrassSimulationAgent)agentSpace.getObjectAt(x, y);
			removeAgentAt(x, y);
			rga.setXY(newX, newY);
			
			agentSpace.putObjectAt(newX, newY, rga);
			retVal = true;
		}
		
		return retVal;
	}

	public RabbitsGrassSimulationAgent getAgentAt(int x, int y) {
		// TODO Auto-generated method stub
		RabbitsGrassSimulationAgent retVal = null;
		
		if(agentSpace.getObjectAt(x, y) != null) {
			retVal = (RabbitsGrassSimulationAgent) agentSpace.getObjectAt(x, y);
			
		}
		
		return retVal;
	}

	
	/*
	 * Retrieves the total in the agent space
	 */
	public double getTotalGrass() {
		// TODO Auto-generated method stub
		int totalGrass = 0;
		for(int i = 0 ; i < agentSpace.getSizeX();i++) {
			for(int j = 0; j < agentSpace.getSizeY();j++) {
				totalGrass += getGrassAt(i, j);
				
			}
		}
		
		return totalGrass;
	}


	}


