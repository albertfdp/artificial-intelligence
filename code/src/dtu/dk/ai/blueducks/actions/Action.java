/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dtu.dk.ai.blueducks.actions;

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

}
