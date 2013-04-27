/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.actions;

import java.util.Map;
import java.util.Map.Entry;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.Direction;
import dk.dtu.ai.blueducks.map.State;

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
//		Cell agentCell = agent.getCell();
//		Cell boxCell = box.getCell();
//		Cell destCell = agentCell.getNeighbour(boxDirection);
//		
//		destCell.attachCellContent(agentCell.getContent());
//		agentCell.attachCellContent(boxCell.getContent());
//		boxCell.attachCellContent(null);
	}

	@Override
	public State getNextState(State state) {
		if (!isApplicable(state))
			return state;
		Cell agentCell = state.getAgentCell();
		Cell destCell = agentCell.getNeighbour(agentDirection);
		
		State nextState = new State(destCell, this, state, agent);
			
		Map<Cell, Box> boxes = state.getBoxes();
		for (Entry<Cell, Box> e : boxes.entrySet()) {
			if (e.getValue() != box) {
				nextState.addBox(e.getKey(), e.getValue());
			} else {
				nextState.addBox(agentCell, box);
			}
		}
		
		return nextState;
	}

	@Override
	public boolean isApplicable(State state) {
		Cell agentCell = state.getAgentCell();
		Cell boxCell = state.getCellForBox(box);
		Cell destCell = agentCell.getNeighbour(agentDirection);
		
		return (
				state.isFree(destCell) // destination is free, and implicit, agent and dest are neighbors
				&& (agentCell.getNeighbour(boxDirection) == boxCell) // agent and box are neighbors
				&& (agent.getColor().equals(box.getColor()))); 
	}

}
