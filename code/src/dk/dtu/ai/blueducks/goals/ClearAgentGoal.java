package dk.dtu.ai.blueducks.goals;

import java.util.HashSet;
import java.util.Set;

import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.State;
import dk.dtu.ai.blueducks.planner.AStarNode;

public class ClearAgentGoal extends Goal {
	
	/** The cells that need to be cleared. */
	private Set<Cell> cells;
	
	public ClearAgentGoal(Set<Cell> cellsToBeCleared) {
		this.cells = new HashSet<Cell>(cellsToBeCleared);
	}

	@Override
	public boolean isSatisfied(AStarNode node) {
		State state = (State) node;
		if (cells.contains(state.getAgentCell()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClearAgentGoal [cells=" + cells + "]";
	}

}
