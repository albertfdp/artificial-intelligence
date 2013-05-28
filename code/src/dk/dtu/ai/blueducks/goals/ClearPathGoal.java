/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.goals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.State;
import dk.dtu.ai.blueducks.planner.AStarNode;

public class ClearPathGoal extends Goal {
	
	
	/** The cells that need to be cleared. */
	private Set<Cell> cells; 
	
	public Set<Cell> getCells() {
		return cells;
	}

	public void setCells(Set<Cell> cells) {
		this.cells = cells;
	}

	/** The box which should cleared. */
	private Box box;
	
	public Box getBox() {
		return box;
	}

	public void setBox(Box box) {
		this.box = box;
	}

	/**
	 * Instantiates a new clear path goal.
	 *
	 * @param plan the plan
	 * @param startIndexOfConflictingArea the start index of conflicting state
	 * @param endIndexOfConflictingArea the end index of conflicting state
	 */
	public ClearPathGoal(List<State> plan, int startIndexOfConflictingArea, int endIndexOfConflictingArea){
		//TODO: Ruxy
	}
	
	public ClearPathGoal(Box box, List<Cell> cells) {
		this.box = box;
		this.cells = new HashSet<Cell>(cells);
	}
	
	@Override
	public boolean isSatisfied(AStarNode node) {
		State state = (State) node;
		
		if (box != null && cells.contains(state.getCellForBox(box)))
			return false;
		
		if (cells.contains(state.getAgentCell()))
			return false;
		
		return true;
	}	

}
