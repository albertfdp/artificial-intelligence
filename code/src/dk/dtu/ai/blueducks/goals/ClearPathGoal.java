/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.goals;

import java.util.List;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.State;
import dk.dtu.ai.blueducks.map.State.CellVisibility;
import dk.dtu.ai.blueducks.planner.AStarNode;

public class ClearPathGoal extends Goal {
	
	
	/** The cells that need to be cleared. */
	private List<Cell> cells; 
	
	/** The box which should cleared. */
	private Box box;
	
	public Box getBox() {
		return box;
	}

	public void setBox(Box box) {
		this.box = box;
	}

	public ClearPathGoal(Box box, List<Cell> cells) {
		this.box = box;
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
		// TODO: the agent and the box can be in the path
		State state = (State) node;
		for (Cell cell : cells) {
			// if cell is occupied and it is not the agent cell
			if (state.isFree(cell) !=CellVisibility.NOT_FREE && (cell != state.getAgentCell())) { // 0 means occupied
				// TODO: if the agent cannot move the box, then he can't do anything, so it's satisfied
				// FIXME: how I do that
				return false;
			}
		}
		return true;
	}
	

}
