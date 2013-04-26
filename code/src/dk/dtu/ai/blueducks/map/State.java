package dk.dtu.ai.blueducks.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.actions.Action;
import dk.dtu.ai.blueducks.goals.Goal;
import dk.dtu.ai.blueducks.planner.AStarNode;

public class State extends AStarNode {
	private Map<Cell, Box> boxes;
	Cell agentCell;
	State previousState;
	Action previousAction;
	
	public State (Cell agentCell, Action previousAction,State previousState){
		boxes = new HashMap<Cell, Box>();
		this.agentCell = agentCell;
		this.previousAction = previousAction;
		this.previousState = previousState;
	}
	public List<AStarNode> getNeighbours() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AStarNode getPreviousNode(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getEdgeFromPrevNode() {
		// TODO Auto-generated method stub
		return null;
	}
	public Map<Cell, Box> getBoxes() {
		// TODO Auto-generated method stub
		return boxes;
	}
	public void addBox(Cell cell, Box box) {
		boxes.put(cell, box);
		
	}
	
	/**
	 * @returns the cell of the agent associated with the state
	 */
	public Cell getAgentCell() {
		return agentCell;
	}
	
	public boolean isFree(Cell cell){
		//TODO: if(cell == this.agentCell || boxes.keySet().contains(cell) || LevelMap.getInstance().getAgents())
		return true;
	}
	public Cell getCellForBox(Box box){
		for(Entry<Cell, Box> e : boxes.entrySet()){
			if(e.getValue() == box){
				return e.getKey();
			}
		}
		return null;
	}
	@Override
	public boolean satisfiesGoal(Goal goal) {
		return goal.isSatisfied(this);
	}
}
