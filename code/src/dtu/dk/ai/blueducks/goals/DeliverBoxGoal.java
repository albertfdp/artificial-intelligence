package dtu.dk.ai.blueducks.goals;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.Cell;

public class DeliverBoxGoal extends Goal {
	
	private Box what;
	private Cell to;
	
	public Box getWhat() {
		return what;
	}

	public void setWhat(Box what) {
		this.what = what;
	}

	public Cell getTo() {
		return to;
	}

	public void setTo(Cell to) {
		this.to = to;
	}

	public DeliverBoxGoal(Box what, Cell to) {
		this.what = what;
		this.to = to;
	}

}
