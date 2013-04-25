/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.actions;


import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.Direction;
import dk.dtu.ai.blueducks.map.State;

/**
 * The Push Action.
 */
public class PushAction extends Action {

	/** The agent direction. */
	Direction agentDirection;

	/** The box direction. */
	Direction boxDirection;

	/** The agent. */
	Agent agent;
	
	/** The box. */
	Box box;
	
	/**
	 * Instantiates an action to push a box.
	 *
	 * @param dirAgent the direction in which the agent moves
	 * @param dirBox the direction in which the box moves
	 * @param agent the agent
	 * @param box the box
	 */
	public PushAction(Direction dirAgent, Direction dirBox, Agent agent, Box box) {
		super();
		this.agentDirection = dirAgent;
		this.boxDirection = dirBox;
		this.agent = agent;
		this.box = box;
	}

	/* (non-Javadoc)
	 * @see dtu.dk.ai.blueducks.actions.Action#toCommandString()
	 */
	@Override
	public String toCommandString() {
		return "Push(" + agentDirection + "," + boxDirection + ")";
	}

	/* (non-Javadoc)
	 * @see dtu.dk.ai.blueducks.actions.Action#updateBeliefs()
	 */
	@Override
	public void updateBeliefs() {
		// TODO Auto-generated method stub
		Cell agentCell = agent.getCell();
		Cell boxCell = box.getCell();
		Cell destCell = boxCell.getNeighbour(boxDirection);
		
		destCell.attachCellContent(boxCell.getContent());
		boxCell.attachCellContent(agentCell.getContent());
		agentCell.attachCellContent(null);
		
		
	}

	@Override
	public State getNextState(State state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isApplicable(State state) {
		// TODO Auto-generated method stub
		return false;
	}
}
