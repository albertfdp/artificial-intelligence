/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.goals;

import java.util.List;

import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.State;
import dk.dtu.ai.blueducks.planner.AStarNode;

public class ClearPathGoal extends Goal {
	
	
	/** The cells that need to be cleared. */
	private List<Cell> cells; 
	
	public ClearPathGoal(List<Cell> cells) {
		this.cells = cells;
	}
	
	public List<Cell> getCells() {
		return cells;
	}

	public void setCells(List<Cell> cells) {
		this.cells = cells;
	}

	@Override
	public boolean isSatisfied(AStarNode node) {
		State state = (State) node;
		for (Cell cell : cells) {
			if (state.isFree(cell) > 0) { // 0 means occupied
				return false;
			}
		}
		return true;
	}
	

}
