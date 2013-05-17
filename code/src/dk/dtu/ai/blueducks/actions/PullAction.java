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

public class PullAction extends Action {

	/** The agent direction. */
	private Direction agentDirection;

	/** The box direction. */
	private Direction boxDirection;

	/** The agent. */
	private Agent agent;

	/** The box. */
	private Box box;
	
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
	
	

	/**
	 * Gets the agent direction.
	 *
	 * @return the agent direction
	 */
	public Direction getAgentDirection() {
		return agentDirection;
	}



	public void setAgentDirection(Direction agentDirection) {
		this.agentDirection = agentDirection;
	}



	public Direction getBoxDirection() {
		return boxDirection;
	}



	public void setBoxDirection(Direction boxDirection) {
		this.boxDirection = boxDirection;
	}



	public Agent getAgent() {
		return agent;
	}



	public void setAgent(Agent agent) {
		this.agent = agent;
	}



	public Box getBox() {
		return box;
	}



	public void setBox(Box box) {
		this.box = box;
	}



	@Override
	public String toCommandString() {
		return "Pull("+ agentDirection + "," + boxDirection + ")";
	}

	@Override
	public void updateBeliefs() {
		
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		Cell boxCell = agentCell.getNeighbour(boxDirection);
		Cell destCell = agentCell.getNeighbour(agentDirection);
		
		Map<Cell, Agent> agents = LevelMap.getInstance().getAgents();
		agents.put(destCell, agent);
		agents.remove(agentCell);
		
		Map<Cell, Box> boxes = LevelMap.getInstance().getCurrentState().getBoxes();
		boxes.put(agentCell, box);
		boxes.remove(boxCell);

	}

	@Override
	public State getNextState(State state) {
		Cell agentCell = state.getAgentCell();
		Cell destCell = agentCell.getNeighbour(agentDirection);
		
		State nextState = new State(destCell, this, state, agent);
		Map<Cell, Box> boxes = state.getBoxes();
		
		// TODO: improve 	
		for (Entry<Cell, Box> e : boxes.entrySet()) {
			if (e.getValue() != box) {
				nextState.addBox(e.getKey(), e.getValue());
			} else {
				nextState.addBox(agentCell, box);
			}
		}
		
		return nextState;
	}

	public void invalidateAction(){
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		Cell destCell = agentCell.getNeighbour(agentDirection);
		LevelMap.getInstance().setAsWall(destCell.x, destCell.y);
	}
}
