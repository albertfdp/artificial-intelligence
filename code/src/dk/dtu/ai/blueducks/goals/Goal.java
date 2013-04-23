/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.goals;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.State;

public abstract class Goal {

	/**
	 * Gets the action.
	 * 
	 * @param currentCell the current cell
	 * @param nextCell the next cell
	 * @param agent the agent
	 * @return the action
	 */
	public abstract Action getAction(Cell currentCell, Cell nextCell, Agent agent);

	/**
	 * Checks whether the goal is satisfied in the given state.
	 * 
	 * @param state the state
	 * @return true, if is satisfied
	 */
	public abstract boolean isSatisfied(State state);
}
