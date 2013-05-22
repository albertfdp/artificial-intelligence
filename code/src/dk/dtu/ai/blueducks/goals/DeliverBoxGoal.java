package dk.dtu.ai.blueducks.goals;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.State;
import dk.dtu.ai.blueducks.planner.AStarNode;

public class DeliverBoxGoal extends Goal {
	
	private Box what;
	private Cell to;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		result = prime * result + ((what == null) ? 0 : what.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeliverBoxGoal other = (DeliverBoxGoal) obj;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		if (what == null) {
			if (other.what != null)
				return false;
		} else if (!what.equals(other.what))
			return false;
		return true;
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

	public DeliverBoxGoal(Box what, Cell to) {
		this.what = what;
		this.to = to;
	}

	@Override
	public boolean isSatisfied(AStarNode node) {
		// TODO: check also if the cell has already been satisfied by another box
		State state = (State) node;
		if (state.getCellForBox(what) == to)
			return true;
		
		return false;
	}

	@Override
	public String toString() {
		return "DeliverBoxGoal [what=" + what.getId() + ", to=" + to + "]";
	}

}
