/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.actions.MoveAction;
import dk.dtu.ai.blueducks.actions.PullAction;
import dk.dtu.ai.blueducks.actions.PushAction;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.planner.AStarNode;

public class State extends AStarNode {

	public enum CellVisibility {
		FREE, NOT_FREE, POSSIBLY_FREE
	};

	BitSet occupiedCells;
	List<Cell> cellsForBoxes;

	/** The agent cell. */
	Cell agentCell;

	/** The previous state. */
	State previousState;

	/** The previous action. */
	Action previousAction;

	/** The agent. */
	Agent agent;

	/** The boxes in goal cell. */
	List<Box> boxesInGoalCell = new ArrayList<Box>();
	
	private int computedHashCode;

	private static final Logger log = Logger.getLogger(State.class.getSimpleName());

	/**
	 * Instantiates a new state.
	 *
	 * @param agentCell the agent cell
	 * @param previousAction the previous action
	 * @param previousState the previous state
	 * @param agent the agent
	 * @param map the map
	 */
	public State(Cell agentCell, Action previousAction, State previousState, Agent agent, BitSet previousStateOccupiedCells, List<Cell> previousStateCellForBoxes){
		this.agentCell = agentCell;
		this.previousAction = previousAction;
		this.previousState = previousState;
		this.agent = agent;
		occupiedCells = new BitSet();
		if(previousStateOccupiedCells != null)
			occupiedCells = (BitSet) previousStateOccupiedCells.clone();
		else
			occupiedCells = new BitSet();
		if(previousStateCellForBoxes != null)
			cellsForBoxes = new ArrayList<Cell>(previousStateCellForBoxes);
		else
			cellsForBoxes = new ArrayList<Cell>();
		
		if(previousState != null) {
			this.computedHashCode = previousState.hashCode();
		} else {
			computeHashCode();
		}
		
		
		
	}

	
	public void clearBoxesOfOtherColor(){
		List<Box> boxes = LevelMap.getInstance().getBoxesList();
		for(Box box : boxes) {
			if(!box.getColor().equals(agent.getColor())){
				Cell cell = cellsForBoxes.get(box.uniqueId);
				occupiedCells.set(cell.uniqueId, false);
				this.computedHashCode = this.computedHashCode - box.powerHashValue * cell.uniqueId;				
			}
		}
	}
	
	public List<Cell> getCellsForBoxes() {
		return cellsForBoxes;
	}

	public BitSet getOccupiedCells() {
		return occupiedCells;
	}

	public void computeHashCode(){
		this.computedHashCode = 0;
		log.info("Compute HASH code");
		List<Box> boxList = LevelMap.getInstance().getBoxesList();
		
		for(int index = 0; index < cellsForBoxes.size(); index++) {
			Cell cell = cellsForBoxes.get(index);
			Box box = boxList.get(index);
			this.computedHashCode = this.computedHashCode + box.powerHashValue * cell.uniqueId;
		}
		
		if(agent != null)
			this.computedHashCode = this.computedHashCode + agent.powerHashValue * agentCell.uniqueId;
		
	}
	
	
	@Override
	public List<AStarNode> getNeighbours() {
		List<Action> actions = getPossibleActions();

		// log.info("Possible Actions: " + actions .get(0));
		List<AStarNode> nodes = new ArrayList<AStarNode>();
		for (Action action : actions) {
			nodes.add(action.getNextState(this));
		}
		return nodes;
	}

	@Override
	public AStarNode getPreviousNode() {
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
	public void setInitalBoxesInLevelMap(Map<Cell, Box> boxes) {
		occupiedCells = new BitSet(Cell.noOfCells);
		cellsForBoxes = new ArrayList<Cell>();
		log.info("CELL FOR BOXES SIZE: " + boxes.size());
		for(int i = 0; i < boxes.size(); i++)
			cellsForBoxes.add(null);
		for(Entry<Cell,Box> entry: boxes.entrySet()){
			log.info("ADDED BOX "+ entry.getValue().uniqueId + " VALUE " + entry.getValue());
			occupiedCells.set(entry.getKey().uniqueId, true);
			cellsForBoxes.set(entry.getValue().uniqueId, entry.getKey());
		}
		
		computeHashCode();
	}

	/**
	 * Gets the boxes.
	 * 
	 * @return the boxes
	 */
//	public Map<Cell, Box> getBoxes() {
//		return boxes;
//	}

//	/**
//	 * Adds the box.
//	 * 
//	 * @param cell the cell
//	 * @param box the box
//	 */
//	//public void addBox(Cell cell, Box box) {
//	//	boxes.put(cell, box);
//
//	//}

	
	public void movedBox(Box box, Cell orig, Cell dest) {
		this.computedHashCode = this.computedHashCode  - box.powerHashValue * orig.uniqueId;
		this.computedHashCode = this.computedHashCode  + box.powerHashValue * dest.uniqueId;
		this.occupiedCells.set(orig.uniqueId, false);
		this.occupiedCells.set(dest.uniqueId, true);
		this.cellsForBoxes.set(box.uniqueId, dest);
	}
	
	public void movedAgent(Cell orig, Cell dest) {
		if(agent != null) {
			this.computedHashCode = this.computedHashCode - agent.powerHashValue * orig.uniqueId;
			this.computedHashCode = this.computedHashCode  + agent.powerHashValue * dest.uniqueId;
		}
	}
	/**
	 * @returns the cell of the agent associated with the state
	 */
	public Cell getAgentCell() {
		return agentCell;
	}

	/**
	 * Checks if is free.
	 * 
	 * @param cell the cell
	 * @return the celll visibility
	 */
	public CellVisibility isFree(Cell cell) {
		if (cell == null || occupiedCells.get(cell.uniqueId) || cell == agentCell || LevelMap.getInstance().getCellAt(cell.x, cell.y) == null || agent.forbidenCell == cell)
			return CellVisibility.NOT_FREE;
		// TODO: where will we use the fact that the cell might/ might not be free
		if (LevelMap.getInstance().isVerified(cell))
			return CellVisibility.FREE;
		return CellVisibility.POSSIBLY_FREE;
	}

	/**
	 * Gets the cell for box.
	 * 
	 * @param box the box
	 * @return the cell for box
	 */
	public Cell getCellForBox(Box box) {
		return cellsForBoxes.get(box.uniqueId);

	}

	/**
	 * Gets the possible actions.
	 * 
	 * @return the possible actions
	 */
	public List<Action> getPossibleActions() {
		List<Action> actions = new ArrayList<Action>();
		List<Cell> neighbourCells = agentCell.getCellNeighbours();

		for (Cell cell : neighbourCells) {
			if(cell == null)
				continue;
			if (isFree(cell) != CellVisibility.NOT_FREE) {
				actions.add(new MoveAction(agentCell.getDirection(cell), agent));
			} else {
				int boxId = cellsForBoxes.indexOf(cell);
				List<Box> boxList = LevelMap.getInstance().getBoxesList();

				if(occupiedCells.get(cell.uniqueId) && boxList.get(boxId).getColor().equals(agent.getColor())) {
				// TODO: Optimize to only use boxes.get(cell) once to not compute hash twice

					for (Cell neighbour : cell.getCellNeighbours()) {
						if (isFree(neighbour) != CellVisibility.NOT_FREE) {
							actions.add(new PushAction(agentCell.getDirection(cell), cell
									.getDirection(neighbour), agent, boxList.get(boxId)));
						}
					}
					for (Cell myNeighbour : neighbourCells) {
						if (isFree(myNeighbour) != CellVisibility.NOT_FREE) {
							actions.add(new PullAction(agentCell.getDirection(myNeighbour), agentCell
									.getDirection(cell), agent, boxList.get(boxId)));
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

	@Override
	public String toString() {
		return "State [agent=" + agent + ", boxesCell=" + cellsForBoxes + ", agentCell=" + agentCell
				+ ", previousAction=" + previousAction + "]";
	}

	public List<Box> getBoxesInGoalCells() {
		List<Box> boxList = LevelMap.getInstance().getBoxesList();
		for (Box box : boxList) {
			if (LevelMap.getInstance().getGoals().get(box.getId()).contains(cellsForBoxes.get(box.uniqueId))) {
				boxesInGoalCell.add(box);
			}
		}
		return boxesInGoalCell;
	}

	@Override
	public int hashCode() {

		return computedHashCode;
	}
	
//	@Override
//	public int hashCode() {
//		final int prime = 5003;
//		int result = 1;
//		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
//		result = prime * result + ((agentCell == null) ? 0 : agentCell.hashCode());
//		result = prime * result + ((boxesInGoalCell == null) ? 0 : boxesInGoalCell.hashCode());
//		result = prime * result + ((cellsForBoxes == null) ? 0 : cellsForBoxes.hashCode());
//		result = prime * result + ((occupiedCells == null) ? 0 : occupiedCells.hashCode());
//		return result;
//	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (agent == null) {
			if (other.agent != null)
				return false;
		} else if (!agent.equals(other.agent))
			return false;
		if (agentCell == null) {
			if (other.agentCell != null)
				return false;
		} else if (!agentCell.equals(other.agentCell))
			return false;
		if (boxesInGoalCell == null) {
			if (other.boxesInGoalCell != null)
				return false;
		} else if (!boxesInGoalCell.equals(other.boxesInGoalCell))
			return false;
		if (cellsForBoxes == null) {
			if (other.cellsForBoxes != null)
				return false;
		} else 
			for(int index = 0; index < cellsForBoxes.size(); index++){
				if(cellsForBoxes.get(index) != other.cellsForBoxes.get(index))
					return false;
			}
		if (occupiedCells == null) {
			if (other.occupiedCells != null)
				return false;
		} else 
			for(int index = 0; index < occupiedCells.size(); index++){
				if(occupiedCells.get(index) != other.occupiedCells.get(index))
					return false;
			}
		return true;
	}

}
