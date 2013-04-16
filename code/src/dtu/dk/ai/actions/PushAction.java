/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dtu.dk.ai.actions;


import dk.dtu.ai.blueducks.map.Direction;

/**
 * The Push Action.
 */
public class PushAction extends Action {

	/** The agent direction. */
	Direction agentDirection;

	/** The box direction. */
	Direction boxDirection;

	/**
	 * Instantiates an action to push a box.
	 * 
	 * @param cellContent the cell content
	 * @param dirAgent the direction in which the agent moves
	 * @param dirBox the direction in which the box moves
	 */
	public PushAction(Direction dirAgent, Direction dirBox) {
		super();
		this.agentDirection = dirAgent;
		this.boxDirection = dirBox;
	}

	@Override
	public String toCommandString() {
		return "Push(" + agentDirection + "," + boxDirection + ")";
	}
}
