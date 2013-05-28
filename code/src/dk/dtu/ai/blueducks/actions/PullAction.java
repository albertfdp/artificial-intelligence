/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.actions;

import java.util.Map;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.Direction;
import dk.dtu.ai.blueducks.map.LevelMap;
import dk.dtu.ai.blueducks.map.MultiAgentState;
import dk.dtu.ai.blueducks.map.State;
import dk.dtu.ai.blueducks.map.State.CellVisibility;

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
		
		Cell previousBoxCell = LevelMap.getInstance().getCurrentState().getCellForBox(box);
		if (LevelMap.getInstance().getLockedCells().contains(previousBoxCell))
			LevelMap.getInstance().unlockCell(previousBoxCell);
		
		LevelMap.getInstance().getCurrentState().movedAgent(agentCell, destCell);
		LevelMap.getInstance().getCurrentState().movedBox(box, boxCell, agentCell);
		LevelMap.getInstance().markAsNotWall(destCell);
	}

	@Override
	public State getNextState(State state) {
		Cell agentCell = state.getAgentCell();
		Cell agentDestCell = agentCell.getNeighbour(agentDirection);
		
		State nextState = new State(agentDestCell, this, state, agent, state.getOccupiedCells(), state.getCellsForBoxes());
		nextState.movedBox(box, agentCell.getNeighbour(boxDirection), agentCell);
		nextState.movedAgent(agentCell, agentDestCell);
		return nextState;
	}

	public void invalidateAction(){
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		Cell destCell = agentCell.getNeighbour(agentDirection);
		LevelMap.getInstance().setAsWall(destCell.x, destCell.y);
	}


	public Cell getDestCell(MultiAgentState state, Agent a, Direction agentDir){
		return state.getCellForAgent(a).getNeighbour(agentDir);
	}

	@Override
	public boolean isInConflict(MultiAgentState state, Action otherAction) {
		Cell destCell = getDestCell(state,agent,agentDirection);
		
		if (!isApplicable(state))
			return true;
		
		if ((otherAction instanceof MoveAction) && 
				(destCell == ((MoveAction)otherAction).getDestCell(state,((MoveAction)otherAction).getAgent()
						,((MoveAction)otherAction).getAgentDirection()))){
			return true;		
		}
		
		if ((otherAction instanceof PullAction) && 
				((destCell == ((PullAction)otherAction).getDestCell(state,((PullAction)otherAction).getAgent()
						,((PullAction)otherAction).getAgentDirection())) || (getBox() == ((PullAction)otherAction).getBox()))){		
			return true;		
		}
		
		if ((otherAction instanceof PushAction) && 
				((destCell == ((PushAction)otherAction).getDestCell(state,((PushAction)otherAction).getBox()
						, ((PushAction)otherAction).getBoxDirection())) || (getBox() == ((PushAction)otherAction).getBox()))){
			return true;		
		}
		return false;
	}


	@Override
	public boolean isApplicable(MultiAgentState state) {
		Cell agentCell = state.getCellForAgent(agent);
		Cell boxCell = state.getCellForBox(box);
		Cell destAgentCell = agentCell.getNeighbour(agentDirection);
		
		if ((boxCell.getCellNeighbours().contains(agentCell)) &&
				(state.isFree(destAgentCell) != CellVisibility.NOT_FREE) &&
				(box.getColor().equals(agent.getColor()))){
			return true;
		}
		
		return false;
	}



	@Override
	public void execute(MultiAgentState state) {
		Cell boxCell = state.getCellForBox(box);
		Cell agentCell = state.getCellForAgent(agent);
		Cell destCell = agentCell.getNeighbour(agentDirection);
		 
		state.changeAgentPosition(destCell, agentCell, agent);
		state.changeBoxPosition(agentCell, boxCell, box);
		
	}



	@Override
	public String toString() {
		return "PullAction ["+ agent + ", " + box + ", aDir=" + agentDirection
				+ ", bDir=" + boxDirection + "]";
	}
}
