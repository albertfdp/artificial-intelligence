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

	public Box getBox() {
		return box;
	}

	public Direction getBoxDirection() {
		return boxDirection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dtu.dk.ai.blueducks.actions.Action#toCommandString()
	 */
	@Override
	public String toCommandString() {
		return "Push(" + agentDirection + "," + boxDirection + ")";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dtu.dk.ai.blueducks.actions.Action#updateBeliefs()
	 */
	@Override
	public void updateBeliefs() {

		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		Cell boxCell = agentCell.getNeighbour(agentDirection);
		Cell destCell = boxCell.getNeighbour(boxDirection);

		Map<Cell, Agent> agents = LevelMap.getInstance().getAgents();
		agents.put(boxCell, agent);
		agents.remove(agentCell);

		LevelMap.getInstance().getCurrentState().movedAgent(agentCell, boxCell);
		LevelMap.getInstance().getCurrentState().movedBox(box, boxCell, destCell);
		LevelMap.getInstance().markAsNotWall(destCell);
	}

	@Override
	public State getNextState(State state) {
		Cell boxCell = state.getCellForBox(box);
		Cell destCell = boxCell.getNeighbour(boxDirection);
		Cell agentCell = state.getAgentCell();

		State nextState = new State(boxCell, this, state, agent, state.getOccupiedCells(), state.getCellsForBoxes());

		nextState.movedBox(box, boxCell, destCell);
		nextState.movedAgent(agentCell, boxCell);

		return nextState;
	}

	public void invalidateAction() {
		Cell agentCell = LevelMap.getInstance().getCellForAgent(agent);
		Cell destCell = agentCell.getNeighbour(agentDirection);
		LevelMap.getInstance().setAsWall(destCell.x, destCell.y);
	}

	public Cell getDestCell(MultiAgentState state, Box b, Direction boxDir) {
		return state.getCellForBox(b).getNeighbour(boxDir);
	}

	@Override
	public boolean isInConflict(MultiAgentState state, Action otherAction) {

		Cell destCell = getDestCell(state, box, boxDirection);

		if (!isApplicable(state))
			return false;

		if ((otherAction instanceof MoveAction)
				&& (destCell == ((MoveAction) otherAction)
						.getDestCell(state, ((MoveAction) otherAction).getAgent(),
								((MoveAction) otherAction).getAgentDirection()))) {
			return false;
		}

		if ((otherAction instanceof PullAction)
				&& ((destCell == ((PullAction) otherAction)
						.getDestCell(state, ((PullAction) otherAction).getAgent(),
								((PullAction) otherAction).getAgentDirection())) || (getBox() == ((PullAction) otherAction)
						.getBox()))) {
			return false;
		}

		if ((otherAction instanceof PushAction)
				&& (destCell == ((PushAction) otherAction).getDestCell(state,
						((PushAction) otherAction).getBox(), ((PushAction) otherAction).getBoxDirection()))) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isApplicable(MultiAgentState state) {
		Cell agentCell = state.getCellForAgent(agent);
		Cell boxCell = state.getCellForBox(box);
		Cell destBoxCell = boxCell.getNeighbour(boxDirection);

		if ((boxCell.getCellNeighbours().contains(agentCell))
				&& (state.isFree(destBoxCell) == CellVisibility.FREE)
				&& (box.getColor().equals(agent.getColor()))) {
			return true;
		}

		return false;
	}

	@Override
	public void execute(MultiAgentState state) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return "PushAction [" + agent + ", " + box + ", aDir=" + agentDirection + ", bDir=" + boxDirection
				+ "]";
	}
}
