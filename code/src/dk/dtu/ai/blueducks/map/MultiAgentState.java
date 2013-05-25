/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.State.CellVisibility;

public class MultiAgentState {
	
	/** The boxes. */
	private Map<Cell, Box> boxes;

	/** The agents. */
	private Map<Cell, Agent> agents;

	
	public Map<Cell, Box> getBoxes() {
		return boxes;
	}


	public Map<Cell, Agent> getAgents() {
		return agents;
	}
	
	public MultiAgentState(Map<Cell, Agent> agents, Map<Cell, Box> boxes) {
		this.agents = new HashMap<Cell, Agent>(agents);
		this.boxes = new HashMap<Cell, Box>(boxes);
	}
	
	public Cell getCellForBox(Box box) {
		for(Entry<Cell, Box> e : boxes.entrySet()){
			if(e.getValue() == box){
				return e.getKey();
			}
		}
		return null;
	}
	
	public Cell getCellForAgent(Agent agent) {
		for(Entry<Cell, Agent> e : agents.entrySet()){
			if(e.getValue() == agent){
				return e.getKey();
			}
		}
		return null;
	}
	
	
	public void changeBoxPosition(Cell destCell, Cell boxCell,Box box) {
		boxes.put(destCell, box);
		boxes.remove(boxCell);
	}

	public void changeAgentPosition(Cell destCell, Cell agentCell, Agent agent) {
		agents.put(destCell, agent);
		agents.remove(agentCell);
	}
	
	/**
	 * Checks if is free.
	 *
	 * @param cell the cell
	 * @return the celll visibility
	 */
	public CellVisibility isFree(Cell cell) {
		if(cell == null || boxes.containsKey(cell) || agents.containsKey(cell))
			return CellVisibility.NOT_FREE;
		//TODO: where will we use the fact that the cell might/ might not be free
		if(LevelMap.getInstance().isVerified(cell))
			return CellVisibility.FREE;
		return CellVisibility.POSSIBLY_FREE;
	}


	@Override
	public String toString() {
		return "MultiAgentState [boxes=" + boxes + ", agents=" + agents + "]";
	}

}
