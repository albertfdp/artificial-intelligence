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

	public Direction getAgentDirection() {
		return agentDirection;
	}

	public Agent getAgent() {
		return agent;
	}

	@Override
	public String toCommandString() {
		return "Move(" + agentDirection + ")";
	}

	@Override
	public void updateBeliefs() {
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		Cell destCell = agentCell.getNeighbour(agentDirection);
		
		Map<Cell, Agent> agents = LevelMap.getInstance().getAgents();
		agents.put(destCell, agent);
		agents.remove(agentCell);
		LevelMap.getInstance().markAsNotWall(destCell);
			
	}

	/** 
	 * Returns the next state when this MoveAction is the transition from the given one
	 * 
	 * @param state	The previous state
	 * @return the next state
	 */
	@Override
	public State getNextState(State state) {
		Cell nextCell = state.getAgentCell().getNeighbour(agentDirection);
		State nextState = new State(nextCell, this, state, agent);
		nextState.setBoxes(state.getBoxes());
		return nextState;	
	}

	@Override
	public String toString() {
		return "MoveAction ["+agent+", aDir=" + agentDirection + "]";
	}
	
	public void invalidateAction(){
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		Cell destCell = agentCell.getNeighbour(agentDirection);
		//TODO: continue
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
				(destCell == ((PullAction)otherAction).getDestCell(state,((PullAction)otherAction).getAgent()
						,((PullAction)otherAction).getAgentDirection()))){
			return true;		
		}
		
		if ((otherAction instanceof PushAction) && 
				(destCell == ((PushAction)otherAction).getDestCell(state,((PushAction)otherAction).getBox()
						,((PushAction)otherAction).getBoxDirection()))){
			return true;		
		}
		return false;
	}

	@Override
	public boolean isApplicable(MultiAgentState state) {
		Cell agentCell = state.getCellForAgent(agent);
		Cell destCell = agentCell.getNeighbour(agentDirection);
		
		if (state.isFree(destCell) != CellVisibility.NOT_FREE)
			return true;
		return false;
	}

	@Override
	public void execute(MultiAgentState state) {
		Cell agentCell = state.getCellForAgent(agent);
		Cell destCell = agentCell.getNeighbour(agentDirection);
		 
		state.changeAgentPosition(destCell, agentCell, agent);

	}

}
