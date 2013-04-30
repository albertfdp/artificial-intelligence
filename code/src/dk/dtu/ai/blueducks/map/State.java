/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.actions.MoveAction;
import dk.dtu.ai.blueducks.actions.PullAction;
import dk.dtu.ai.blueducks.actions.PushAction;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.planner.AStarNode;

public class State extends AStarNode {
	
	/** The boxes. */
	private Map<Cell, Box> boxes;
	
	/** The agent cell. */
	Cell agentCell;
	
	/** The previous state. */
	State previousState;
	
	/** The previous action. */
	Action previousAction;
	
	/** The agent. */
	Agent agent;
	
	/**
	 * Instantiates a new state.
	 *
	 * @param agentCell the agent cell
	 * @param previousAction the previous action
	 * @param previousState the previous state
	 * @param agent the agent
	 */
	public State (Cell agentCell, Action previousAction, State previousState, Agent agent){
		boxes = new HashMap<Cell, Box>();
		this.agentCell = agentCell;
		this.previousAction = previousAction;
		this.previousState = previousState;
		this.agent = agent;
	}
	
	
	@Override
	public List<AStarNode> getNeighbours() {
		List<Action> actions = getPossibleActions();
		List<AStarNode> nodes = new ArrayList<AStarNode>();
		for (Action action : actions) {
			nodes.add(action.getNextState(this));
		}
		return nodes;
	}

	@Override
	public AStarNode getPreviousNode(){
		return this.previousState;
	}

	@Override
	public Object getEdgeFromPrevNode() {
		return this.previousAction;
	}
	
	/**
	 * Sets the boxes.
	 *
	 * @param boxes the boxes
	 */
	public void setBoxes(Map<Cell, Box> boxes) {
		this.boxes = boxes;
	}
	
	/**
	 * Gets the boxes.
	 *
	 * @return the boxes
	 */
	public Map<Cell, Box> getBoxes() {
		return boxes;
	}
	
	/**
	 * Adds the box.
	 *
	 * @param cell the cell
	 * @param box the box
	 */
	public void addBox(Cell cell, Box box) {
		boxes.put(cell, box);
		
	}
	
	/**
	 * @returns the cell of the agent associated with the state
	 */
	public Cell getAgentCell() {
		return agentCell;
	}
	
	/**
	 * Checks if a given cell is free.
	 *
	 * @param cell the cell
	 * @return true, if is free
	 */
	public boolean isFree(Cell cell) {
		if(cell == null || cell == this.agentCell || boxes.keySet().contains(cell))
			return false;
		return true;
	}
	
	/**
	 * Gets the cell for box.
	 *
	 * @param box the box
	 * @return the cell for box
	 */
	public Cell getCellForBox(Box box){
		for(Entry<Cell, Box> e : boxes.entrySet()){
			if(e.getValue() == box){
				return e.getKey();
			}
		}
		return null;
	}

	/**
	 * Gets the possible actions.
	 *
	 * @return the possible actions
	 */
	public List<Action> getPossibleActions() {
		List<Action> actions = new ArrayList<Action>();
		List<Cell> neighbourCells = agentCell.getNeighbours();
		for (Cell cell : neighbourCells) {
			if (isFree(cell)) {
				actions.add(new MoveAction(agentCell.getDirection(cell), agent));
			} else {
				if (boxes.keySet().contains(cell) && boxes.get(cell).getColor() == agent.getColor()) {
					for (Cell neighbour : cell.getNeighbours()) {
						if (isFree(neighbour)) {
							actions.add(new PushAction(agentCell.getDirection(cell), cell
									.getDirection(neighbour), agent, boxes.get(cell)));
						}
					}
					for (Cell myNeighbour : neighbourCells) {
						if (isFree(myNeighbour)) {
							actions.add(new PullAction(agentCell.getDirection(myNeighbour), agentCell
									.getDirection(cell), agent, boxes.get(cell)));
						}
					}
				}
			}
		}
		
		return actions;
	}

	@Override
	public boolean satisfiesGoal(Goal goal) {
		return goal.isSatisfied(this);
	}

	
//	/**
//	 * Duplicate - shallow copy of the object
//	 *
//	 * @return a copy of the state 
//	 */
//	public State duplicate() {
//		State st = new State(agentCell, previousAction, previousState, agent);
//		st.boxes = new HashMap<Cell, Box>(this.boxes);
//		return st;
//	}
}
