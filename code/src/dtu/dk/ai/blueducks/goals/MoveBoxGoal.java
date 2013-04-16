/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dtu.dk.ai.blueducks.goals;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.actions.Action;

/**
 * The Class MoveBoxGoal.
 */
public class MoveBoxGoal extends Goal {
	
	/** The what. */
	private Box what;
	
	/** The to. */
	private Cell to;
	
	/**
	 * Instantiates a new move box goal.
	 *
	 * @param what the what
	 * @param to the to
	 */
	public MoveBoxGoal(Box what, Cell to) {
		this.what = what;
		this.to = to;
	}

	/**
	 * Gets the what.
	 *
	 * @return the what
	 */
	public Box getWhat() {
		return what;
	}

	/**
	 * Sets the what.
	 *
	 * @param what the new what
	 */
	public void setWhat(Box what) {
		this.what = what;
	}

	/**
	 * Gets the to.
	 *
	 * @return the to
	 */
	public Cell getTo() {
		return to;
	}

	/**
	 * Sets the to.
	 *
	 * @param to the new to
	 */
	public void setTo(Cell to) {
		this.to = to;
	}
	
	/**
	 * Gets the next action to be executed given the current and destination cell.
	 *
	 * @param currentCell the current cell
	 * @param nextCell the destination cell
	 * @return the action to be executed
	 */	
	@Override
	public Action getAction(Cell currentCell, Cell nextCell) {
		return null;
	}

}
