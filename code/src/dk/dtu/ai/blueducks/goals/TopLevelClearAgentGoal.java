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


}
