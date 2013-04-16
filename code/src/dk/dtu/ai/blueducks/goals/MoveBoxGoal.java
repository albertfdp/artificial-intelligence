package dk.dtu.ai.blueducks.goals;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.map.Cell;

public class MoveBoxGoal extends Goal {
	
	private Box what;
	private Cell to;
	
	public MoveBoxGoal(Box what, Cell to) {
		this.what = what;
		this.to = to;
	}

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
	
	@Override
	public Action getAction(Cell currentCell, Cell nextCell) {
		return null;
	}

}
