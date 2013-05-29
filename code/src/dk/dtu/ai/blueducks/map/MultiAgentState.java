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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.State.CellVisibility;

public class MultiAgentState {
	
	
	BitSet occupiedCells;
	List<Cell> cellsForBoxes;
	
	/** The boxes. */
//	private Map<Cell, Box> boxes;

	/** The agents. */
	private Map<Cell, Agent> agents;



	public Map<Cell, Agent> getAgents() {
		return agents;
	}
	
	public MultiAgentState(Map<Cell, Agent> agents, BitSet occupiedVector, List<Cell> cellsForBoxes) {
		this.agents = new HashMap<Cell, Agent>(agents);
		this.occupiedCells = new BitSet(occupiedVector.size());
		if(occupiedVector != null)
			this.occupiedCells.or(occupiedVector);
		this.cellsForBoxes = new ArrayList<>();
		if(cellsForBoxes != null)
			this.cellsForBoxes.addAll(cellsForBoxes);

	}
	
	public Cell getCellForBox(Box box) {
		return cellsForBoxes.get(box.uniqueId);
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
		this.occupiedCells.set(boxCell.uniqueId, false);
		this.occupiedCells.set(destCell.uniqueId, true);
		this.cellsForBoxes.set(box.uniqueId, destCell);
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
		if(cell == null || occupiedCells.get(cell.uniqueId) || agents.containsKey(cell))
			return CellVisibility.NOT_FREE;
		//TODO: where will we use the fact that the cell might/ might not be free
		if(LevelMap.getInstance().isVerified(cell))
			return CellVisibility.FREE;
		return CellVisibility.POSSIBLY_FREE;
	}


	@Override
	public String toString() {
		return "MultiAgentState [CellForBoxes=" + cellsForBoxes + ", agents=" + agents + "]";
	}


	public BitSet getOccupiedCells() {
		return occupiedCells;
	}


	public List<Cell> getCellForBoxes() {
		return cellsForBoxes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agents == null) ? 0 : agents.hashCode());
		result = prime * result + ((cellsForBoxes == null) ? 0 : cellsForBoxes.hashCode());
		result = prime * result + ((occupiedCells == null) ? 0 : occupiedCells.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MultiAgentState other = (MultiAgentState) obj;
		if (agents == null) {
			if (other.agents != null)
				return false;
		} else if (!agents.equals(other.agents))
			return false;
		if (cellsForBoxes == null) {
			if (other.cellsForBoxes != null)
				return false;
		} else if (!cellsForBoxes.equals(other.cellsForBoxes))
			return false;
		if (occupiedCells == null) {
			if (other.occupiedCells != null)
				return false;
		} else if (!occupiedCells.equals(other.occupiedCells))
			return false;
		return true;
	}

}
