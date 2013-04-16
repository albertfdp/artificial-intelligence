/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.actions;


import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.Direction;

/**
 * The Class MoveAction.
 */
public class MoveAction extends Action {

	/** The agent direction. */
	Direction agentDirection;

	
	/** The agent. */
	Agent agent;
	/**
	 * Instantiates an action to push a box.
	 *
	 * @param dirAgent the direction in which the agent moves
	 */
	public MoveAction(Direction dirAgent, Agent agent) {
		super();
		this.agentDirection = dirAgent;
		this.agent = agent; 
	}

	@Override
	public String toCommandString() {
		return "Move(" + agentDirection + ")";
	}

	@Override
	public void updateBeliefs() {
		Cell agentCell = agent.getCell();
		Cell destCell = agentCell.getNeighbour(agentDirection);
		
		destCell.attachCellContent(agentCell.getContent());
		agentCell.attachCellContent(null);
		
		
	}
}
