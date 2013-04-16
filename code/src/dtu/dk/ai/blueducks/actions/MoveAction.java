package dtu.dk.ai.blueducks.actions;

import dk.dtu.ai.blueducks.map.Direction;

public class MoveAction extends Action {

	/** The agent direction. */
	Direction agentDirection;

	/**
	 * Instantiates an action to push a box.
	 * 
	 * @param cellContent the cell content
	 * @param dirAgent the direction in which the agent moves
	 * @param dirBox the direction in which the box moves
	 */
	public MoveAction(Direction dirAgent) {
		super();
		this.agentDirection = dirAgent;
	}

	@Override
	public String toCommandString() {
		return "Push(" + agentDirection + ")";
	}
}
