/*
 * Artificial Intelligence and Multi-Agent Systems
 * Denmarks Tehnical University
 * 
 * Blue Ducks
 * Spring 2013
 */
package dk.dtu.ai.blueducks.goals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dk.dtu.ai.blueducks.Agent;
import dk.dtu.ai.blueducks.Box;
import dk.dtu.ai.blueducks.map.Cell;
import dk.dtu.ai.blueducks.map.State;
import dk.dtu.ai.blueducks.planner.AStarNode;

public class ClearPathGoal extends Goal {

	public Agent getRequestingAgent() {
		return requestingAgent;
	}

	/** The cells that need to be cleared. */
	private Set<Cell> cells;
	private Agent requestingAgent;

	public Set<Cell> getCells() {
		return cells;
	}

	public void setCells(Set<Cell> cells) {
		this.cells = cells;
	}

	/** The box which should cleared. */
	private Box box;

	public Box getBox() {
		return box;
	}

	public void setBox(Box box) {
		this.box = box;
	}

	/**
	 * Instantiates a new clear path goal.
	 * 
	 * @param plan the plan
	 * @param startIndexOfConflictingArea the start index of conflicting state
	 * @param endIndexOfConflictingArea the end index of conflicting state
	 */
	public ClearPathGoal(List<State> plan, int startIndexOfConflictingArea, int endIndexOfConflictingArea) {
		int i;
		List<Cell> toBeClearedCells = new ArrayList<Cell>();
		for (i = startIndexOfConflictingArea; i <= endIndexOfConflictingArea; i++) {
			toBeClearedCells.add(plan.get(i).getAgentCell());
		}
		this.cells = new HashSet<Cell>(toBeClearedCells);

	}

	public ClearPathGoal(Box box, Set<Cell> cells, Agent agent) {
		this.box = box;
		this.requestingAgent = agent;
		this.cells = cells;
	}

	@Override
	public boolean isSatisfied(AStarNode node) {
		State state = (State) node;

		if (box != null && cells.contains(state.getCellForBox(box)))
			return false;

		if (cells.contains(state.getAgentCell()))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "ClearPathGoal [cells=" + cells + ", box=" + box + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((box == null) ? 0 : box.hashCode());
		result = prime * result + ((cells == null) ? 0 : cells.hashCode());
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
		ClearPathGoal other = (ClearPathGoal) obj;
		if (box == null) {
			if (other.box != null)
				return false;
		} else if (!box.equals(other.box))
			return false;
		if (cells == null) {
			if (other.cells != null)
				return false;
		} else if (!cells.equals(other.cells))
			return false;
		return true;
	}

}
