import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;

import java.awt.Color;

/**
 * Class that implements the simulation agent for the rabbits grass simulation.

 * @author
 */

public class RabbitsGrassSimulationAgent implements Drawable {
	private int x;
	private int y;
	
	private int vX;
	private int vY;
	
	private int grass;
    private int energy;
 
    private int ID;
	private RabbitsGrassSimulationSpace rgSpace;
	
    private static int IDNumber = 0;	
	public RabbitsGrassSimulationAgent(int minLifespan, int maxLifespan) {
		
		x = -1;
		y = -1;
		grass = 0;
		setVxVy();
		
		// we want the steps to live to be a value between [0-minLifespan]
		energy = (int)((Math.random()*(maxLifespan - minLifespan))+ minLifespan); 		
		IDNumber++;
		ID = IDNumber;
	}
	
	public int  getEnergy () {
		return energy;
	}
	
	private void setVxVy() {
		// TODO Auto-generated method stub
		vX = 0;
		vY = 0;
		
		/* We want these moves N,W,S,E*/
		
		do {
			
			vX = (int) Math.floor(Math.random() * 3 ) -1;
			vY = (int) Math.floor(Math.random() * 3 ) -1;
			
		}while(((vX!=0)  && (vY!= 0 )) || (vX == vY));
		// this assures that they can't move in diagonal 
		
	}

	public void setXY(int newX, int newY) {
		x = newX;
		y = newY;
	}
	
	public void setRabbitGrassSpace(RabbitsGrassSimulationSpace rgs) {
		rgSpace = rgs;
	}
	
	public String getID() {
		return "A-" + ID;
	}

	public void draw(SimGraphics arg0) {
		// TODO Auto-generated method stub
		
		if(energy > 10 ) {
			arg0.drawFastRoundRect(Color.green);
		}else  {
			arg0.drawFastRoundRect(Color.blue);
		}
		
	}
		
	public void step() {
		
		setVxVy();
			
		int newX = x + vX;
		int newY = y + vY;
		
		Object2DGrid grid = rgSpace.getCurrentAgentSpace();
		
		newX = (newX + grid.getSizeX()) % grid.getSizeX();
		newY = (newY + grid.getSizeY()) % grid.getSizeY();
		
		System.out.println("print vX :" +  vX + "print vY :" +vY);
		
		//we check if the agent isn't dead
	
		
		if(tryMove(newX,newY)) {
			
			this.grass += rgSpace.takeGrassAt(x, y);
			this.energy+=this.grass;
		
		}else {
			/*We check for collision between to agents
			 * */
			RabbitsGrassSimulationAgent rga = rgSpace.getAgentAt(newX,newY);
			if(rga!=null) {
				setVxVy();
			}	
		}
		this.grass+=rgSpace.takeGrassAt(x, y);
		energy--;
		
	}
	
	public void reduceEnergyLevel() {
		this.energy-= 50;;
	}
	
	private boolean tryMove(int newX, int newY) {
		
		return rgSpace.moveAgentAt(this.x, this.y,newX,newY);
	}

	public int getX() {
		// TODO Auto-generated method stub
		return x;
	}
	public int getGrass() {
		return grass;
	}
	
	public int getStepsToLive() {
		return energy;
	}

	public int getY() {
		// TODO Auto-generated method stub
		return y;
	}

	public void report() {
		// TODO Auto-generated method stub
		
		System.out.println(getID() + 
				" at "+
				" ("+x + "," + y +")"+ " has eaten " + getGrass() + " units " + " and " + getStepsToLive() + " steps to live.");
		
		
	}
	
	public boolean isDead() {
		return energy == 0;
	}
	
	public void receiveGrass(int amount) {
		grass+=amount;
	}
	
	

}
