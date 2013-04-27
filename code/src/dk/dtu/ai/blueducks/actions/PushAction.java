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
import dk.dtu.ai.blueducks.map.LevelMap;
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
		
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		Cell boxCell = LevelMap.getInstance().getCurrentState().getCellForBox(box);
		Cell destCell = agentCell.getNeighbour(boxDirection);
		
		Map<Cell, Agent> agents = LevelMap.getInstance().getAgents();
		agents.put(boxCell, agent);
		agents.remove(agentCell);
		
		Map<Cell, Box> boxes = LevelMap.getInstance().getCurrentState().getBoxes();
		boxes.put(destCell, box);
		boxes.remove(boxCell);
	
	}
	
	@Override
	public State getNextState(State state) {
		if (!isApplicable(state))
			return state;
		Cell boxCell = state.getCellForBox(box);
		Cell destCell = boxCell.getNeighbour(boxDirection);
					
		State nextState = new State(boxCell, this, state, agent);
		Map<Cell, Box> boxes = state.getBoxes();
		for (Entry<Cell, Box> e : boxes.entrySet()) {
			if (e.getValue() != box) {
				nextState.addBox(e.getKey(), e.getValue());
			} else {
				nextState.addBox(destCell, box);
			}
		}
		return nextState;
	}

	@Override
	public boolean isApplicable(State state) {
		Cell agentCell = state.getAgentCell();
		Cell boxCell = state.getCellForBox(box);
		Cell destCell = boxCell.getNeighbour(boxDirection);
		
		return (
				state.isFree(destCell) // destination is free, and implicit, box and dest are neighbors
				&& (agentCell.getNeighbour(agentDirection) == boxCell) // agent and box are neighbors
				&& (agent.getColor().equals(box.getColor()))); 
		
	}

}
