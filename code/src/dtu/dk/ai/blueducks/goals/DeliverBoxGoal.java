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

public class DeliverBoxGoal extends Goal {
	
	private Box what;
	private Cell to;
	
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
	 * Instantiates a new deliver box goal.
	 *
	 * @param what the what
	 * @param to the to
	 */
	public DeliverBoxGoal(Box what, Cell to) {
		this.what = what;
		this.to = to;
	}
	
	public Action getAction (Cell currentCell, Cell nextCell) {
		return null;
	}

}
