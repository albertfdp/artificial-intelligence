/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dtu.dk.ai.blueducks.goals;

import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.map.Cell;

public abstract class Goal {
		
	/**
	 * Gets the next action to be executed given the current and destination cell.
	 *
	 * @param currentCell the current cell
	 * @param nextCell the destination cell
	 * @return the action to be executed
	 */
	public abstract Action getAction (Cell currentCell, Cell nextCell);

}
