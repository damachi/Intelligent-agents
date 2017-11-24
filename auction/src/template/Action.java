package template;

public enum Action {

	PICKUP, DELIVER;
	
	@Override
	public String toString() {
		switch(this) {
			case PICKUP : return "pickUp";
			case DELIVER: return "deliver";
			default : throw new IllegalArgumentException();
		}
		
	}
}
