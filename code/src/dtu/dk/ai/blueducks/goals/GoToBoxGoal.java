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
import dk.dtu.ai.blueducks.actions.MoveAction;

/**
 * The Class GoToBoxGoal.
 */
public class GoToBoxGoal extends Goal {
	
	/** The from. */
	private Cell from;
	
	/** The to. */
	private Box to;
	
	/**
	 * Instantiates a new go to box goal.
	 *
	 * @param from the from
	 * @param to the to
	 */
	public GoToBoxGoal (Cell from, Box to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * Gets the from.
	 *
	 * @return the from
	 */
	public Cell getFrom() {
		return from;
	}

	/**
	 * Sets the from.
	 *
	 * @param from the new from
	 */
	public void setFrom(Cell from) {
		this.from = from;
	}

	/**
	 * Gets the to.
	 *
	 * @return the to
	 */
	public Box getTo() {
		return to;
	}

	/**
	 * Sets the to.
	 *
	 * @param to the new to
	 */
	public void setTo(Box to) {
		this.to = to;
	}
	
	/**
	 * Gets the next action to be executed given the current and destination cell.
	 *
	 * @param currentCell the current cell
	 * @param nextCell the destination cell
	 * @return the action to be executed
	 */	
	public Action getAction(Cell currentCell, Cell nextCell) {
		//return new MoveAction(currentCell.getDirection(nextCell));
		return null;
	}

}
