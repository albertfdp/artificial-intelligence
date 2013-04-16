package dtu.dk.ai.goals;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.Cell;

public class GoToBoxGoal extends Goal {
	
	private Cell from;
	private Box to;
	
	public GoToBoxGoal (Cell from, Box to) {
		this.from = from;
		this.to = to;
	}

}
