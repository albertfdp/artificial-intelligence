/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dtu.dk.ai.blueducks.actions;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.Direction;

public class PullAction extends Action {

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
	 * @param cellContent the cell content
	 * @param dirAgent the direction in which the agent moves
	 * @param dirBox the direction in which the box moves
	 */
	public PullAction(Direction dirAgent, Direction dirBox, Agent agent, Box box) {
		super();
		this.agentDirection = dirAgent;
		this.boxDirection = dirBox;
		this.agent = agent;
		this.box = box; 
	}

	@Override
	public String toCommandString() {
		return "Pull("+ agentDirection + "," + boxDirection + ")";
	}

	@Override
	public void updateBeliefs() {
		Cell agentCell = agent.getCell();
		Cell boxCell = box.getCell();
		Cell destCell = agentCell.getNeighbour(boxDirection);
		
		destCell.attachCellContent(agentCell.getContent());
		agentCell.attachCellContent(boxCell.getContent());
		boxCell.attachCellContent(null);
	}
}
