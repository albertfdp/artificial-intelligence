package dk.dtu.ai.blueducks.goals;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.State;
import dk.dtu.ai.blueducks.planner.AStarNode;

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
	public boolean isSatisfied(AStarNode node) {
		State state = (State) node;
		if (state.getCellForBox(what) == to)
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "MoveBoxGoal [what=" + what + ", to=" + to + "]";
	}

}
