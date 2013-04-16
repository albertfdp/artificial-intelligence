/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.domain;

import dk.dtu.ai.blueducks.map.CellContent;
import dk.dtu.ai.blueducks.map.Direction;


public class Command {
	
	private final static String COMMAND_MOVE = "Move";
	private final static String COMMAND_PUSH = "Push";
	private final static String COMMAND_PULL = "Pull";
	private final static String COMMAND_NO_OPERATION = "NoOp";
	
	/**
	 * Move.
	 *
	 * @param cellContent the cell content (agent or box)
	 * @param direction the direction
	 * @return the string
	 */
	public static String Move (CellContent cellContent, Direction direction) {
		return COMMAND_MOVE + "(" + cellContent + "," + direction + ")";
	}
	
	/**
	 * Push.
	 *
	 * @param cellContent the cell content (agent or box)
	 * @param agentDirection the agent direction
	 * @param boxDirection the box direction
	 * @return the string
	 */
	public static String Push (CellContent cellContent, Direction agentDirection, Direction boxDirection) {
		return COMMAND_PUSH + "(" + cellContent + "," + agentDirection + "," + boxDirection + ")";
	}
	
	/**
	 * Pull.
	 *
	 * @param cellContent the cell content (agent or box)
	 * @param agentDirection the agent direction
	 * @param currentBoxDirection the current box direction relative to the last position of the agent
	 * @return the string
	 */
	public static String Pull (CellContent cellContent, Direction agentDirection, Direction currentBoxDirection) {
		return COMMAND_PULL + "(" + cellContent + "," + agentDirection + "," + currentBoxDirection + ")";
	}
	
	/**
	 * No operation.
	 *
	 * @param cellContent the cell content (agent or box)
	 * @return the string
	 */
	public static String NoOperation(CellContent cellContent) {
		return COMMAND_NO_OPERATION + "("  + cellContent + ")";
	}
	

	

}
