package dk.dtu.ai.blueducks.goals;

import java.util.HashSet;
import java.util.Set;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.State;
import dk.dtu.ai.blueducks.planner.AStarNode;

public class TopLevelClearAgentGoal extends Goal{
	/** The cells that need to be cleared. */
	private Set<Cell> cells;
	private Agent agentToBeCleared;
	
	public TopLevelClearAgentGoal(Set<Cell> cellsToBeCleared, Agent agent) {
		this.cells = new HashSet<Cell>(cellsToBeCleared);
		this.agentToBeCleared = agent;
	}

	public Set<Cell> getCells() {
		return cells;
	}

	@Override
	public boolean isSatisfied(AStarNode node) {
		Cell cell = LevelMap.getInstance().getCellForAgent(agentToBeCleared);
		if (cells.contains(cell))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agentToBeCleared == null) ? 0 : agentToBeCleared.hashCode());
		result = prime * result + ((cells == null) ? 0 : cells.hashCode());
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
		TopLevelClearAgentGoal other = (TopLevelClearAgentGoal) obj;
		if (agentToBeCleared == null) {
			if (other.agentToBeCleared != null)
				return false;
		} else if (!agentToBeCleared.equals(other.agentToBeCleared))
			return false;
		if (cells == null) {
			if (other.cells != null)
				return false;
		} else if (!cells.equals(other.cells))
			return false;
		return true;
	}


}
