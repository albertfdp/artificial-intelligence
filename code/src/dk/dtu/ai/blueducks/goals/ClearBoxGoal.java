package dk.dtu.ai.blueducks.goals;

import java.util.HashSet;
import java.util.Set;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.State;
import dk.dtu.ai.blueducks.planner.AStarNode;

public class ClearBoxGoal extends Goal {
	
	/** The cells that need to be cleared. */
	private Set<Cell> cells; 
	
	/** The box which should cleared. */
	private Box box;
	
	public Set<Cell> getCells() {
		return cells;
	}

	public void setCells(Set<Cell> cells) {
		this.cells = cells;
	}

	public Box getBox() {
		return box;
	}

	public void setBox(Box box) {
		this.box = box;
	}

	public ClearBoxGoal(Box box, Set<Cell> cells) {
		this.box = box;
		this.cells = new HashSet<Cell>(cells);
	}

	@Override
	public boolean isSatisfied(AStarNode node) {
		State state = (State) node;
		if (cells.contains(state.getCellForBox(box)))
			return false;
		
		return true;
	}

}
