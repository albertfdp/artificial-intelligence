/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.domain;

import java.util.ArrayList;

import dk.dtu.ai.blueducks.map.Direction;


public class Command {
	
	private final static String COMMAND_MOVE = "Move";
	private final static String COMMAND_PUSH = "Push";
	private final static String COMMAND_PULL = "Pull";
	private final static String COMMAND_NO_OPERATION = "NoOp";
	
	public static String Move (Direction dir) {
		return COMMAND_MOVE + "(" + dir + ")";
	}
	
	public static String Push (Direction from, Direction to) {
		return COMMAND_PUSH + "(" + from + "," + to + ")";
	}
	
	public static String Pull (Direction from, Direction to) {
		return COMMAND_PULL + "(" + from + "," + to + ")";
	}
	
	public static String NoOperation() {
		return COMMAND_NO_OPERATION;
	}
	

	

}
