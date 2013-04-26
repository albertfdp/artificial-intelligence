/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.actions;

import dk.dtu.ai.blueducks.map.State;

/**
 * An Action executable by the agent.
 */
public abstract class Action {

	/**
	 * Gets the string representing the command to be sent to the server.
	 * 
	 * @return the string
	 */
	public abstract String toCommandString();

	/**
	 * Update the beliefs (LevelMap) according to the action.
	 * It should only be called by Mother Odin - NOT the agent threads!!! 
	 */
	public abstract void updateBeliefs();

	/**
	 * Gets the next state obtained by applying the action on the given state. 
	 * 
	 * @param state the state
	 * @return the next state
	 */
	public abstract State getNextState(State state);
	
	/**
	 * Checks if is applicable.
	 *
	 * @param state the state
	 * @return true, if is applicable
	 */
	public abstract boolean isApplicable(State state);
}


