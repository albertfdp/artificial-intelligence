/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.actions;

/**
 * The Class Action.
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
	 */
	public abstract void updateBeliefs();

}
