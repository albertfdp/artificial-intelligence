package dtu.dk.ai.goals;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.Cell;

public class DeliverBoxGoal extends Goal {
	
	private Box what;
	private Cell to;
	
	public DeliverBoxGoal(Box what, Cell to) {
		this.what = what;
		this.to = to;
	}

}
