package dk.dtu.ai.blueducks.goals;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.State;

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

	@Override
	public Action getAction(Cell currentCell, Cell nextCell, Agent agent) {
		return null;
	}

	@Override
	public boolean isSatisfied(State state) {
		// TODO: check also if the cell has already been satisfied by another box
		if (state.getCellForBox(what) == to)
			return true;
		
		return false;
	}

}
