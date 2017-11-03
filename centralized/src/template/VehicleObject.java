package template;

import logist.simulation.Vehicle;

/**
 *
 *Encapsulates a vehicle object
 */
public class VehicleObject {
	
	Vehicle vehicle;
	int capacity;
	
	/**
	 * @param vehicle : vehicle object
	 * 
	 */
	public VehicleObject(Vehicle vehicle) {
		this.vehicle = vehicle;
		this.capacity = vehicle.capacity();
	}

}
